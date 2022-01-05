package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.ikkesøkt;

import java.util.Comparator;

import no.nav.foreldrepenger.fpformidling.inntektarbeidytelse.InntektArbeidYtelse;
import no.nav.foreldrepenger.fpformidling.inntektarbeidytelse.Inntektsmelding;

public class IAYMapper {

    public static Inntektsmelding hentNyesteInntektsmelding(InntektArbeidYtelse iay) {
        return iay.getInntektsmeldinger().stream()
                .max(Comparator.comparing(Inntektsmelding::innsendingstidspunkt))
                .orElseThrow(() -> new IllegalStateException("Finner ingen inntektsmelding"));
    }
}
