package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;

import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Prosent;

import java.time.LocalDate;
import java.util.Objects;

import static no.nav.foreldrepenger.fpformidling.domene.typer.Dato.formaterDato;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Utbetalingsperiode {
    private String periodeFom;
    @JsonIgnore
    private LocalDate periodeFomDate;
    private String periodeTom;
    @JsonIgnore
    private LocalDate periodeTomDate;
    private int dagsats;
    private Prosent utbetalingsgrad = Prosent.NULL;
    private long utbetaltTilSøker;
    @JsonIgnore
    private String aktivitetNavn;
    @JsonIgnore
    private Språkkode språkkode;

    public LocalDate getPeriodeFom() {
        return periodeFomDate;
    }

    public LocalDate getPeriodeTom() {
        return periodeTomDate;
    }

    public int getDagsats() {
        return dagsats;
    }
    public Prosent getUtbetalingsgrad() {
        return utbetalingsgrad;
    }

    public long getUtbetaltTilSøker() {
        return utbetaltTilSøker;
    }

    public Språkkode getSpråkkode() {
        return språkkode;
    }

    public String getAktivitetNavn() {
        return aktivitetNavn;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        var that = (Utbetalingsperiode) object;
        return Objects.equals(periodeFom, that.periodeFom) && Objects.equals(periodeTom, that.periodeTom) && Objects.equals(dagsats, that.dagsats)
            && Objects.equals(utbetalingsgrad, that.utbetalingsgrad) && Objects.equals(utbetaltTilSøker, that.utbetaltTilSøker) && Objects.equals(aktivitetNavn, that.aktivitetNavn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(periodeFom, periodeTom, dagsats, utbetalingsgrad, utbetaltTilSøker, aktivitetNavn);
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

        public Builder medDagsats(int dagsats) {
            this.kladd.dagsats = dagsats;
            return this;
        }

        public Builder medUtbetalingsgrad(Prosent utbetalingsgrad) {
            this.kladd.utbetalingsgrad = utbetalingsgrad;
            return this;
        }

        public Builder medUtbetaltTilSøker(long utbetaltTilSøker) {
            this.kladd.utbetaltTilSøker = utbetaltTilSøker;
            return this;
        }

        public Builder medAktivitetNavn(String aktivitetNavn) {
            this.kladd.aktivitetNavn = aktivitetNavn;
            return this;
        }

        public Utbetalingsperiode build() {
            return this.kladd;
        }
    }

}
