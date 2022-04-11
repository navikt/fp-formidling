package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.soknad;

import java.time.LocalDate;

import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling.familiehendelse.SøknadType;

public class SoknadBackendDto {

    private SøknadType soknadType;
    private LocalDate mottattDato;
    private LocalDate soknadsdato;
    private OppgittRettighetDto oppgittRettighet;

    protected SoknadBackendDto() {
    }

    public SøknadType getSoknadType() {
        return soknadType;
    }

    public void setSoknadType(SøknadType soknadType) {
        this.soknadType = soknadType;
    }

    public LocalDate getMottattDato() {
        return mottattDato;
    }

    public void setMottattDato(LocalDate mottattDato) {
        this.mottattDato = mottattDato;
    }

    public LocalDate getSoknadsdato() {
        return soknadsdato;
    }

    public void setSoknadsdato(LocalDate soknadsdato) {
        this.soknadsdato = soknadsdato;
    }

    public OppgittRettighetDto getOppgittRettighet() {
        return oppgittRettighet;
    }

    public void setOppgittRettighet(OppgittRettighetDto oppgittRettighet) {
        this.oppgittRettighet = oppgittRettighet;
    }
}
