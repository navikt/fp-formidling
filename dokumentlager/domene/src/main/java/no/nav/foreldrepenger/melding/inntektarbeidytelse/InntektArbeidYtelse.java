package no.nav.foreldrepenger.melding.inntektarbeidytelse;

import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.fpsak.dto.inntektarbeidytelse.InntektArbeidYtelseDto;

public class InntektArbeidYtelse {

    List<Inntektsmelding> inntektsmeldinger = new ArrayList<>();

    public InntektArbeidYtelse(InntektArbeidYtelseDto dto) {
        dto.getInntektsmeldinger().forEach(im -> inntektsmeldinger.add(new Inntektsmelding(im)));
        this.inntektsmeldinger = inntektsmeldinger;
    }

    public List<Inntektsmelding> getInntektsmeldinger() {
        return inntektsmeldinger;
    }
}
