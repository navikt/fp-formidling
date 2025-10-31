package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp;

import java.util.List;

import no.nav.foreldrepenger.fpformidling.domene.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Årsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.Vedtaksperiode;

/**
 * Klassen utleder hvorvidt for forskjellige blokker / undermaler i innvilgelse foreldrepenger brevet skal inkluderes:
 */
public final class UndermalInkluderingMapper {

    private UndermalInkluderingMapper() {
    }

    public static boolean skalInkludereInnvilget(List<Vedtaksperiode> vedtaksperioder, KonsekvensForYtelsen konsekvens) {
        if (KonsekvensForYtelsen.ENDRING_I_BEREGNING.equals(konsekvens)) {
            return false;
        }
        var innvilgetPerioder = vedtaksperioder.stream().filter(Vedtaksperiode::isInnvilget).toList();
        return innvilgetPerioder.size() > 1
            || erRevurderingMedEndringIUttak(konsekvens)
            || harKunEnPeriodeMedGradering(innvilgetPerioder)
            || harKunEnPeriodeMedOverføring(innvilgetPerioder);
    }

    public static boolean skalInkludereAvslag(List<Vedtaksperiode> vedtaksperioder, KonsekvensForYtelsen konsekvens) {
        return vedtaksperioder.stream().filter(periode -> !periodeMed4102UtenTapteDager(periode)).anyMatch(Vedtaksperiode::isAvslått)
            && (!KonsekvensForYtelsen.ENDRING_I_BEREGNING.equals(konsekvens));
    }

    private static boolean periodeMed4102UtenTapteDager(Vedtaksperiode periode) {
        return Årsak.of(PeriodeResultatÅrsak.BARE_FAR_RETT_IKKE_SØKT.getKode()).equals(periode.getÅrsak()) && periode.getAntallTapteDager() == 0;
    }

    private static boolean harKunEnPeriodeMedGradering(List<Vedtaksperiode> vedtaksperioder) {
        return vedtaksperioder.size() == 1 && vedtaksperioder.get(0).getÅrsak().erGraderingÅrsak();
    }

    private static boolean harKunEnPeriodeMedOverføring(List<Vedtaksperiode> vedtaksperioder) {
        return vedtaksperioder.size() == 1 && vedtaksperioder.get(0).getÅrsak().erOverføring();
    }

    private static boolean erRevurderingMedEndringIUttak(KonsekvensForYtelsen konsekvens) {
        return KonsekvensForYtelsen.ENDRING_I_UTTAK.equals(konsekvens) || KonsekvensForYtelsen.ENDRING_I_BEREGNING_OG_UTTAK.equals(konsekvens);
    }
}
