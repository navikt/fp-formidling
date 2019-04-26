package no.nav.foreldrepenger.melding.beregningsgrunnlag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import no.nav.foreldrepenger.melding.typer.Beløp;

public class Beregningsgrunnlag {
    private Beløp grunnbeløp;
    private List<BeregningsgrunnlagPeriode> beregningsgrunnlagPerioder = new ArrayList<>();
    private List<BeregningsgrunnlagAktivitetStatus> aktivitetStatuser = new ArrayList<>();
    private Hjemmel hjemmel;

    private Beregningsgrunnlag() {

    }

    public static Beregningsgrunnlag.Builder ny() {
        return new Beregningsgrunnlag.Builder();
    }

    public Beløp getGrunnbeløp() {
        return grunnbeløp;
    }

    public List<BeregningsgrunnlagPeriode> getBeregningsgrunnlagPerioder() {
        return beregningsgrunnlagPerioder;
    }

    public List<BeregningsgrunnlagAktivitetStatus> getAktivitetStatuser() {
        return Collections.unmodifiableList(aktivitetStatuser);
    }

    public Hjemmel getHjemmel() {
        return hjemmel;
    }

    public static class Builder {
        private Beløp grunnbeløp;
        private List<BeregningsgrunnlagPeriode> beregningsgrunnlagPerioder = new ArrayList<>();
        private List<BeregningsgrunnlagAktivitetStatus> aktivitetStatuser = new ArrayList<>();
        private Hjemmel hjemmel;

        public Builder medGrunnbeløp(Beløp grunnbeløp) {
            this.grunnbeløp = grunnbeløp;
            return this;
        }

        public Builder leggTilBeregningsgrunnlagPeriode(BeregningsgrunnlagPeriode beregningsgrunnlagPeriode) {
            this.beregningsgrunnlagPerioder.add(beregningsgrunnlagPeriode);
            return this;
        }

        public Builder leggTilBeregningsgrunnlagAktivitetStatus(BeregningsgrunnlagAktivitetStatus beregningsgrunnlagAktivitetStatus) {
            this.aktivitetStatuser.add(beregningsgrunnlagAktivitetStatus);
            return this;
        }

        public Builder medhHjemmel(Hjemmel hjemmel) {
            this.hjemmel = hjemmel;
            return this;
        }

        public Beregningsgrunnlag build() {
            Beregningsgrunnlag beregningsgrunnlag = new Beregningsgrunnlag();
            beregningsgrunnlag.grunnbeløp = grunnbeløp;
            beregningsgrunnlag.beregningsgrunnlagPerioder = beregningsgrunnlagPerioder;
            beregningsgrunnlag.aktivitetStatuser = aktivitetStatuser;
            beregningsgrunnlag.hjemmel = hjemmel;
            return beregningsgrunnlag;
        }

    }

}
