package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper.sortering;

import java.util.Comparator;

import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.BeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.fpformidling.domene.tilkjentytelse.TilkjentYtelsePeriode;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.UttakResultatPeriode;

public class PeriodeComparator {

    private PeriodeComparator() {
    }

    public static final Comparator<TilkjentYtelsePeriode> TILKJENTYTELSERESULTAT = Comparator.comparing(TilkjentYtelsePeriode::getPeriodeFom);

    public static final Comparator<BeregningsgrunnlagPeriode> BEREGNINGSGRUNNLAG = Comparator.comparing(
        BeregningsgrunnlagPeriode::getBeregningsgrunnlagPeriodeFom);

    public static final Comparator<UttakResultatPeriode> UTTAKRESULTAT = Comparator.comparing(UttakResultatPeriode::getFom);
}
