package no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Objects;

import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.AnnenAktivitet;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.Arbeidsforhold;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.Næring;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.Utbetalingsperiode;

public final class UtbetalingsperiodeMergerFelles {
    static Utbetalingsperiode slåSammenPerioder(Utbetalingsperiode periodeEn, Utbetalingsperiode periodeTo) {
        periodeEn.setAntallTapteDager(periodeEn.getAntallTapteDager() + (periodeTo.getAntallTapteDager()));
        periodeEn.setPeriodeTom(periodeTo.getPeriodeTom());
        return periodeEn;
    }

    static boolean erFomRettEtterTomDato(Utbetalingsperiode periodeEn, Utbetalingsperiode periodeTo) {
        return erFomRettEtterTomDato(periodeEn.getPeriodeTom(), periodeTo.getPeriodeFom());
    }

    static boolean likeAktiviteter(Utbetalingsperiode periodeEn, Utbetalingsperiode periodeTo) {
        return likeArbeidsforhold(periodeEn, periodeTo) && likNæring(periodeEn, periodeTo) && likeAndreAktiviteter(periodeEn, periodeTo);
    }

    private static boolean erFomRettEtterTomDato(LocalDate periodeEnTom, LocalDate periodeToFom) {
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

    private static boolean likeAndreAktiviteter(Utbetalingsperiode periodeEn, Utbetalingsperiode periodeTo) {
        boolean alleMatcher = likAktivitetsliste(periodeEn, periodeTo);
        if (!alleMatcher) {
            return false;
        }
        if (periodeEn.getAnnenAktivitetsliste() == null) {
            return true;
        }
        for (AnnenAktivitet akt : periodeEn.getAnnenAktivitetsliste()) {
            if (!finnesMatch(akt, periodeTo)) {
                alleMatcher = false;
            }
        }
        return alleMatcher;
    }

    private static boolean likAktivitetsliste(Utbetalingsperiode periodeEn, Utbetalingsperiode periodeTo) {
        if (periodeEn.getAnnenAktivitetsliste() == null && periodeTo.getAnnenAktivitetsliste() == null) {
            return true;
        }
        if ((periodeEn.getAnnenAktivitetsliste() == null) != (periodeTo.getAnnenAktivitetsliste() == null)) {
            return false;
        }
        return periodeEn.getAnnenAktivitetsliste().size() == periodeTo.getAnnenAktivitetsliste().size();
    }

    private static boolean harLikeMangeArbeidsforhold(Utbetalingsperiode periodeEn, Utbetalingsperiode periodeTo) {
        if (periodeEn.getArbeidsforholdsliste() == null && periodeTo.getArbeidsforholdsliste() == null) {
            return true;
        }
        if ((periodeEn.getArbeidsforholdsliste() == null) != (periodeTo.getArbeidsforholdsliste() == null)) {
            return false;
        }
        return periodeEn.getArbeidsforholdsliste().size() == periodeTo.getArbeidsforholdsliste().size();
    }

    private static boolean likNæring(Utbetalingsperiode periodeEn, Utbetalingsperiode periodeTo) {
        if (periodeEn.getNæring() == null && periodeTo.getNæring() == null) {
            return true;
        } else if ((periodeEn.getNæring() == null) != (periodeTo.getNæring() == null)) {
            return false;
        }
        if (periodeEn.getNæring() == null && periodeTo.getNæring() == null) {
            return true;
        } else if ((periodeEn.getNæring() == null) != (periodeTo.getNæring() == null)) {
            return false;
        }
        return likNæringType(periodeEn.getNæring(), periodeTo.getNæring());
    }

    private static boolean likeArbeidsforhold(Utbetalingsperiode periodeEn, Utbetalingsperiode periodeTo) {
        boolean alleMatcher = harLikeMangeArbeidsforhold(periodeEn, periodeTo);
        if (!alleMatcher) {
            return false;
        }
        if (periodeEn.getArbeidsforholdsliste() == null) {
            return true;
        }
        for (Arbeidsforhold arb : periodeEn.getArbeidsforholdsliste()) {
            if (!finnesMatch(arb, periodeTo)) {
                alleMatcher = false;
            }
        }
        return alleMatcher;
    }

    private static boolean finnesMatch(AnnenAktivitet akt, Utbetalingsperiode periode) {
        boolean match = false;
        for (AnnenAktivitet akt2 : periode.getAnnenAktivitetsliste()) {
            if (likAnnenAktivitetType(akt, akt2)) {
                match = true;
            }
        }
        return match;
    }

    private static boolean finnesMatch(Arbeidsforhold arb, Utbetalingsperiode periode) {
        boolean match = false;
        for (Arbeidsforhold arb2 : periode.getArbeidsforholdsliste()) {
            if (likArbeidsforholdType(arb, arb2)) {
                match = true;
            }
        }
        return match;
    }

    private static boolean likNæringType(Næring næringEn, Næring næringTo) {
        return Objects.equals(næringEn.getSistLignedeÅr(), næringTo.getSistLignedeÅr());
    }

    private static boolean likAnnenAktivitetType(AnnenAktivitet akt, AnnenAktivitet akt2) {
        return Objects.equals(akt.getAktivitetStatus(), akt2.getAktivitetStatus());
    }

    private static boolean likArbeidsforholdType(Arbeidsforhold arb, Arbeidsforhold arb2) {
        return Objects.equals(arb.getArbeidsgiverNavn(), arb2.getArbeidsgiverNavn()) &&
               Objects.equals(arb.getAktivitetDagsats(), arb2.getAktivitetDagsats()) &&
               Objects.equals(arb.getNaturalytelseEndringDato(), arb2.getNaturalytelseEndringDato()) &&
               Objects.equals(arb.getNaturalytelseEndringType(), arb2.getNaturalytelseEndringType()) &&
               Objects.equals(arb.getNaturalytelseNyDagsats(), arb2.getNaturalytelseNyDagsats());
    }
}
