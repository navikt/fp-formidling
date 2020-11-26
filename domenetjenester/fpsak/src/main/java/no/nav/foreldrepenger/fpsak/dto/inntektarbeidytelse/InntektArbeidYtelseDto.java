package no.nav.foreldrepenger.fpsak.dto.inntektarbeidytelse;

import java.util.Collections;
import java.util.List;

public class InntektArbeidYtelseDto {

    private List<InntektsmeldingDto> inntektsmeldinger = Collections.emptyList();

    public void setInntektsmeldinger(List<InntektsmeldingDto> inntektsmeldinger) {
        this.inntektsmeldinger = inntektsmeldinger;
    }

    public List<InntektsmeldingDto> getInntektsmeldinger() {
        return inntektsmeldinger;
    }

}
