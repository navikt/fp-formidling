package no.nav.foreldrepenger.melding.typer;

import static no.nav.vedtak.konfig.Tid.TIDENES_ENDE;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DatoIntervallImpl implements DatoIntervall {
    private LocalDate fomDato;
    private LocalDate tomDato;

    static Map<Integer, String> månedMap;
    static {
        månedMap = new HashMap<>();
        månedMap.put(1, "januar");
        månedMap.put(2, "februar");
        månedMap.put(3, "mars");
        månedMap.put(4, "april");
        månedMap.put(5, "mai");
        månedMap.put(6, "juni");
        månedMap.put(7, "juli");
        månedMap.put(8, "august");
        månedMap.put(9, "september");
        månedMap.put(10, "oktober");
        månedMap.put(11, "november");
        månedMap.put(12, "desember");
    }

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
