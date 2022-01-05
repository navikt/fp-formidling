package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SAKSNUMMER;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SØKERS_FNR;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SØKERS_NAVN;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentData;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardHendelseBuilder;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.standardBehandling;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.standardBehandlingBuilder;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDatoNorsk;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentData;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.FritekstbrevDokumentdata;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;

@ExtendWith(MockitoExtension.class)
public class FritekstbrevDokumentdataMapperTest {

    private static final String OVERSKRIFT = "Hovedoverskrift";
    private static final String BRØDTEKST_INN = "_Overskrift\nEn setning.\nMer tekst.";
    private static final String BRØDTEKST_UT = "##### Overskrift\nEn setning.\\\nMer tekst.";

    private DokumentData dokumentData;

    private FritekstbrevDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    public void before() {
        dokumentData = lagStandardDokumentData(DokumentMalType.FRITEKSTBREV);
        dokumentdataMapper = new FritekstbrevDokumentdataMapper();
    }

    @Test
    public void skal_mappe_felter_for_fritekstbrev_til_bruker_fra_hendelsen() {
        // Arrange
        Behandling behandling = standardBehandling();
        DokumentFelles dokumentFelles = lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.JA, false);
        DokumentHendelse dokumentHendelse = lagStandardHendelseBuilder().medTittel(OVERSKRIFT).medFritekst(BRØDTEKST_INN).build();

        // Act
        FritekstbrevDokumentdata dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(dokumentdata.getFelles()).isNotNull();
        assertThat(dokumentdata.getFelles().getSøkerNavn()).isEqualTo(SØKERS_NAVN);
        assertThat(dokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(SØKERS_FNR));
        assertThat(dokumentdata.getFelles().getMottakerNavn()).isNull();
        assertThat(dokumentdata.getFelles().getBrevDato()).isEqualTo(formaterDatoNorsk(LocalDate.now()));
        assertThat(dokumentdata.getFelles().getHarVerge()).isEqualTo(true);
        assertThat(dokumentdata.getFelles().getErKopi()).isEqualTo(true);
        assertThat(dokumentdata.getFelles().getSaksnummer()).isEqualTo(SAKSNUMMER);
        assertThat(dokumentdata.getFelles().getYtelseType()).isEqualTo("FP");
        assertThat(dokumentdata.getFelles().getBehandlesAvKA()).isEqualTo(false);
        assertThat(dokumentdata.getFelles().getErUtkast()).isEqualTo(false);

        assertThat(dokumentdata.getOverskrift()).isEqualTo(OVERSKRIFT);
        assertThat(dokumentdata.getBrødtekst().getFritekst()).isEqualTo(BRØDTEKST_UT);
    }

    @Test
    public void skal_mappe_felter_for_fritekstbrev_fra_behandlingsresultatet_når_hendelsen_ikke_har_dem() {
        // Arrange
        Behandlingsresultat behandlingsresultat = Behandlingsresultat.builder()
                .medOverskrift(OVERSKRIFT)
                .medFritekstbrev(BRØDTEKST_INN)
                .build();
        Behandling behandling = standardBehandlingBuilder()
                .medBehandlingsresultat(behandlingsresultat)
                .build();
        DokumentFelles dokumentFelles = lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.JA, false);
        DokumentHendelse dokumentHendelse = lagStandardHendelseBuilder().medTittel(null).medFritekst(null).build();

        // Act
        FritekstbrevDokumentdata dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(dokumentdata.getOverskrift()).isEqualTo(OVERSKRIFT);
        assertThat(dokumentdata.getBrødtekst().getFritekst()).isEqualTo(BRØDTEKST_UT);
    }
}