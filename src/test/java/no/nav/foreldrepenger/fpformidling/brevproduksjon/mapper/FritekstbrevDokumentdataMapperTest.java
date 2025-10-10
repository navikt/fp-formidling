package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDatoNorsk;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlag;
import no.nav.foreldrepenger.fpformidling.typer.DokumentMal;

@ExtendWith(MockitoExtension.class)
class FritekstbrevDokumentdataMapperTest {

    private static final String OVERSKRIFT = "Hovedoverskrift";
    private static final String BRØDTEKST_INN = "_Overskrift\nEn setning.\nMer tekst.";
    private static final String BRØDTEKST_UT = "##### Overskrift\nEn setning.\\\nMer tekst.";

    private FritekstbrevDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    void before() {
        dokumentdataMapper = new FritekstbrevDokumentdataMapper();
    }

    @Test
    void skal_mappe_felter_for_fritekstbrev_til_bruker_fra_hendelsen() {
        // Arrange
        var behandling = DatamapperTestUtil.defaultBuilder().fagsakYtelseType(BrevGrunnlag.FagsakYtelseType.FORELDREPENGER).build();
        var dokumentFelles = DatamapperTestUtil.lagStandardDokumentFelles(FagsakYtelseType.FORELDREPENGER);
        var dokumentHendelse = DatamapperTestUtil.lagStandardHendelseBuilder()
            .medTittel(OVERSKRIFT)
            .medFritekst(BRØDTEKST_INN)
            .medDokumentMal(DokumentMal.FRITEKSTBREV)
            .build();

        // Act
        var dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(dokumentdata.getFelles()).isNotNull();
        assertThat(dokumentdata.getFelles().getSøkerNavn()).isEqualTo(DatamapperTestUtil.SØKERS_NAVN);
        assertThat(dokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(DatamapperTestUtil.SØKERS_FNR));
        assertThat(dokumentdata.getFelles().getMottakerNavn()).isNull();
        assertThat(dokumentdata.getFelles().getBrevDato()).isEqualTo(formaterDatoNorsk(LocalDate.now()));
        assertThat(dokumentdata.getFelles().getHarVerge()).isFalse();
        assertThat(dokumentdata.getFelles().getErKopi()).isFalse();
        assertThat(dokumentdata.getFelles().getSaksnummer()).isEqualTo(DatamapperTestUtil.SAKSNUMMER);
        assertThat(dokumentdata.getFelles().getYtelseType()).isEqualTo("FP");
        assertThat(dokumentdata.getFelles().getErUtkast()).isFalse();

        assertThat(dokumentdata.getOverskrift()).isEqualTo(OVERSKRIFT);
        assertThat(dokumentdata.getBrødtekst().getFritekst()).isEqualTo(BRØDTEKST_UT);
    }

    @Test
    void skal_mappe_felter_for_fritekstbrev_fra_behandlingsresultatet_når_hendelsen_ikke_har_dem() {
        // Arrange
        var behandlingsresultat = new BrevGrunnlag.Behandlingsresultat(null, null, BrevGrunnlag.Behandlingsresultat.BehandlingResultatType.INNVILGET,
            null, new BrevGrunnlag.Behandlingsresultat.Fritekst(OVERSKRIFT, BRØDTEKST_INN, null), null, false, null, List.of(), List.of());
        var behandling = DatamapperTestUtil.defaultBuilder()
            .behandlingsresultat(behandlingsresultat)
            .fagsakYtelseType(BrevGrunnlag.FagsakYtelseType.FORELDREPENGER)
            .build();
        var dokumentFelles = DatamapperTestUtil.lagStandardDokumentFelles(FagsakYtelseType.FORELDREPENGER);
        var dokumentHendelse = DatamapperTestUtil.lagStandardHendelseBuilder()
            .medTittel(null)
            .medFritekst(null)
            .medDokumentMal(DokumentMal.FRITEKSTBREV)
            .build();

        // Act
        var dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(dokumentdata.getOverskrift()).isEqualTo(OVERSKRIFT);
        assertThat(dokumentdata.getBrødtekst().getFritekst()).isEqualTo(BRØDTEKST_UT);
    }
}
