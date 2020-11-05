package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto;

public class FellesDokumentdata {
    private String søkerNavn;
    private String søkerPersonnummer;
    private String fritekst;
    private String brevDato;
    private boolean erAutomatiskBehandlet;
    private boolean erKopi;
    private boolean harVerge;
    private String saksnummer;

    public FellesDokumentdata(Builder builder) {
        this.søkerNavn = builder.søkerNavn;
        this.søkerPersonnummer = builder.søkerPersonnummer;
        this.fritekst = builder.fritekst;
        this.brevDato = builder.brevDato;
        this.erAutomatiskBehandlet = builder.erAutomatiskBehandlet;
        this.erKopi = builder.erKopi;
        this.harVerge = builder.harVerge;
        this.saksnummer = builder.saksnummer;
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

    public String getBrevDato() {
        return brevDato;
    }

    public boolean getErAutomatiskBehandlet () { return erAutomatiskBehandlet; }

    public boolean getErKopi() { return erKopi; }

    public boolean getHarVerge() { return harVerge; }

    public String getSaksnummer() { return saksnummer; }

    public static class Builder {
        private String søkerNavn;
        private String søkerPersonnummer;
        private String fritekst;
        private String brevDato;
        private boolean erAutomatiskBehandlet;
        private boolean erKopi;
        private boolean harVerge;
        private String saksnummer;

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

        public FellesDokumentdata.Builder brevDato(String brevDato) {
            this.brevDato = brevDato;
            return this;
        }

        public FellesDokumentdata.Builder erAutomatiskBehandlet(boolean erAutomatiskBehandlet) {
            this.erAutomatiskBehandlet = erAutomatiskBehandlet;
            return this;
        }

        public FellesDokumentdata.Builder erKopi(boolean erKopi) {
            this.erKopi = erKopi;
            return this;
        }

        public FellesDokumentdata.Builder harVerge(boolean harVerge) {
            this.harVerge = harVerge;
            return this;
        }

        public FellesDokumentdata.Builder saksnummer(String saksnummer) {
            this.saksnummer = saksnummer;
            return this;
        }

        public FellesDokumentdata build() {
            return new FellesDokumentdata(this);
        }
    }
}
