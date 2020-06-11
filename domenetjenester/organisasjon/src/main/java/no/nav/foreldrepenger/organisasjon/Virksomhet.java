package no.nav.foreldrepenger.organisasjon;

import java.util.Objects;


public class Virksomhet {

    private String orgnr;
    private String navn;
    private String adresselinje1;
    private String adresselinje2;
    private String adresselinje3;
    private String landkode;
    private String postNr;
    private String poststed;

    public String getOrgnr() {
        return orgnr;
    }

    public String getNavn() {
        return navn;
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

    public String getLandkode() {
        return landkode;
    }

    public String getPostNr() {
        return postNr;
    }

    public String getPoststed() {
        return poststed;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (!(obj instanceof Virksomhet)) {
            return false;
        }
        Virksomhet other = (Virksomhet) obj;
        return Objects.equals(this.getOrgnr(), other.getOrgnr());
    }

    @Override
    public int hashCode() {
        return Objects.hash(orgnr);
    }

    @Override
    public String toString() {
        return "Virksomhet{" +
                "navn=" + navn +
                ", orgnr=" + orgnr +
                '}';
    }

    public static class Builder {
        private Virksomhet mal;

        /**
         * For oppretting av
         */
        public Builder() {
            this.mal = new Virksomhet();
        }

        public Builder medOrgnr(String orgnr) {
            this.mal.orgnr = orgnr;
            return this;
        }

        public Builder medNavn(String navn) {
            this.mal.navn = navn;
            return this;
        }

        public Builder medAdresselinje1(String adresselinje1) {
            this.mal.adresselinje1 = adresselinje1;
            return this;
        }

        public Builder medAdresselinje2(String adresselinje2) {
            this.mal.adresselinje2 = adresselinje2;
            return this;
        }

        public Builder medAdresselinje3(String adresselinje3) {
            this.mal.adresselinje3 = adresselinje3;
            return this;
        }

        public Builder medLandkode(String landkode) {
            this.mal.landkode = landkode;
            return this;
        }

        public Builder medPostNr(String postNr) {
            this.mal.postNr = postNr;
            return this;
        }

        public Builder medPoststed(String poststed) {
            this.mal.poststed = poststed;
            return this;
        }

        public Virksomhet build() {
            return mal;
        }
    }
}
