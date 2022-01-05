package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp;

import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;

import no.nav.foreldrepenger.fpformidling.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Prosent;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Uttaksperiode {
    private String periodeFom;
    @JsonIgnore
    private LocalDate periodeFomDate;
    private String periodeTom;
    @JsonIgnore
    private LocalDate periodeTomDate;
    private Prosent utbetalingsgrad = Prosent.NULL;
    @JsonIgnore
    private Språkkode språkkode;

    public LocalDate getPeriodeFom() {
        return periodeFomDate;
    }

    public LocalDate getPeriodeTom() {
        return periodeTomDate;
    }

    public Prosent getUtbetalingsgrad() {
        return utbetalingsgrad;
    }

    public Språkkode getSpråkkode() {
        return språkkode;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        var that = (Uttaksperiode) object;
        return Objects.equals(periodeFom, that.periodeFom)
                && Objects.equals(periodeTom, that.periodeTom)
                && Objects.equals(utbetalingsgrad, that.utbetalingsgrad);
    }

    @Override
    public int hashCode() {
        return Objects.hash(periodeFom, periodeTom, utbetalingsgrad);
    }

    public static Builder ny() {
        return new Builder();
    }

    public static Builder ny(Uttaksperiode eksisterende) {
        return new Builder(eksisterende);
    }

    public static class Builder {
        private Uttaksperiode kladd;

        private Builder() {
            this.kladd = new Uttaksperiode();
        }

        private Builder(Uttaksperiode eksisterende) {
            this.kladd = new Uttaksperiode();
            this.kladd.periodeFom = formaterDato(eksisterende.getPeriodeFom(), eksisterende.getSpråkkode());
            this.kladd.periodeFomDate = eksisterende.getPeriodeFom();
            this.kladd.periodeTom = formaterDato(eksisterende.getPeriodeTom(), eksisterende.getSpråkkode());
            this.kladd.periodeTomDate = eksisterende.getPeriodeTom();
            this.kladd.utbetalingsgrad = eksisterende.getUtbetalingsgrad();
            this.kladd.språkkode = eksisterende.getSpråkkode();
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

        public Builder medUtbetalingsgrad(Prosent utbetalingsgrad) {
            this.kladd.utbetalingsgrad = utbetalingsgrad;
            return this;
        }

        public Uttaksperiode build() {
            return this.kladd;
        }
    }
}