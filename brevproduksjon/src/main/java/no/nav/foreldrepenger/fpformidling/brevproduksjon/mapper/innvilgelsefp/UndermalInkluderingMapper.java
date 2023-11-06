package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp;

import java.util.List;

import no.nav.foreldrepenger.fpformidling.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Årsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.Utbetalingsperiode;
import no.nav.foreldrepenger.fpformidling.uttak.fp.PeriodeResultatÅrsak;

/**
 * Klassen utleder hvorvidt for forskjellige blokker / undermaler i innvilgelse foreldrepenger brevet skal inkluderes:
 */
public final class UndermalInkluderingMapper {

    private UndermalInkluderingMapper() {
    }

    public static boolean skalInkludereInnvilget(List<Utbetalingsperiode> utbetalingsperioder, String konsekvens) {
        if (KonsekvensForYtelsen.ENDRING_I_BEREGNING.getKode().equals(konsekvens)) {
            return false;
        }
        var innvilgetPerioder = utbetalingsperioder.stream().filter(Utbetalingsperiode::isInnvilget).toList();
        return innvilgetPerioder.size() > 1
            || erRevurderingMedEndringIUttak(konsekvens)
            || harKunEnPeriodeMedGradering(innvilgetPerioder)
            || harKunEnPeriodeMedOverføring(innvilgetPerioder);
    }

    public static boolean skalInkludereAvslag(List<Utbetalingsperiode> utbetalingsperioder, String konsekvens) {
        return utbetalingsperioder.stream().filter(periode -> !periodeMed4102UtenTapteDager(periode)).anyMatch(Utbetalingsperiode::isAvslått)
            && (!KonsekvensForYtelsen.ENDRING_I_BEREGNING.getKode().equals(konsekvens));
    }

    private static boolean periodeMed4102UtenTapteDager(Utbetalingsperiode periode) {
        return Årsak.of(PeriodeResultatÅrsak.BARE_FAR_RETT_IKKE_SØKT.getKode()).equals(periode.getÅrsak()) && periode.getAntallTapteDager() == 0;
    }

    private static boolean harKunEnPeriodeMedGradering(List<Utbetalingsperiode> utbetalingsperioder) {
        return utbetalingsperioder.size() == 1 && utbetalingsperioder.get(0).getÅrsak().erGraderingÅrsak();
    }

    private static boolean harKunEnPeriodeMedOverføring(List<Utbetalingsperiode> utbetalingsperioder) {
        return utbetalingsperioder.size() == 1 && utbetalingsperioder.get(0).getÅrsak().erOverføring();
    }

    private static boolean erRevurderingMedEndringIUttak(String konsekvens) {
        return KonsekvensForYtelsen.ENDRING_I_UTTAK.getKode().equals(konsekvens) || KonsekvensForYtelsen.ENDRING_I_BEREGNING_OG_UTTAK.getKode()
            .equals(konsekvens);
    }
}
