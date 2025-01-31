package no.nav.foreldrepenger.fpformidling.brevproduksjon.bestiller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.task.BrevTaskProperties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.EngangsstønadInnvilgelseDokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapperProvider;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.task.DistribuerBrevTask;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.historikk.SendKvitteringTask;
import no.nav.foreldrepenger.fpformidling.domene.aktør.Personinfo;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.behandling.BehandlingResourceLink;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakBackend;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.domene.personopplysning.NavBrukerKjønn;
import no.nav.foreldrepenger.fpformidling.domene.tilkjentytelse.TilkjentYtelseEngangsstønad;
import no.nav.foreldrepenger.fpformidling.domene.verge.Verge;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.Dokgen;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.journal.OpprettJournalpostTjeneste;
import no.nav.foreldrepenger.fpformidling.integrasjon.organisasjon.VirksomhetTjeneste;
import no.nav.foreldrepenger.fpformidling.integrasjon.pdl.PersonAdapter;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.fpformidling.typer.AktørId;
import no.nav.foreldrepenger.fpformidling.typer.DokumentMal;
import no.nav.foreldrepenger.fpformidling.typer.JournalpostId;
import no.nav.foreldrepenger.fpformidling.typer.PersonIdent;
import no.nav.foreldrepenger.fpformidling.typer.Saksnummer;
import no.nav.vedtak.felles.integrasjon.dokarkiv.dto.OpprettJournalpostResponse;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskGruppe;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskTjeneste;
import no.nav.vedtak.felles.prosesstask.api.TaskType;

@ExtendWith(MockitoExtension.class)
class BrevBestillerTjenesteTest {

    private static final UUID BEHANDLING_UUID = UUID.randomUUID();
    private static final String NAVN = "Nav Navesen";
    private static final PersonIdent SØKER_FNR = new PersonIdent("11111111111");
    private static final AktørId SØKER = new AktørId("2222222222222");
    private static final PersonIdent VERGE_FNR = new PersonIdent("77777777777");
    private static final AktørId VERGE = new AktørId("8888888888888");
    private static final Saksnummer SAKSNUMMER = new Saksnummer("123456");
    private static final byte[] BREVET = "BREV".getBytes();
    private static final JournalpostId JOURNALPOST = new JournalpostId("7654321");
    private static final DokumentMalType DOKUMENT_MAL_TYPE = DokumentMalType.ENGANGSSTØNAD_INNVILGELSE;
    private static final long HENDELSE_ID = 1L;
    private static final String DOKUMENT_INFO_ID = "987";

    private static final TaskType HISTORIKK_TASK = TaskType.forProsessTask(SendKvitteringTask.class);
    private static final TaskType DIST_TASK = TaskType.forProsessTask(DistribuerBrevTask.class);

    @Mock
    private PersonAdapter personAdapter;
    @Mock
    private VirksomhetTjeneste virksomhetTjeneste;
    @Mock
    private DomeneobjektProvider domeneobjektProvider;
    @Mock
    private Dokgen dokgenRestKlient;
    @Mock
    private OpprettJournalpostTjeneste opprettJournalpostTjeneste;
    @Mock
    private DokumentdataMapperProvider dokumentdataMapperProvider;
    @Mock
    private ProsessTaskTjeneste taskTjeneste;

    private EngangsstønadInnvilgelseDokumentdataMapper dokumentdataMapper;
    private BrevBestillerTjeneste tjeneste;


    @BeforeEach
    void beforeEach() {
        dokumentdataMapper = new EngangsstønadInnvilgelseDokumentdataMapper(new BrevParametere(6, 3, Period.ofWeeks(3), Period.ofWeeks(4)),
            domeneobjektProvider);
        var dokumentFellesDataMapper = new DokumentFellesDataMapper(personAdapter, domeneobjektProvider, virksomhetTjeneste);
        var dokgenBrevproduksjonTjeneste = new DokgenBrevproduksjonTjeneste(dokumentFellesDataMapper, domeneobjektProvider,
            dokgenRestKlient, opprettJournalpostTjeneste, dokumentdataMapperProvider, taskTjeneste);
        tjeneste = new BrevBestillerTjeneste(domeneobjektProvider, dokgenBrevproduksjonTjeneste);
    }

