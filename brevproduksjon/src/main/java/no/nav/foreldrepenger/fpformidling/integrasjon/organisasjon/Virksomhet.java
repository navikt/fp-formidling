package no.nav.foreldrepenger.fpformidling.integrasjon.organisasjon;

import java.util.Objects;

public class Virksomhet {

    private String orgnr;
    private String navn;

    public String getOrgnr() {
        return orgnr;
    }

    public String getNavn() {
        return navn;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (!(obj instanceof Virksomhet)) {
            return false;
        }
        var other = (Virksomhet) obj;
        return Objects.equals(this.getOrgnr(), other.getOrgnr());
    }

    @Override
    public int hashCode() {
        return Objects.hash(orgnr);
    }

    @Override
    public String toString() {
        return "Virksomhet{" + "navn=" + navn + ", orgnr=" + orgnr + '}';
    }

    public static class Builder {
        private Virksomhet mal;

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

        public Virksomhet build() {
            return mal;
        }
    }
}
