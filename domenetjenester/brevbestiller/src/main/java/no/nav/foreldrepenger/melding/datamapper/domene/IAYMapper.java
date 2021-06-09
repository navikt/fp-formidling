package no.nav.foreldrepenger.melding.datamapper.domene;

import java.util.Comparator;

import no.nav.foreldrepenger.melding.inntektarbeidytelse.InntektArbeidYtelse;
import no.nav.foreldrepenger.melding.inntektarbeidytelse.Inntektsmelding;

public class IAYMapper {

    public static Inntektsmelding hentNyesteInntektsmelding(InntektArbeidYtelse iay) {
        return iay.getInntektsmeldinger().stream()
                .max(Comparator.comparing(Inntektsmelding::innsendingstidspunkt))
                .orElseThrow(() -> new IllegalStateException("Finner ingen inntektsmelding"));
    }
}
