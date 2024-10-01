package no.nav.foreldrepenger.fpformidling.domene.inntektarbeidytelse;

import java.math.BigDecimal;

public record ArbeidsforholdInntektsmelding(String arbeidsgiverIdent, String arbeidsgiverNavn, BigDecimal stillingsprosent,
                                            boolean erInntektsmeldingMottatt) {
    @Override
    public String toString() {
        return "ArbeidsforholdInntektsmelding{" + "arbeidsgiverIdent=" + arbeidsgiverIdent + ", arbeidsgiverNavn=" + arbeidsgiverNavn + ", stillingsprosent="
            + stillingsprosent + ", erInntektsmeldingMottatt=" + erInntektsmeldingMottatt + '}';
    }
}
