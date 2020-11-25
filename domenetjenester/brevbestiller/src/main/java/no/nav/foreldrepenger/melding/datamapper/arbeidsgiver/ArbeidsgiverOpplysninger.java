package no.nav.foreldrepenger.melding.datamapper.arbeidsgiver;

import java.time.LocalDate;

import no.nav.foreldrepenger.melding.typer.AktørId;


public class ArbeidsgiverOpplysninger {

    private final String referanse;
    private final String identifikator;
    private final String navn;
    private LocalDate fødselsdato; // Fødselsdato for privatperson som arbeidsgiver

    public ArbeidsgiverOpplysninger(AktørId aktørId, String identifikator, String navn, LocalDate fødselsdato) {
        this.referanse = aktørId.getId();
        this.identifikator = identifikator;
        this.navn = navn;
        this.fødselsdato = fødselsdato;
    }

    public ArbeidsgiverOpplysninger(String identifikator, String navn) {
        this.referanse = identifikator;
        this.identifikator = identifikator;
        this.navn = navn;
    }

    public String getReferanse() {
        return referanse;
    }

    public String getIdentifikator() {
        return identifikator;
    }

    public String getNavn() {
        return navn;
    }

    public LocalDate getFødselsdato() {
        return fødselsdato;
    }
}
