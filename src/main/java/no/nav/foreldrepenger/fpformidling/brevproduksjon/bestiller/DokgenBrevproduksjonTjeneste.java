package no.nav.foreldrepenger.fpformidling.brevproduksjon.bestiller;

import static io.micrometer.core.instrument.Metrics.counter;

import java.time.LocalDateTime;
import java.util.UUID;

import no.nav.foreldrepenger.fpformidling.typer.Saksnummer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapperProvider;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.task.BrevTaskProperties;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.task.DistribuerBrevTask;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.task.FerdigstillForsendelseTask;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.task.TilknyttVedleggTask;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.historikk.SendKvitteringTask;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.BestillingType;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentData;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.Distribusjonstype;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.Dokgen;
import no.nav.foreldrepenger.fpformidling.integrasjon.journal.OpprettJournalpostTjeneste;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.fpformidling.typer.JournalpostId;
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.felles.integrasjon.dokarkiv.dto.OpprettJournalpostResponse;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskGruppe;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskTjeneste;
import no.nav.vedtak.mapper.json.DefaultJsonMapper;

@ApplicationScoped
public class DokgenBrevproduksjonTjeneste {
    private static final Logger LOG = LoggerFactory.getLogger(DokgenBrevproduksjonTjeneste.class);
    private static final Logger SECURE_LOG = LoggerFactory.getLogger("secureLogger");

    private DokumentFellesDataMapper dokumentFellesDataMapper;
    private DomeneobjektProvider domeneobjektProvider;
    private Dokgen dokgenKlient;
    private OpprettJournalpostTjeneste opprettJournalpostTjeneste;
    private DokumentdataMapperProvider dokumentdataMapperProvider;
    private ProsessTaskTjeneste taskTjeneste;

    DokgenBrevproduksjonTjeneste() {
        // CDI
    }

    @Inject
    public DokgenBrevproduksjonTjeneste(DokumentFellesDataMapper dokumentFellesDataMapper,
                                        DomeneobjektProvider domeneobjektProvider,
                                        Dokgen dokgenKlient,
                                        OpprettJournalpostTjeneste opprettJournalpostTjeneste,
                                        DokumentdataMapperProvider dokumentdataMapperProvider,
                                        ProsessTaskTjeneste taskTjeneste) {
        this.dokumentFellesDataMapper = dokumentFellesDataMapper;
        this.domeneobjektProvider = domeneobjektProvider;
        this.dokgenKlient = dokgenKlient;
        this.opprettJournalpostTjeneste = opprettJournalpostTjeneste;
        this.dokumentdataMapperProvider = dokumentdataMapperProvider;
        this.taskTjeneste = taskTjeneste;
    }

    public byte[] forhåndsvisBrev(DokumentHendelse dokumentHendelse, Behandling behandling) {
        var utkast = BestillingType.UTKAST;
        var dokumentMalType = DokumentMalType.valueOf(dokumentHendelse.getDokumentMal().name());
        var dokumentData = utledDokumentDataFor(behandling, dokumentMalType, utkast);
        // hvis verge finnes produseres det 2 brev. Vi forhåndsviser kun en av dem.
        return genererDokument(dokumentHendelse, behandling, dokumentMalType, dokumentData.getFørsteDokumentFelles(), utkast);
    }

    public void bestillBrev(DokumentHendelse dokumentHendelse,
                            Behandling behandling,
                            DokumentMalType journalførSom) {

        var bestillingType = BestillingType.BESTILL;
        var dokumentMal = DokumentMalType.valueOf(dokumentHendelse.getDokumentMal().name());
        var dokumentData = utledDokumentDataFor(behandling, dokumentMal, bestillingType);
        var teller = 0;
        for (var dokumentFelles : dokumentData.getDokumentFelles()) {
            var brev = genererDokument(dokumentHendelse, behandling, dokumentMal, dokumentFelles, bestillingType);

            var unikBestillingsUuidPerDokFelles = dokumentHendelse.getBestillingUuid().toString();
            if (teller > 0) {
                unikBestillingsUuidPerDokFelles = dokumentHendelse.getBestillingUuid() + "-" + teller;
            }
            teller++;

            var innsynMedVedlegg = erInnsynMedVedlegg(behandling, dokumentMal);
            var response = opprettJournalpostTjeneste.journalførUtsendelse(brev, dokumentMal, dokumentFelles, dokumentHendelse,
                behandling.getFagsakBackend().getSaksnummer(), !innsynMedVedlegg, unikBestillingsUuidPerDokFelles, journalførSom, behandling.getFagsakBackend().getYtelseType()) // NoSonar
                ;

            var journalpostId = new JournalpostId(response.journalpostId());

            LOG.info("Journalført {} for bestilling {}", journalpostId, unikBestillingsUuidPerDokFelles);

            if (innsynMedVedlegg) {
                leggTilVedleggOgFerdigstillForsendelse(dokumentHendelse.getBehandlingUuid(), journalpostId, dokumentFelles.getSaksnummer());
            }

            distribuerBrevOgLagHistorikk(dokumentHendelse, response, journalpostId, innsynMedVedlegg, dokumentFelles.getSaksnummer(),
                unikBestillingsUuidPerDokFelles, journalførSom);

            counter("brev_distribuert", "malType", dokumentMal.getKode(), "brevType", journalførSom.getKode()).increment();
        }
    }

