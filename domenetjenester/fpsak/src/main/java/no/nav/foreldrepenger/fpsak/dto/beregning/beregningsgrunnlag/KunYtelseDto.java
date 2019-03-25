package no.nav.foreldrepenger.fpsak.dto.beregning.beregningsgrunnlag;

import java.util.ArrayList;
import java.util.List;

public class KunYtelseDto {

    private List<BrukersAndelDto> andeler = new ArrayList<>();
    private boolean fodendeKvinneMedDP;
    private Boolean erBesteberegning = null;

    public List<BrukersAndelDto> getAndeler() {
        return andeler;
    }

    public void setAndeler(List<BrukersAndelDto> andeler) {
        this.andeler = andeler;
    }

    public void leggTilAndel(BrukersAndelDto andel) {
        andeler.add(andel);
    }

    public boolean isFodendeKvinneMedDP() {
        return fodendeKvinneMedDP;
    }

    public void setFodendeKvinneMedDP(boolean fodendeKvinneMedDP) {
        this.fodendeKvinneMedDP = fodendeKvinneMedDP;
    }

    public Boolean getErBesteberegning() {
        return erBesteberegning;
    }

    public void setErBesteberegning(boolean erBesteberegning) {
        this.erBesteberegning = erBesteberegning;
    }
}
