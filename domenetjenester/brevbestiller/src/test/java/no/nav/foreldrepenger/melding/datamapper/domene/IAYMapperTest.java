package no.nav.foreldrepenger.melding.datamapper.domene;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.melding.inntektarbeidytelse.InntektArbeidYtelse;

public class IAYMapperTest {

    @Test
    public void skalKasteException_hvisDetIkkeFinnesInntektsmelding() {
        assertThatThrownBy(() -> IAYMapper.hentVillkårligInntektsmelding(InntektArbeidYtelse.ny().build()))
                .isInstanceOf(IllegalStateException.class);
    }

}
