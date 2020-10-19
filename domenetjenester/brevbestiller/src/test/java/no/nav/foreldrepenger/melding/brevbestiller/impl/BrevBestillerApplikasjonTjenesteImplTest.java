package no.nav.foreldrepenger.melding.brevbestiller.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.melding.aktør.AdresseType;
import no.nav.foreldrepenger.melding.aktør.Adresseinfo;
import no.nav.foreldrepenger.melding.aktør.Personinfo;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingResourceLink;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatES;
import no.nav.foreldrepenger.melding.brevbestiller.dto.DokumentbestillingDtoMapper;
import no.nav.foreldrepenger.melding.brevmapper.DokumentdataMapperProvider;
import no.nav.foreldrepenger.melding.brevmapper.brev.InnvilgelseEngangstønadDokumentdataMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.repository.DokumentRepository;
import no.nav.foreldrepenger.melding.fagsak.FagsakBackend;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.historikk.DokumentHistorikkinnslag;
import no.nav.foreldrepenger.melding.historikk.HistorikkinnslagType;
import no.nav.foreldrepenger.melding.integrasjon.dokdist.DokdistRestKlient;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.DokgenRestKlient;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.Dokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.journal.FerdigstillJournalpostTjeneste;
import no.nav.foreldrepenger.melding.integrasjon.journal.OpprettJournalpostTjeneste;
import no.nav.foreldrepenger.melding.integrasjon.journal.TilknyttVedleggTjeneste;
import no.nav.foreldrepenger.melding.integrasjon.journal.dto.DokumentOpprettResponse;
import no.nav.foreldrepenger.melding.integrasjon.journal.dto.OpprettJournalpostResponse;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.melding.organisasjon.VirksomhetTjeneste;
import no.nav.foreldrepenger.melding.personopplysning.NavBrukerKjønn;
import no.nav.foreldrepenger.melding.personopplysning.PersonstatusType;
import no.nav.foreldrepenger.melding.typer.AktørId;
import no.nav.foreldrepenger.melding.typer.JournalpostId;
import no.nav.foreldrepenger.melding.typer.PersonIdent;
import no.nav.foreldrepenger.melding.typer.Saksnummer;
import no.nav.foreldrepenger.melding.verge.Verge;
import no.nav.foreldrepenger.tps.TpsTjeneste;
import no.nav.vedtak.felles.testutilities.Whitebox;

@ExtendWith(MockitoExtension.class)
public class BrevBestillerApplikasjonTjenesteImplTest {

    private static final UUID BEHANDLING_UUID = UUID.randomUUID();
    private static final String NAVN = "Nav Navesen";
    private static final PersonIdent SØKER_FNR = new PersonIdent("11111111111");
    private static final AktørId SØKER = new AktørId("2222222222222");
    private static final PersonIdent VERGE_FNR = new PersonIdent("77777777777");
    private static final AktørId VERGE = new AktørId("888888888888");
    private static final Saksnummer SAKSNUMMER = new Saksnummer("123456");
    private static final byte[] BREVET = "BREV".getBytes();
    private static final JournalpostId JOURNALPOST = new JournalpostId("7654321");
    private static final DokumentMalType DOKUMENT_MAL_TYPE = DokumentMalType.INNVILGELSE_ENGANGSSTØNAD;
    private static final long HENDELSE_ID = 1L;
    private static final String DOKUMENT_INFO_ID = "987";

    @Mock
    private TpsTjeneste tpsTjeneste;
    @Mock
    private VirksomhetTjeneste virksomhetTjeneste;
    @Mock
    private DomeneobjektProvider domeneobjektProvider;
    @Mock
    private DokumentRepository dokumentRepository;
    @Mock
    private DokgenRestKlient dokgenRestKlient;
    @Mock
    private OpprettJournalpostTjeneste opprettJournalpostTjeneste;
    @Mock
    private TilknyttVedleggTjeneste tilknyttVedleggTjeneste;
    @Mock
    private FerdigstillJournalpostTjeneste ferdigstillJournalpostTjeneste;
    @Mock
    private DokdistRestKlient dokdistRestKlient;
    @Mock
    private DokumentMalUtleder dokumentMalUtleder;
    @Mock
    private DokprodBrevproduksjonTjeneste dokprodBrevproduksjonTjeneste;
    @Mock
    private DokumentdataMapperProvider dokumentdataMapperProvider;

