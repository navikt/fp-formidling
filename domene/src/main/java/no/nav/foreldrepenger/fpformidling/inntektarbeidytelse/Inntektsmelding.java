package no.nav.foreldrepenger.fpformidling.inntektarbeidytelse;

import java.time.LocalDate;

public record Inntektsmelding(String arbeidsgiverNavn, String arbeidsgiverReferanse, LocalDate innsendingstidspunkt) {
}
