package no.nav.foreldrepenger.fpformidling.domene.typer;

import java.time.LocalDate;
import java.util.Objects;

public class DatoIntervallImpl implements DatoIntervall {
    private Dato fomDato;
    private Dato tomDato;

    @Override
    public Dato getFom() {
        return fomDato;
    }

    @Override
    public Dato getTom() {
        return tomDato;
    }

    @Override
    public LocalDate getFomDato() {
        return fomDato.toLocalDate();
    }

    @Override
    public LocalDate getTomDato() {
        return tomDato.toLocalDate();
    }

    public DatoIntervallImpl(Dato fomDato, Dato tomDato) {
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

    public DatoIntervallImpl(LocalDate fom, LocalDate tom) {
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