    private InnvilgelseEngangstønadDokumentdataMapper dokumentdataMapper;
    private NavKontaktKonfigurasjon navKontaktKonfigurasjon;
    private DokumentbestillingDtoMapper dokumentbestillingDtoMapper;
    private DokumentFellesDataMapper dokumentFellesDataMapper;
    private DokgenBrevproduksjonTjeneste dokgenBrevproduksjonTjeneste;
    private BrevBestillerApplikasjonTjenesteImpl tjeneste;

    @BeforeEach
    public void beforeEach() {
        dokumentdataMapper = new InnvilgelseEngangstønadDokumentdataMapper(new BrevParametere(6, 3, Period.ofWeeks(3), Period.ofWeeks(4)), domeneobjektProvider);
        navKontaktKonfigurasjon = new NavKontaktKonfigurasjon("1", "2", "3", "4", "5", "6", "7");
        dokumentbestillingDtoMapper = new DokumentbestillingDtoMapper();
        dokumentFellesDataMapper = new DokumentFellesDataMapper(tpsTjeneste, domeneobjektProvider, navKontaktKonfigurasjon, virksomhetTjeneste);
        dokgenBrevproduksjonTjeneste = new DokgenBrevproduksjonTjeneste(dokumentFellesDataMapper, domeneobjektProvider, dokumentRepository, dokgenRestKlient, opprettJournalpostTjeneste, tilknyttVedleggTjeneste, ferdigstillJournalpostTjeneste, dokdistRestKlient, dokumentdataMapperProvider);
        tjeneste = new BrevBestillerApplikasjonTjenesteImpl(dokumentMalUtleder, domeneobjektProvider, dokumentbestillingDtoMapper, dokprodBrevproduksjonTjeneste, dokgenBrevproduksjonTjeneste);
    }

    @Test
    public void skal_generere_og_sende_brev_til_både_søker_og_verge_og_returnere_historikkinnslag() {
        // Arrange
        Personinfo personinfo = mockTps(true);
        Behandling behandling = mockDomeneobjektProvider(personinfo, true);
        DokumentHendelse dokumentHendelse = opprettDokumentHendelse();
        when(dokumentMalUtleder.utledDokumentmal(eq(behandling), eq(dokumentHendelse))).thenReturn(DokumentMalType.INNVILGELSE_ENGANGSSTØNAD);
        when(dokgenRestKlient.genererPdf(anyString(), any(Språkkode.class), any(Dokumentdata.class))).thenReturn(BREVET);
        mockJournal(dokumentHendelse);
        when(dokumentdataMapperProvider.getDokumentdataMapper(eq(DOKUMENT_MAL_TYPE))).thenReturn(dokumentdataMapper);

        // Act
        List<DokumentHistorikkinnslag> historikkinnslag = tjeneste.bestillBrev(dokumentHendelse);

        // Assert
        assertThat(historikkinnslag).hasSize(2);
        assertThat(historikkinnslag.get(0).getBehandlingUuid()).isEqualTo(BEHANDLING_UUID);
        assertThat(historikkinnslag.get(0).getHendelseId()).isEqualTo(HENDELSE_ID);
        assertThat(historikkinnslag.get(0).getDokumentId()).isEqualTo(DOKUMENT_INFO_ID);
        assertThat(historikkinnslag.get(0).getJournalpostId()).isEqualTo(JOURNALPOST);
        assertThat(historikkinnslag.get(0).getHistorikkinnslagType()).isEqualTo(HistorikkinnslagType.BREV_SENT);
        assertThat(historikkinnslag.get(0).getDokumentMalType()).isEqualTo(DOKUMENT_MAL_TYPE);
        verify(dokgenRestKlient, times(2)).genererPdf(anyString(), any(Språkkode.class), any(Dokumentdata.class));
        verify(opprettJournalpostTjeneste, times(2))
                .journalførUtsendelse(eq(BREVET), eq(DOKUMENT_MAL_TYPE), any(DokumentFelles.class), eq(dokumentHendelse), eq(SAKSNUMMER), eq(true));
        verify(dokdistRestKlient, times(2)).distribuerJournalpost(JOURNALPOST);
    }

