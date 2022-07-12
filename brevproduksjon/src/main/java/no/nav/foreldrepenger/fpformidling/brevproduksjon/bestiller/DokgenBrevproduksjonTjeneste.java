package no.nav.foreldrepenger.fpformidling.brevproduksjon.bestiller;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapperProvider;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.task.BrevTaskProperties;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.task.DistribuerBrevTask;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.task.FerdigstillForsendelseTask;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.task.TilknyttVedleggTask;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.historikk.SendKvitteringTask;
import no.nav.foreldrepenger.fpformidling.dokumentdata.BestillingType;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentData;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.dokumentdata.repository.DokumentRepository;
import no.nav.foreldrepenger.fpformidling.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.Distribusjonstype;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.Dokgen;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.http.JavaClient;
import no.nav.foreldrepenger.fpformidling.integrasjon.journal.OpprettJournalpostTjeneste;
import no.nav.foreldrepenger.fpformidling.integrasjon.journal.dto.OpprettJournalpostResponse;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.fpformidling.typer.JournalpostId;
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskGruppe;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskTjeneste;
import no.nav.vedtak.mapper.json.DefaultJsonMapper;

@ApplicationScoped
public class DokgenBrevproduksjonTjeneste {
    private static final Logger LOG = LoggerFactory.getLogger(DokgenBrevproduksjonTjeneste.class);
    private static final Logger SECURE_LOGGER = LoggerFactory.getLogger("secureLogger");

