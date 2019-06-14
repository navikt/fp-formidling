package no.nav.foreldrepenger.melding.typer;

import java.time.LocalDate;
import java.util.Objects;

public class DatoIntervallImpl implements DatoIntervall {
    private LocalDate fomDato;
    private LocalDate tomDato;

    @Override
    public LocalDate getFomDato() {
        return fomDato;
    }

    @Override
    public LocalDate getTomDato() {
        return tomDato;
    }

    public DatoIntervallImpl(LocalDate fomDato, LocalDate tomDato) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DatoIntervall that = (DatoIntervall) o;
        return Objects.equals(getFomDato(), that.getFomDato()) &&
                Objects.equals(getTomDato(), that.getTomDato());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFomDato(), getTomDato());
    }

}
