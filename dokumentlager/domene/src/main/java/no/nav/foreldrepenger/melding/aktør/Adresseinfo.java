package no.nav.foreldrepenger.melding.aktør;

import java.time.LocalDate;
import java.util.Objects;

import no.nav.foreldrepenger.melding.typer.PersonIdent;

public class Adresseinfo {

    private boolean registrertDød;
    private AdresseType gjeldendePostadresseType;

    //For mapping
    private PersonIdent personIdent;
    private String mottakerNavn;
    private String matrikkelId;
    private String adresselinje1;
    private String adresselinje2;
    private String adresselinje3;
    private String adresselinje4;
    private String land;
    private String postNr;
    private String poststed;

    private LocalDate gyldigFom;

    private Adresseinfo() {
    }

    public String getMottakerNavn() {
        return mottakerNavn;
    }

    public String getMatrikkelId() {
        return matrikkelId;
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

    public LocalDate getGyldigFom() {
        return gyldigFom;
    }

    public static Adresseinfo.Builder builder(AdresseType adresseType) {
        return new Adresseinfo.Builder(adresseType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Adresseinfo that = (Adresseinfo) o;
        return gjeldendePostadresseType == that.gjeldendePostadresseType &&
                Objects.equals(matrikkelId, that.matrikkelId) &&
                Objects.equals(adresselinje1, that.adresselinje1) &&
                Objects.equals(adresselinje2, that.adresselinje2) &&
                Objects.equals(adresselinje3, that.adresselinje3) &&
                Objects.equals(adresselinje4, that.adresselinje4) &&
                Objects.equals(postNr, that.postNr) &&
                Objects.equals(poststed, that.poststed) &&
                Objects.equals(land, that.land);
    }


    public static boolean erLikeNokAdresser(Adresseinfo a1, Adresseinfo a2) {
        return a1.gjeldendePostadresseType == a2.gjeldendePostadresseType &&
                Objects.equals(a1.matrikkelId, a2.matrikkelId) &&
                Objects.equals(a1.postNr, a2.postNr) &&
                (Objects.equals(a1.land, a2.land) || a1.land == null || a2.land == null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gjeldendePostadresseType, matrikkelId, adresselinje1, adresselinje2, adresselinje3, adresselinje4, postNr, poststed, land);
    }

    public static class Builder {
        private Adresseinfo kladd;

        public Builder(AdresseType gjeldende) {
            this.kladd = new Adresseinfo();
            this.kladd.gjeldendePostadresseType = gjeldende;
        }

        public Builder(AdresseType gjeldende, PersonIdent fnr, String mottakerNavn, boolean registrertDød) {
            this.kladd = new Adresseinfo();
            this.kladd.gjeldendePostadresseType = gjeldende;
            this.kladd.personIdent = fnr;
            this.kladd.mottakerNavn = mottakerNavn;
            this.kladd.registrertDød = registrertDød;
        }


        public Builder medMatrikkelId(String matrikkelId) {
            this.kladd.matrikkelId = matrikkelId;
            return this;
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

        public Builder medGyldigFom(LocalDate gyldigFom) {
            this.kladd.gyldigFom = gyldigFom;
            return this;
        }

        public Adresseinfo build() {
            verifyStateForBuild();
            return this.kladd;
        }

        public Adresseinfo buildTemporary() {
            return this.kladd;
        }

        private void verifyStateForBuild() {
            Objects.requireNonNull(this.kladd.mottakerNavn, "mottakerNavn");
            Objects.requireNonNull(this.kladd.personIdent, "fnr");
            Objects.requireNonNull(this.kladd.gjeldendePostadresseType, "gjeldendePostadresseType");
        }
    }
}
