package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp;

import java.util.Set;
import java.util.TreeSet;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.FellesMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.LovhjemmelComparator;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlag;

public class UttakMapper {

    private UttakMapper() {
    }

    public static String mapLovhjemlerForUttak(BrevGrunnlag.ForeldrepengerUttak foreldrepengerUttak, String konsekvensForYtelse, boolean innvilgetRevurdering) {
        Set<String> lovhjemler = new TreeSet<>(new LovhjemmelComparator());
        for (var periode : foreldrepengerUttak.perioderSøker()) {
            //TODO TFP-6069 - riktig?
            if (!periode.graderingAvslagÅrsak().equals("-")) {
                lovhjemler.add(periode.graderingsAvslagÅrsakLovhjemmel());
            }
            lovhjemler.add(periode.periodeResultatÅrsakLovhjemmel());
        }
        return FellesMapper.formaterLovhjemlerUttak(lovhjemler, konsekvensForYtelse, innvilgetRevurdering);
    }
}
