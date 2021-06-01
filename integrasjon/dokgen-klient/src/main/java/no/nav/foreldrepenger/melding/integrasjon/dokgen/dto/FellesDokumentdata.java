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
    private String mottakerNavn;
    private String ytelseType;

    public static Builder ny() { return new Builder(); }

    public String getSøkerNavn() { return søkerNavn; }

    public String getSøkerPersonnummer() { return søkerPersonnummer; }

    public String getFritekst() { return fritekst; }

    public String getBrevDato() { return brevDato; }

    public boolean getErAutomatiskBehandlet () { return erAutomatiskBehandlet; }

    public boolean getErKopi() { return erKopi; }

    public boolean getHarVerge() { return harVerge; }

    public String getSaksnummer() { return saksnummer; }

    public String getMottakerNavn() { return mottakerNavn; }

    public String getYtelseType() {
        return ytelseType;
    }

    // Til bruk når alternativt ulansert brev skal genereres i testfasen av innvilgelse FP
    public void anonymiser() {
        this.søkerNavn = søkerNavn.substring(0, 3) + " ANONYMISERT";
        this.søkerPersonnummer = søkerPersonnummer.substring(0, 4) + "** *****";
        if (this.mottakerNavn != null) {
            this.mottakerNavn = mottakerNavn.substring(0, 3) + " ANONYMISERT";
        }
    }

    public static class Builder {
        private FellesDokumentdata kladd;

       private Builder() {
            this.kladd = new FellesDokumentdata();
        }

        public Builder medSøkerNavn(String søkerNavn) {
            this.kladd.søkerNavn = søkerNavn;
            return this;
        }

        public Builder medSøkerPersonnummer(String søkerPersonnummer) {
            this.kladd.søkerPersonnummer = søkerPersonnummer;
            return this;
        }

        public Builder medFritekst(String fritekst) {
            this.kladd.fritekst = fritekst;
            return this;
        }

        public Builder medBrevDato(String brevDato) {
            this.kladd.brevDato = brevDato;
            return this;
        }

        public Builder medErAutomatiskBehandlet(boolean erAutomatiskBehandlet) {
            this.kladd.erAutomatiskBehandlet = erAutomatiskBehandlet;
            return this;
        }

        public Builder medErKopi(boolean erKopi) {
            this.kladd.erKopi = erKopi;
            return this;
        }

        public Builder medHarVerge(boolean harVerge) {
            this.kladd.harVerge = harVerge;
            return this;
        }

        public Builder medSaksnummer(String saksnummer) {
            this.kladd.saksnummer = saksnummer;
            return this;
        }

        public Builder medMottakerNavn(String mottakerNavn) {
            this.kladd.mottakerNavn = mottakerNavn;
            return this;
        }

        public FellesDokumentdata.Builder medYtelseType(String ytelseType) {
            this.kladd.ytelseType = ytelseType;
            return this;
        }

        public FellesDokumentdata build() {
            return this.kladd;
        }
    }
}
