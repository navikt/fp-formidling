package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp;

import java.util.Set;
import java.util.TreeSet;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.FellesMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.LovhjemmelComparator;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.LovhjemmelUtil;
import no.nav.foreldrepenger.fpformidling.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.uttak.ForeldrepengerUttak;
import no.nav.foreldrepenger.fpformidling.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.fpformidling.uttak.kodeliste.PeriodeResultatÅrsak;

public class UttakMapper {

    public UttakMapper() {
        //CDI
    }

    public static String mapLovhjemlerForUttak(ForeldrepengerUttak foreldrepengerUttak, String konsekvensForYtelse, boolean innvilgetRevurdering) {
        Set<String> lovhjemler = new TreeSet<>(new LovhjemmelComparator());
        for (var periode : foreldrepengerUttak.perioder()) {
            var årsak = utledÅrsakskode(periode);
            if (årsak.erUkjent()) {
                continue;
            }
            if (årsak.erGraderingAvslagÅrsak()) {
                LovhjemmelUtil.hentLovhjemlerFraJson(FagsakYtelseType.FORELDREPENGER, periode.getPeriodeResultatÅrsak());
            }
            lovhjemler.addAll(LovhjemmelUtil.hentLovhjemlerFraJson(FagsakYtelseType.FORELDREPENGER, årsak));
        }
        return FellesMapper.formaterLovhjemlerUttak(lovhjemler, konsekvensForYtelse, innvilgetRevurdering);
    }

    private static PeriodeResultatÅrsak utledÅrsakskode(UttakResultatPeriode uttakPeriode) {
        if (erGraderingAvslått(uttakPeriode) && uttakPeriode.isInnvilget()) {
            return uttakPeriode.getGraderingAvslagÅrsak();
        } else if (uttakPeriode.getPeriodeResultatÅrsak() != null) {
            return uttakPeriode.getPeriodeResultatÅrsak();
        }
        return PeriodeResultatÅrsak.UKJENT;
    }

    private static boolean erGraderingAvslått(UttakResultatPeriode uttakPeriode) {
        return !uttakPeriode.erGraderingInnvilget()
                && !uttakPeriode.getGraderingAvslagÅrsak().erUkjent();
    }
}
