package no.nav.foreldrepenger.fpsak.mapper.sortering;

import java.util.Comparator;

import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelsePeriode;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.BeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.fpformidling.uttak.UttakResultatPeriode;

public class PeriodeComparator {
    public static final Comparator<TilkjentYtelsePeriode> TILKJENTYTELSERESULTAT =
            Comparator.comparing(TilkjentYtelsePeriode::getPeriodeFom);

    public static final Comparator<BeregningsgrunnlagPeriode> BEREGNINGSGRUNNLAG =
            Comparator.comparing(BeregningsgrunnlagPeriode::getBeregningsgrunnlagPeriodeFom);

    public static final Comparator<UttakResultatPeriode> UTTAKRESULTAT =
            Comparator.comparing(UttakResultatPeriode::getFom);
}
