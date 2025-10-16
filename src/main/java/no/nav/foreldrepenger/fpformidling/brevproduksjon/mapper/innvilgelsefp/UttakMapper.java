package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp;

import java.util.Set;
import java.util.TreeSet;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.FellesMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.LovhjemmelComparator;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;

public class UttakMapper {

    private UttakMapper() {
    }

    public static String mapLovhjemlerForUttak(BrevGrunnlagDto.Foreldrepenger foreldrepenger, String konsekvensForYtelse, boolean innvilgetRevurdering) {
        Set<String> lovhjemler = new TreeSet<>(new LovhjemmelComparator());
        for (var periode : foreldrepenger.perioderSøker()) {
            if (periode.graderingAvslagÅrsak() != null && !"-".equals(periode.graderingAvslagÅrsak())) {
                var årsak = new PeriodeResultatÅrsak(periode.graderingAvslagÅrsak(),
                    PeriodeResultatÅrsak.GRADERING_AVSLAG_ÅRSAK_DISCRIMINATOR, periode.graderingsAvslagÅrsakLovhjemmel());
                lovhjemler.addAll(årsak.hentLovhjemlerFraJson());
            }
            if (periode.periodeResultatÅrsakLovhjemmel() != null) {
                var årsak = new PeriodeResultatÅrsak(periode.periodeResultatÅrsak(),
                    PeriodeResultatÅrsak.PERIODE_ÅRSAK_DISCRIMINATOR, periode.periodeResultatÅrsakLovhjemmel());
                lovhjemler.addAll(årsak.hentLovhjemlerFraJson());
            }
        }
        return FellesMapper.formaterLovhjemlerUttak(lovhjemler, konsekvensForYtelse, innvilgetRevurdering);
    }
}
