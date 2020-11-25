package no.nav.foreldrepenger.fpsak.dto.soknad;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.SøknadType;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SoknadAdopsjonDto.class),
        @JsonSubTypes.Type(value = SoknadFodselDto.class)
})
public abstract class SoknadDto {

    private SøknadType soknadType;
    private LocalDate mottattDato;
    private LocalDate soknadsdato;
    private OppgittRettighetDto oppgittRettighet;

    protected SoknadDto() {
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
