package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.avslagfp;

import static no.nav.foreldrepenger.melding.typer.Dato.formaterDato;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;

import no.nav.foreldrepenger.melding.geografisk.Språkkode;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class AvslåttPeriode {
    private String avslagsårsak;
    @JsonIgnore
    private LocalDate periodeFomDate;
    private String periodeFom;
    @JsonIgnore
    private LocalDate periodeTomDate;
    private String periodeTom;
    private int antallTapteDager;
    @JsonIgnore
    private Språkkode språkkode;

    public String getAvslagsårsak() {
        return avslagsårsak;
    }

    public LocalDate getPeriodeFom() {
        return periodeFomDate;
    }

    public LocalDate getPeriodeTom() {
        return periodeTomDate;
    }

    public int getAntallTapteDager() {
        return antallTapteDager;
    }

    public Språkkode getSpråkkode() {
        return språkkode;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        var that = (AvslåttPeriode) object;
        return Objects.equals(avslagsårsak, that.avslagsårsak)
                && Objects.equals(periodeFom, that.periodeFom)
                && Objects.equals(periodeTom, that.periodeTom)
                && Objects.equals(antallTapteDager, that.antallTapteDager);
    }

    @Override
    public int hashCode() {
        return Objects.hash(avslagsårsak, periodeFom, periodeTom, antallTapteDager);
    }

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private AvslåttPeriode kladd;

        private Builder() {
            this.kladd = new AvslåttPeriode();
        }

        public Builder medAvslagsårsak(String avslagsårsak) {
            this.kladd.avslagsårsak = avslagsårsak;
            return this;
        }

        public Builder medPeriodeFom(LocalDate periodeFom, Språkkode språkkode) {
            this.kladd.periodeFom = formaterDato(periodeFom, språkkode);
            this.kladd.periodeFomDate = periodeFom;
            this.kladd.språkkode = språkkode;
            return this;
        }

        public Builder medPeriodeTom(LocalDate periodeTom, Språkkode språkkode) {
            this.kladd.periodeTom = formaterDato(periodeTom, språkkode);
            this.kladd.periodeTomDate = periodeTom;
            this.kladd.språkkode = språkkode;
            return this;
        }

        public Builder medAntallTapteDager(int antallTapteDager) {
            this.kladd.antallTapteDager = antallTapteDager;
            return this;
        }

        public AvslåttPeriode build() {
            return this.kladd;
        }
    }
}
