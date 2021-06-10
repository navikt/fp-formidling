package no.nav.foreldrepenger.melding.brevbestiller.impl;

import static no.nav.foreldrepenger.melding.brevbestiller.XmlUtil.elementTilString;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import no.nav.foreldrepenger.felles.integrasjon.rest.DefaultJsonMapper;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.brevbestiller.api.BrevproduksjonTjeneste;
import no.nav.foreldrepenger.melding.brevbestiller.task.BrevTaskProperties;
import no.nav.foreldrepenger.melding.brevbestiller.task.DistribuerBrevTask;
import no.nav.foreldrepenger.melding.brevbestiller.task.FerdigstillForsendelseTask;
import no.nav.foreldrepenger.melding.brevbestiller.task.TilknyttVedleggTask;
import no.nav.foreldrepenger.melding.brevmapper.DokumentdataMapper;
import no.nav.foreldrepenger.melding.brevmapper.DokumentdataMapperProvider;
import no.nav.foreldrepenger.melding.datamapper.DokumentXmlDataMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.dokumentdata.BestillingType;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentData;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.repository.DokumentRepository;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.historikk.DokumentHistorikkinnslag;
import no.nav.foreldrepenger.melding.historikk.HistorikkAktør;
import no.nav.foreldrepenger.melding.historikk.HistorikkRepository;
import no.nav.foreldrepenger.melding.historikk.HistorikkinnslagType;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.Dokgen;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.Dokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.journal.OpprettJournalpostTjeneste;
import no.nav.foreldrepenger.melding.integrasjon.journal.dto.OpprettJournalpostResponse;
import no.nav.foreldrepenger.melding.kafkatjenester.historikk.task.PubliserHistorikkTaskProperties;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.melding.typer.JournalpostId;
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskGruppe;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskRepository;

@ApplicationScoped
public class DokgenBrevproduksjonTjeneste implements BrevproduksjonTjeneste {
    private static final Logger LOGGER = LoggerFactory.getLogger(DokgenBrevproduksjonTjeneste.class);

    private DokumentFellesDataMapper dokumentFellesDataMapper;
    private DomeneobjektProvider domeneobjektProvider;
    private DokumentRepository dokumentRepository;
    private Dokgen dokgenRestKlient;
    private OpprettJournalpostTjeneste opprettJournalpostTjeneste;
    private DokumentdataMapperProvider dokumentdataMapperProvider;
    private ProsessTaskRepository prosessTaskRepository;
    private HistorikkRepository historikkRepository;
    private DokprodBrevproduksjonTjeneste dokprodBrevproduksjonTjeneste;

    DokgenBrevproduksjonTjeneste() {
        // CDI
    }

    @Inject
    public DokgenBrevproduksjonTjeneste(DokumentFellesDataMapper dokumentFellesDataMapper,
            DomeneobjektProvider domeneobjektProvider,
            DokumentRepository dokumentRepository,
            /* @Jersey */Dokgen dokgenRestKlient,
            OpprettJournalpostTjeneste opprettJournalpostTjeneste,
            DokumentdataMapperProvider dokumentdataMapperProvider,
            ProsessTaskRepository prosessTaskRepository,
            HistorikkRepository historikkRepository,
            DokprodBrevproduksjonTjeneste dokprodBrevproduksjonTjeneste) {
        this.dokumentFellesDataMapper = dokumentFellesDataMapper;
        this.domeneobjektProvider = domeneobjektProvider;
        this.dokumentRepository = dokumentRepository;
        this.dokgenRestKlient = dokgenRestKlient;
        this.opprettJournalpostTjeneste = opprettJournalpostTjeneste;
        this.dokumentdataMapperProvider = dokumentdataMapperProvider;
        this.prosessTaskRepository = prosessTaskRepository;
        this.historikkRepository = historikkRepository;
        this.dokprodBrevproduksjonTjeneste = dokprodBrevproduksjonTjeneste;
    }

    @Override
    public byte[] forhandsvisBrev(DokumentHendelse dokumentHendelse, Behandling behandling, DokumentMalType dokumentMal) {
        DokumentData dokumentData = lagDokumentData(behandling, dokumentMal, BestillingType.UTKAST);
        dokumentFellesDataMapper.opprettDokumentDataForBehandling(behandling, dokumentData, dokumentHendelse);
        dokumentRepository.lagre(dokumentData);
        DokumentFelles førsteDokumentFelles = dokumentData.getFørsteDokumentFelles();

        DokumentdataMapper dokumentdataMapper = dokumentdataMapperProvider.getDokumentdataMapper(dokumentMal);
        Dokumentdata dokumentdata = dokumentdataMapper.mapTilDokumentdata(førsteDokumentFelles, dokumentHendelse, behandling);
        byte[] brev;
        try {
            brev = dokgenRestKlient.genererPdf(dokumentdataMapper.getTemplateNavn(), behandling.getSpråkkode(), dokumentdata);
        } catch (Exception e) {
            throw new TekniskException("FPFORMIDLING-221006",
                    String.format("Klarte ikke hente forhåndvise mal %s for behandling %s.", dokumentMal.getKode(), behandling.getUuid().toString()),
                    e);
        }
        LOGGER.info("Dokument av type {} i behandling id {} er forhåndsvist", dokumentMal.getKode(), behandling.getUuid().toString());
        return brev;
    }

