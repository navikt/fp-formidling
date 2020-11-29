package no.nav.foreldrepenger.melding.aktør;

import java.util.Objects;

import no.nav.foreldrepenger.melding.typer.PersonIdent;

public class Adresseinfo {

    private boolean registrertDød;
    private AdresseType gjeldendePostadresseType;

    //For mapping
    private PersonIdent personIdent;
    private String mottakerNavn;
    private String adresselinje1;
    private String adresselinje2;
    private String adresselinje3;
    private String adresselinje4;
    private String land;
    private String postNr;
    private String poststed;

    private Adresseinfo() {
    }

    public String getMottakerNavn() {
        return mottakerNavn;
    }

    public String getAdresselinje1() {
        return adresselinje1;
    }

    public String getAdresselinje2() {
        return adresselinje2;
    }

    public String getAdresselinje3() {
        return adresselinje3;
    }

    public String getAdresselinje4() {
        return adresselinje4;
    }

    public String getPostNr() {
        return postNr;
    }

    public String getPoststed() {
        return poststed;
    }

    public String getLand() {
        return land;
    }

    public PersonIdent getPersonIdent() {
        return personIdent;
    }

    public AdresseType getGjeldendePostadresseType() {
        return gjeldendePostadresseType;
    }

    public boolean isRegistrertDød() {
        return registrertDød;
    }

    public static class Builder {
        private Adresseinfo kladd;

        public Builder(AdresseType gjeldende, PersonIdent fnr, String mottakerNavn, boolean registrertDød) {
            this.kladd = new Adresseinfo();
            this.kladd.gjeldendePostadresseType = gjeldende;
            this.kladd.personIdent = fnr;
            this.kladd.mottakerNavn = mottakerNavn;
            this.kladd.registrertDød = registrertDød;
        }

        public Builder medAdresselinje1(String adresselinje1) {
            this.kladd.adresselinje1 = adresselinje1;
            return this;
        }

        public Builder medAdresselinje2(String adresselinje2) {
            this.kladd.adresselinje2 = adresselinje2;
            return this;
        }

        public Builder medAdresselinje3(String adresselinje3) {
            this.kladd.adresselinje3 = adresselinje3;
            return this;
        }

        public Builder medAdresselinje4(String adresselinje4) {
            this.kladd.adresselinje4 = adresselinje4;
            return this;
        }

        public Builder medPostNr(String postNr) {
            this.kladd.postNr = postNr;
            return this;
        }

        public Builder medPoststed(String poststed) {
            this.kladd.poststed = poststed;
            return this;
        }

        public Builder medLand(String land) {
            this.kladd.land = land;
            return this;
        }

        public Adresseinfo build() {
            verifyStateForBuild();
            return this.kladd;
        }

        private void verifyStateForBuild() {
            Objects.requireNonNull(this.kladd.mottakerNavn, "mottakerNavn");
            Objects.requireNonNull(this.kladd.personIdent, "fnr");
            Objects.requireNonNull(this.kladd.gjeldendePostadresseType, "gjeldendePostadresseType");
        }
    }
}