    @Test
    public void skal_ikke_sende_til_verge_når_verge_ikke_er_definert() {
        // Arrange
        Personinfo personinfo = mockTps(false);
        Behandling behandling = mockDomeneobjektProvider(personinfo, false);
        DokumentHendelse dokumentHendelse = opprettDokumentHendelse();
        when(dokumentMalUtleder.utledDokumentmal(eq(behandling), eq(dokumentHendelse))).thenReturn(DokumentMalType.INNVILGELSE_ENGANGSSTØNAD);
        when(dokgenRestKlient.genererPdf(anyString(), any(Språkkode.class), any(Dokumentdata.class))).thenReturn(BREVET);
        mockJournal(dokumentHendelse);
        when(dokumentdataMapperProvider.getDokumentdataMapper(eq(DOKUMENT_MAL_TYPE))).thenReturn(dokumentdataMapper);

        // Act
        List<DokumentHistorikkinnslag> historikkinnslag = tjeneste.bestillBrev(dokumentHendelse);

        // Assert
        assertThat(historikkinnslag).hasSize(1);
        verify(dokgenRestKlient, times(1)).genererPdf(anyString(), any(Språkkode.class), any(Dokumentdata.class));
        verify(opprettJournalpostTjeneste, times(1))
                .journalførUtsendelse(eq(BREVET), eq(DOKUMENT_MAL_TYPE), any(DokumentFelles.class), eq(dokumentHendelse), eq(SAKSNUMMER), eq(true));
        verify(dokdistRestKlient, times(1)).distribuerJournalpost(JOURNALPOST);
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
        Personinfo personinfoSøker = new Personinfo.Builder()
                .medPersonIdent(SØKER_FNR)
                .medAktørId(SØKER)
                .medNavn(NAVN)
                .medNavBrukerKjønn(NavBrukerKjønn.MANN)
                .build();
        when(tpsTjeneste.hentBrukerForAktør(eq(SØKER))).thenReturn(Optional.of(personinfoSøker));
        Adresseinfo adresseSøker = new Adresseinfo.Builder(AdresseType.BOSTEDSADRESSE, SØKER_FNR, NAVN, PersonstatusType.BOSA).build();
        when(tpsTjeneste.hentAdresseinformasjon(eq(SØKER_FNR))).thenReturn(adresseSøker);

        if (harVerge) {
            Personinfo personinfoVerge = new Personinfo.Builder()
                    .medPersonIdent(VERGE_FNR)
                    .medAktørId(VERGE)
                    .medNavn("Verge Vergesen")
                    .medNavBrukerKjønn(NavBrukerKjønn.KVINNE)
                    .build();
            when(tpsTjeneste.hentBrukerForAktør(eq(VERGE))).thenReturn(Optional.of(personinfoVerge));
            Adresseinfo adresseVerge = new Adresseinfo.Builder(AdresseType.BOSTEDSADRESSE, VERGE_FNR, NAVN, PersonstatusType.BOSA).build();
            when(tpsTjeneste.hentAdresseinformasjon(eq(VERGE_FNR))).thenReturn(adresseVerge);
            when(tpsTjeneste.hentAktørForFnr(VERGE_FNR)).thenReturn(Optional.of(VERGE));
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
                .medSpråkkode(Språkkode.nb)
                .leggTilResourceLink(harVerge ? BehandlingResourceLink.ny().medRel("soeker-verge").build() : BehandlingResourceLink.ny().medRel("annet").build())
                .build();
        when(domeneobjektProvider.hentFagsakBackend(eq(behandling))).thenReturn(fagsakBackend);
        when(domeneobjektProvider.hentBehandling(any(UUID.class))).thenReturn(behandling);
        if (harVerge) {
            Verge verge = new Verge(VERGE_FNR.getIdent(), "", "");
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
        Whitebox.setInternalState(dokumentHendelse, "id", HENDELSE_ID);
        return dokumentHendelse;
    }

    private void mockJournal(DokumentHendelse dokumentHendelse) {
        DokumentOpprettResponse dokumentOpprettResponse = new DokumentOpprettResponse(DOKUMENT_INFO_ID);
        OpprettJournalpostResponse opprettJournalpostResponse = new OpprettJournalpostResponse(JOURNALPOST.getVerdi(), true, List.of(dokumentOpprettResponse));
        when(opprettJournalpostTjeneste.journalførUtsendelse(eq(BREVET), eq(DOKUMENT_MAL_TYPE), any(DokumentFelles.class), eq(dokumentHendelse), eq(SAKSNUMMER), eq(true)))
                .thenReturn(opprettJournalpostResponse);
    }
}