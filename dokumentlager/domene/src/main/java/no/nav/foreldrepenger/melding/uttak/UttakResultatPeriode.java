package no.nav.foreldrepenger.melding.uttak;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import no.nav.foreldrepenger.melding.typer.DatoIntervall;

public class UttakResultatPeriode {
    private PeriodeResultatÅrsak periodeResultatÅrsak; //Kodeliste.PeriodeResultatÅrsak
    private DatoIntervall tidsperiode;
    private GraderingAvslagÅrsak graderingAvslagÅrsak; //Kodeliste.GraderingAvslagÅrsak
    private UttakUtsettelseType uttakUtsettelseType; //Kodeliste.UttakUtsettelseType
    private PeriodeResultatType periodeResultatType; //Kodeliste.PeriodeResultatType
    private List<UttakResultatPeriodeAktivitet> aktiviteter = new ArrayList<>();

    private UttakResultatPeriode(Builder builder) {
        periodeResultatÅrsak = builder.periodeResultatÅrsak;
        tidsperiode = builder.tidsperiode;
        graderingAvslagÅrsak = builder.graderingAvslagÅrsak;
        uttakUtsettelseType = builder.uttakUtsettelseType;
        periodeResultatType = builder.periodeResultatType;
        aktiviteter = builder.aktiviteter;
    }

    public static Builder ny() {
        return new Builder();
    }

    public PeriodeResultatÅrsak getPeriodeResultatÅrsak() {
        return periodeResultatÅrsak;
    }

    public LocalDate getFom() {
        return tidsperiode.getFomDato();
    }

    public LocalDate getTom() {
        return tidsperiode.getTomDato();
    }

    public GraderingAvslagÅrsak getGraderingAvslagÅrsak() {
        return graderingAvslagÅrsak;
    }

    public UttakUtsettelseType getUttakUtsettelseType() {
        return uttakUtsettelseType;
    }

    public PeriodeResultatType getPeriodeResultatType() {
        return periodeResultatType;
    }

    public List<UttakResultatPeriodeAktivitet> getAktiviteter() {
        return aktiviteter;
    }

    public boolean isInnvilget() {
        return Objects.equals(getPeriodeResultatType(), PeriodeResultatType.INNVILGET);
    }

    public boolean erGraderingInnvilget() {
        return aktiviteter.stream().anyMatch(UttakResultatPeriodeAktivitet::getGraderingInnvilget);
    }


    public static final class Builder {
        private PeriodeResultatÅrsak periodeResultatÅrsak;
        private DatoIntervall tidsperiode;
        private GraderingAvslagÅrsak graderingAvslagÅrsak;
        private UttakUtsettelseType uttakUtsettelseType;
        private PeriodeResultatType periodeResultatType;
        private List<UttakResultatPeriodeAktivitet> aktiviteter;

        private Builder() {
        }

        public Builder medPeriodeResultatÅrsak(PeriodeResultatÅrsak periodeResultatÅrsak) {
            this.periodeResultatÅrsak = periodeResultatÅrsak;
            return this;
        }

        public Builder medTidsperiode(DatoIntervall tidsperiode) {
            this.tidsperiode = tidsperiode;
            return this;
        }

        public Builder medGraderingAvslagÅrsak(GraderingAvslagÅrsak graderingAvslagÅrsak) {
            this.graderingAvslagÅrsak = graderingAvslagÅrsak;
            return this;
        }

        public Builder medUttakUtsettelseType(UttakUtsettelseType uttakUtsettelseType) {
            this.uttakUtsettelseType = uttakUtsettelseType;
            return this;
        }

        public Builder medPeriodeResultatType(PeriodeResultatType periodeResultatType) {
            this.periodeResultatType = periodeResultatType;
            return this;
        }

        public Builder medAktiviteter(List<UttakResultatPeriodeAktivitet> aktiviteter) {
            this.aktiviteter = aktiviteter;
            return this;
        }

        public UttakResultatPeriode build() {
            return new UttakResultatPeriode(this);
        }
    }
}
