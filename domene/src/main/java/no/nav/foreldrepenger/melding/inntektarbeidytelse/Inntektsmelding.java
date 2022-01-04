package no.nav.foreldrepenger.melding.inntektarbeidytelse;

import java.time.LocalDate;

public record Inntektsmelding(String arbeidsgiverNavn, String arbeidsgiverReferanse, LocalDate innsendingstidspunkt) {
}
