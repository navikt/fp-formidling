package no.nav.foreldrepenger.melding.brevbestiller.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.PersonAdapter;
import no.nav.foreldrepenger.melding.aktør.AdresseType;
import no.nav.foreldrepenger.melding.aktør.Adresseinfo;
import no.nav.foreldrepenger.melding.aktør.Personinfo;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingResourceLink;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatES;
import no.nav.foreldrepenger.melding.brevbestiller.task.DistribuerBrevTask;
import no.nav.foreldrepenger.melding.brevmapper.DokumentdataMapperProvider;
import no.nav.foreldrepenger.melding.brevmapper.brev.EngangsstønadInnvilgelseDokumentdataMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.repository.DokumentRepository;
import no.nav.foreldrepenger.melding.fagsak.FagsakBackend;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.historikk.DokumentHistorikkinnslag;
import no.nav.foreldrepenger.melding.historikk.HistorikkRepository;
import no.nav.foreldrepenger.melding.historikk.HistorikkinnslagType;
import no.nav.foreldrepenger.melding.integrasjon.dokdist.Dokdist;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.Dokgen;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.journal.OpprettJournalpostTjeneste;
import no.nav.foreldrepenger.melding.integrasjon.journal.dto.DokumentOpprettResponse;
import no.nav.foreldrepenger.melding.integrasjon.journal.dto.OpprettJournalpostResponse;
import no.nav.foreldrepenger.melding.kafkatjenester.historikk.task.PubliserHistorikkTask;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.melding.organisasjon.VirksomhetTjeneste;
import no.nav.foreldrepenger.melding.personopplysning.NavBrukerKjønn;
import no.nav.foreldrepenger.melding.typer.AktørId;
import no.nav.foreldrepenger.melding.typer.JournalpostId;
import no.nav.foreldrepenger.melding.typer.PersonIdent;
import no.nav.foreldrepenger.melding.typer.Saksnummer;
import no.nav.foreldrepenger.melding.verge.Verge;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskGruppe;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskTjeneste;
import no.nav.vedtak.felles.prosesstask.api.TaskType;

@ExtendWith(MockitoExtension.class)
public class BrevBestillerTjenesteTest {

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

    private static final TaskType HISTORIKK_TASK = TaskType.forProsessTask(PubliserHistorikkTask.class);
    private static final TaskType DIST_TASK = TaskType.forProsessTask(DistribuerBrevTask.class);

    @Mock
    private PersonAdapter personAdapter;
    @Mock
    private VirksomhetTjeneste virksomhetTjeneste;
    @Mock
    private DomeneobjektProvider domeneobjektProvider;
    @Mock
    private DokumentRepository dokumentRepository;
    @Mock
    private Dokgen dokgenRestKlient;
    @Mock
    private OpprettJournalpostTjeneste opprettJournalpostTjeneste;
    @Mock
    private Dokdist dokdistRestKlient;
    @Mock
    private DokumentMalUtleder dokumentMalUtleder;
    @Mock
    private DokprodBrevproduksjonTjeneste dokprodBrevproduksjonTjeneste;
    @Mock
    private DokumentdataMapperProvider dokumentdataMapperProvider;
    @Mock
    private ProsessTaskTjeneste taskTjeneste;
    @Mock
    private HistorikkRepository historikkRepository;

    private EngangsstønadInnvilgelseDokumentdataMapper dokumentdataMapper;
    private NavKontaktKonfigurasjon navKontaktKonfigurasjon;
    private DokumentFellesDataMapper dokumentFellesDataMapper;
    private DokgenBrevproduksjonTjeneste dokgenBrevproduksjonTjeneste;
    private BrevBestillerTjeneste tjeneste;

    @BeforeEach
    public void beforeEach() {
        dokumentdataMapper = new EngangsstønadInnvilgelseDokumentdataMapper(new BrevParametere(6, 3, Period.ofWeeks(3), Period.ofWeeks(4)),
                domeneobjektProvider);
        navKontaktKonfigurasjon = new NavKontaktKonfigurasjon("1", "2", "3", "4", "5", "6", "7");
        dokumentFellesDataMapper = new DokumentFellesDataMapper(personAdapter, domeneobjektProvider, navKontaktKonfigurasjon, virksomhetTjeneste);
        dokgenBrevproduksjonTjeneste = new DokgenBrevproduksjonTjeneste(dokumentFellesDataMapper, domeneobjektProvider, dokumentRepository,
                dokgenRestKlient, opprettJournalpostTjeneste, dokumentdataMapperProvider, taskTjeneste, historikkRepository,
                dokprodBrevproduksjonTjeneste);
        tjeneste = new BrevBestillerTjeneste(dokumentMalUtleder, domeneobjektProvider,
                dokprodBrevproduksjonTjeneste, dokgenBrevproduksjonTjeneste);
    }

