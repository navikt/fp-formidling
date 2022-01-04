package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static java.util.List.of;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SAKSNUMMER;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SØKERS_FNR;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SØKERS_NAVN;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.VERGES_NAVN;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentData;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardHendelseBuilder;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDatoEngelsk;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDatoNorsk;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.behandling.BehandlingType;
import no.nav.foreldrepenger.fpformidling.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.fpformidling.behandling.BehandlingÅrsak;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentData;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentFelles.Kopi;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentKategori;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentTypeId;
import no.nav.foreldrepenger.fpformidling.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.InnhenteOpplysningerDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Fritekst;
import no.nav.foreldrepenger.fpformidling.klage.KlageDokument;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingÅrsakType;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.fpformidling.mottattdokument.MottattDokument;

@ExtendWith(MockitoExtension.class)
public class InnhenteOpplysningerDokumentdataMapperTest {

    private static final String FRITEKST = "Tekst1\n- Vedlegg1\n- Vedlegg2\nTekst2\nTekst3\n- Vedlegg3\nTekst4";
    private static final LocalDate SØKNAD_DATO = LocalDate.now().minusDays(1);
    private static final LocalDate KLAGE_DATO = LocalDate.now().minusDays(2);

    @Mock
    private DomeneobjektProvider domeneobjektProvider = mock(DomeneobjektProvider.class);

    private BrevMapperUtil brevMapperUtil;

    private DokumentData dokumentData;

    private InnhenteOpplysningerDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    public void before() {
        BrevParametere brevParametere = new BrevParametere(6, 2, Period.ZERO, Period.ZERO);
        brevMapperUtil = new BrevMapperUtil(brevParametere);
        dokumentData = lagStandardDokumentData(DokumentMalType.INNHENTE_OPPLYSNINGER);
        dokumentdataMapper = new InnhenteOpplysningerDokumentdataMapper(brevMapperUtil, domeneobjektProvider);

        MottattDokument mottattDokument = new MottattDokument(SØKNAD_DATO, DokumentTypeId.FORELDREPENGER_ENDRING_SØKNAD, DokumentKategori.SØKNAD);
        when(domeneobjektProvider.hentMottatteDokumenter(any(Behandling.class))).thenReturn(of(mottattDokument));
    }

