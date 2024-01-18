package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatoVerktøy.erFomRettEtterTomDato;
import static no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Årsak.erRegnetSomLike;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.AnnenAktivitet;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.Arbeidsforhold;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.Næring;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.Vedtaksperiode;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.PeriodeResultatÅrsak;

public final class VedtaksperiodeMerger {

    private VedtaksperiodeMerger() {
    }

    public static List<Vedtaksperiode> mergePerioder(List<Vedtaksperiode> perioder) {
        if (perioder.size() <= 1) {
            return perioder; // ikke noe å se på.
        }
        return slåSammenSammenhengendePerioder(perioder);
    }

    private static List<Vedtaksperiode> slåSammenSammenhengendePerioder(List<Vedtaksperiode> perioder) {
        List<Vedtaksperiode> resultat = new ArrayList<>();
        if (perioder.isEmpty()) {
            return resultat;
        }
        for (var index = 0; index < perioder.size() - 1; index++) {
            var sistePeriode = (index == perioder.size() - 2);
            var periodeEn = perioder.get(index);
            var periodeTo = perioder.get(index + 1);
            if (erPerioderSammenhengendeOgSkalSlåSammen(periodeEn, periodeTo)) {
                var nyPeriode = slåSammenPerioder(periodeEn, periodeTo);
                perioder.set(index + 1, nyPeriode);
                if (sistePeriode) {
                    resultat.add(nyPeriode);
                }
            } else {
                resultat.add(periodeEn);
                if (sistePeriode) {
                    resultat.add(periodeTo);
                }
            }
        }
        return resultat;
    }

    private static Vedtaksperiode slåSammenPerioder(Vedtaksperiode periodeEn, Vedtaksperiode periodeTo) {
        return Vedtaksperiode.ny()
            .medInnvilget(periodeEn.isInnvilget())
            .medÅrsak(periodeEn.getÅrsak())
            .medPeriodeFom(periodeEn.getPeriodeFom(), periodeEn.getSpråkkode())
            .medPeriodeTom(periodeTo.getPeriodeTom(), periodeEn.getSpråkkode())
            .medPeriodeDagsats(periodeEn.getPeriodeDagsats())
            .medAntallTapteDager(finnRiktigAntallTapteDager(periodeEn, periodeTo), BigDecimal.ZERO)
            .medPrioritertUtbetalingsgrad(periodeEn.getPrioritertUtbetalingsgrad())
            .medFullUtbetaling(periodeEn.isFullUtbetaling())
            .medStønadskontoType(periodeEn.getStønadskontoType())
            .medArbeidsforhold(periodeEn.getArbeidsforholdsliste())
            .medNæring(periodeEn.getNæring())
            .medAnnenAktivitet(periodeEn.getAnnenAktivitetsliste())
            .medTidligstMottattDatoAlleredeFormatert(periodeEn.getTidligstMottattDato())
            .build();
    }

    private static int finnRiktigAntallTapteDager(Vedtaksperiode periodeEn, Vedtaksperiode periodeTo) {
        var tapteDagerPeriodeEn = periodeEn.getTapteDagerTemp();
        var tapteDagerPeriodeTo = periodeTo.getTapteDagerTemp();

        if (!Objects.equals(tapteDagerPeriodeEn, BigDecimal.ZERO) && !Objects.equals(tapteDagerPeriodeTo, BigDecimal.ZERO)) {
            return tapteDagerPeriodeEn.add(tapteDagerPeriodeTo).setScale(1, RoundingMode.DOWN).intValue();
        } else {
            return periodeEn.getAntallTapteDager() + periodeTo.getAntallTapteDager();
        }
    }

    private static boolean erPerioderSammenhengendeOgSkalSlåSammen(Vedtaksperiode periodeEn, Vedtaksperiode periodeTo) {
        return sammeStatusOgÅrsak(periodeEn, periodeTo) && likPeriodeDagsats(periodeEn, periodeTo) && likeAktiviteter(periodeEn, periodeTo)
            && erFomRettEtterTomDato(periodeEn.getPeriodeTom(), periodeTo.getPeriodeFom()) && hvisAvslåttSøknadsfristLikeTidligstMottattDato(periodeEn, periodeTo);
    }

    private static boolean hvisAvslåttSøknadsfristLikeTidligstMottattDato(Vedtaksperiode periodeEn, Vedtaksperiode periodeTo) {
        if (periodeEn.getÅrsak() != null && PeriodeResultatÅrsak.SØKNADSFRIST.getKode().equals((periodeEn.getÅrsak()).getKode())) {
            return Objects.equals(periodeEn.getÅrsak(), periodeTo.getÅrsak()) && periodeEn.getTidligstMottattDato().equals(periodeTo.getTidligstMottattDato());
        } else {
            return true;
        }
    }

    static boolean likeAktiviteter(Vedtaksperiode periodeEn, Vedtaksperiode periodeTo) {
        return likeArbeidsforhold(periodeEn, periodeTo) && likNæring(periodeEn, periodeTo) && likeAndreAktiviteter(periodeEn, periodeTo);
    }

