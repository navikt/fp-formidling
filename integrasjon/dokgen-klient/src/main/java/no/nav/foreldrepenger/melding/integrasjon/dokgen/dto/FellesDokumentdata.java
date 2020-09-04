package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto;

import java.time.LocalDate;

public class FellesDokumentdata {
    private String søkerNavn;
    private String søkerPersonnummer;
    private String fritekst;
    private LocalDate brevDato;

    public FellesDokumentdata(String søkerNavn, String søkerPersonnummer, String fritekst, LocalDate brevDato) {
        this.søkerNavn = søkerNavn;
        this.søkerPersonnummer = søkerPersonnummer;
        this.fritekst = fritekst;
        this.brevDato = brevDato;
    }

    public String getSøkerNavn() {
        return søkerNavn;
    }

    public String getSøkerPersonnummer() {
        return søkerPersonnummer;
    }

    public String getFritekst() {
        return fritekst;
    }

    public LocalDate getBrevDato() {
        return brevDato;
    }
}
