package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SAKSNUMMER;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SØKERS_FNR;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SØKERS_NAVN;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.VERGES_NAVN;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentData;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardHendelseBuilder;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.standardBehandling;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDatoNorsk;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentData;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;

@ExtendWith(MockitoExtension.class)
class IngenEndringDokumentdataMapperTest {

    private DokumentData dokumentData;

    private IngenEndringDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    void before() {
        dokumentData = lagStandardDokumentData(DokumentMalType.INGEN_ENDRING);
        dokumentdataMapper = new IngenEndringDokumentdataMapper();
    }

    @Test
    void skal_mappe_felter_for_brev_til_bruker() {
        // Arrange
        var behandling = standardBehandling();
        var dokumentFelles = lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.JA, false);
        var dokumentHendelse = lagStandardHendelseBuilder().build();

        // Act
        var ingenEndringDokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(ingenEndringDokumentdata.getFelles()).isNotNull();
        assertThat(ingenEndringDokumentdata.getFelles().getSøkerNavn()).isEqualTo(SØKERS_NAVN);
        assertThat(ingenEndringDokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(SØKERS_FNR));
        assertThat(ingenEndringDokumentdata.getFelles().getMottakerNavn()).isNull();
        assertThat(ingenEndringDokumentdata.getFelles().getBrevDato()).isEqualTo(formaterDatoNorsk(LocalDate.now()));
        assertThat(ingenEndringDokumentdata.getFelles().getHarVerge()).isTrue();
        assertThat(ingenEndringDokumentdata.getFelles().getErKopi()).isTrue();
        assertThat(ingenEndringDokumentdata.getFelles().getSaksnummer()).isEqualTo(SAKSNUMMER);
        assertThat(ingenEndringDokumentdata.getFelles().getYtelseType()).isEqualTo("FP");
        assertThat(ingenEndringDokumentdata.getFelles().getErUtkast()).isFalse();
    }

    @Test
    void skal_mappe_felter_for_brev_til_verge() {
        // Arrange
        var behandling = standardBehandling();
        var dokumentFelles = lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.NEI, true);
        var dokumentHendelse = lagStandardHendelseBuilder().build();

        // Act
        var ingenEndringDokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(ingenEndringDokumentdata.getFelles()).isNotNull();
        assertThat(ingenEndringDokumentdata.getFelles().getSøkerNavn()).isEqualTo(SØKERS_NAVN);
        assertThat(ingenEndringDokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(SØKERS_FNR));
        assertThat(ingenEndringDokumentdata.getFelles().getMottakerNavn()).isEqualTo(VERGES_NAVN);
        assertThat(ingenEndringDokumentdata.getFelles().getHarVerge()).isTrue();
        assertThat(ingenEndringDokumentdata.getFelles().getErKopi()).isFalse();
    }
}