    private static boolean sammeStatusOgÅrsak(Vedtaksperiode periodeEn, Vedtaksperiode periodeTo) {
        return (Objects.equals(periodeEn.isInnvilget(), periodeTo.isInnvilget()) || Objects.equals(periodeEn.isAvslått(), periodeTo.isAvslått())) && (
            Objects.equals(periodeEn.getÅrsak(), periodeTo.getÅrsak()) || erRegnetSomLike(periodeEn.getÅrsak(), periodeTo.getÅrsak()));
    }

    private static boolean likPeriodeDagsats(Vedtaksperiode periodeEn, Vedtaksperiode periodeTo) {
        return Objects.equals(periodeEn.getPeriodeDagsats(), periodeTo.getPeriodeDagsats());
    }

    private static boolean likeAndreAktiviteter(Vedtaksperiode periodeEn, Vedtaksperiode periodeTo) {
        var alleMatcher = likAktivitetsliste(periodeEn, periodeTo);
        if (!alleMatcher) {
            return false;
        }
        if (periodeEn.getAnnenAktivitetsliste() == null) {
            return true;
        }
        for (var akt : periodeEn.getAnnenAktivitetsliste()) {
            if (!finnesMatch(akt, periodeTo)) {
                alleMatcher = false;
            }
        }
        return alleMatcher;
    }

    private static boolean likAktivitetsliste(Vedtaksperiode periodeEn, Vedtaksperiode periodeTo) {
        if (periodeEn.getAnnenAktivitetsliste() == null && periodeTo.getAnnenAktivitetsliste() == null) {
            return true;
        }
        if ((periodeEn.getAnnenAktivitetsliste() == null) != (periodeTo.getAnnenAktivitetsliste() == null)) {
            return false;
        }
        return periodeEn.getAnnenAktivitetsliste().size() == periodeTo.getAnnenAktivitetsliste().size();
    }

    private static boolean harLikeMangeArbeidsforhold(Vedtaksperiode periodeEn, Vedtaksperiode periodeTo) {
        if (periodeEn.getArbeidsforholdsliste() == null && periodeTo.getArbeidsforholdsliste() == null) {
            return true;
        }
        if ((periodeEn.getArbeidsforholdsliste() == null) != (periodeTo.getArbeidsforholdsliste() == null)) {
            return false;
        }
        return periodeEn.getArbeidsforholdsliste().size() == periodeTo.getArbeidsforholdsliste().size();
    }

    private static boolean likNæring(Vedtaksperiode periodeEn, Vedtaksperiode periodeTo) {
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

    private static boolean likeArbeidsforhold(Vedtaksperiode periodeEn, Vedtaksperiode periodeTo) {
        var alleMatcher = harLikeMangeArbeidsforhold(periodeEn, periodeTo);
        if (!alleMatcher) {
            return false;
        }
        if (periodeEn.getArbeidsforholdsliste() == null) {
            return true;
        }
        for (var arb : periodeEn.getArbeidsforholdsliste()) {
            if (!finnesMatch(arb, periodeTo)) {
                alleMatcher = false;
            }
        }
        return alleMatcher;
    }

    private static boolean finnesMatch(AnnenAktivitet akt, Vedtaksperiode periode) {
        var match = false;
        for (var akt2 : periode.getAnnenAktivitetsliste()) {
            if (likAnnenAktivitetType(akt, akt2)) {
                match = true;
            }
        }
        return match;
    }

    private static boolean finnesMatch(Arbeidsforhold arb, Vedtaksperiode periode) {
        var match = false;
        for (var arb2 : periode.getArbeidsforholdsliste()) {
            if (likArbeidsforholdType(arb, arb2)) {
                match = true;
            }
        }
        return match;
    }

    private static boolean likNæringType(Næring næringEn, Næring næringTo) {
        return Objects.equals(næringEn.getSistLignedeÅr(), næringTo.getSistLignedeÅr()) && Objects.equals(næringEn.getAktivitetDagsats(),
            næringTo.getAktivitetDagsats());
    }

    private static boolean likAnnenAktivitetType(AnnenAktivitet akt, AnnenAktivitet akt2) {
        return Objects.equals(akt.getAktivitetStatus(), akt2.getAktivitetStatus()) && Objects.equals(akt.getAktivitetDagsats(),
            akt2.getAktivitetDagsats());
    }

    private static boolean likArbeidsforholdType(Arbeidsforhold arb, Arbeidsforhold arb2) {
        return Objects.equals(arb.getArbeidsgiverNavn(), arb2.getArbeidsgiverNavn()) && Objects.equals(arb.getAktivitetDagsats(),
            arb2.getAktivitetDagsats()) && Objects.equals(arb.getNaturalytelseEndringType(), arb2.getNaturalytelseEndringType()) && Objects.equals(
            arb.getBruttoInkludertBortfaltNaturalytelsePrAar(), arb2.getBruttoInkludertBortfaltNaturalytelsePrAar());
    }
}
