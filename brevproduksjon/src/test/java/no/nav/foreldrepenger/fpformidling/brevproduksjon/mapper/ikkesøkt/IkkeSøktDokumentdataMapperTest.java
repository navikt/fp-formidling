package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.ikkesøkt;

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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentData;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.inntektarbeidytelse.Inntektsmelding;
import no.nav.foreldrepenger.fpformidling.inntektarbeidytelse.Inntektsmeldinger;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;

@ExtendWith(MockitoExtension.class)
public class IkkeSøktDokumentdataMapperTest {

    private static final String ARBEIDSGIVER = "Arbeidsgiver AS";
    private static final LocalDate INNSENDINGSTIDSPUNKT = LocalDate.now().minusDays(2);

    @Mock
    private DomeneobjektProvider domeneobjektProvider = mock(DomeneobjektProvider.class);

    private DokumentData dokumentData;

    private IkkeSøktDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    public void before() {
        dokumentData = lagStandardDokumentData(DokumentMalType.INNHENTE_OPPLYSNINGER);
        dokumentdataMapper = new IkkeSøktDokumentdataMapper(domeneobjektProvider);

        var inntektsmelding = new Inntektsmelding(ARBEIDSGIVER, "", INNSENDINGSTIDSPUNKT);
        var inntektsmeldinger = new Inntektsmeldinger(List.of(inntektsmelding));
        when(domeneobjektProvider.hentInntektsmeldinger(any(Behandling.class))).thenReturn(inntektsmeldinger);
    }

    @Test
    public void skal_mappe_felter_for_brev_til_bruker() {
        // Arrange
        var behandling = standardBehandling();
        var dokumentFelles = lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.JA, false);
        var dokumentHendelse = lagStandardHendelseBuilder().build();

        // Act
        var ikkeSøktDokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(ikkeSøktDokumentdata.getFelles()).isNotNull();
        assertThat(ikkeSøktDokumentdata.getFelles().getSøkerNavn()).isEqualTo(SØKERS_NAVN);
        assertThat(ikkeSøktDokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(SØKERS_FNR));
        assertThat(ikkeSøktDokumentdata.getFelles().getMottakerNavn()).isNull();
        assertThat(ikkeSøktDokumentdata.getFelles().getBrevDato()).isEqualTo(formaterDatoNorsk(LocalDate.now()));
        assertThat(ikkeSøktDokumentdata.getFelles().getHarVerge()).isEqualTo(true);
        assertThat(ikkeSøktDokumentdata.getFelles().getErKopi()).isEqualTo(true);
        assertThat(ikkeSøktDokumentdata.getFelles().getSaksnummer()).isEqualTo(SAKSNUMMER);
        assertThat(ikkeSøktDokumentdata.getFelles().getYtelseType()).isEqualTo("FP");
        assertThat(ikkeSøktDokumentdata.getFelles().getBehandlesAvKA()).isEqualTo(false);
        assertThat(ikkeSøktDokumentdata.getFelles().getErUtkast()).isEqualTo(false);

        assertThat(ikkeSøktDokumentdata.getArbeidsgiverNavn()).isEqualTo(ARBEIDSGIVER);
        assertThat(ikkeSøktDokumentdata.getMottattDato()).isEqualTo(formaterDatoNorsk(INNSENDINGSTIDSPUNKT));
    }

    @Test
    public void skal_mappe_felter_for_brev_til_verge() {
        // Arrange
        var behandling = standardBehandling();
        var dokumentFelles = lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.NEI, true);
        var dokumentHendelse = lagStandardHendelseBuilder().build();

        // Act
        var ikkeSøktDokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(ikkeSøktDokumentdata.getFelles()).isNotNull();
        assertThat(ikkeSøktDokumentdata.getFelles().getSøkerNavn()).isEqualTo(SØKERS_NAVN);
        assertThat(ikkeSøktDokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(SØKERS_FNR));
        assertThat(ikkeSøktDokumentdata.getFelles().getMottakerNavn()).isEqualTo(VERGES_NAVN);
        assertThat(ikkeSøktDokumentdata.getFelles().getHarVerge()).isEqualTo(true);
        assertThat(ikkeSøktDokumentdata.getFelles().getErKopi()).isEqualTo(false);
    }
}
