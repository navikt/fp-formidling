package no.nav.foreldrepenger.melding.inntektarbeidytelse;

import java.time.LocalDate;
import java.util.List;

public record Inntektsmelding(String arbeidsgiverNavn, String arbeidsgiverReferanse, List<UtsettelsePeriode> utsettelsePerioder,
        LocalDate innsendingstidspunkt) {
}
