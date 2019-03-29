package no.nav.foreldrepenger.melding.beregning;

import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsresultat.BeregningsresultatEngangsstønadDto;

public class BeregningsresultatES {
    private Long beløp;

    public BeregningsresultatES(BeregningsresultatEngangsstønadDto dto) {
        this.beløp = dto.getBeregnetTilkjentYtelse();
    }

    public Long getBeløp() {
        return beløp;
    }
}
