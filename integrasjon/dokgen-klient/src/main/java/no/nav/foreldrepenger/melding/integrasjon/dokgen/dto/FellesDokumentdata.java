package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto;

import java.time.LocalDate;

public class FellesDokumentdata {
    private String søkerNavn;
    private String søkerPersonnummer;
    private String fritekst;
    private LocalDate brevDato;

    public FellesDokumentdata(Builder builder) {
        this.søkerNavn = builder.søkerNavn;
        this.søkerPersonnummer = builder.søkerPersonnummer;
        this.fritekst = builder.fritekst;
        this.brevDato = builder.brevDato;
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

    public static class Builder {
        private String søkerNavn;
        private String søkerPersonnummer;
        private String fritekst;
        private LocalDate brevDato;

        public FellesDokumentdata.Builder søkerNavn(String søkerNavn) {
            this.søkerNavn = søkerNavn;
            return this;
        }

        public FellesDokumentdata.Builder søkerPersonnummer(String søkerPersonnummer) {
            this.søkerPersonnummer = søkerPersonnummer;
            return this;
        }

        public FellesDokumentdata.Builder fritekst(String fritekst) {
            this.fritekst = fritekst;
            return this;
        }

        public FellesDokumentdata.Builder brevDato(LocalDate brevDato) {
            this.brevDato = brevDato;
            return this;
        }

        public FellesDokumentdata build() {
            return new FellesDokumentdata(this);
        }
    }
}
