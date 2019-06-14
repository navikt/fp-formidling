package no.nav.foreldrepenger.melding.datamapper;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoPeriod;
import java.time.chrono.Chronology;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.util.HashMap;
import java.util.Map;

import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.vedtak.util.FPDateUtil;

public class BrevMapperUtil {
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

    public static LocalDate getSvarFrist(BrevParametere brevParametere) {
        return FPDateUtil.iDag().plusDays(brevParametere.getSvarfristDager());
    }

    public static ChronoLocalDate medFormatering(LocalDate localDate) {
        return new ChronoLocalDate() {
            private LocalDate date = localDate;

            @Override
            public long getLong(TemporalField field) {
                return 0;
            }

            @Override
            public Chronology getChronology() {
                return date.getChronology();
            }

            @Override
            public int lengthOfMonth() {
                return date.lengthOfMonth();
            }

            @Override
            public long until(Temporal endExclusive, TemporalUnit unit) {
                return date.until(endExclusive, unit);
            }

            @Override
            public ChronoPeriod until(ChronoLocalDate endDateExclusive) {
                return date.until(endDateExclusive);
            }

            @Override
            public String toString() {
                return formaterDato(date);
            }
        };
    }

    static String formaterDato(LocalDate dato) {
        return dato.getDayOfMonth() + ". " + månedMap.get(dato.getMonthValue()) + " " + dato.getYear();
    }
}
