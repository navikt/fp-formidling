package no.nav.foreldrepenger.melding.søknad;

import java.time.LocalDate;

import no.nav.foreldrepenger.melding.ytelsefordeling.OppgittRettighet;

public class Søknad {
    private LocalDate mottattDato;
    private LocalDate søknadsdato;
    private OppgittRettighet oppgittRettighet;

    public Søknad(LocalDate mottattDato, LocalDate søknadsdato, OppgittRettighet oppgittRettighet) {
        this.mottattDato = mottattDato;
        this.søknadsdato = søknadsdato;
        this.oppgittRettighet = oppgittRettighet;
    }


    public LocalDate getMottattDato() {
        return mottattDato;
    }

    public LocalDate getSøknadsdato() {
        return søknadsdato;
    }

    public OppgittRettighet getOppgittRettighet() {
        return oppgittRettighet;
    }
}
