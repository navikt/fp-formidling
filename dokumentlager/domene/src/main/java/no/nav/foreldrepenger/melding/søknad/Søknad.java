package no.nav.foreldrepenger.melding.søknad;

import java.time.LocalDate;

import no.nav.foreldrepenger.melding.ytelsefordeling.OppgittRettighet;

public record Søknad(LocalDate mottattDato, LocalDate søknadsdato, OppgittRettighet oppgittRettighet) {
}