    @Test
    public void skal_generere_og_sende_brev_til_både_søker_og_verge_og_returnere_historikkinnslag() {
        // Arrange
        Personinfo personinfo = mockTps(true);
        Behandling behandling = mockDomeneobjektProvider(personinfo, true);
        DokumentHendelse dokumentHendelse = opprettDokumentHendelse();
        when(dokumentMalUtleder.utledDokumentmal(eq(behandling), eq(dokumentHendelse))).thenReturn(DokumentMalType.ENGANGSSTØNAD_INNVILGELSE);
        when(dokgenRestKlient.genererPdf(anyString(), any(Språkkode.class), any(Dokumentdata.class))).thenReturn(BREVET);
        mockJournal(dokumentHendelse);
        when(dokumentdataMapperProvider.getDokumentdataMapper(eq(DOKUMENT_MAL_TYPE))).thenReturn(dokumentdataMapper);
        ArgumentCaptor<DokumentHistorikkinnslag> historikkCaptor = ArgumentCaptor.forClass(DokumentHistorikkinnslag.class);
        ArgumentCaptor<ProsessTaskGruppe> taskCaptor = ArgumentCaptor.forClass(ProsessTaskGruppe.class);

        // Act
        tjeneste.bestillBrev(dokumentHendelse);

        // Assert
        verify(dokgenRestKlient, times(2)).genererPdf(anyString(), any(Språkkode.class), any(Dokumentdata.class));
        verify(opprettJournalpostTjeneste, times(2))
                .journalførUtsendelse(eq(BREVET), eq(DOKUMENT_MAL_TYPE), any(DokumentFelles.class), eq(dokumentHendelse), eq(SAKSNUMMER), eq(true));
        verify(historikkRepository, times(2)).lagre(historikkCaptor.capture());
        DokumentHistorikkinnslag historikkinnslag = historikkCaptor.getAllValues().get(0);
        assertThat(historikkinnslag.getBehandlingUuid()).isEqualTo(BEHANDLING_UUID);
        assertThat(historikkinnslag.getHendelseId()).isEqualTo(HENDELSE_ID);
        assertThat(historikkinnslag.getDokumentId()).isEqualTo(DOKUMENT_INFO_ID);
        assertThat(historikkinnslag.getJournalpostId()).isEqualTo(JOURNALPOST);
        assertThat(historikkinnslag.getHistorikkinnslagType()).isEqualTo(HistorikkinnslagType.BREV_SENT);
        assertThat(historikkinnslag.getDokumentMalType()).isEqualTo(DOKUMENT_MAL_TYPE);
        verify(taskTjeneste, times(2)).lagre(taskCaptor.capture());
        assertThat(taskCaptor.getValue().getTasks()).hasSize(2);
        assertThat(taskCaptor.getValue().getTasks().get(0).task().taskType()).isEqualTo(DIST_TASK);
        assertThat(taskCaptor.getValue().getTasks().get(1).task().taskType()).isEqualTo(HISTORIKK_TASK);
    }

    @Test
    public void skal_ikke_sende_til_verge_når_verge_ikke_er_definert() {
        // Arrange
        Personinfo personinfo = mockTps(false);
        Behandling behandling = mockDomeneobjektProvider(personinfo, false);
        DokumentHendelse dokumentHendelse = opprettDokumentHendelse();
        when(dokumentMalUtleder.utledDokumentmal(eq(behandling), eq(dokumentHendelse))).thenReturn(DokumentMalType.ENGANGSSTØNAD_INNVILGELSE);
        when(dokgenRestKlient.genererPdf(anyString(), any(Språkkode.class), any(Dokumentdata.class))).thenReturn(BREVET);
        mockJournal(dokumentHendelse);
        when(dokumentdataMapperProvider.getDokumentdataMapper(eq(DOKUMENT_MAL_TYPE))).thenReturn(dokumentdataMapper);
        ArgumentCaptor<ProsessTaskGruppe> taskCaptor = ArgumentCaptor.forClass(ProsessTaskGruppe.class);

        // Act
        tjeneste.bestillBrev(dokumentHendelse);

        // Assert
        verify(dokgenRestKlient, times(1)).genererPdf(anyString(), any(Språkkode.class), any(Dokumentdata.class));
        verify(opprettJournalpostTjeneste, times(1))
                .journalførUtsendelse(eq(BREVET), eq(DOKUMENT_MAL_TYPE), any(DokumentFelles.class), eq(dokumentHendelse), eq(SAKSNUMMER), eq(true));
        verify(historikkRepository, times(1)).lagre(any(DokumentHistorikkinnslag.class));
        verify(taskTjeneste, times(1)).lagre(taskCaptor.capture());
        assertThat(taskCaptor.getValue().getTasks()).hasSize(2);
        assertThat(taskCaptor.getValue().getTasks().get(0).task().taskType()).isEqualTo(DIST_TASK);
        assertThat(taskCaptor.getValue().getTasks().get(1).task().taskType()).isEqualTo(HISTORIKK_TASK);
    }

