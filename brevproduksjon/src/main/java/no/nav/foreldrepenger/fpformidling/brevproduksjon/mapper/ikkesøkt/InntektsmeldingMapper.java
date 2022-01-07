package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.ikkesøkt;

import java.util.Comparator;

import no.nav.foreldrepenger.fpformidling.inntektarbeidytelse.Inntektsmelding;
import no.nav.foreldrepenger.fpformidling.inntektarbeidytelse.Inntektsmeldinger;

public class InntektsmeldingMapper {

    public static Inntektsmelding hentNyesteInntektsmelding(Inntektsmeldinger iay) {
        return iay.getInntektsmeldinger().stream()
                .max(Comparator.comparing(Inntektsmelding::innsendingstidspunkt))
                .orElseThrow(() -> new IllegalStateException("Finner ingen inntektsmelding"));
    }
}
