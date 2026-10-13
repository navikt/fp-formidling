package no.nav.foreldrepenger.fpformidling.domene.inntektarbeidytelse;

public record ArbeidsforholdInntektsmelding(String arbeidsgiverIdent, String arbeidsgiverNavn, boolean erInntektsmeldingMottatt) {
    @Override
    public String toString() {
        return "ArbeidsforholdInntektsmelding{" + "arbeidsgiverIdent=" + arbeidsgiverIdent + ", arbeidsgiverNavn=" + arbeidsgiverNavn
            + ", erInntektsmeldingMottatt=" + erInntektsmeldingMottatt + '}';
    }
}
