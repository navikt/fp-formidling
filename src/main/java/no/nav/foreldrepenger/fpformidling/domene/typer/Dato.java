package no.nav.foreldrepenger.fpformidling.domene.typer;

import static java.time.format.DateTimeFormatter.ofPattern;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoPeriod;
import java.time.chrono.Chronology;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.util.Locale;
import java.util.Objects;

import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;

/**
 * En wrapper-klasse for LocalDate hvor toString() returnerer datoen formatert.
 * Gjør den praktisk å bruke som dato-input til Handelbars templates.
 */
public class Dato implements ChronoLocalDate {

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

    public static String formaterDato(LocalDate dato, Språkkode språkkode) {
        return Språkkode.EN.equals(språkkode) ? formaterDatoEngelsk(dato) : formaterDatoNorsk(dato);
    }

    public static String formaterDatoNorsk(LocalDate dato) {
        if (dato == null) {
            return null;
        }
        return dato.format(ofPattern("d. MMMM yyyy", Locale.forLanguageTag("NO")));
    }

    public static String formaterDatoEngelsk(LocalDate dato) {
        if (dato == null) {
            return null;
        }
        return dato.format(ofPattern(String.format("d'%s' 'of' MMMM yyyy", getDayOfMonthSuffix(dato.getDayOfMonth())), Locale.ENGLISH));
    }

    private static String getDayOfMonthSuffix(final int dayOfMonth) {
        var th = "th";
        if (dayOfMonth >= 11 && dayOfMonth <= 13) {
            return th;
        }
        return switch (dayOfMonth % 10) {
            case 1 -> "st";
            case 2 -> "nd";
            case 3 -> "rd";
            default -> th;
        };
    }

    @Override
    public String toString() {
        return formaterDatoNorsk(localDate);
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        var dato = (Dato) o;
        return Objects.equals(localDate, dato.localDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(localDate);
    }
}
