package no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp;

import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.UtbetalingsperiodeMergerFelles.erFomRettEtterTomDato;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.UtbetalingsperiodeMergerFelles.likeAktiviteter;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.UtbetalingsperiodeMergerFelles.slåSammenPerioder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.Utbetalingsperiode;

public final class UtbetalingsperiodeMerger {
    private static List<String> nonEqualKoderSomLikevelOppfyllerMerge =
            List.of("2001", "2002", "2003", "2004", "2005", "2006", "2007");

    public static List<Utbetalingsperiode> mergePerioder(List<Utbetalingsperiode> perioder) {
        if (perioder.size() <= 1) {
            return perioder; // ikke noe å se på.
        }
        return slåSammenSammenhengendePerioder(perioder);
    }

    private static List<Utbetalingsperiode> slåSammenSammenhengendePerioder(List<Utbetalingsperiode> perioder) {
        List<Utbetalingsperiode> resultat = new ArrayList<>();
        if (perioder.isEmpty()) {
            return resultat;
        }
        for (int index = 0; index < perioder.size() - 1; index++) {
            boolean sistePeriode = (index == perioder.size() - 2);
            Utbetalingsperiode periodeEn = perioder.get(index);
            Utbetalingsperiode periodeTo = perioder.get(index + 1);
            if (erPerioderSammenhengendeOgSkalSlåSammen(periodeEn, periodeTo)) {
                Utbetalingsperiode nyPeriode = slåSammenPerioder(periodeEn, periodeTo);
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

    private static boolean erPerioderSammenhengendeOgSkalSlåSammen(Utbetalingsperiode periodeEn, Utbetalingsperiode periodeTo) {
        return sammeStatusOgÅrsak(periodeEn, periodeTo) && likeAktiviteter(periodeEn, periodeTo) && erFomRettEtterTomDato(periodeEn, periodeTo);
    }

    private static boolean sammeStatusOgÅrsak(Utbetalingsperiode periodeEn, Utbetalingsperiode periodeTo) {
        return Objects.equals(periodeEn.isInnvilget(), periodeTo.isInnvilget())
                && (Objects.equals(periodeEn.getÅrsak(), periodeTo.getÅrsak())
                || erRegnetSomLike(periodeEn.getÅrsak(), periodeTo.getÅrsak()));
    }

    private static boolean erRegnetSomLike(String årsak1, String årsak2) {
        return nonEqualKoderSomLikevelOppfyllerMerge.containsAll(Set.of(årsak1, årsak2));
    }
}
