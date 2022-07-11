package no.nav.foreldrepenger.fpformidling.integrasjon.oppgave;

import java.time.DayOfWeek;
import java.time.LocalDate;

public final class VirkedagUtil {

    private static final long FREDAG = DayOfWeek.FRIDAY.getValue();
    private static final long SØNDAG = DayOfWeek.SUNDAY.getValue();

    private VirkedagUtil() {
    }

    public static LocalDate fomVirkedag(LocalDate dato) {
        if (dato.getDayOfWeek().getValue() > FREDAG) {
            return dato.plusDays(1 + SØNDAG - dato.getDayOfWeek().getValue());
        }
        return dato;
    }
}
