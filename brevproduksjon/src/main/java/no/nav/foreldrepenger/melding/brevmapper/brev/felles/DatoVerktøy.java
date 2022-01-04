package no.nav.foreldrepenger.melding.brevmapper.brev.felles;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class DatoVerktøy {
    public static boolean erFomRettEtterTomDato(LocalDate periodeEnTom, LocalDate periodeToFom) {
        return periodeEnTom.plusDays(1).isEqual(periodeToFom) ||
                periodeEnTom.plusDays(2).isEqual(periodeToFom) && erLørdagEllerSøndag(periodeEnTom.plusDays(1)) ||
                periodeEnTom.plusDays(3).isEqual(periodeToFom) && erLørdag(periodeEnTom.plusDays(1)) && erSøndag(periodeEnTom.plusDays(2));
    }

    private static boolean erLørdagEllerSøndag(LocalDate date) {
        return erLørdag(date) || erSøndag(date);
    }

    private static boolean erSøndag(LocalDate date) {
        return date.getDayOfWeek().equals(DayOfWeek.SUNDAY);
    }

    private static boolean erLørdag(LocalDate date) {
        return date.getDayOfWeek().equals(DayOfWeek.SATURDAY);
    }
}
