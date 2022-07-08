package no.nav.foreldrepenger.fpformidling.integrasjon.oppgave;

import java.time.DayOfWeek;
import java.time.LocalDate;

public final class VirkedagUtil {

    private static final long MANDAG = DayOfWeek.MONDAY.getValue();
    private static final long FREDAG = DayOfWeek.FRIDAY.getValue();
    private static final long LØRDAG = DayOfWeek.SATURDAY.getValue();
    private static final long SØNDAG = DayOfWeek.SUNDAY.getValue();

    private VirkedagUtil() {
    }

    public static LocalDate fomVirkedag(LocalDate dato) {
        if (dato.getDayOfWeek().getValue() > FREDAG) {
            return dato.plusDays(1 + SØNDAG - dato.getDayOfWeek().getValue());
        }
        return dato;
    }

    public static LocalDate tomVirkedag(LocalDate dato) {
        if (dato.getDayOfWeek().getValue() > FREDAG) {
            return dato.minusDays(dato.getDayOfWeek().getValue() - FREDAG);
        }
        return dato;
    }

    public static LocalDate fredagLørdagTilSøndag(LocalDate dato) {
        if (dato.getDayOfWeek().getValue() >= FREDAG) {
            return dato.plusDays(SØNDAG - dato.getDayOfWeek().getValue());
        }
        return dato;
    }

    public static LocalDate lørdagSøndagTilMandag(LocalDate dato) {
        if (dato.getDayOfWeek().getValue() >= LØRDAG) {
            return dato.plusDays(SØNDAG - dato.getDayOfWeek().getValue() + 1);
        }
        return dato;
    }
}
