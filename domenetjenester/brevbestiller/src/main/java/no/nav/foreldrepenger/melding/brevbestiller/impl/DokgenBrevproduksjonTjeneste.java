package no.nav.foreldrepenger.melding.brevbestiller.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.felles.integrasjon.rest.DefaultJsonMapper;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.brevbestiller.task.BrevTaskProperties;
import no.nav.foreldrepenger.melding.brevbestiller.task.DistribuerBrevTask;
import no.nav.foreldrepenger.melding.brevbestiller.task.FerdigstillForsendelseTask;
import no.nav.foreldrepenger.melding.brevbestiller.task.TilknyttVedleggTask;
import no.nav.foreldrepenger.melding.brevmapper.DokumentdataMapper;
import no.nav.foreldrepenger.melding.brevmapper.DokumentdataMapperProvider;
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
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.journal.OpprettJournalpostTjeneste;
import no.nav.foreldrepenger.melding.integrasjon.journal.dto.OpprettJournalpostResponse;
import no.nav.foreldrepenger.melding.kafkatjenester.historikk.task.PubliserHistorikkTask;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.melding.typer.JournalpostId;
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskGruppe;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskTjeneste;

@ApplicationScoped
public class DokgenBrevproduksjonTjeneste {
    private static final Logger LOGGER = LoggerFactory.getLogger(DokgenBrevproduksjonTjeneste.class);
    private static final Logger SECURE_LOGGER = LoggerFactory.getLogger("secureLogger");

