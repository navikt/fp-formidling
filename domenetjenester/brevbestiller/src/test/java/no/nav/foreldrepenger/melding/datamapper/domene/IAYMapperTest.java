package no.nav.foreldrepenger.melding.datamapper.domene;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.melding.inntektarbeidytelse.InntektArbeidYtelse;
import no.nav.foreldrepenger.melding.inntektarbeidytelse.Inntektsmelding;

public class IAYMapperTest {

    @Test
    public void skal_kaste_exception_hvis_det_ikke_finnes_inntektsmelding() {
        assertThatThrownBy(() -> IAYMapper.hentNyesteInntektsmelding(InntektArbeidYtelse.ny().build()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void skal_returnere_nyeste_inntektsmelding() {
        // Arrange
        Inntektsmelding inntektsmelding1 = new Inntektsmelding("Feil", "", LocalDate.now().minusDays(10));
        Inntektsmelding inntektsmelding2 = new Inntektsmelding("Fasit", "", LocalDate.now().minusDays(1));
        Inntektsmelding inntektsmelding3 = new Inntektsmelding("Feil", "", LocalDate.now().minusDays(4));
        InntektArbeidYtelse inntektArbeidYtelse = InntektArbeidYtelse.ny()
                .medInntektsmeldinger(List.of(inntektsmelding1, inntektsmelding2, inntektsmelding3))
                .build();

        // Act
        Inntektsmelding inntektsmelding = IAYMapper.hentNyesteInntektsmelding(inntektArbeidYtelse);

        // Assert
        assertThat(inntektsmelding.arbeidsgiverNavn()).isEqualTo("Fasit");
    }

}