    private DokumentFellesDataMapper dokumentFellesDataMapper;
    private DomeneobjektProvider domeneobjektProvider;
    private DokumentRepository dokumentRepository;
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
                                        DokumentRepository dokumentRepository,
                                        @JavaClient Dokgen dokgenKlient,
                                        OpprettJournalpostTjeneste opprettJournalpostTjeneste,
                                        DokumentdataMapperProvider dokumentdataMapperProvider,
                                        ProsessTaskTjeneste taskTjeneste) {
        this.dokumentFellesDataMapper = dokumentFellesDataMapper;
        this.domeneobjektProvider = domeneobjektProvider;
        this.dokumentRepository = dokumentRepository;
        this.dokgenKlient = dokgenKlient;
        this.opprettJournalpostTjeneste = opprettJournalpostTjeneste;
        this.dokumentdataMapperProvider = dokumentdataMapperProvider;
        this.taskTjeneste = taskTjeneste;
    }

    public byte[] forhåndsvisBrev(DokumentHendelse dokumentHendelse, Behandling behandling, DokumentMalType dokumentMal) {
        var utkast = BestillingType.UTKAST;
        DokumentData dokumentData = lagreDokumentDataFor(behandling, dokumentMal, utkast);
        // hvis verge finnes produseres det 2 brev. Vi forhåndsviser kun en av dem.
        return genererDokument(dokumentHendelse, behandling, dokumentMal, dokumentData.getFørsteDokumentFelles(), utkast);
    }

    public void bestillBrev(DokumentHendelse dokumentHendelse, Behandling behandling, DokumentMalType dokumentMal) {
        var bestill = BestillingType.BESTILL;
        DokumentData dokumentData = lagreDokumentDataFor(behandling, dokumentMal, bestill);
        int teller = 0;
        for (DokumentFelles dokumentFelles : dokumentData.getDokumentFelles()) {
            byte[] brev = genererDokument(dokumentHendelse, behandling, dokumentMal, dokumentFelles, bestill);

            var unikBestillingsUuidPerDokFelles = dokumentHendelse.getBestillingUuid().toString();
            if( teller > 0) {
                unikBestillingsUuidPerDokFelles = dokumentHendelse.getBestillingUuid() + "-" + teller;
            }
            teller++;

            boolean innsynMedVedlegg = erInnsynMedVedlegg(behandling, dokumentMal);
            OpprettJournalpostResponse response = opprettJournalpostTjeneste.journalførUtsendelse(brev,
                    dokumentMal,
                    dokumentFelles,
                    dokumentHendelse,
                    behandling.getFagsakBackend().getSaksnummer(),
                    !innsynMedVedlegg,
                    behandling.getBehandlingsresultat() != null ? behandling.getBehandlingsresultat().getOverskrift() : null,
                    unikBestillingsUuidPerDokFelles) // NoSonar
            ;

            JournalpostId journalpostId = new JournalpostId(response.getJournalpostId());

            if (innsynMedVedlegg) {
                leggTilVedleggOgFerdigstillForsendelse(dokumentHendelse.getBehandlingUuid(), journalpostId);
            }

            distribuerBrevOgLagHistorikk(dokumentHendelse, dokumentMal, response, journalpostId, innsynMedVedlegg, dokumentFelles.getSaksnummer().getVerdi(), unikBestillingsUuidPerDokFelles);
        }
    }

    private byte[] genererDokument(DokumentHendelse dokumentHendelse,
                                   Behandling behandling,
                                   DokumentMalType dokumentMal,
                                   DokumentFelles dokumentFelles,
                                   BestillingType bestillingType) {

        DokumentdataMapper dokumentdataMapper = dokumentdataMapperProvider.getDokumentdataMapper(dokumentMal);
        Dokumentdata dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, BestillingType.UTKAST == bestillingType);
        dokumentFelles.setBrevData(DefaultJsonMapper.toJson(dokumentdata));

        byte[] brev;
        try {
            brev = dokgenKlient.genererPdf(dokumentdataMapper.getTemplateNavn(), behandling.getSpråkkode(), dokumentdata);
        } catch (Exception e) {
            dokumentdata.getFelles().anonymiser();
            SECURE_LOGGER.warn("Klarte ikke å generere brev av følgende brevdata: {}", DefaultJsonMapper.toJson(dokumentdata));
            throw new TekniskException("FPFORMIDLING-221006", String.format("Klarte ikke å generere mal %s for behandling %s for bestilling med type %s",
                    dokumentMal.getKode(), behandling.getUuid(), bestillingType), e);
        }
        if (LOG.isInfoEnabled()) {
            LOG.info("Dokument av type {} i behandling id {} er generert.", dokumentMal.getKode(), behandling.getUuid());
        }
        return brev;
    }

    private DokumentData lagreDokumentDataFor(Behandling behandling, DokumentMalType dokumentMal, BestillingType bestillingType) {
        DokumentData dokumentData = lagDokumentData(behandling, dokumentMal, bestillingType);
        dokumentFellesDataMapper.opprettDokumentDataForBehandling(behandling, dokumentData);
        dokumentRepository.lagre(dokumentData);
        return dokumentData;
    }

    private DokumentData lagDokumentData(Behandling behandling, DokumentMalType dokumentMalType, BestillingType bestillingType) {
        return DokumentData.builder()
                .medDokumentMalType(dokumentMalType)
                .medBehandlingUuid(behandling.getUuid())
                .medBestiltTid(LocalDateTime.now())
                .medBestillingType(bestillingType.name())
                .build();
    }

    private void distribuerBrevOgLagHistorikk(DokumentHendelse dokumentHendelse, DokumentMalType dokumentMal, OpprettJournalpostResponse response, JournalpostId journalpostId, boolean innsynMedVedlegg, String saksnummer, String unikBestillingsId) {
        ProsessTaskGruppe taskGruppe = new ProsessTaskGruppe();
        taskGruppe.addNesteSekvensiell(
                opprettDistribuerBrevTask(journalpostId,
                        innsynMedVedlegg,
                        dokumentHendelse.getBehandlingUuid(),
                        DistribusjonstypeUtleder.utledFor(dokumentMal),
                        saksnummer,
                        unikBestillingsId));

        taskGruppe.addNesteSekvensiell(
                opprettPubliserHistorikkTask(dokumentHendelse.getBehandlingUuid(),
                        dokumentHendelse.getBestillingUuid(),
                        dokumentMal,
                        response.getJournalpostId(),
                        response.getDokumenter().get(0).getDokumentInfoId()));
        taskGruppe.setCallIdFraEksisterende();
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
        prosessTaskData.setCallIdFraEksisterende();
        return prosessTaskData;
    }

    private ProsessTaskData opprettFerdigstillForsendelseTask(JournalpostId journalpostId) {
        ProsessTaskData prosessTaskData = ProsessTaskData.forProsessTask(FerdigstillForsendelseTask.class);
        prosessTaskData.setProperty(BrevTaskProperties.JOURNALPOST_ID, journalpostId.getVerdi());
        prosessTaskData.setCallIdFraEksisterende();
        return prosessTaskData;
    }

    private ProsessTaskData opprettDistribuerBrevTask(JournalpostId journalpostId,
                                                      boolean innsynMedVedlegg,
                                                      UUID behandlingUuId,
                                                      Distribusjonstype distribusjonstype,
                                                      String saksnummer,
                                                      String unikBestillingsId) {
        ProsessTaskData prosessTaskData = ProsessTaskData.forProsessTask(DistribuerBrevTask.class);
        prosessTaskData.setProperty(BrevTaskProperties.JOURNALPOST_ID, journalpostId.getVerdi());
        prosessTaskData.setProperty(BrevTaskProperties.BESTILLING_ID, unikBestillingsId);
        prosessTaskData.setProperty(BrevTaskProperties.DISTRIBUSJONSTYPE, distribusjonstype.name());
        // For logging context
        prosessTaskData.setProperty(BrevTaskProperties.BEHANDLING_UUID, String.valueOf(behandlingUuId));
        prosessTaskData.setProperty(BrevTaskProperties.SAKSNUMMER, saksnummer);
        // må vente til vedlegg er knyttet og journalpost er ferdigstilt
        if (innsynMedVedlegg) {
            prosessTaskData.setNesteKjøringEtter(LocalDateTime.now().plusMinutes(1));
        }
        prosessTaskData.setCallIdFraEksisterende();
        return prosessTaskData;
    }

    private ProsessTaskData opprettPubliserHistorikkTask(UUID behandlingUuid,
                                                         UUID bestillingUuid,
                                                         DokumentMalType dokumentMal,
                                                         String journalpostId,
                                                         String dokumentId) {
        ProsessTaskData prosessTaskData = ProsessTaskData.forProsessTask(SendKvitteringTask.class);
        prosessTaskData.setProperty(BrevTaskProperties.BEHANDLING_UUID, behandlingUuid.toString());
        prosessTaskData.setProperty(SendKvitteringTask.BESTILLING_UUID, bestillingUuid.toString());
        prosessTaskData.setProperty(SendKvitteringTask.DOKUMENT_MAL_TYPE, dokumentMal.getKode());
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
