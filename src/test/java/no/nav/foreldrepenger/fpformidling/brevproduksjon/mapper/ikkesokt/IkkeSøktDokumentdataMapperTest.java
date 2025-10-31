package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.ikkesokt;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SAKSNUMMER;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SØKERS_FNR;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SØKERS_NAVN;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.VERGES_NAVN;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.defaultBuilder;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardHendelseBuilder;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.inntektsmelding;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDatoNorsk;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.arbeidsgiver.ArbeidsgiverTjeneste;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FellesDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;

@ExtendWith(MockitoExtension.class)
class IkkeSøktDokumentdataMapperTest {

    private static final String ARBEIDSGIVER = "Arbeidsgiver AS";
    private static final String ARBEIDSGIVER_REF = "123456";
    private static final LocalDate INNSENDINGSTIDSPUNKT = LocalDate.now().minusDays(2);

    private final ArbeidsgiverTjeneste arbeidsgiverTjeneste = mock(ArbeidsgiverTjeneste.class);
    private IkkeSøktDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    void before() {
        when(arbeidsgiverTjeneste.hentArbeidsgiverNavn(any())).thenReturn(ARBEIDSGIVER);
        dokumentdataMapper = new IkkeSøktDokumentdataMapper(arbeidsgiverTjeneste);
    }

    @Test
    void skal_mappe_felter_for_brev_til_bruker() {
        // Arrange
        var behandling = opprettBehandling();
        var dokumentFelles = lagStandardDokumentFelles(DokumentFelles.Kopi.JA, false, FagsakYtelseType.FORELDREPENGER);
        var dokumentHendelse = lagStandardHendelseBuilder().build();

        // Act
        var ikkeSøktDokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(ikkeSøktDokumentdata.getFelles()).isNotNull();
        assertThat(ikkeSøktDokumentdata.getFelles().getSøkerNavn()).isEqualTo(SØKERS_NAVN);
        assertThat(ikkeSøktDokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(SØKERS_FNR));
        assertThat(ikkeSøktDokumentdata.getFelles().getMottakerNavn()).isNull();
        assertThat(ikkeSøktDokumentdata.getFelles().getBrevDato()).isEqualTo(formaterDatoNorsk(LocalDate.now()));
        assertThat(ikkeSøktDokumentdata.getFelles().getHarVerge()).isTrue();
        assertThat(ikkeSøktDokumentdata.getFelles().getErKopi()).isTrue();
        assertThat(ikkeSøktDokumentdata.getFelles().getSaksnummer()).isEqualTo(SAKSNUMMER);
        assertThat(ikkeSøktDokumentdata.getFelles().getYtelseType()).isEqualTo(FellesDokumentdata.YtelseType.FP);
        assertThat(ikkeSøktDokumentdata.getFelles().getErUtkast()).isFalse();

        assertThat(ikkeSøktDokumentdata.getArbeidsgiverNavn()).isEqualTo(ARBEIDSGIVER);
        assertThat(ikkeSøktDokumentdata.getMottattDato()).isEqualTo(formaterDatoNorsk(INNSENDINGSTIDSPUNKT));
    }

    @Test
    void skal_mappe_felter_for_brev_til_verge() {
        // Arrange
        var behandling = opprettBehandling();
        var dokumentFelles = lagStandardDokumentFelles(DokumentFelles.Kopi.NEI, true, FagsakYtelseType.FORELDREPENGER);
        var dokumentHendelse = lagStandardHendelseBuilder().build();

        // Act
        var ikkeSøktDokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(ikkeSøktDokumentdata.getFelles()).isNotNull();
        assertThat(ikkeSøktDokumentdata.getFelles().getSøkerNavn()).isEqualTo(SØKERS_NAVN);
        assertThat(ikkeSøktDokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(SØKERS_FNR));
        assertThat(ikkeSøktDokumentdata.getFelles().getMottakerNavn()).isEqualTo(VERGES_NAVN);
        assertThat(ikkeSøktDokumentdata.getFelles().getHarVerge()).isTrue();
        assertThat(ikkeSøktDokumentdata.getFelles().getErKopi()).isFalse();
    }

    private BrevGrunnlagDto opprettBehandling() {
        var inntektsmeldingData = inntektsmelding().arbeidsgiverReferanse(ARBEIDSGIVER_REF)
            .innsendingstidspunkt(
                LocalDateTime.of(INNSENDINGSTIDSPUNKT.getYear(), INNSENDINGSTIDSPUNKT.getMonth(), INNSENDINGSTIDSPUNKT.getDayOfMonth(), 0, 0))
            .build();

        return defaultBuilder().fagsakYtelseType(BrevGrunnlagDto.FagsakYtelseType.FORELDREPENGER)
            .inntektsmeldinger(List.of(inntektsmeldingData))
            .build();
    }
}
