package no.nav.foreldrepenger.melding.poststed;

import java.time.LocalDate;
import java.util.Objects;

public class KodeverkKode {

    private String kode;
    private String navn;
    private LocalDate gyldigFom;
    private LocalDate gyldigTom;

    public String getKode() {
        return kode;
    }

    public String getNavn() {
        return navn;
    }

    public LocalDate getGyldigFom() {
        return gyldigFom;
    }

    public LocalDate getGyldigTom() {
        return gyldigTom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KodeverkKode that = (KodeverkKode) o;
        return Objects.equals(kode, that.kode) &&
                Objects.equals(navn, that.navn) &&
                Objects.equals(gyldigFom, that.gyldigFom) &&
                Objects.equals(gyldigTom, that.gyldigTom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kode, navn, gyldigFom, gyldigTom);
    }

    public static class Builder {
        private KodeverkKode kodeMal = new KodeverkKode();

        public Builder medKode(String kode) {
            kodeMal.kode = kode;
            return this;
        }

        public Builder medNavn(String navn) {
            kodeMal.navn = navn;
            return this;
        }

        public Builder medGyldigFom(LocalDate fom) {
            kodeMal.gyldigFom = fom;
            return this;
        }

        public Builder medGyldigTom(LocalDate tom) {
            kodeMal.gyldigTom = tom;
            return this;
        }

        public KodeverkKode build(){
            return kodeMal;
        }
    }
}
