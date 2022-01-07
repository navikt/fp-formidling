package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.ikkesøkt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.inntektarbeidytelse.Inntektsmelding;
import no.nav.foreldrepenger.fpformidling.inntektarbeidytelse.Inntektsmeldinger;

public class InntektsmeldingMapperTest {

    @Test
    public void skal_kaste_exception_hvis_det_ikke_finnes_inntektsmelding() {
        assertThatThrownBy(() -> InntektsmeldingMapper.hentNyesteInntektsmelding(new Inntektsmeldinger(List.of())))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void skal_returnere_nyeste_inntektsmelding() {
        // Arrange
        Inntektsmelding inntektsmelding1 = new Inntektsmelding("Feil", "", LocalDate.now().minusDays(10));
        Inntektsmelding inntektsmelding2 = new Inntektsmelding("Fasit", "", LocalDate.now().minusDays(1));
        Inntektsmelding inntektsmelding3 = new Inntektsmelding("Feil", "", LocalDate.now().minusDays(4));
        var inntektsmeldinger = new Inntektsmeldinger(List.of(inntektsmelding1, inntektsmelding2, inntektsmelding3));

        // Act
        var inntektsmelding = InntektsmeldingMapper.hentNyesteInntektsmelding(inntektsmeldinger);

        // Assert
        assertThat(inntektsmelding.arbeidsgiverNavn()).isEqualTo("Fasit");
    }

}