    private DokumentFellesDataMapper dokumentFellesDataMapper;
    private DomeneobjektProvider domeneobjektProvider;
    private DokumentRepository dokumentRepository;
    private Dokgen dokgenRestKlient;
    private OpprettJournalpostTjeneste opprettJournalpostTjeneste;
    private DokumentdataMapperProvider dokumentdataMapperProvider;
    private ProsessTaskTjeneste taskTjeneste;
    private HistorikkRepository historikkRepository;

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
            ProsessTaskTjeneste taskTjeneste,
            HistorikkRepository historikkRepository) {
        this.dokumentFellesDataMapper = dokumentFellesDataMapper;
        this.domeneobjektProvider = domeneobjektProvider;
        this.dokumentRepository = dokumentRepository;
        this.dokgenRestKlient = dokgenRestKlient;
        this.opprettJournalpostTjeneste = opprettJournalpostTjeneste;
        this.dokumentdataMapperProvider = dokumentdataMapperProvider;
        this.taskTjeneste = taskTjeneste;
        this.historikkRepository = historikkRepository;
    }

    public byte[] forhandsvisBrev(DokumentHendelse dokumentHendelse, Behandling behandling, DokumentMalType dokumentMal) {
        DokumentData dokumentData = lagDokumentData(behandling, dokumentMal, BestillingType.UTKAST);
        dokumentFellesDataMapper.opprettDokumentDataForBehandling(behandling, dokumentData, dokumentHendelse);
        dokumentRepository.lagre(dokumentData);
        DokumentFelles førsteDokumentFelles = dokumentData.getFørsteDokumentFelles();

        DokumentdataMapper dokumentdataMapper = dokumentdataMapperProvider.getDokumentdataMapper(dokumentMal);
        Dokumentdata dokumentdata = dokumentdataMapper.mapTilDokumentdata(førsteDokumentFelles, dokumentHendelse, behandling, true);
        førsteDokumentFelles.setBrevData(DefaultJsonMapper.toJson(dokumentdata));

        byte[] brev;
        try {
            brev = dokgenRestKlient.genererPdf(dokumentdataMapper.getTemplateNavn(), behandling.getSpråkkode(), dokumentdata);
        } catch (Exception e) {
            dokumentdata.getFelles().anonymiser();
            SECURE_LOGGER.warn("Klarte ikke å generere brev av følgende brevdata: {}", DefaultJsonMapper.toJson(dokumentdata));

            throw new TekniskException("FPFORMIDLING-221006",
                    String.format("Klarte ikke hente forhåndvise mal %s for behandling %s.", dokumentMal.getKode(), behandling.getUuid().toString()),
                    e);
        }
        LOGGER.info("Dokument av type {} i behandling id {} er forhåndsvist", dokumentMal.getKode(), behandling.getUuid().toString());
        return brev;
    }

    public void bestillBrev(DokumentHendelse dokumentHendelse, Behandling behandling, DokumentMalType dokumentMal) {
        DokumentData dokumentData = lagDokumentData(behandling, dokumentMal, BestillingType.BESTILL);
        dokumentFellesDataMapper.opprettDokumentDataForBehandling(behandling, dokumentData, dokumentHendelse);
        dokumentRepository.lagre(dokumentData);
        boolean innsynMedVedlegg = erInnsynMedVedlegg(behandling, dokumentMal);

        for (DokumentFelles dokumentFelles : dokumentData.getDokumentFelles()) {
            DokumentdataMapper dokumentdataMapper = dokumentdataMapperProvider.getDokumentdataMapper(dokumentMal);
            Dokumentdata dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);
            dokumentFelles.setBrevData(DefaultJsonMapper.toJson(dokumentdata));

            byte[] brev;
            try {
                brev = dokgenRestKlient.genererPdf(dokumentdataMapper.getTemplateNavn(), behandling.getSpråkkode(), dokumentdata);
            } catch (Exception e) {
                dokumentdata.getFelles().anonymiser();
                SECURE_LOGGER.warn("Klarte ikke å generere brev av følgende brevdata: {}", DefaultJsonMapper.toJson(dokumentdata));
                throw new TekniskException("FPFORMIDLING-221045",
                        String.format("Klarte ikke å produsere mal %s for behandling %s.", dokumentMal.getKode(), behandling.getUuid().toString()),
                        e);
            }
            OpprettJournalpostResponse response = opprettJournalpostTjeneste.journalførUtsendelse(brev, dokumentMal, dokumentFelles, dokumentHendelse,
                    behandling.getFagsakBackend().getSaksnummer(), !innsynMedVedlegg);
            JournalpostId journalpostId = new JournalpostId(response.getJournalpostId());

            if (innsynMedVedlegg) {
                leggTilVedleggOgFerdigstillForsendelse(dokumentHendelse.getBehandlingUuid(), journalpostId);
            }

            distribuerBrevOgLagHistorikk(dokumentHendelse, dokumentMal, response, journalpostId, innsynMedVedlegg);
        }
    }

    private DokumentData lagDokumentData(Behandling behandling, DokumentMalType dokumentMalType, BestillingType bestillingType) {
        return DokumentData.builder()
                .medDokumentMalType(dokumentMalType)
                .medBehandlingUuid(behandling.getUuid())
                .medBestiltTid(LocalDateTime.now())
                .medBestillingType(bestillingType.name())
                .build();
    }

    private void distribuerBrevOgLagHistorikk(DokumentHendelse dokumentHendelse, DokumentMalType dokumentMal, OpprettJournalpostResponse response,
            JournalpostId journalpostId, boolean innsynMedVedlegg) {
        ProsessTaskGruppe taskGruppe = new ProsessTaskGruppe();
        taskGruppe.addNesteSekvensiell(opprettDistribuerBrevTask(journalpostId, innsynMedVedlegg, dokumentHendelse.getBehandlingUuid()));
        DokumentHistorikkinnslag historikkinnslag = lagHistorikkinnslag(dokumentHendelse, response, dokumentMal);
        taskGruppe.addNesteSekvensiell(opprettPubliserHistorikkTask(historikkinnslag));
        taskTjeneste.lagre(taskGruppe);
    }

    private void leggTilVedleggOgFerdigstillForsendelse(UUID behandlingUid, JournalpostId journalpostId) {
        ProsessTaskGruppe taskGruppe = new ProsessTaskGruppe();
        taskGruppe.addNesteSekvensiell(opprettTilknyttVedleggTask(behandlingUid, journalpostId));
        taskGruppe.addNesteSekvensiell(opprettFerdigstillForsendelseTask(journalpostId));
        taskTjeneste.lagre(taskGruppe);
    }

    private ProsessTaskData opprettTilknyttVedleggTask(UUID behandlingUuId, JournalpostId journalpostId) {
        ProsessTaskData prosessTaskData = ProsessTaskData.forProsessTask(TilknyttVedleggTask.class);
        prosessTaskData.setProperty(BrevTaskProperties.JOURNALPOST_ID, journalpostId.getVerdi());
        prosessTaskData.setProperty(BrevTaskProperties.BEHANDLING_UUID, (String.valueOf(behandlingUuId)));
        return prosessTaskData;
    }

    private ProsessTaskData opprettFerdigstillForsendelseTask(JournalpostId journalpostId) {
        ProsessTaskData prosessTaskData = ProsessTaskData.forProsessTask(FerdigstillForsendelseTask.class);
        prosessTaskData.setProperty(BrevTaskProperties.JOURNALPOST_ID, journalpostId.getVerdi());
        return prosessTaskData;
    }

    private ProsessTaskData opprettDistribuerBrevTask(JournalpostId journalpostId, boolean innsynMedVedlegg, UUID behandlingUuId) {
        ProsessTaskData prosessTaskData = ProsessTaskData.forProsessTask(DistribuerBrevTask.class);
        prosessTaskData.setProperty(BrevTaskProperties.JOURNALPOST_ID, journalpostId.getVerdi());
        prosessTaskData.setProperty(BrevTaskProperties.BEHANDLING_UUID, (String.valueOf(behandlingUuId)));
        // må vente til vedlegg er knyttet og journalpost er ferdigstilt
        if (innsynMedVedlegg) {
            prosessTaskData.setNesteKjøringEtter(LocalDateTime.now().plusMinutes(1));
        }
        return prosessTaskData;
    }

    private ProsessTaskData opprettPubliserHistorikkTask(DokumentHistorikkinnslag historikkinnslag) {
        ProsessTaskData prosessTaskData = ProsessTaskData.forProsessTask(PubliserHistorikkTask.class);
        prosessTaskData.setProperty(PubliserHistorikkTask.HISTORIKK_ID, String.valueOf(historikkinnslag.getId()));
        return prosessTaskData;
    }

    private boolean erInnsynMedVedlegg(Behandling behandling, DokumentMalType dokumentMal) {
        if (!DokumentMalType.INNSYN_SVAR_DOK.equals(dokumentMal) && !DokumentMalType.INNSYN_SVAR.equals(dokumentMal)) {
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
