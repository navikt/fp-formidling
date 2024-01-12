package no.nav.foreldrepenger.fpformidling.domene.inntektarbeidytelse;

import java.util.List;

public record Inntektsmeldinger(List<Inntektsmelding> inntektsmeldinger) {

    public List<Inntektsmelding> getInntektsmeldinger() {
        return inntektsmeldinger != null ? inntektsmeldinger : List.of();
    }

}
