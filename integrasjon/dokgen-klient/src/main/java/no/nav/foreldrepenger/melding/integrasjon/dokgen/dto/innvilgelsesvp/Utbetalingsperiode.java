package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsesvp;

import static no.nav.foreldrepenger.melding.typer.Dato.formaterDato;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;

import no.nav.foreldrepenger.melding.geografisk.Språkkode;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Utbetalingsperiode {
    private String periodeFom;
    @JsonIgnore
    private LocalDate periodeFomDate;
    private String periodeTom;
    @JsonIgnore
    private LocalDate periodeTomDate;
    private long periodeDagsats;
    private long utbetaltTilSøker;
    @JsonIgnore
    private Språkkode språkkode;

    public LocalDate getPeriodeFom() {
        return periodeFomDate;
    }

    public LocalDate getPeriodeTom() {
        return periodeTomDate;
    }

    public long getPeriodeDagsats() {
        return periodeDagsats;
    }

    public long getUtbetaltTilSøker() {
        return utbetaltTilSøker;
    }

    public Språkkode getSpråkkode() {
        return språkkode;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        var that = (Utbetalingsperiode) object;
        return Objects.equals(periodeFom, that.periodeFom)
                && Objects.equals(periodeTom, that.periodeTom)
                && Objects.equals(periodeDagsats, that.periodeDagsats)
                && Objects.equals(utbetaltTilSøker, that.utbetaltTilSøker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(periodeFom, periodeTom, periodeDagsats, utbetaltTilSøker);
    }

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private Utbetalingsperiode kladd;

        private Builder() {
            this.kladd = new Utbetalingsperiode();
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

        public Builder medPeriodeDagsats(long periodeDagsats) {
            this.kladd.periodeDagsats = periodeDagsats;
            return this;
        }

        public Builder medUtbetaltTilSøker(long utbetaltTilSøker) {
            this.kladd.utbetaltTilSøker = utbetaltTilSøker;
            return this;
        }

        public Utbetalingsperiode build() {
            return this.kladd;
        }
    }
}