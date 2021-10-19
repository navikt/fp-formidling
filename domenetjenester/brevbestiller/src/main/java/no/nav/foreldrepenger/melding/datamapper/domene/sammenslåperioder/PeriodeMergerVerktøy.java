package no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder;

import java.time.DayOfWeek;
import java.time.LocalDate;

import javax.xml.datatype.XMLGregorianCalendar;

import no.nav.foreldrepenger.melding.integrasjon.dokument.avslag.foreldrepenger.AvslagsAarsakType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.PeriodeType;
import no.nav.foreldrepenger.xmlutils.DateUtil;

public class PeriodeMergerVerktøy {

    private PeriodeMergerVerktøy() {
        //SONAR
    }

    static boolean erFomRettEtterTomDato(PeriodeType periodeEn, PeriodeType periodeTo) {
        return erFomRettEtterTomDato(periodeEn.getPeriodeTom(), periodeTo.getPeriodeFom());
    }

    static boolean erFomRettEtterTomDato(AvslagsAarsakType periodeEn, AvslagsAarsakType periodeTo) {
        return erFomRettEtterTomDato(periodeEn.getPeriodeTom(), periodeTo.getPeriodeFom());
    }

    private static boolean erFomRettEtterTomDato(XMLGregorianCalendar periodeEnTom, XMLGregorianCalendar periodeToFom) { //NOSONAR - denne er i bruk...
        return erFomRettEtterTomDato(DateUtil.convertToLocalDate(periodeEnTom), DateUtil.convertToLocalDate(periodeToFom));
    }

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
