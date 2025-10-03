package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.inntektarbeidytelse.ArbeidsforholdInntektsmelding;

@ExtendWith(MockitoExtension.class)
class EtterlysInntektsmeldingDokumentdataMapperTest {

    @Mock
    private DomeneobjektProvider domeneobjektProvider;

    private EtterlysInntektsmeldingDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    void setup() {
        dokumentdataMapper = new EtterlysInntektsmeldingDokumentdataMapper(domeneobjektProvider);
    }

    @Test
    void test_map_fagtype_foreldrepenger() {
        var behandling = DatamapperTestUtil.standardForeldrepengerBehandling();
        var dokumentFelles = DatamapperTestUtil.lagStandardDokumentFelles();
        var dokumentHendelse = DatamapperTestUtil.standardDokumenthendelse();

        var inntektsmeldingerStatus = List.of(new ArbeidsforholdInntektsmelding("12345679", "ArbeidsgiverNavn", BigDecimal.valueOf(100), false));
        when(domeneobjektProvider.hentArbeidsforholdInntektsmeldingerStatus(behandling)).thenReturn(inntektsmeldingerStatus);

        var dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        assertThat(dokumentdata.getFelles().getYtelseType()).isEqualTo(FagsakYtelseType.FORELDREPENGER.getKode());
        assertThat(dokumentdata.getSøknadDato()).isNotEmpty();
        assertThat(dokumentdata.getInntektsmeldingerStatus()).isEqualTo(inntektsmeldingerStatus);
    }

    @Test
    void test_map_fagtype_svangerskapspenger() {
        var behandling = DatamapperTestUtil.standardSvangerskapspengerBehandling();
        var dokumentFelles = DatamapperTestUtil.lagStandardDokumentFelles();
        var dokumentHendelse = DatamapperTestUtil.lagStandardHendelseBuilder().build();
        var inntektsmeldingerStatus = List.of(new ArbeidsforholdInntektsmelding("12345679", "ArbeidsgiverNavn", BigDecimal.valueOf(100), false));
        when(domeneobjektProvider.hentArbeidsforholdInntektsmeldingerStatus(behandling)).thenReturn(inntektsmeldingerStatus);
        var dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        assertThat(dokumentdata.getFelles().getYtelseType()).isEqualTo(FagsakYtelseType.SVANGERSKAPSPENGER.getKode());
        assertThat(dokumentdata.getSøknadDato()).isNotEmpty();
        assertThat(dokumentdata.getInntektsmeldingerStatus()).isEqualTo(inntektsmeldingerStatus);
    }

}
