package no.nav.foreldrepenger.melding.søknad;

import java.time.LocalDate;

public class Søknad {
    private LocalDate mottattDato;
    private LocalDate søknadsdato;
    private Object OppgittRettighet;

    public Søknad(LocalDate mottattDato) {
        this.mottattDato = mottattDato;
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