    @Test
    void skal_generere_og_sende_brev_til_både_søker_og_verge() {
        // Arrange
        var randomBestillingsUuid = UUID.randomUUID();
        var personinfo = mockPdl(true);
        var behandling = mockDomeneobjektProvider(personinfo, true);
        var dokumentHendelse = opprettDokumentHendelse(randomBestillingsUuid, DokumentMal.ENGANGSSTØNAD_INNVILGELSE, null);
        when(dokgenRestKlient.genererPdf(anyString(), any(Språkkode.class), any(Dokumentdata.class))).thenReturn(BREVET);
        mockJournal(dokumentHendelse);
        when(dokumentdataMapperProvider.getDokumentdataMapper(DOKUMENT_MAL_TYPE)).thenReturn(dokumentdataMapper);
        var verge = new Verge(VERGE.getId(), "", "", LocalDate.now().minusDays(1), LocalDate.now().plusMonths(1));
        when(domeneobjektProvider.hentVerge(behandling)).thenReturn(Optional.of(verge));

        var taskCaptor = ArgumentCaptor.forClass(ProsessTaskGruppe.class);

        // Act
        tjeneste.bestillBrev(dokumentHendelse);

        // Assert
        verify(dokgenRestKlient, times(2)).genererPdf(anyString(), any(Språkkode.class), any(Dokumentdata.class));
        verify(opprettJournalpostTjeneste, times(2)).journalførUtsendelse(eq(BREVET), eq(DOKUMENT_MAL_TYPE), any(DokumentFelles.class),
            eq(dokumentHendelse), eq(SAKSNUMMER), eq(true), any(), eq(DOKUMENT_MAL_TYPE), eq(FagsakYtelseType.FORELDREPENGER));
        verify(taskTjeneste, times(2)).lagre(taskCaptor.capture());

        assertThat(taskCaptor.getValue().getTasks()).hasSize(2);
        assertThat(taskCaptor.getValue().getTasks().get(0).task().taskType()).isEqualTo(DIST_TASK);
        assertThat(taskCaptor.getValue().getTasks().get(1).task().taskType()).isEqualTo(HISTORIKK_TASK);
        assertThat(taskCaptor.getAllValues().get(1).getTasks().get(0).task().getPropertyValue(BrevTaskProperties.BESTILLING_ID)).isEqualTo(
            randomBestillingsUuid + "-" + 1);
    }

    @Test
    void skal_ikke_generere_brev_til_verge_om_vergen_er_ugyldig() {
        // Arrange
        var randomBestillingsUuid = UUID.randomUUID();
        var personinfo = mockPdl(true);
        var behandling = mockDomeneobjektProvider(personinfo, true);
        var dokumentHendelse = opprettDokumentHendelse(randomBestillingsUuid, DokumentMal.ENGANGSSTØNAD_INNVILGELSE, null);
        when(dokgenRestKlient.genererPdf(anyString(), any(Språkkode.class), any(Dokumentdata.class))).thenReturn(BREVET);
        mockJournal(dokumentHendelse);
        when(dokumentdataMapperProvider.getDokumentdataMapper(DOKUMENT_MAL_TYPE)).thenReturn(dokumentdataMapper);
        var verge = new Verge(VERGE.getId(), "", "", LocalDate.now().minusMonths(1), LocalDate.now().minusDays(1));
        when(domeneobjektProvider.hentVerge(behandling)).thenReturn(Optional.of(verge));
        var taskCaptor = ArgumentCaptor.forClass(ProsessTaskGruppe.class);

        // Act
        tjeneste.bestillBrev(dokumentHendelse);

        // Assert
        verify(dokgenRestKlient, times(1)).genererPdf(anyString(), any(Språkkode.class), any(Dokumentdata.class));
        verify(opprettJournalpostTjeneste, times(1)).journalførUtsendelse(eq(BREVET), eq(DOKUMENT_MAL_TYPE), any(DokumentFelles.class),
            eq(dokumentHendelse), eq(SAKSNUMMER), eq(true), any(), eq(DOKUMENT_MAL_TYPE), eq(FagsakYtelseType.FORELDREPENGER));
        verify(taskTjeneste, times(1)).lagre(taskCaptor.capture());

        assertThat(taskCaptor.getValue().getTasks()).hasSize(2);
        assertThat(taskCaptor.getValue().getTasks().get(0).task().taskType()).isEqualTo(DIST_TASK);
        assertThat(taskCaptor.getValue().getTasks().get(1).task().taskType()).isEqualTo(HISTORIKK_TASK);
        assertThat(taskCaptor.getAllValues().get(0).getTasks().get(0).task().getPropertyValue(BrevTaskProperties.BESTILLING_ID)).isEqualTo(String.valueOf(randomBestillingsUuid));
    }

