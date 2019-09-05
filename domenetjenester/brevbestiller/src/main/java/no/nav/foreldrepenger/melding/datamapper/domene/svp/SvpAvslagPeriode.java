package no.nav.foreldrepenger.melding.datamapper.domene.svp;

import java.time.LocalDate;
import java.util.Objects;

import no.nav.foreldrepenger.melding.typer.Dato;
import no.nav.foreldrepenger.melding.uttak.svp.PeriodeIkkeOppfyltÅrsak;

final class SvpAvslagPeriode {

    private int aarsakskode;
    private Dato fom;
    private Dato tom;

    private SvpAvslagPeriode() {
        // Skjul default constructor
    }

    private SvpAvslagPeriode(SvpAvslagPeriode.Builder builder) {
        this.aarsakskode = builder.aarsakskode;
        this.fom = builder.fom;
        this.tom = builder.tom;
    }

    @SuppressWarnings("WeakerAccess") // Handlebar trenger public getter
    public Dato getFom() {
        return fom;
    }

    @SuppressWarnings("WeakerAccess") // Handlebar trenger public getter
    public Dato getTom() {
        return tom;
    }

    @SuppressWarnings("WeakerAccess") // Handlebar trenger public getter
    public int getAarsakskode() {
        return aarsakskode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SvpAvslagPeriode that = (SvpAvslagPeriode) o;
        return aarsakskode == that.aarsakskode &&
                Objects.equals(fom, that.fom) &&
                Objects.equals(tom, that.tom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aarsakskode, fom, tom);
    }

    @Override
    public String toString() {
        return "SvpAvslagPeriode{" +
                "aarsakskode=" + aarsakskode +
                ", fom=" + fom +
                ", tom=" + tom +
                '}';
    }

    public static final class Builder {

        private int aarsakskode;
        private Dato fom;
        private Dato tom;

        private Builder() {
            // Skjul default constructor
        }

        public static SvpAvslagPeriode.Builder ny() {
            return new SvpAvslagPeriode.Builder();
        }

        SvpAvslagPeriode.Builder medPeriodeÅrsak(PeriodeIkkeOppfyltÅrsak periodeIkkeOppfyltÅrsak) {
            if (!PeriodeIkkeOppfyltÅrsak.INGEN.equals(periodeIkkeOppfyltÅrsak)) {
                this.aarsakskode = Integer.valueOf(periodeIkkeOppfyltÅrsak.getKode());
            }
            return this;
        }

        SvpAvslagPeriode.Builder medFom(Dato fom) {
            this.fom = fom;
            return this;
        }

        SvpAvslagPeriode.Builder medFom(LocalDate fom) {
            this.fom = new Dato(fom);
            return this;
        }


        SvpAvslagPeriode.Builder medTom(Dato tom) {
            this.tom = tom;
            return this;
        }

        SvpAvslagPeriode.Builder medTom(LocalDate tom) {
            this.tom = new Dato(tom);
            return this;
        }

        public SvpAvslagPeriode build() {
            return new SvpAvslagPeriode(this);
        }

    }

}

