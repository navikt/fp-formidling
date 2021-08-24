package no.nav.foreldrepenger.melding.datamapper.domene.svp;

import no.nav.foreldrepenger.melding.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.melding.typer.DatoIntervall;

public record SVPUtbetalingsperiodeInnvilgelse(Long dagsats, Long utbetaltTilSoker, DatoIntervall periode) {

    public SVPUtbetalingsperiodeInnvilgelse(BeregningsresultatPeriode beregningsresultatPeriode) {
        this(beregningsresultatPeriode.getDagsats(),
                beregningsresultatPeriode.getUtbetaltTilSoker(),
                beregningsresultatPeriode.getPeriode());
    }

    // gettere fordi brevmal-mapper ikke takler records

    public Long getDagsats() {
        return dagsats;
    }

    public Long getUtbetaltTilSoker() {
        return utbetaltTilSoker;
    }

    public DatoIntervall getPeriode() {
        return periode;
    }
}