    @Test
    public void skal_bare_kalle_dokgen_tjenesten_for_maler_som_er_konvertert() {
        // Arrange
        DokumentHendelse dokumentHendelse = opprettDokumentHendelse();
        when(dokumentMalUtleder.utledDokumentmal(any(), any())).thenReturn(DokumentMalType.INNVILGELSE_FORELDREPENGER_DOK);

        // Act
        tjeneste.bestillBrev(dokumentHendelse);

        // Assert
        verify(dokprodBrevproduksjonTjeneste, times(1)).bestillBrev(eq(dokumentHendelse), any(), eq(DokumentMalType.INNVILGELSE_FORELDREPENGER_DOK));
        verifyNoInteractions(dokgenRestKlient);
        verifyNoInteractions(opprettJournalpostTjeneste);
        verifyNoInteractions(dokdistRestKlient);
    }

    private Personinfo mockTps(boolean harVerge) {
        Personinfo personinfoSøker = Personinfo.getbuilder(SØKER)
                .medPersonIdent(SØKER_FNR)
                .medNavn(NAVN)
                .medNavBrukerKjønn(NavBrukerKjønn.MANN)
                .build();
        lenient().when(personAdapter.hentBrukerForAktør(eq(SØKER))).thenReturn(Optional.of(personinfoSøker));
        Adresseinfo adresseSøker = new Adresseinfo.Builder(AdresseType.BOSTEDSADRESSE, SØKER_FNR, NAVN, false).build();
        lenient().when(personAdapter.hentAdresseinformasjon(eq(SØKER))).thenReturn(Optional.of(adresseSøker));

        if (harVerge) {
            Personinfo personinfoVerge = Personinfo.getbuilder(VERGE)
                    .medPersonIdent(VERGE_FNR)
                    .medNavn("Verge Vergesen")
                    .medNavBrukerKjønn(NavBrukerKjønn.KVINNE)
                    .build();
            lenient().when(personAdapter.hentBrukerForAktør(eq(VERGE))).thenReturn(Optional.of(personinfoVerge));
            Adresseinfo adresseVerge = new Adresseinfo.Builder(AdresseType.BOSTEDSADRESSE, VERGE_FNR, NAVN, false).build();
            lenient().when(personAdapter.hentAdresseinformasjon(eq(VERGE))).thenReturn(Optional.of(adresseVerge));
        }
        return personinfoSøker;
    }

    private Behandling mockDomeneobjektProvider(Personinfo personinfo, boolean harVerge) {
        FagsakBackend fagsakBackend = FagsakBackend.ny()
                .medSaksnummer(SAKSNUMMER.getVerdi())
                .medAktørId(personinfo.getAktørId())
                .build();
        Behandling behandling = Behandling.builder()
                .medUuid(BEHANDLING_UUID)
                .medFagsakBackend(fagsakBackend)
                .medSpråkkode(Språkkode.NB)
                .leggTilResourceLink(
                        harVerge ? BehandlingResourceLink.ny().medRel("soeker-verge").build() : BehandlingResourceLink.ny().medRel("annet").build())
                .build();
        when(domeneobjektProvider.hentFagsakBackend(eq(behandling))).thenReturn(fagsakBackend);
        when(domeneobjektProvider.hentBehandling(any(UUID.class))).thenReturn(behandling);
        if (harVerge) {
            Verge verge = new Verge(VERGE.getId(), "", "");
            when(domeneobjektProvider.hentVerge(eq(behandling))).thenReturn(Optional.of(verge));
        }
        when(domeneobjektProvider.hentBeregningsresultatES(eq(behandling))).thenReturn(new BeregningsresultatES(1L));
        return behandling;
    }

    private DokumentHendelse opprettDokumentHendelse() {
        DokumentHendelse dokumentHendelse = DokumentHendelse.builder()
                .medBehandlingUuid(BEHANDLING_UUID)
                .medBestillingUuid(UUID.randomUUID())
                .medYtelseType(FagsakYtelseType.FORELDREPENGER)
                .medBehandlendeEnhetNavn("Navkontoret")
                .build();
        dokumentHendelse.setId(HENDELSE_ID);
        return dokumentHendelse;
    }

    private void mockJournal(DokumentHendelse dokumentHendelse) {
        DokumentOpprettResponse dokumentOpprettResponse = new DokumentOpprettResponse(DOKUMENT_INFO_ID);
        OpprettJournalpostResponse opprettJournalpostResponse = new OpprettJournalpostResponse(JOURNALPOST.getVerdi(), "", true,
                List.of(dokumentOpprettResponse));
        when(opprettJournalpostTjeneste.journalførUtsendelse(eq(BREVET), eq(DOKUMENT_MAL_TYPE), any(DokumentFelles.class), eq(dokumentHendelse),
                eq(SAKSNUMMER), eq(true)))
                        .thenReturn(opprettJournalpostResponse);
    }
}