    @Test
    void skal_utlede_riktig_dokument_type_hvis_fritekst_er_valgt() {
        // Arrange
        var dokumentHendelse = opprettDokumentHendelse(UUID.randomUUID(), DokumentMal.FRITEKSTBREV, DokumentMal.FORELDREPENGER_INNVILGELSE);
        var behandling = mock(Behandling.class);
        when(domeneobjektProvider.hentBehandling(any())).thenReturn(behandling);

        var dokgenBrevproduksjonTjeneste = mock(DokgenBrevproduksjonTjeneste.class);

        tjeneste = new BrevBestillerTjeneste(domeneobjektProvider, dokgenBrevproduksjonTjeneste);

        // Act
        tjeneste.bestillBrev(dokumentHendelse);

        // Assert
        verify(dokgenBrevproduksjonTjeneste).bestillBrev(dokumentHendelse, behandling, DokumentMalType.FORELDREPENGER_INNVILGELSE);
        verify(domeneobjektProvider).hentFagsakBackend(behandling);

        verifyNoMoreInteractions(dokgenBrevproduksjonTjeneste, domeneobjektProvider);
    }

    @Test
    void skal_ikke_utlede_riktig_dokument_type_om_ikke_fritekst() {
        // Arrange
        var dokumentHendelse = opprettDokumentHendelse(UUID.randomUUID(), DokumentMal.FORELDREPENGER_INNVILGELSE, null);
        var behandling = mock(Behandling.class);
        when(domeneobjektProvider.hentBehandling(any())).thenReturn(behandling);

        var dokgenBrevproduksjonTjeneste = mock(DokgenBrevproduksjonTjeneste.class);

        tjeneste = new BrevBestillerTjeneste(domeneobjektProvider, dokgenBrevproduksjonTjeneste);

        // Act
        tjeneste.bestillBrev(dokumentHendelse);

        // Assert
        verify(dokgenBrevproduksjonTjeneste).bestillBrev(dokumentHendelse, behandling, DokumentMalType.FORELDREPENGER_INNVILGELSE);
        verify(domeneobjektProvider).hentFagsakBackend(behandling);

        verifyNoMoreInteractions(dokgenBrevproduksjonTjeneste, domeneobjektProvider);
    }

    @Test
    void skal_ikke_utlede_riktig_dokument_type_ved_forhåndsvisning() {
        // Arrange
        var dokumentHendelse = opprettDokumentHendelse(UUID.randomUUID(), DokumentMal.FORELDREPENGER_ANNULLERT, null);
        var behandling = mock(Behandling.class);
        when(domeneobjektProvider.hentBehandling(any())).thenReturn(behandling);

        var dokgenBrevproduksjonTjeneste = mock(DokgenBrevproduksjonTjeneste.class);

        tjeneste = new BrevBestillerTjeneste(domeneobjektProvider, dokgenBrevproduksjonTjeneste);

        // Act
        tjeneste.forhandsvisBrev(dokumentHendelse);

        // Assert
        verify(dokgenBrevproduksjonTjeneste).forhåndsvisBrev(dokumentHendelse, behandling);
        verify(domeneobjektProvider).hentFagsakBackend(behandling);

        verifyNoMoreInteractions(dokgenBrevproduksjonTjeneste, domeneobjektProvider);
    }

    @Test
    void skal_ikke_sende_til_verge_når_verge_ikke_er_definert() {
        // Arrange
        var randomBestillingsUuid = UUID.randomUUID();
        var personinfo = mockPdl(false);
        mockDomeneobjektProvider(personinfo, false);
        var dokumentHendelse = opprettDokumentHendelse(randomBestillingsUuid, DokumentMal.ENGANGSSTØNAD_INNVILGELSE, null);
        when(dokgenRestKlient.genererPdf(anyString(), any(Språkkode.class), any(Dokumentdata.class))).thenReturn(BREVET);
        mockJournal(dokumentHendelse);
        when(dokumentdataMapperProvider.getDokumentdataMapper(DOKUMENT_MAL_TYPE)).thenReturn(dokumentdataMapper);
        var taskCaptor = ArgumentCaptor.forClass(ProsessTaskGruppe.class);

        // Act
        tjeneste.bestillBrev(dokumentHendelse);

        // Assert
        verify(dokgenRestKlient, times(1)).genererPdf(anyString(), any(Språkkode.class), any(Dokumentdata.class));
        verify(opprettJournalpostTjeneste, times(1)).journalførUtsendelse(eq(BREVET), eq(DOKUMENT_MAL_TYPE), any(DokumentFelles.class),
            eq(dokumentHendelse), eq(SAKSNUMMER), eq(true), any(), eq(DOKUMENT_MAL_TYPE), eq(FagsakYtelseType.FORELDREPENGER));
        verify(taskTjeneste, times(1)).lagre(taskCaptor.capture());
        assertThat(taskCaptor.getValue().getTasks()).hasSize(2);
        assertThat(taskCaptor.getValue().getTasks().get(0).task().taskType()).isEqualTo(DIST_TASK);
        assertThat(taskCaptor.getValue().getTasks().get(1).task().taskType()).isEqualTo(HISTORIKK_TASK);
        assertThat(taskCaptor.getAllValues().get(0).getTasks().get(0).task().getPropertyValue(BrevTaskProperties.BESTILLING_ID)).isEqualTo(
            String.valueOf(randomBestillingsUuid));
    }

