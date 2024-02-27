package no.nav.foreldrepenger.fpformidling.brevproduksjon.bestiller;

import static io.micrometer.core.instrument.Metrics.counter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

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

    public byte[] forhåndsvisBrev(DokumentHendelseEntitet dokumentHendelseEntitet, Behandling behandling) {
        var utkast = BestillingType.UTKAST;
        var dokumentMal = dokumentHendelseEntitet.getDokumentMal();
        var dokumentData = utledDokumentDataFor(behandling, dokumentMal, utkast);
        // hvis verge finnes produseres det 2 brev. Vi forhåndsviser kun en av dem.
        return genererDokument(dokumentHendelseEntitet, behandling, dokumentMal, dokumentData.getFørsteDokumentFelles(), utkast);
    }

    public void bestillBrev(DokumentHendelseEntitet dokumentHendelseEntitet,
                            Behandling behandling) {
        var bestillingType = BestillingType.BESTILL;
        var dokumentMal = dokumentHendelseEntitet.getDokumentMal();
        var journalførSom = dokumentHendelseEntitet.getJournalførSom();
        var dokumentData = utledDokumentDataFor(behandling, dokumentMal, bestillingType);
        var teller = 0;
        for (var dokumentFelles : dokumentData.getDokumentFelles()) {
            var brev = genererDokument(dokumentHendelseEntitet, behandling, dokumentMal, dokumentFelles, bestillingType);

            var unikBestillingsUuidPerDokFelles = dokumentHendelseEntitet.getBestillingUuid().toString();
            if (teller > 0) {
                unikBestillingsUuidPerDokFelles = dokumentHendelseEntitet.getBestillingUuid() + "-" + teller;
            }
            teller++;

            var innsynMedVedlegg = erInnsynMedVedlegg(behandling, dokumentMal);
            var response = opprettJournalpostTjeneste.journalførUtsendelse(brev, dokumentFelles, dokumentHendelseEntitet,
                behandling.getFagsakBackend().getSaksnummer(), !innsynMedVedlegg,
                behandling.getBehandlingsresultat() != null ? behandling.getBehandlingsresultat().getOverskrift() : null,
                unikBestillingsUuidPerDokFelles, behandling.getFagsakBackend().getYtelseType()) // NoSonar
                ;

            var journalpostId = new JournalpostId(response.journalpostId());

            LOG.info("Journalført {} for bestilling {}", journalpostId, unikBestillingsUuidPerDokFelles);

            if (innsynMedVedlegg) {
                leggTilVedleggOgFerdigstillForsendelse(dokumentHendelseEntitet.getBehandlingUuid(), journalpostId);
            }

            distribuerBrevOgLagHistorikk(dokumentHendelseEntitet, response, journalpostId, innsynMedVedlegg, dokumentFelles.getSaksnummer().getVerdi(),
                unikBestillingsUuidPerDokFelles);

            counter("brev_distribuert", "malType", dokumentMal.name(), "brevType", Optional.ofNullable(journalførSom).map(
                no.nav.foreldrepenger.fpformidling.typer.DokumentMalEnum::name).orElse(dokumentMal.name())).increment();
        }
    }

    private byte[] genererDokument(DokumentHendelseEntitet dokumentHendelseEntitet,
                                   Behandling behandling,
                                   DokumentMalType dokumentMal,
                                   DokumentFelles dokumentFelles,
                                   BestillingType bestillingType) {
        Objects.requireNonNull(bestillingType, "bestillingType");

        var dokumentdataMapper = dokumentdataMapperProvider.getDokumentdataMapper(dokumentMal);
        var dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelseEntitet, behandling,
            BestillingType.UTKAST.equals(bestillingType));

        byte[] brev;
        try {
            brev = dokgenKlient.genererPdf(dokumentdataMapper.getTemplateNavn(), behandling.getSpråkkode(), dokumentdata);
        } catch (Exception e) {
            dokumentdata.getFelles().anonymiser();
            SECURE_LOG.warn("Klarte ikke å generere brev av følgende brevdata: {}", DefaultJsonMapper.toJson(dokumentdata));
            throw new TekniskException("FPFORMIDLING-221006",
                String.format("Klarte ikke å generere mal %s for behandling %s for bestilling med type %s", dokumentMal,
                    behandling.getUuid(), bestillingType), e);
        }
        if (LOG.isInfoEnabled()) {
            LOG.info("Dokument av type {} i behandling id {} ble generert.", dokumentMal, behandling.getUuid());
        }
        return brev;
    }

    public String genererJson(DokumentHendelseEntitet dokumentHendelseEntitet, Behandling behandling, DokumentMalType dokumentMal, BestillingType bestillingType) {
        var dokumentData = utledDokumentDataFor(behandling, dokumentMal, bestillingType);
        var dokumentfelles = dokumentData.getFørsteDokumentFelles();
        var dokumentdataMapper = dokumentdataMapperProvider.getDokumentdataMapper(dokumentMal);
        var dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentfelles, dokumentHendelseEntitet, behandling,
            BestillingType.UTKAST == bestillingType);
        dokumentdata.getFelles().anonymiser();
        return DefaultJsonMapper.toJson(dokumentdata);
    }

    private DokumentData utledDokumentDataFor(Behandling behandling, DokumentMalType dokumentMal, BestillingType bestillingType) {
        var dokumentData = utledDokumentData(behandling, dokumentMal, bestillingType);
        dokumentFellesDataMapper.opprettDokumentDataForBehandling(behandling, dokumentData);
        return dokumentData;
    }

    private DokumentData utledDokumentData(Behandling behandling, DokumentMalType dokumentMal, BestillingType bestillingType) {
        return DokumentData.builder()
            .medDokumentMalType(dokumentMal)
            .medBehandlingUuid(behandling.getUuid())
            .medBestiltTid(LocalDateTime.now())
            .medBestillingType(bestillingType.name())
            .build();
    }

    private void distribuerBrevOgLagHistorikk(DokumentHendelseEntitet dokumentHendelseEntitet,
                                              OpprettJournalpostResponse response,
                                              JournalpostId journalpostId,
                                              boolean innsynMedVedlegg,
                                              String saksnummer,
                                              String unikBestillingsId) {
        var taskGruppe = new ProsessTaskGruppe();
        taskGruppe.addNesteSekvensiell(opprettDistribuerBrevTask(journalpostId, innsynMedVedlegg, dokumentHendelseEntitet.getBehandlingUuid(),
            DistribusjonstypeUtleder.utledFor(dokumentHendelseEntitet.getJournalførSom()), saksnummer, unikBestillingsId));

        taskGruppe.addNesteSekvensiell(
            opprettPubliserHistorikkTask(dokumentHendelseEntitet.getBehandlingUuid(),
                dokumentHendelseEntitet.getBestillingUuid(),
                response.journalpostId(),
                response.dokumenter().getFirst().dokumentInfoId()));
        taskGruppe.setCallIdFraEksisterende();
        taskTjeneste.lagre(taskGruppe);
    }

    private void leggTilVedleggOgFerdigstillForsendelse(UUID behandlingUid, JournalpostId journalpostId) {
        var taskGruppe = new ProsessTaskGruppe();
        taskGruppe.addNesteSekvensiell(opprettTilknyttVedleggTask(behandlingUid, journalpostId));
        taskGruppe.addNesteSekvensiell(opprettFerdigstillForsendelseTask(journalpostId));
        taskTjeneste.lagre(taskGruppe);
    }

    private ProsessTaskData opprettTilknyttVedleggTask(UUID behandlingUuId, JournalpostId journalpostId) {
        var prosessTaskData = ProsessTaskData.forProsessTask(TilknyttVedleggTask.class);
        prosessTaskData.setProperty(BrevTaskProperties.JOURNALPOST_ID, journalpostId.getVerdi());
        prosessTaskData.setProperty(BrevTaskProperties.BEHANDLING_UUID, (String.valueOf(behandlingUuId)));
        prosessTaskData.setCallIdFraEksisterende();
        return prosessTaskData;
    }

    private ProsessTaskData opprettFerdigstillForsendelseTask(JournalpostId journalpostId) {
        var prosessTaskData = ProsessTaskData.forProsessTask(FerdigstillForsendelseTask.class);
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
        var prosessTaskData = ProsessTaskData.forProsessTask(DistribuerBrevTask.class);
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

    private ProsessTaskData opprettPubliserHistorikkTask(UUID behandlingUuid, UUID bestillingUuid, String journalpostId, String dokumentId) {
        var prosessTaskData = ProsessTaskData.forProsessTask(SendKvitteringTask.class);
        prosessTaskData.setProperty(BrevTaskProperties.BEHANDLING_UUID, behandlingUuid.toString());
        prosessTaskData.setProperty(SendKvitteringTask.BESTILLING_UUID, bestillingUuid.toString());
        prosessTaskData.setProperty(SendKvitteringTask.JOURNALPOST_ID, journalpostId);
        prosessTaskData.setProperty(SendKvitteringTask.DOKUMENT_ID, dokumentId);
        prosessTaskData.setCallIdFraEksisterende();
        return prosessTaskData;
    }

    private boolean erInnsynMedVedlegg(Behandling behandling, no.nav.foreldrepenger.fpformidling.typer.DokumentMalEnum dokumentMal) {
        if (!no.nav.foreldrepenger.fpformidling.typer.DokumentMalEnum.INNSYN_SVAR.equals(dokumentMal)) {
            return false;
        }
        return !domeneobjektProvider.hentInnsyn(behandling).getInnsynDokumenter().isEmpty();
    }
}
