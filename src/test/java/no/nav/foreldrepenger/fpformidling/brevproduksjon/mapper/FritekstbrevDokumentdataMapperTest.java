package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.fpformidling.domene.typer.Dato.formaterDatoNorsk;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentData;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;

@ExtendWith(MockitoExtension.class)
class FritekstbrevDokumentdataMapperTest {

    private static final String OVERSKRIFT = "Hovedoverskrift";
    private static final String BRØDTEKST_INN = "_Overskrift\nEn setning.\nMer tekst.";
    private static final String BRØDTEKST_UT = "##### Overskrift\nEn setning.\\\nMer tekst.";

    private DokumentData dokumentData;

    private FritekstbrevDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    void before() {
        dokumentData = DatamapperTestUtil.lagStandardDokumentData(DokumentMalType.FRITEKSTBREV);
        dokumentdataMapper = new FritekstbrevDokumentdataMapper();
    }

    @Test
    void skal_mappe_felter_for_fritekstbrev_til_bruker_fra_hendelsen() {
        // Arrange
        var behandling = DatamapperTestUtil.standardBehandling();
        var dokumentFelles = DatamapperTestUtil.lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.JA, false);
        var dokumentHendelse = DatamapperTestUtil.lagStandardHendelseBuilder().medTittel(OVERSKRIFT).medFritekst(BRØDTEKST_INN).build();

        // Act
        var dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(dokumentdata.getFelles()).isNotNull();
        assertThat(dokumentdata.getFelles().getSøkerNavn()).isEqualTo(DatamapperTestUtil.SØKERS_NAVN);
        assertThat(dokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(DatamapperTestUtil.SØKERS_FNR));
        assertThat(dokumentdata.getFelles().getMottakerNavn()).isNull();
        assertThat(dokumentdata.getFelles().getBrevDato()).isEqualTo(formaterDatoNorsk(LocalDate.now()));
        assertThat(dokumentdata.getFelles().getHarVerge()).isTrue();
        assertThat(dokumentdata.getFelles().getErKopi()).isTrue();
        assertThat(dokumentdata.getFelles().getSaksnummer()).isEqualTo(DatamapperTestUtil.SAKSNUMMER);
        assertThat(dokumentdata.getFelles().getYtelseType()).isEqualTo("FP");
        assertThat(dokumentdata.getFelles().getBehandlesAvKA()).isFalse();
        assertThat(dokumentdata.getFelles().getErUtkast()).isFalse();

        assertThat(dokumentdata.getOverskrift()).isEqualTo(OVERSKRIFT);
        assertThat(dokumentdata.getBrødtekst().getFritekst()).isEqualTo(BRØDTEKST_UT);
    }

    @Test
    void skal_mappe_felter_for_fritekstbrev_fra_behandlingsresultatet_når_hendelsen_ikke_har_dem() {
        // Arrange
        var behandlingsresultat = Behandlingsresultat.builder().medOverskrift(OVERSKRIFT).medFritekstbrev(BRØDTEKST_INN).build();
        var behandling = DatamapperTestUtil.standardBehandlingBuilder().medBehandlingsresultat(behandlingsresultat).build();
        var dokumentFelles = DatamapperTestUtil.lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.JA, false);
        var dokumentHendelse = DatamapperTestUtil.lagStandardHendelseBuilder().medTittel(null).medFritekst(null).build();

        // Act
        var dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(dokumentdata.getOverskrift()).isEqualTo(OVERSKRIFT);
        assertThat(dokumentdata.getBrødtekst().getFritekst()).isEqualTo(BRØDTEKST_UT);
    }
}
