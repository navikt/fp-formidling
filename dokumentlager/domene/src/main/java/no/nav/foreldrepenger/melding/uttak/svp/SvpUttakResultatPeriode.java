package no.nav.foreldrepenger.melding.uttak.svp;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import no.nav.foreldrepenger.melding.typer.DatoIntervall;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatType;

public class SvpUttakResultatPeriode implements Comparable<SvpUttakResultatPeriode> {
    private long utbetalingsgrad;
    private long aktivitetDagsats;

    private Optional<String> arbeidsgiverNavn;

    private Optional<ArbeidsforholdIkkeOppfyltÅrsak> arbeidsforholdIkkeOppfyltÅrsak;

    private PeriodeResultatType periodeResultatType;

    private PeriodeIkkeOppfyltÅrsak periodeIkkeOppfyltÅrsak;

    private DatoIntervall tidsperiode;

    public long getAktivitetDagsats() {
        return aktivitetDagsats;
    }

    public int getAarsakskode() {
        if (PeriodeIkkeOppfyltÅrsak.INGEN.getKode().equals(getPeriodeIkkeOppfyltÅrsak().getKode())) {
            return 0;
        }
        return Integer.parseInt(getPeriodeIkkeOppfyltÅrsak().getKode());
    }

    public String getArbeidsgiverNavn() {
        return arbeidsgiverNavn.orElse("Andel");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SvpUttakResultatPeriode that = (SvpUttakResultatPeriode) o;
        return getUtbetalingsgrad() == that.getUtbetalingsgrad() &&
                getAktivitetDagsats() == that.getAktivitetDagsats() &&
                Objects.equals(getTidsperiode(), that.getTidsperiode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUtbetalingsgrad(), getAktivitetDagsats(), getTidsperiode());
    }

    @Override
    public int compareTo(SvpUttakResultatPeriode o) {
        return this.getTidsperiode().compareTo(o.getTidsperiode());
    }

    public Optional<ArbeidsforholdIkkeOppfyltÅrsak> getArbeidsforholdIkkeOppfyltÅrsak() {
        return arbeidsforholdIkkeOppfyltÅrsak;
    }

    private SvpUttakResultatPeriode(Builder builder) {
        utbetalingsgrad = builder.utbetalingsgrad;
        aktivitetDagsats = builder.aktivitetDagsats;
        arbeidsgiverNavn = builder.arbeidsgiverNavn;
        arbeidsforholdIkkeOppfyltÅrsak = builder.arbeidsforholdIkkeOppfyltÅrsak;
        periodeResultatType = builder.periodeResultatType;
        periodeIkkeOppfyltÅrsak = builder.periodeIkkeOppfyltÅrsak;
        tidsperiode = builder.tidsperiode;
    }

    public DatoIntervall getTidsperiode() {
        return tidsperiode;
    }

    public LocalDate getFom() {
        return tidsperiode.getFomDato();
    }

    public LocalDate getTom() {
        return tidsperiode.getTomDato();
    }

    public int getUtbetalingsgrad() {
        return (int) utbetalingsgrad;
    }

    public PeriodeResultatType getPeriodeResultatType() {
        return periodeResultatType;
    }

    public PeriodeIkkeOppfyltÅrsak getPeriodeIkkeOppfyltÅrsak() {
        return periodeIkkeOppfyltÅrsak;
    }

    public boolean isInnvilget() {
        return Objects.equals(getPeriodeResultatType(), PeriodeResultatType.INNVILGET);
    }

    public static Builder ny() {
        return new Builder();
    }

    public static Builder ny(SvpUttakResultatPeriode copy) {
        return new Builder(copy);
    }

    public static final class Builder {
        private long utbetalingsgrad;
        private PeriodeResultatType periodeResultatType = PeriodeResultatType.IKKE_FASTSATT;
        private PeriodeIkkeOppfyltÅrsak periodeIkkeOppfyltÅrsak = PeriodeIkkeOppfyltÅrsak.INGEN;
        private DatoIntervall tidsperiode;
        private long aktivitetDagsats;
        private Optional<String> arbeidsgiverNavn = Optional.empty();
        private Optional<ArbeidsforholdIkkeOppfyltÅrsak> arbeidsforholdIkkeOppfyltÅrsak = Optional.empty();

        public Builder() {
        }

        public Builder(SvpUttakResultatPeriode copy) {
            this.utbetalingsgrad = copy.getUtbetalingsgrad();
            this.aktivitetDagsats = copy.getAktivitetDagsats();
            this.arbeidsgiverNavn = copy.arbeidsgiverNavn;
            this.arbeidsforholdIkkeOppfyltÅrsak = copy.getArbeidsforholdIkkeOppfyltÅrsak();
            this.periodeResultatType = copy.getPeriodeResultatType();
            this.periodeIkkeOppfyltÅrsak = copy.getPeriodeIkkeOppfyltÅrsak();
            this.tidsperiode = copy.getTidsperiode();
        }

        public Builder medUtbetalingsgrad(long utbetalingsgrad) {
            this.utbetalingsgrad = utbetalingsgrad;
            return this;
        }

        public Builder medPeriodeResultatType(PeriodeResultatType periodeResultatType) {
            this.periodeResultatType = periodeResultatType;
            return this;
        }

        public Builder medPeriodeIkkeOppfyltÅrsak(PeriodeIkkeOppfyltÅrsak periodeIkkeOppfyltÅrsak) {
            this.periodeIkkeOppfyltÅrsak = periodeIkkeOppfyltÅrsak;
            return this;
        }

        public Builder medTidsperiode(DatoIntervall tidsperiode) {
            this.tidsperiode = tidsperiode;
            return this;
        }

        public Builder medAktivitetDagsats(long aktivitetDagsats) {
            this.aktivitetDagsats = aktivitetDagsats;
            return this;
        }

        public SvpUttakResultatPeriode build() {
            return new SvpUttakResultatPeriode(this);
        }

        public Builder medArbeidsforholdIkkeOppfyltÅrsak(Optional<ArbeidsforholdIkkeOppfyltÅrsak> arbeidsforholdIkkeOppfyltÅrsak) {
            this.arbeidsforholdIkkeOppfyltÅrsak = arbeidsforholdIkkeOppfyltÅrsak;
            return this;
        }

        public Builder medArbeidsgiverNavn(String arbeidsgiverNavn) {
            this.arbeidsgiverNavn = Optional.ofNullable(arbeidsgiverNavn);
            return this;
        }
    }
}