    @Test
    public void skal_mappe_felter_for_brev_til_bruker() {
        // Arrange
        Behandling behandling = opprettBehandling(Språkkode.NB);
        DokumentFelles dokumentFelles = lagStandardDokumentFelles(dokumentData, Kopi.JA, false);
        DokumentHendelse dokumentHendelse = lagDokumentHendelse();

        // Act
        InnhenteOpplysningerDokumentdata innhenteOpplysningerDokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(innhenteOpplysningerDokumentdata.getFelles()).isNotNull();
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getSøkerNavn()).isEqualTo(SØKERS_NAVN);
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(SØKERS_FNR));
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getMottakerNavn()).isNull();
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getBrevDato()).isEqualTo(formaterDatoNorsk(LocalDate.now()));
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getHarVerge()).isEqualTo(true);
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getErKopi()).isEqualTo(true);
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getSaksnummer()).isEqualTo(SAKSNUMMER);
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getYtelseType()).isEqualTo("FP");
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getFritekst()).isEqualTo(Fritekst.fra(FRITEKST));
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getBehandlesAvKA()).isFalse();
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getErUtkast()).isEqualTo(false);

        assertThat(innhenteOpplysningerDokumentdata.getFørstegangsbehandling()).isFalse();
        assertThat(innhenteOpplysningerDokumentdata.getRevurdering()).isTrue();
        assertThat(innhenteOpplysningerDokumentdata.getEndringssøknad()).isTrue();
        assertThat(innhenteOpplysningerDokumentdata.getDød()).isFalse();
        assertThat(innhenteOpplysningerDokumentdata.getKlage()).isFalse();
        assertThat(innhenteOpplysningerDokumentdata.getSøknadDato()).isEqualTo(formaterDatoNorsk(SØKNAD_DATO));
        assertThat(innhenteOpplysningerDokumentdata.getFristDato()).isEqualTo(formaterDatoNorsk(brevMapperUtil.getSvarFrist()));
    }

    @Test
    public void skal_mappe_felter_for_brev_til_verge() {
        // Arrange
        Behandling behandling = opprettBehandling(Språkkode.NB);
        DokumentFelles dokumentFelles = lagStandardDokumentFelles(dokumentData, Kopi.NEI, true);
        DokumentHendelse dokumentHendelse = lagDokumentHendelse();

        // Act
        InnhenteOpplysningerDokumentdata innhenteOpplysningerDokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(innhenteOpplysningerDokumentdata.getFelles()).isNotNull();
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getSøkerNavn()).isEqualTo(SØKERS_NAVN);
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(SØKERS_FNR));
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getMottakerNavn()).isEqualTo(VERGES_NAVN);
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getHarVerge()).isEqualTo(true);
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getErKopi()).isEqualTo(false);
    }

    @Test
    public void skal_mappe_datoer_med_engelsk_format() {
        // Arrange
        Behandling behandling = opprettBehandling(Språkkode.EN);
        DokumentFelles dokumentFelles = lagStandardDokumentFelles(dokumentData, Kopi.JA, false);
        DokumentHendelse dokumentHendelse = lagDokumentHendelse();

        // Act
        InnhenteOpplysningerDokumentdata innhenteOpplysningerDokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(innhenteOpplysningerDokumentdata.getFelles()).isNotNull();
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getBrevDato()).isEqualTo(formaterDatoEngelsk(LocalDate.now()));
        assertThat(innhenteOpplysningerDokumentdata.getSøknadDato()).isEqualTo(formaterDatoEngelsk(SØKNAD_DATO));
        assertThat(innhenteOpplysningerDokumentdata.getFristDato()).isEqualTo(formaterDatoEngelsk(brevMapperUtil.getSvarFrist()));
    }

    @Test
    public void skal_mappe_behandlesAvKA_når_det_er_angitt_på_hendelsen() {
        // Arrange
        Behandling behandling = opprettKlageBehandling("NFP");
        DokumentFelles dokumentFelles = lagStandardDokumentFelles(dokumentData, Kopi.NEI, false);
        DokumentHendelse dokumentHendelse = lagStandardHendelseBuilder().medBehandlendeEnhetNavn("NAV Klageinstans").build();
        mockKlageDokument();

        // Act
        InnhenteOpplysningerDokumentdata innhenteOpplysningerDokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(innhenteOpplysningerDokumentdata.getKlage()).isTrue();
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getBehandlesAvKA()).isTrue();
    }

    @Test
    public void skal_ikke_mappe_behandlesAvKA_når_det_er_angitt_noe_annet_på_hendelsen() {
        // Arrange
        Behandling behandling = opprettKlageBehandling("NAV Klageinstans");
        DokumentFelles dokumentFelles = lagStandardDokumentFelles(dokumentData, Kopi.NEI, false);
        DokumentHendelse dokumentHendelse = lagStandardHendelseBuilder().medBehandlendeEnhetNavn("NFP").build();
        mockKlageDokument();

        // Act
        InnhenteOpplysningerDokumentdata innhenteOpplysningerDokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(innhenteOpplysningerDokumentdata.getKlage()).isTrue();
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getBehandlesAvKA()).isFalse();
    }

    @Test
    public void skal_mappe_behandlesAvKA_fra_behandlingen_når_det_ikke_er_angitt_på_hendelsen() {
        // Arrange
        Behandling behandling = opprettKlageBehandling("NAV Klageinstans");
        DokumentFelles dokumentFelles = lagStandardDokumentFelles(dokumentData, Kopi.NEI, false);
        DokumentHendelse dokumentHendelse = lagDokumentHendelse();
        mockKlageDokument();

        // Act
        InnhenteOpplysningerDokumentdata innhenteOpplysningerDokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(innhenteOpplysningerDokumentdata.getKlage()).isTrue();
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getBehandlesAvKA()).isTrue();
    }

    @Test
    public void skal_ikke_mappe_behandlesAvKA_når_det_ikke_er_angitt_på_verken_hendelsen_eller_behandlingen() {
        // Arrange
        Behandling behandling = opprettKlageBehandling("NFP");
        DokumentFelles dokumentFelles = lagStandardDokumentFelles(dokumentData, Kopi.NEI, false);
        DokumentHendelse dokumentHendelse = lagDokumentHendelse();
        mockKlageDokument();

        // Act
        InnhenteOpplysningerDokumentdata innhenteOpplysningerDokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(innhenteOpplysningerDokumentdata.getKlage()).isTrue();
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getBehandlesAvKA()).isFalse();
    }

    private Behandling opprettBehandling(Språkkode språkkode) {
        return Behandling.builder()
                .medUuid(UUID.randomUUID())
                .medBehandlingType(BehandlingType.REVURDERING)
                .medBehandlingÅrsaker(of(BehandlingÅrsak.builder().medBehandlingÅrsakType(BehandlingÅrsakType.RE_ENDRING_FRA_BRUKER).build()))
                .medBehandlingsresultat(Behandlingsresultat.builder()
                        .medBehandlingResultatType(BehandlingResultatType.INNVILGET).build())
                .medSpråkkode(språkkode)
                .build();
    }

    private Behandling opprettKlageBehandling(String behandlendeEnhet) {
        return Behandling.builder()
                .medUuid(UUID.randomUUID())
                .medBehandlingType(BehandlingType.KLAGE)
                .medBehandlendeEnhetNavn(behandlendeEnhet)
                .build();
    }

    private DokumentHendelse lagDokumentHendelse() {
        return lagStandardHendelseBuilder()
                .medFritekst(FRITEKST)
                .build();
    }

    private void mockKlageDokument() {
        KlageDokument klageDokument = new KlageDokument(KLAGE_DATO);
        when(domeneobjektProvider.hentKlageDokument(any(Behandling.class))).thenReturn(klageDokument);
    }
}
