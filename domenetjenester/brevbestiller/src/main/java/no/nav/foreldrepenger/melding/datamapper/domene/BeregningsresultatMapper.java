package no.nav.foreldrepenger.melding.datamapper.domene;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import no.nav.foreldrepenger.melding.beregning.BeregningsresultatAndel;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatFP;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatPeriode;

public final class BeregningsresultatMapper {

    private BeregningsresultatMapper() {
    }

    public static long finnTotalBrukerAndel(BeregningsresultatFP beregningsresultatFP) {
        return harBrukerAndel(beregningsresultatFP) ? 1 : 0; // Dette er i praksis en boolean, så holder med å bruke 0 og 1
    }

    private static boolean harBrukerAndel(BeregningsresultatFP beregningsresultatFP) {
        return beregningsresultatFP.getBeregningsresultatPerioder().stream()
                .map(BeregningsresultatPeriode::getBeregningsresultatAndelList)
                .flatMap(List::stream)
                .anyMatch(BeregningsresultatAndel::erBrukerMottaker);
    }

    private static Optional<BeregningsresultatPeriode> finnFørsteInnvilgedePeriode(BeregningsresultatFP beregningsresultat) {
        return beregningsresultat.getBeregningsresultatPerioder()
                .stream()
                .filter(harDagsatsOverNull())
                .min(Comparator.comparing(BeregningsresultatPeriode::getBeregningsresultatPeriodeFom));
    }

    private static Predicate<BeregningsresultatPeriode> harDagsatsOverNull() {
        return beregningsresultatPeriode -> beregningsresultatPeriode.getDagsats() != null && beregningsresultatPeriode.getDagsats() > 0;
    }

    public static long finnMånedsbeløp(BeregningsresultatFP beregningsresultat) {
        return finnFørsteInnvilgedePeriode(beregningsresultat).map(BeregningsresultatMapper::getMånedsbeløp).orElse(0L);
    }

    private static long getMånedsbeløp(BeregningsresultatPeriode førstePeriode) {
        return førstePeriode.getDagsats() * 260 / 12;
    }
}
