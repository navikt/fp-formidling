package no.nav.foreldrepenger.fpformidling.søknad;

import java.time.LocalDate;

import no.nav.foreldrepenger.fpformidling.ytelsefordeling.OppgittRettighet;

public record Søknad(LocalDate mottattDato, LocalDate søknadsdato, OppgittRettighet oppgittRettighet) {
}
