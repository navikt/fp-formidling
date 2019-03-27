package no.nav.foreldrepenger.melding.behandling.beregningsgrunnlag;

import java.util.ArrayList;
import java.util.List;

public class Beregningsgrunnlag {
    private int grunnbeløp;
    private String hjemmel; //BeregningsgrunnlagAktivitetStatus.hjemmel
    private List<BeregningsgrunnlagPeriode> beregningsgrunnlagPerioder = new ArrayList<>();

    public int getGrunnbeløp() {
        return grunnbeløp;
    }

    public String getHjemmel() {
        return hjemmel;
    }

    public List<BeregningsgrunnlagPeriode> getBeregningsgrunnlagPerioder() {
        return beregningsgrunnlagPerioder;
    }
}
