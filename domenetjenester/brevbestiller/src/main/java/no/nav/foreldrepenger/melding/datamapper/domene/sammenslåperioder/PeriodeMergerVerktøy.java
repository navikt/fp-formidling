package no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder;

import no.nav.foreldrepenger.melding.integrasjon.dokument.avslag.foreldrepenger.AvslagsAarsakType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.AnnenAktivitetType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.ArbeidsforholdType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.NæringType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.PeriodeType;
import no.nav.vedtak.felles.integrasjon.felles.ws.DateUtil;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

public class PeriodeMergerVerktøy {

    private PeriodeMergerVerktøy() {
        //SONAR
    }

    static PeriodeType slåSammenPerioder(PeriodeType periodeEn, PeriodeType periodeTo) {
        periodeEn.setAntallTapteDager(periodeEn.getAntallTapteDager().add(periodeTo.getAntallTapteDager()));
        periodeEn.setPeriodeTom(periodeTo.getPeriodeTom());
        return periodeEn;
    }

    static AvslagsAarsakType slåSammenPerioder(AvslagsAarsakType periodeEn, AvslagsAarsakType periodeTo) {
        periodeEn.setAntallTapteDager(periodeEn.getAntallTapteDager().add(periodeTo.getAntallTapteDager()));
        periodeEn.setPeriodeTom(periodeTo.getPeriodeTom());
        return periodeEn;
    }

    static boolean sammeStatusOgÅrsak(PeriodeType periodeEn, PeriodeType periodeTo) {
        return Objects.equals(periodeEn.isInnvilget(), periodeTo.isInnvilget())
                && (Objects.equals(periodeEn.getÅrsak(), periodeTo.getÅrsak())
                || erRegnetSomLike(periodeEn.getÅrsak(), periodeTo.getÅrsak()));
    }

    private static boolean erRegnetSomLike(String årsak1, String årsak2) {
        return PeriodeMergerInnvilgelse.nonEqualKoderSomLikevelOppfyllerMerge.containsAll(Set.of(årsak1, årsak2));
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

    static boolean likeAktiviteter(PeriodeType periodeEn, PeriodeType periodeTo) {
        return likeArbeidsforhold(periodeEn, periodeTo) && likNæring(periodeEn, periodeTo) && likeAndreAktiviteter(periodeEn, periodeTo);
    }

    static boolean likeAndreAktiviteter(PeriodeType periodeEn, PeriodeType periodeTo) {
        boolean alleMatcher = likAktivitetsliste(periodeEn, periodeTo);
        if (!alleMatcher) {
            return false;
        }
        if (periodeEn.getAnnenAktivitetListe() == null) {
            return true;
        }
        for (AnnenAktivitetType akt : periodeEn.getAnnenAktivitetListe().getAnnenAktivitet()) {
            if (!finnesMatch(akt, periodeTo)) {
                alleMatcher = false;
            }
        }
        return alleMatcher;
    }

    private static boolean likAktivitetsliste(PeriodeType periodeEn, PeriodeType periodeTo) {
        if (periodeEn.getAnnenAktivitetListe() == null && periodeTo.getAnnenAktivitetListe() == null) {
            return true;
        }
        if ((periodeEn.getAnnenAktivitetListe() == null) != (periodeTo.getAnnenAktivitetListe() == null)) {
            return false;
        }
        return periodeEn.getAnnenAktivitetListe().getAnnenAktivitet().size() == periodeTo.getAnnenAktivitetListe().getAnnenAktivitet().size();
    }

    private static boolean likArbeidsforholdListe(PeriodeType periodeEn, PeriodeType periodeTo) {
        if (periodeEn.getArbeidsforholdListe() == null && periodeTo.getArbeidsforholdListe() == null) {
            return true;
        }
        if ((periodeEn.getArbeidsforholdListe() == null) != (periodeTo.getArbeidsforholdListe() == null)) {
            return false;
        }
        return periodeEn.getArbeidsforholdListe().getArbeidsforhold().size() == periodeTo.getArbeidsforholdListe().getArbeidsforhold().size();
    }

    static boolean likNæring(PeriodeType periodeEn, PeriodeType periodeTo) {
        if (periodeEn.getNæringListe() == null && periodeTo.getNæringListe() == null) {
            return true;
        } else if ((periodeEn.getNæringListe() == null) != (periodeTo.getNæringListe() == null)) {
            return false;
        }
        if (periodeEn.getNæringListe().getNæring() == null && periodeTo.getNæringListe().getNæring() == null) {
            return true;
        } else if ((periodeEn.getNæringListe().getNæring() == null) != (periodeTo.getNæringListe().getNæring() == null)) {
            return false;
        }
        return likNæringType(periodeEn.getNæringListe().getNæring(), periodeTo.getNæringListe().getNæring());
    }

    static boolean likeArbeidsforhold(PeriodeType periodeEn, PeriodeType periodeTo) {
        boolean alleMatcher = likArbeidsforholdListe(periodeEn, periodeTo);
        if (!alleMatcher) {
            return false;
        }
        if (periodeEn.getArbeidsforholdListe() == null) {
            return true;
        }
        for (ArbeidsforholdType arb : periodeEn.getArbeidsforholdListe().getArbeidsforhold()) {
            if (!finnesMatch(arb, periodeTo)) {
                alleMatcher = false;
            }
        }
        return alleMatcher;
    }

    private static boolean finnesMatch(AnnenAktivitetType akt, PeriodeType periode) {
        boolean match = false;
        for (AnnenAktivitetType akt2 : periode.getAnnenAktivitetListe().getAnnenAktivitet()) {
            if (likAnnenAktivitetType(akt, akt2)) {
                match = true;
            }
        }
        return match;
    }

    private static boolean finnesMatch(ArbeidsforholdType arb, PeriodeType periode) { //NOSONAR - denne er i bruk...
        boolean match = false;
        for (ArbeidsforholdType arb2 : periode.getArbeidsforholdListe().getArbeidsforhold()) {
            if (likArbeidsforholdType(arb, arb2)) {
                match = true;
            }
        }
        return match;
    }

    private static boolean likNæringType(NæringType næringEn, NæringType næringTo) {
        return Objects.equals(næringEn.getAktivitetDagsats(), næringTo.getAktivitetDagsats()) &&
                Objects.equals(næringEn.getInntekt1(), næringTo.getInntekt1()) &&
                Objects.equals(næringEn.getInntekt2(), næringTo.getInntekt2()) &&
                Objects.equals(næringEn.getInntekt3(), næringTo.getInntekt3()) &&
                Objects.equals(næringEn.getSistLignedeÅr(), næringTo.getSistLignedeÅr());
    }

    private static boolean likAnnenAktivitetType(AnnenAktivitetType akt, AnnenAktivitetType akt2) {
        return Objects.equals(akt.getAktivitetType(), akt2.getAktivitetType()) &&
                Objects.equals(akt.getAktivitetDagsats(), akt2.getAktivitetDagsats());
    }

    private static boolean likArbeidsforholdType(ArbeidsforholdType arb, ArbeidsforholdType arb2) {
        return Objects.equals(arb.getAktivitetDagsats(), arb2.getAktivitetDagsats()) &&
                Objects.equals(arb.getNaturalytelseEndringDato(), arb2.getNaturalytelseEndringDato()) &&
                Objects.equals(arb.getNaturalytelseEndringType(), arb2.getNaturalytelseEndringType()) &&
                Objects.equals(arb.getNaturalytelseNyDagsats(), arb2.getNaturalytelseNyDagsats());
    }
}
