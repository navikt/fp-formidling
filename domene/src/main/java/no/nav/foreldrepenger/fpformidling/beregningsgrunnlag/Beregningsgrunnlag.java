package no.nav.foreldrepenger.fpformidling.beregningsgrunnlag;

import no.nav.foreldrepenger.fpformidling.typer.Beløp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Beregningsgrunnlag {
    private Beløp grunnbeløp;
    private List<BeregningsgrunnlagPeriode> beregningsgrunnlagPerioder = new ArrayList<>();
    private List<BeregningsgrunnlagAktivitetStatus> aktivitetStatuser = new ArrayList<>();
    private Hjemmel hjemmel;
    private boolean erBesteberegnet;

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

    public boolean getErBesteberegnet() {
        return erBesteberegnet;
    }

    public static class Builder {
        private Beløp grunnbeløp;
        private List<BeregningsgrunnlagPeriode> beregningsgrunnlagPerioder = new ArrayList<>();
        private List<BeregningsgrunnlagAktivitetStatus> aktivitetStatuser = new ArrayList<>();
        private Hjemmel hjemmel;
        private boolean erBesteberegnet;

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

        public Builder medBesteberegnet(boolean erBesteberegnet) {
            this.erBesteberegnet = erBesteberegnet;
            return this;
        }

        public Beregningsgrunnlag build() {
            var beregningsgrunnlag = new Beregningsgrunnlag();
            beregningsgrunnlag.grunnbeløp = grunnbeløp;
            beregningsgrunnlag.beregningsgrunnlagPerioder = beregningsgrunnlagPerioder;
            beregningsgrunnlag.aktivitetStatuser = aktivitetStatuser;
            beregningsgrunnlag.hjemmel = hjemmel;
            beregningsgrunnlag.erBesteberegnet = erBesteberegnet;
            return beregningsgrunnlag;
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        var that = (Beregningsgrunnlag) o;
        return erBesteberegnet == that.erBesteberegnet && Objects.equals(grunnbeløp, that.grunnbeløp) && Objects.equals(beregningsgrunnlagPerioder,
            that.beregningsgrunnlagPerioder) && Objects.equals(aktivitetStatuser, that.aktivitetStatuser) && hjemmel == that.hjemmel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(grunnbeløp, beregningsgrunnlagPerioder, aktivitetStatuser, hjemmel, erBesteberegnet);
    }

    @Override
    public String toString() {
        return "Beregningsgrunnlag{" + "grunnbeløp=" + grunnbeløp + ", beregningsgrunnlagPerioder=" + beregningsgrunnlagPerioder
            + ", aktivitetStatuser=" + aktivitetStatuser + ", hjemmel=" + hjemmel + ", erBesteberegnet=" + erBesteberegnet + '}';
    }
}
