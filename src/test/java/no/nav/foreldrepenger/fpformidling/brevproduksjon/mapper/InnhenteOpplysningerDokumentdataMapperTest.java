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
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.behandling.BehandlingType;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.fpformidling.domene.behandling.BehandlingÅrsak;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentData;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles.Kopi;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentKategori;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentTypeId;
import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto;
import no.nav.foreldrepenger.fpformidling.domene.klage.KlageDokument;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingÅrsakType;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.fpformidling.domene.mottattdokument.MottattDokument;

@ExtendWith(MockitoExtension.class)
class InnhenteOpplysningerDokumentdataMapperTest {

    private static final String FRITEKST = "Tekst1\n- Vedlegg1\n- Vedlegg2\nTekst2\nTekst3\n- Vedlegg3\nTekst4";
    private static final LocalDate SØKNAD_DATO = LocalDate.now().minusDays(1);
    private static final LocalDate KLAGE_DATO = LocalDate.now().minusDays(2);

    @Mock
    private DomeneobjektProvider domeneobjektProvider = mock(DomeneobjektProvider.class);

    private BrevMapperUtil brevMapperUtil;

    private DokumentData dokumentData;

    private InnhenteOpplysningerDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    void before() {
        var brevParametere = new BrevParametere(6, 2, Period.ZERO, Period.ZERO);
        brevMapperUtil = new BrevMapperUtil(brevParametere);
        dokumentData = lagStandardDokumentData(DokumentMalType.INNHENTE_OPPLYSNINGER);
        dokumentdataMapper = new InnhenteOpplysningerDokumentdataMapper(brevMapperUtil, domeneobjektProvider);

        var mottattDokumenter = List.of(
            new MottattDokument(SØKNAD_DATO.minusDays(10), DokumentTypeId.FORELDREPENGER_ENDRING_SØKNAD, DokumentKategori.SØKNAD),
            new MottattDokument(SØKNAD_DATO, DokumentTypeId.FORELDREPENGER_ENDRING_SØKNAD, DokumentKategori.SØKNAD));
        when(domeneobjektProvider.hentMottatteDokumenter(any(Behandling.class))).thenReturn(mottattDokumenter);
    }