    private Personinfo mockPdl(boolean harVerge) {
        var personinfoSøker = Personinfo.getbuilder(SØKER).medPersonIdent(SØKER_FNR).medNavn(NAVN).medNavBrukerKjønn(NavBrukerKjønn.MANN).build();
        lenient().when(personAdapter.hentBrukerForAktør(any(), eq(SØKER))).thenReturn(Optional.of(personinfoSøker));

        if (harVerge) {
            var personinfoVerge = Personinfo.getbuilder(VERGE)
                .medPersonIdent(VERGE_FNR)
                .medNavn("Verge Vergesen")
                .medNavBrukerKjønn(NavBrukerKjønn.KVINNE)
                .build();
            lenient().when(personAdapter.hentBrukerForAktør(any(), eq(VERGE))).thenReturn(Optional.of(personinfoVerge));
        }
        return personinfoSøker;
    }

    private Behandling mockDomeneobjektProvider(Personinfo personinfo, boolean harGyldigVerge) {
        var fagsakBackend = FagsakBackend.ny().medSaksnummer(SAKSNUMMER.getVerdi()).medAktørId(personinfo.getAktørId()).medFagsakYtelseType(FagsakYtelseType.FORELDREPENGER).build();
        var behandling = Behandling.builder()
            .medUuid(BEHANDLING_UUID)
            .medFagsakBackend(fagsakBackend)
            .medSpråkkode(Språkkode.NB)
            .medAvsluttet(LocalDateTime.now())
            .leggTilResourceLink(
                harGyldigVerge ? BehandlingResourceLink.ny().medRel("verge-backend").build() : BehandlingResourceLink.ny().medRel("annet").build())
            .build();
        when(domeneobjektProvider.hentFagsakBackend(behandling)).thenReturn(fagsakBackend);
        when(domeneobjektProvider.hentBehandling(any(UUID.class))).thenReturn(behandling);
        when(domeneobjektProvider.hentTilkjentYtelseEngangsstønad(behandling)).thenReturn(new TilkjentYtelseEngangsstønad(1L));
        return behandling;
    }

    private DokumentHendelse opprettDokumentHendelse(UUID randomBestillingsUuid, DokumentMal dokumentMal, DokumentMal journalførSom) {
        var dokumentHendelse = DokumentHendelse.builder()
            .medBehandlingUuid(BEHANDLING_UUID)
            .medBestillingUuid(randomBestillingsUuid)
            .medDokumentMal(dokumentMal)
            .medJournalførSom(journalførSom)
            .build();
        dokumentHendelse.setId(HENDELSE_ID);
        return dokumentHendelse;
    }

    private void mockJournal(DokumentHendelse dokumentHendelse) {
        var dokumentOpprettResponse = new OpprettJournalpostResponse.DokumentInfoResponse(DOKUMENT_INFO_ID);
        var opprettJournalpostResponse = new OpprettJournalpostResponse(JOURNALPOST.getVerdi(), true, List.of(dokumentOpprettResponse));
        when(opprettJournalpostTjeneste.journalførUtsendelse(eq(BREVET), eq(DOKUMENT_MAL_TYPE), any(DokumentFelles.class), eq(dokumentHendelse),
            eq(SAKSNUMMER), eq(true), any(), eq(DOKUMENT_MAL_TYPE), eq(FagsakYtelseType.FORELDREPENGER))).thenReturn(opprettJournalpostResponse);
    }
}
