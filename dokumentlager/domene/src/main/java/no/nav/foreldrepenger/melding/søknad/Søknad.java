package no.nav.foreldrepenger.melding.søknad;

import java.time.LocalDate;

public class Søknad {
    private LocalDate mottattDato;
    private LocalDate søknadsdato;
    private Object OppgittRettighet;

    public Søknad(LocalDate mottattDato, LocalDate søknadsdato) {
        this.mottattDato = mottattDato;
        this.søknadsdato = søknadsdato;
    }


    public LocalDate getMottattDato() {
        return mottattDato;
    }

    public LocalDate getSøknadsdato() {
        return søknadsdato;
    }

    public Object getOppgittRettighet() {
        return OppgittRettighet;
    }
}
