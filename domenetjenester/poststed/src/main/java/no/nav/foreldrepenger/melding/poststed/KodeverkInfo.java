package no.nav.foreldrepenger.melding.poststed;

import java.time.LocalDate;
import java.util.Objects;

public class KodeverkInfo {

    private String navn;
    private String versjon;
    private LocalDate versjonDato;

    public String getNavn() {
        return navn;
    }

    public String getVersjon() {
        return versjon;
    }

    public LocalDate getVersjonDato() {
        return versjonDato;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KodeverkInfo that = (KodeverkInfo) o;
        return Objects.equals(navn, that.navn) &&
                Objects.equals(versjon, that.versjon) &&
                Objects.equals(versjonDato, that.versjonDato);
    }

    @Override
    public int hashCode() {
        return Objects.hash(navn, versjon, versjonDato);
    }

    public static class Builder {
        private KodeverkInfo kodeMal = new KodeverkInfo();

        public Builder medVersjon(String versjon) {
            kodeMal.versjon = versjon;
            return this;
        }

        public Builder medNavn(String navn) {
            kodeMal.navn = navn;
            return this;
        }

        public Builder medVersjonDato(LocalDate dato) {
            kodeMal.versjonDato = dato;
            return this;
        }

        public KodeverkInfo build(){
            return kodeMal;
        }
    }
}
