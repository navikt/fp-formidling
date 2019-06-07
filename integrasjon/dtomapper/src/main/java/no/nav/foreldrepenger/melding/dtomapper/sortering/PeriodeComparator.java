package no.nav.foreldrepenger.melding.dtomapper.sortering;

import java.util.Comparator;

import no.nav.foreldrepenger.melding.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriode;

public class PeriodeComparator {
    public static final Comparator<BeregningsresultatPeriode> BEREGNINGSRESULTAT =
            Comparator.comparing(BeregningsresultatPeriode::getBeregningsresultatPeriodeFom);

    public static final Comparator<BeregningsgrunnlagPeriode> BEREGNINGSGRUNNLAG =
            Comparator.comparing(BeregningsgrunnlagPeriode::getBeregningsgrunnlagPeriodeFom);

    public static final Comparator<UttakResultatPeriode> UTTAKRESULTAT =
            Comparator.comparing(UttakResultatPeriode::getFomDato);
}
