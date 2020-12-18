package no.nav.foreldrepenger.melding.datamapper.domene;

import no.nav.foreldrepenger.melding.inntektarbeidytelse.InntektArbeidYtelse;
import no.nav.foreldrepenger.melding.inntektarbeidytelse.Inntektsmelding;

public class IAYMapper {

    public static Inntektsmelding hentVillkårligInntektsmelding(InntektArbeidYtelse iay) {
        return iay.getInntektsmeldinger().stream().findAny().orElseThrow(() -> {
            throw new IllegalStateException("Finner ingen inntektsmelding");
        });
    }
}