    private byte[] genererDokument(DokumentHendelse dokumentHendelse,
                                   Behandling behandling,
                                   DokumentMalType dokumentMalType,
                                   DokumentFelles dokumentFelles,
                                   BestillingType bestillingType) {

        var dokumentdataMapper = dokumentdataMapperProvider.getDokumentdataMapper(dokumentMalType);
        var dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling,
            BestillingType.UTKAST == bestillingType);

        byte[] brev;
        try {
            brev = dokgenKlient.genererPdf(dokumentdataMapper.getTemplateNavn(), behandling.getSpråkkode(), dokumentdata);
        } catch (Exception e) {
            dokumentdata.getFelles().anonymiser();
            SECURE_LOG.warn("Klarte ikke å generere brev av følgende brevdata: {}", DefaultJsonMapper.toJson(dokumentdata));
            throw new TekniskException("FPFORMIDLING-221006",
                String.format("Klarte ikke å generere mal %s for behandling %s for bestilling med type %s", dokumentMalType.getKode(),
                    behandling.getUuid(), bestillingType), e);
        }
        if (LOG.isInfoEnabled()) {
            LOG.info("Dokument av type {} i behandling id {} ble generert.", dokumentMalType.getKode(), behandling.getUuid());
        }
        return brev;
    }

    public String genererJson(DokumentHendelse dokumentHendelse, Behandling behandling, BestillingType bestillingType) {
        var dokumentMal = DokumentMalType.valueOf(dokumentHendelse.getDokumentMal().name());
        var dokumentdataMapper = dokumentdataMapperProvider.getDokumentdataMapper(dokumentMal);
        var dokumentData = utledDokumentDataFor(behandling, dokumentMal, bestillingType);
        var dokumentfelles = dokumentData.getFørsteDokumentFelles();
        var dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentfelles, dokumentHendelse, behandling,
            BestillingType.UTKAST == bestillingType);
        dokumentdata.getFelles().anonymiser();
        return DefaultJsonMapper.toJson(dokumentdata);
    }

    private DokumentData utledDokumentDataFor(Behandling behandling, DokumentMalType dokumentMal, BestillingType bestillingType) {
        var dokumentData = utledDokumentData(behandling, dokumentMal, bestillingType);
        dokumentFellesDataMapper.opprettDokumentDataForBehandling(behandling, dokumentData);
        return dokumentData;
    }

    private DokumentData utledDokumentData(Behandling behandling, DokumentMalType dokumentMalType, BestillingType bestillingType) {
        return DokumentData.builder()
            .medDokumentMalType(dokumentMalType)
            .medBehandlingUuid(behandling.getUuid())
            .medBestiltTid(LocalDateTime.now())
            .medBestillingType(bestillingType.name())
            .build();
    }

    private void distribuerBrevOgLagHistorikk(DokumentHendelse dokumentHendelse,
                                              OpprettJournalpostResponse response,
                                              JournalpostId journalpostId,
                                              boolean innsynMedVedlegg,
                                              Saksnummer saksnummer,
                                              String unikBestillingsId,
                                              DokumentMalType originalDokumentType) {
        var taskGruppe = new ProsessTaskGruppe();
        taskGruppe.addNesteSekvensiell(opprettDistribuerBrevTask(journalpostId, innsynMedVedlegg, dokumentHendelse.getBehandlingUuid(),
            DistribusjonstypeUtleder.utledFor(originalDokumentType), saksnummer, unikBestillingsId));

        taskGruppe.addNesteSekvensiell(
            opprettPubliserHistorikkTask(dokumentHendelse.getBehandlingUuid(), saksnummer,
                dokumentHendelse.getBestillingUuid(),
                response.journalpostId(),
                response.dokumenter().getFirst().dokumentInfoId()));
        taskGruppe.setCallIdFraEksisterende();
        taskTjeneste.lagre(taskGruppe);
    }

    private void leggTilVedleggOgFerdigstillForsendelse(UUID behandlingUid, JournalpostId journalpostId, Saksnummer saksnummer) {
        var taskGruppe = new ProsessTaskGruppe();
        taskGruppe.addNesteSekvensiell(opprettTilknyttVedleggTask(behandlingUid, journalpostId, saksnummer));
        taskGruppe.addNesteSekvensiell(opprettFerdigstillForsendelseTask(behandlingUid, journalpostId, saksnummer));
        taskTjeneste.lagre(taskGruppe);
    }

    private ProsessTaskData opprettTilknyttVedleggTask(UUID behandlingUuId, JournalpostId journalpostId, Saksnummer saksnummer) {
        var prosessTaskData = ProsessTaskData.forProsessTask(TilknyttVedleggTask.class);
        prosessTaskData.setSaksnummer(saksnummer.getVerdi());
        prosessTaskData.setProperty(BrevTaskProperties.JOURNALPOST_ID, journalpostId.getVerdi());
        prosessTaskData.setProperty(BrevTaskProperties.BEHANDLING_UUID, (String.valueOf(behandlingUuId)));
        prosessTaskData.setCallIdFraEksisterende();
        return prosessTaskData;
    }

    private ProsessTaskData opprettFerdigstillForsendelseTask(UUID behandlingUuId, JournalpostId journalpostId, Saksnummer saksnummer) {
        var prosessTaskData = ProsessTaskData.forProsessTask(FerdigstillForsendelseTask.class);
        prosessTaskData.setSaksnummer(saksnummer.getVerdi());
        prosessTaskData.setProperty(BrevTaskProperties.JOURNALPOST_ID, journalpostId.getVerdi());
        prosessTaskData.setProperty(BrevTaskProperties.BEHANDLING_UUID, (String.valueOf(behandlingUuId)));
        prosessTaskData.setCallIdFraEksisterende();
        return prosessTaskData;
    }

    private ProsessTaskData opprettDistribuerBrevTask(JournalpostId journalpostId,
                                                      boolean innsynMedVedlegg,
                                                      UUID behandlingUuId,
                                                      Distribusjonstype distribusjonstype,
                                                      Saksnummer saksnummer,
                                                      String unikBestillingsId) {
        var prosessTaskData = ProsessTaskData.forProsessTask(DistribuerBrevTask.class);
        prosessTaskData.setSaksnummer(saksnummer.getVerdi());
        prosessTaskData.setProperty(BrevTaskProperties.JOURNALPOST_ID, journalpostId.getVerdi());
        prosessTaskData.setProperty(BrevTaskProperties.BESTILLING_ID, unikBestillingsId);
        prosessTaskData.setProperty(BrevTaskProperties.DISTRIBUSJONSTYPE, distribusjonstype.name());
        // For logging context
        prosessTaskData.setProperty(BrevTaskProperties.BEHANDLING_UUID, String.valueOf(behandlingUuId));
        // må vente til vedlegg er knyttet og journalpost er ferdigstilt
        if (innsynMedVedlegg) {
            prosessTaskData.setNesteKjøringEtter(LocalDateTime.now().plusMinutes(1));
        }
        prosessTaskData.setCallIdFraEksisterende();
        return prosessTaskData;
    }

    private ProsessTaskData opprettPubliserHistorikkTask(UUID behandlingUuid, Saksnummer saksnummer, UUID bestillingUuid, String journalpostId, String dokumentId) {
        var prosessTaskData = ProsessTaskData.forProsessTask(SendKvitteringTask.class);
        prosessTaskData.setSaksnummer(saksnummer.getVerdi());
        prosessTaskData.setProperty(BrevTaskProperties.BEHANDLING_UUID, behandlingUuid.toString());
        prosessTaskData.setProperty(SendKvitteringTask.BESTILLING_UUID, bestillingUuid.toString());
        prosessTaskData.setProperty(SendKvitteringTask.JOURNALPOST_ID, journalpostId);
        prosessTaskData.setProperty(SendKvitteringTask.DOKUMENT_ID, dokumentId);
        prosessTaskData.setCallIdFraEksisterende();
        return prosessTaskData;
    }

    private boolean erInnsynMedVedlegg(Behandling behandling, DokumentMalType dokumentMal) {
        if (!DokumentMalType.INNSYN_SVAR.equals(dokumentMal)) {
            return false;
        }
        return !domeneobjektProvider.hentInnsyn(behandling).getInnsynDokumenter().isEmpty();
    }
}
