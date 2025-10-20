package no.nav.foreldrepenger.fpformidling.typer;

import static no.nav.vedtak.konfig.Tid.TIDENES_ENDE;

import java.time.LocalDate;
import java.util.Objects;

public class DatoIntervall {

    private final Dato fomDato;
    private final Dato tomDato;

    public static DatoIntervall fraOgMedTilOgMed(LocalDate fom, LocalDate tom) {
        return new DatoIntervall(fom, tom);
    }

    public static DatoIntervall fraOgMed(LocalDate fom) {
        return new DatoIntervall(fom, TIDENES_ENDE);
    }

    public Dato getFom() {
        return fomDato;
    }

    public Dato getTom() {
        return tomDato;
    }

    public LocalDate getFomDato() {
        return fomDato.toLocalDate();
    }

    public LocalDate getTomDato() {
        return tomDato.toLocalDate();
    }

    public DatoIntervall(Dato fomDato, Dato tomDato) {
        if (fomDato == null) {
            throw new IllegalArgumentException("Fra og med dato må være satt.");
        }
        if (tomDato == null) {
            throw new IllegalArgumentException("Til og med dato må være satt.");
        }
        if (tomDato.isBefore(fomDato)) {
            throw new IllegalArgumentException("Til og med dato før fra og med dato.");
        }
        this.fomDato = fomDato;
        this.tomDato = tomDato;
    }

    public DatoIntervall(LocalDate fom, LocalDate tom) {
        this(new Dato(fom), new Dato(tom));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        var that = (DatoIntervall) o;
        return Objects.equals(getFom(), that.getFom()) && Objects.equals(getTom(), that.getTom());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFomDato(), getTomDato());
    }

    @Override
    public String toString() {
        return "DatoIntervallImpl{" + "fomDato=" + fomDato + ", tomDato=" + tomDato + '}';
    }
}
