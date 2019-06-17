package no.nav.foreldrepenger.melding.typer;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoPeriod;
import java.time.chrono.Chronology;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * En wrapper-klasse for LocalDate hvor toString() returnerer datoen formatert.
 * Gjør den praktisk å bruke som dato-input til Handelbars templates.
 */
public class Dato implements ChronoLocalDate {
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

    private LocalDate localDate;

    public Dato(LocalDate localDate) {
        this.localDate = localDate;
    }

    public int getDayOfMonth() {
        return localDate.getDayOfMonth();
    }

    public int getMonthValue() {
        return localDate.getMonthValue();
    }

    public int getYear() {
        return localDate.getYear();
    }

    public LocalDate toLocalDate() {
        return localDate;
    }

    public static Dato medFormatering(LocalDate localDate) {
        return new Dato(localDate);
    }

    public static String formaterDato(LocalDate dato) {
        return dato.getDayOfMonth() + ". " + månedMap.get(dato.getMonthValue()) + " " + dato.getYear();
    }

    @Override
    public String toString() {
        return formaterDato(localDate);
    }

    @Override
    public long getLong(TemporalField field) {
        return localDate.getLong(field);
    }

    @Override
    public Chronology getChronology() {
        return localDate.getChronology();
    }

    @Override
    public int lengthOfMonth() {
        return localDate.lengthOfMonth();
    }

    @Override
    public long until(Temporal endExclusive, TemporalUnit unit) {
        return localDate.until(endExclusive, unit);
    }

    @Override
    public ChronoPeriod until(ChronoLocalDate endDateExclusive) {
        return localDate.until(endDateExclusive);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dato dato = (Dato) o;
        return Objects.equals(localDate, dato.localDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(localDate);
    }
}
