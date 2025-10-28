package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.arbeidsgiver.ArbeidsgiverTjeneste;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FellesDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;
import no.nav.foreldrepenger.kontrakter.fpsak.inntektsmeldinger.ArbeidsforholdInntektsmeldingerDto;

@ExtendWith(MockitoExtension.class)
class EtterlysInntektsmeldingDokumentdataMapperTest {

    public static final String ARBEIDSGIVER_NAVN = "Test Navn AS";
    private EtterlysInntektsmeldingDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    void setup() {
        var arbeidsgiverTjeneste = mock(ArbeidsgiverTjeneste.class);
        when(arbeidsgiverTjeneste.hentArbeidsgiverNavn(anyString())).thenReturn(ARBEIDSGIVER_NAVN);
        dokumentdataMapper = new EtterlysInntektsmeldingDokumentdataMapper(arbeidsgiverTjeneste);
    }

    @Test
    void test_map_fagtype_foreldrepenger() {
        var imStatus = new ArbeidsforholdInntektsmeldingerDto.ArbeidsforholdInntektsmeldingDto("12345679", BigDecimal.valueOf(100), false);
        var inntektsmeldingerStatus = new ArbeidsforholdInntektsmeldingerDto(List.of(imStatus));
        var behandling = DatamapperTestUtil.defaultBuilder()
            .inntektsmeldingerStatus(inntektsmeldingerStatus)
            .fagsakYtelseType(BrevGrunnlagDto.FagsakYtelseType.FORELDREPENGER)
            .sisteSøknadMottattDato(LocalDate.now())
            .build();
        var dokumentFelles = DatamapperTestUtil.lagStandardDokumentFelles(FagsakYtelseType.FORELDREPENGER);
        var dokumentHendelse = DatamapperTestUtil.standardDokumenthendelse();


        var dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        assertThat(dokumentdata.getFelles().getYtelseType()).isEqualTo(FellesDokumentdata.YtelseType.FP);
        assertThat(dokumentdata.getSøknadDato()).isNotEmpty();
        assertThat(dokumentdata.getInntektsmeldingerStatus()).hasSize(1);
        assertThat(dokumentdata.getInntektsmeldingerStatus().getFirst().erInntektsmeldingMottatt()).isEqualTo(imStatus.erInntektsmeldingMottatt());
        assertThat(dokumentdata.getInntektsmeldingerStatus().getFirst().arbeidsgiverIdent()).isEqualTo(imStatus.arbeidsgiverIdent());
        assertThat(dokumentdata.getInntektsmeldingerStatus().getFirst().arbeidsgiverNavn()).isEqualTo(ARBEIDSGIVER_NAVN);
        assertThat(dokumentdata.getInntektsmeldingerStatus().getFirst().stillingsprosent()).isEqualTo(imStatus.stillingsprosent());
    }

    @Test
    void test_map_fagtype_svangerskapspenger() {
        var imStatus = new ArbeidsforholdInntektsmeldingerDto.ArbeidsforholdInntektsmeldingDto("12345679", BigDecimal.valueOf(100), false);
        var inntektsmeldingerStatus = List.of(imStatus);
        var behandling = DatamapperTestUtil.defaultBuilder()
            .fagsakYtelseType(BrevGrunnlagDto.FagsakYtelseType.SVANGERSKAPSPENGER)
            .behandlingType(BrevGrunnlagDto.BehandlingType.FØRSTEGANGSSØKNAD)
            .inntektsmeldingerStatus(new ArbeidsforholdInntektsmeldingerDto(inntektsmeldingerStatus))
            .sisteSøknadMottattDato(LocalDate.now())
            .build();
        var dokumentFelles = DatamapperTestUtil.lagStandardDokumentFelles(FagsakYtelseType.SVANGERSKAPSPENGER);
        var dokumentHendelse = DatamapperTestUtil.standardDokumenthendelse();
        var dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        assertThat(dokumentdata.getFelles().getYtelseType()).isEqualTo(FellesDokumentdata.YtelseType.SVP);
        assertThat(dokumentdata.getSøknadDato()).isNotEmpty();
        assertThat(dokumentdata.getInntektsmeldingerStatus()).hasSize(inntektsmeldingerStatus.size());
        assertThat(dokumentdata.getInntektsmeldingerStatus().getFirst().erInntektsmeldingMottatt()).isEqualTo(imStatus.erInntektsmeldingMottatt());
        assertThat(dokumentdata.getInntektsmeldingerStatus().getFirst().arbeidsgiverIdent()).isEqualTo(imStatus.arbeidsgiverIdent());
        assertThat(dokumentdata.getInntektsmeldingerStatus().getFirst().arbeidsgiverNavn()).isEqualTo(ARBEIDSGIVER_NAVN);
        assertThat(dokumentdata.getInntektsmeldingerStatus().getFirst().stillingsprosent()).isEqualTo(imStatus.stillingsprosent());
    }

}
