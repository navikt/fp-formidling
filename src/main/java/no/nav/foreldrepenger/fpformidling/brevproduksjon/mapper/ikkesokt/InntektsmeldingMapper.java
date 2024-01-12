package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.ikkesokt;

import java.util.Comparator;

import no.nav.foreldrepenger.fpformidling.domene.inntektarbeidytelse.Inntektsmelding;
import no.nav.foreldrepenger.fpformidling.domene.inntektarbeidytelse.Inntektsmeldinger;

public class InntektsmeldingMapper {

    private InntektsmeldingMapper() {
    }

    public static Inntektsmelding hentNyesteInntektsmelding(Inntektsmeldinger iay) {
        return iay.getInntektsmeldinger()
            .stream()
            .max(Comparator.comparing(Inntektsmelding::innsendingstidspunkt))
            .orElseThrow(() -> new IllegalStateException("Finner ingen inntektsmelding"));
    }
}
