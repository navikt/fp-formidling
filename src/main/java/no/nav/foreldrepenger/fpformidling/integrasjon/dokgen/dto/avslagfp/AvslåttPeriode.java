package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.avslagfp;

import static no.nav.foreldrepenger.fpformidling.domene.typer.Dato.formaterDato;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;

import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Årsak;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class AvslåttPeriode {
    private Årsak avslagsårsak;
    @JsonIgnore
    private LocalDate periodeFomDate;
    private String periodeFom;
    @JsonIgnore
    private LocalDate periodeTomDate;
    private String periodeTom;
    private int antallTapteDager;
    @JsonIgnore
    private Språkkode språkkode;
    @JsonIgnore
    private BigDecimal tapteDagerTemp;

    public Årsak getAvslagsårsak() {
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

    public BigDecimal getTapteDagerTemp() {
        return tapteDagerTemp;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        var that = (AvslåttPeriode) object;
        return Objects.equals(avslagsårsak, that.avslagsårsak) && Objects.equals(periodeFom, that.periodeFom) && Objects.equals(periodeTom,
            that.periodeTom) && Objects.equals(antallTapteDager, that.antallTapteDager);
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

        public Builder medAvslagsårsak(Årsak avslagsårsak) {
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

        public Builder medAntallTapteDager(int antallTapteDager, BigDecimal tapteDagerTemp) {
            this.kladd.antallTapteDager = antallTapteDager;
            this.kladd.tapteDagerTemp = tapteDagerTemp;
            return this;
        }

        public AvslåttPeriode build() {
            return this.kladd;
        }
    }
}
