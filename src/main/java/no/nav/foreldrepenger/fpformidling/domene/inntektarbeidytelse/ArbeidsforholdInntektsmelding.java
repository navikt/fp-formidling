package no.nav.foreldrepenger.fpformidling.domene.inntektarbeidytelse;

import java.math.BigDecimal;

public record ArbeidsforholdInntektsmelding(String arbeidsgiverIdent, String arbeidsgiverNavn, BigDecimal stillingsprosent,
                                            boolean erInntektsmeldingMottatt) {

}