    @Test
    void skal_mappe_felter_for_brev_til_bruker() {
        // Arrange
        var behandling = opprettBehandling(Språkkode.NB);
        var dokumentFelles = lagStandardDokumentFelles(dokumentData, Kopi.JA, false);
        var dokumentHendelse = lagDokumentHendelse();

        // Act
        var innhenteOpplysningerDokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(innhenteOpplysningerDokumentdata.getFelles()).isNotNull();
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getSøkerNavn()).isEqualTo(SØKERS_NAVN);
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(SØKERS_FNR));
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getMottakerNavn()).isNull();
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getBrevDato()).isEqualTo(formaterDatoNorsk(LocalDate.now()));
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getHarVerge()).isTrue();
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getErKopi()).isTrue();
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getSaksnummer()).isEqualTo(SAKSNUMMER);
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getYtelseType()).isEqualTo("FP");
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getFritekst()).isEqualTo(FritekstDto.fra(FRITEKST));
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getBehandlesAvKA()).isFalse();
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getErUtkast()).isFalse();

        assertThat(innhenteOpplysningerDokumentdata.getFørstegangsbehandling()).isFalse();
        assertThat(innhenteOpplysningerDokumentdata.getRevurdering()).isTrue();
        assertThat(innhenteOpplysningerDokumentdata.getEndringssøknad()).isTrue();
        assertThat(innhenteOpplysningerDokumentdata.getDød()).isFalse();
        assertThat(innhenteOpplysningerDokumentdata.getKlage()).isFalse();
        assertThat(innhenteOpplysningerDokumentdata.getSøknadDato()).isEqualTo(formaterDatoNorsk(SØKNAD_DATO));
        assertThat(innhenteOpplysningerDokumentdata.getFristDato()).isEqualTo(formaterDatoNorsk(brevMapperUtil.getSvarFrist()));
    }

    @Test
    void skal_mappe_felter_for_brev_til_verge() {
        // Arrange
        var behandling = opprettBehandling(Språkkode.NB);
        var dokumentFelles = lagStandardDokumentFelles(dokumentData, Kopi.NEI, true);
        var dokumentHendelse = lagDokumentHendelse();

        // Act
        var innhenteOpplysningerDokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(innhenteOpplysningerDokumentdata.getFelles()).isNotNull();
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getSøkerNavn()).isEqualTo(SØKERS_NAVN);
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(SØKERS_FNR));
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getMottakerNavn()).isEqualTo(VERGES_NAVN);
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getHarVerge()).isTrue();
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getErKopi()).isFalse();
    }

    @Test
    void skal_mappe_datoer_med_engelsk_format() {
        // Arrange
        var behandling = opprettBehandling(Språkkode.EN);
        var dokumentFelles = lagStandardDokumentFelles(dokumentData, Kopi.JA, false);
        var dokumentHendelse = lagDokumentHendelse();

        // Act
        var innhenteOpplysningerDokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(innhenteOpplysningerDokumentdata.getFelles()).isNotNull();
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getBrevDato()).isEqualTo(formaterDatoEngelsk(LocalDate.now()));
        assertThat(innhenteOpplysningerDokumentdata.getSøknadDato()).isEqualTo(formaterDatoEngelsk(SØKNAD_DATO));
        assertThat(innhenteOpplysningerDokumentdata.getFristDato()).isEqualTo(formaterDatoEngelsk(brevMapperUtil.getSvarFrist()));
    }

    @Test
    void skal_mappe_behandlesAvKA_når_det_er_angitt_på_hendelsen() {
        // Arrange
        var behandling = opprettKlageBehandling("NFP");
        var dokumentFelles = lagStandardDokumentFelles(dokumentData, Kopi.NEI, false);
        var dokumentHendelse = lagStandardHendelseBuilder().medBehandlendeEnhetNavn("NAV Klageinstans").build();
        mockKlageDokument();

        // Act
        var innhenteOpplysningerDokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(innhenteOpplysningerDokumentdata.getKlage()).isTrue();
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getBehandlesAvKA()).isTrue();
    }

    @Test
    void skal_ikke_mappe_behandlesAvKA_når_det_er_angitt_noe_annet_på_hendelsen() {
        // Arrange
        var behandling = opprettKlageBehandling("NAV Klageinstans");
        var dokumentFelles = lagStandardDokumentFelles(dokumentData, Kopi.NEI, false);
        var dokumentHendelse = lagStandardHendelseBuilder().medBehandlendeEnhetNavn("NFP").build();
        mockKlageDokument();

        // Act
        var innhenteOpplysningerDokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(innhenteOpplysningerDokumentdata.getKlage()).isTrue();
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getBehandlesAvKA()).isFalse();
    }

    @Test
    void skal_mappe_behandlesAvKA_fra_behandlingen_når_det_ikke_er_angitt_på_hendelsen() {
        // Arrange
        var behandling = opprettKlageBehandling("NAV Klageinstans");
        var dokumentFelles = lagStandardDokumentFelles(dokumentData, Kopi.NEI, false);
        var dokumentHendelse = lagDokumentHendelse();
        mockKlageDokument();

        // Act
        var innhenteOpplysningerDokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(innhenteOpplysningerDokumentdata.getKlage()).isTrue();
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getBehandlesAvKA()).isTrue();
    }

    @Test
    void skal_ikke_mappe_behandlesAvKA_når_det_ikke_er_angitt_på_verken_hendelsen_eller_behandlingen() {
        // Arrange
        var behandling = opprettKlageBehandling("NFP");
        var dokumentFelles = lagStandardDokumentFelles(dokumentData, Kopi.NEI, false);
        var dokumentHendelse = lagDokumentHendelse();
        mockKlageDokument();

        // Act
        var innhenteOpplysningerDokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(innhenteOpplysningerDokumentdata.getKlage()).isTrue();
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getBehandlesAvKA()).isFalse();
    }

    private Behandling opprettBehandling(Språkkode språkkode) {
        return Behandling.builder()
            .medUuid(UUID.randomUUID())
            .medBehandlingType(BehandlingType.REVURDERING)
            .medBehandlingÅrsaker(of(BehandlingÅrsak.builder().medBehandlingÅrsakType(BehandlingÅrsakType.RE_ENDRING_FRA_BRUKER).build()))
            .medBehandlingsresultat(Behandlingsresultat.builder().medBehandlingResultatType(BehandlingResultatType.INNVILGET).build())
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
        return lagStandardHendelseBuilder().medFritekst(FRITEKST).build();
    }

    private void mockKlageDokument() {
        var klageDokument = new KlageDokument(KLAGE_DATO);
        when(domeneobjektProvider.hentKlageDokument(any(Behandling.class))).thenReturn(klageDokument);
    }
}