    @Override
    public List<DokumentHistorikkinnslag> bestillBrev(DokumentHendelse dokumentHendelse, Behandling behandling, DokumentMalType dokumentMal) {
        DokumentData dokumentData = lagDokumentData(behandling, dokumentMal, BestillingType.BESTILL);
        dokumentFellesDataMapper.opprettDokumentDataForBehandling(behandling, dokumentData, dokumentHendelse);
        dokumentRepository.lagre(dokumentData);
        boolean innsynMedVedlegg = erInnsynMedVedlegg(behandling, dokumentMal);

        for (DokumentFelles dokumentFelles : dokumentData.getDokumentFelles()) {
            DokumentdataMapper dokumentdataMapper = dokumentdataMapperProvider.getDokumentdataMapper(dokumentMal);
            Dokumentdata dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling);
            dokumentFelles.setBrevData(DefaultJsonMapper.toJson(dokumentdata));
            opprettAlternativeBrevDataOmNødvendig(dokumentHendelse, behandling, dokumentMal, dokumentFelles);

            byte[] brev = dokgenRestKlient.genererPdf(dokumentdataMapper.getTemplateNavn(), behandling.getSpråkkode(), dokumentdata);
            OpprettJournalpostResponse response = opprettJournalpostTjeneste.journalførUtsendelse(brev, dokumentMal, dokumentFelles, dokumentHendelse,
                    behandling.getFagsakBackend().getSaksnummer(), !innsynMedVedlegg);
            JournalpostId journalpostId = new JournalpostId(response.getJournalpostId());

            if (innsynMedVedlegg) {
                leggTilVedleggOgFerdigstillForsendelse(dokumentHendelse.getBehandlingUuid(), journalpostId);
            }

            distribuerBrevOgLagHistorikk(dokumentHendelse, dokumentMal, response, journalpostId, innsynMedVedlegg);
        }
        return Collections.emptyList(); // TODO(JEJ): Omstrukturere koden når DokprodBrevproduksjonTjeneste er historie
    }

    private DokumentData lagDokumentData(Behandling behandling, DokumentMalType dokumentMalType, BestillingType bestillingType) {
        return DokumentData.builder()
                .medDokumentMalType(dokumentMalType)
                .medBehandlingUuid(behandling.getUuid())
                .medBestiltTid(LocalDateTime.now())
                .medBestillingType(bestillingType.name())
                .build();
    }

    private void opprettAlternativeBrevDataOmNødvendig(DokumentHendelse dokumentHendelse, Behandling behandling, DokumentMalType dokumentMal,
            DokumentFelles dokumentFelles) {
        if (DokumentMalType.INNVILGELSE_FORELDREPENGER.equals(dokumentMal)) {
            try {
                var saksnummer = dokprodBrevproduksjonTjeneste.bestemSaksnummer(DokumentMalType.INNVILGELSE_FORELDREPENGER_DOK,
                        dokumentFelles.getSaksnummer(), domeneobjektProvider.hentFagsakBackend(behandling).getAktørId());
                Element brevXmlElement = DokumentXmlDataMapper.mapTilBrevXml(DokumentMalType.INNVILGELSE_FORELDREPENGER_DOK, dokumentFelles,
                        dokumentHendelse, behandling, saksnummer);
                dokumentFelles.setAlternativeBrevData(elementTilString(brevXmlElement));
            } catch (Exception e) {
                LOGGER.info("Feilet i å lage Dokprod-versjonen av innvilgelse foreldrepenger for bestilling {} og behandling {}",
                        dokumentHendelse.getBestillingUuid(), dokumentHendelse.getBehandlingUuid(), e);
            }
        }
    }

    private void distribuerBrevOgLagHistorikk(DokumentHendelse dokumentHendelse, DokumentMalType dokumentMal, OpprettJournalpostResponse response,
            JournalpostId journalpostId, boolean innsynMedVedlegg) {
        ProsessTaskGruppe taskGruppe = new ProsessTaskGruppe();
        taskGruppe.addNesteSekvensiell(opprettDistribuerBrevTask(journalpostId, innsynMedVedlegg, dokumentHendelse.getBehandlingUuid()));
        DokumentHistorikkinnslag historikkinnslag = lagHistorikkinnslag(dokumentHendelse, response, dokumentMal);
        taskGruppe.addNesteSekvensiell(opprettPubliserHistorikkTask(historikkinnslag));
        prosessTaskRepository.lagre(taskGruppe);
    }

    private void leggTilVedleggOgFerdigstillForsendelse(UUID behandlingUid, JournalpostId journalpostId) {
        ProsessTaskGruppe taskGruppe = new ProsessTaskGruppe();
        taskGruppe.addNesteSekvensiell(opprettTilknyttVedleggTask(behandlingUid, journalpostId));
        taskGruppe.addNesteSekvensiell(opprettFerdigstillForsendelseTask(journalpostId));
        prosessTaskRepository.lagre(taskGruppe);
    }

    private ProsessTaskData opprettTilknyttVedleggTask(UUID behandlingUuId, JournalpostId journalpostId) {
        ProsessTaskData prosessTaskData = new ProsessTaskData(TilknyttVedleggTask.TASKTYPE);
        prosessTaskData.setProperty(BrevTaskProperties.JOURNALPOST_ID, journalpostId.getVerdi());
        prosessTaskData.setProperty(BrevTaskProperties.BEHANDLING_UUID, (String.valueOf(behandlingUuId)));
        return prosessTaskData;
    }

    private ProsessTaskData opprettFerdigstillForsendelseTask(JournalpostId journalpostId) {
        ProsessTaskData prosessTaskData = new ProsessTaskData(FerdigstillForsendelseTask.TASKTYPE);
        prosessTaskData.setProperty(BrevTaskProperties.JOURNALPOST_ID, journalpostId.getVerdi());
        return prosessTaskData;
    }

    private ProsessTaskData opprettDistribuerBrevTask(JournalpostId journalpostId, boolean innsynMedVedlegg, UUID behandlingUuId) {
        ProsessTaskData prosessTaskData = new ProsessTaskData(DistribuerBrevTask.TASKTYPE);
        prosessTaskData.setProperty(BrevTaskProperties.JOURNALPOST_ID, journalpostId.getVerdi());
        prosessTaskData.setProperty(BrevTaskProperties.BEHANDLING_UUID, (String.valueOf(behandlingUuId)));
        // må vente til vedlegg er knyttet og journalpost er ferdigstilt
        if (innsynMedVedlegg) {
            prosessTaskData.setNesteKjøringEtter(LocalDateTime.now().plusMinutes(1));
        }
        return prosessTaskData;
    }

    private ProsessTaskData opprettPubliserHistorikkTask(DokumentHistorikkinnslag historikkinnslag) {
        ProsessTaskData prosessTaskData = new ProsessTaskData(PubliserHistorikkTaskProperties.TASKTYPE);
        prosessTaskData.setProperty(PubliserHistorikkTaskProperties.HISTORIKK_ID, String.valueOf(historikkinnslag.getId()));
        return prosessTaskData;
    }

    private boolean erInnsynMedVedlegg(Behandling behandling, DokumentMalType dokumentMal) {
        if (!DokumentMalType.INNSYNSKRAV_SVAR.equals(dokumentMal) && !DokumentMalType.INNSYN_SVAR.equals(dokumentMal)) {
            return false;
        }
        return !domeneobjektProvider.hentInnsyn(behandling).getInnsynDokumenter().isEmpty();
    }

    private DokumentHistorikkinnslag lagHistorikkinnslag(DokumentHendelse dokumentHendelse,
            OpprettJournalpostResponse response,
            DokumentMalType dokumentMal) {
        DokumentHistorikkinnslag historikkinnslag = DokumentHistorikkinnslag.builder()
                .medBehandlingUuid(dokumentHendelse.getBehandlingUuid())
                .medHistorikkUuid(UUID.randomUUID())
                .medHendelseId(dokumentHendelse.getId())
                .medJournalpostId(new JournalpostId(response.getJournalpostId()))
                .medDokumentId(response.getDokumenter().get(0).getDokumentInfoId())
                .medHistorikkAktør(
                        dokumentHendelse.getHistorikkAktør() != null ? dokumentHendelse.getHistorikkAktør() : HistorikkAktør.VEDTAKSLØSNINGEN)
                .medDokumentMalType(dokumentMal)
                .medHistorikkinnslagType(HistorikkinnslagType.BREV_SENT)
                .build();

        historikkRepository.lagre(historikkinnslag);
        LOGGER.info("Opprettet historikkinnslag for bestilt brev: {}", historikkinnslag.toString());
        return historikkinnslag;
    }
}
