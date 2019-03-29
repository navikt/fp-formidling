package no.nav.foreldrepenger.melding.datamapper.domene;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Test;

import no.nav.foreldrepenger.fpsak.dto.inntektarbeidytelse.InntektArbeidYtelseDto;
import no.nav.foreldrepenger.melding.inntektarbeidytelse.InntektArbeidYtelse;

public class IAYMapperTest {

    @Test
    public void skalKasteException_hvisDetIkkeFinnesInntektsmelding() {
        assertThatThrownBy(() -> IAYMapper.hentVillkårligInntektsmelding(new InntektArbeidYtelse(new InntektArbeidYtelseDto())))
                .isInstanceOf(IllegalStateException.class);
    }

}
