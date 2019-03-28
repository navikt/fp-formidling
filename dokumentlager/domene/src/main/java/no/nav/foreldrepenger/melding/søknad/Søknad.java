package no.nav.foreldrepenger.melding.søknad;

import java.time.LocalDate;

import no.nav.foreldrepenger.fpsak.dto.soknad.SoknadDto;

public class Søknad {
    private LocalDate mottattDato;
    private LocalDate søknadsdato;
    private Object OppgittRettighet;

    public Søknad(SoknadDto dto) {
        this.søknadsdato = dto.getMottattDato();
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
