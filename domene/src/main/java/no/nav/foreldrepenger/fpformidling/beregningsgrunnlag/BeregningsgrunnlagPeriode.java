package no.nav.foreldrepenger.fpformidling.beregningsgrunnlag;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import no.nav.foreldrepenger.fpformidling.typer.DatoIntervall;

public class BeregningsgrunnlagPeriode {
    private Long dagsats;
    private BigDecimal bruttoPrÅr;
    private BigDecimal avkortetPrÅr;
    private List<PeriodeÅrsak> periodeÅrsakKoder;
    private DatoIntervall periode;
    private List<BeregningsgrunnlagPrStatusOgAndel> beregningsgrunnlagPrStatusOgAndelList = new ArrayList<>();

    private BeregningsgrunnlagPeriode() {
    }

    private BeregningsgrunnlagPeriode(Builder builder) {
        dagsats = builder.dagsats;
        bruttoPrÅr = builder.bruttoPrÅr;
        avkortetPrÅr = builder.avkortetPrÅr;
        periodeÅrsakKoder = builder.periodeÅrsaker;
        periode = builder.periode;
        beregningsgrunnlagPrStatusOgAndelList = builder.beregningsgrunnlagPrStatusOgAndelList;
    }

    public static Builder ny() {
        return new Builder();
    }

    public Long getDagsats() {
        return dagsats;
    }

    public BigDecimal getBruttoPrÅr() {
        return bruttoPrÅr;
    }

    public List<PeriodeÅrsak> getPeriodeÅrsakKoder() {
        return periodeÅrsakKoder;
    }

    public BigDecimal getAvkortetPrÅr() {
        return avkortetPrÅr;
    }

    public LocalDate getBeregningsgrunnlagPeriodeFom() {
        return periode.getFomDato();
    }

    public LocalDate getBeregningsgrunnlagPeriodeTom() {
        return periode.getTomDato();
    }

    public List<BeregningsgrunnlagPrStatusOgAndel> getBeregningsgrunnlagPrStatusOgAndelList() {
        return Collections.unmodifiableList(beregningsgrunnlagPrStatusOgAndelList);
    }

    public static final class Builder {
        private Long dagsats;
        private BigDecimal bruttoPrÅr;
        private BigDecimal avkortetPrÅr;
        private List<PeriodeÅrsak> periodeÅrsaker;
        private DatoIntervall periode;
        private List<BeregningsgrunnlagPrStatusOgAndel> beregningsgrunnlagPrStatusOgAndelList;

        private Builder() {
        }

        public Builder medDagsats(Long val) {
            dagsats = val;
            return this;
        }

        public Builder medBruttoPrÅr(BigDecimal val) {
            bruttoPrÅr = val;
            return this;
        }

        public Builder medAvkortetPrÅr(BigDecimal val) {
            avkortetPrÅr = val;
            return this;
        }

        public Builder medperiodeÅrsaker(List<PeriodeÅrsak> val) {
            periodeÅrsaker = val;
            return this;
        }

        public Builder medPeriode(DatoIntervall val) {
            periode = val;
            return this;
        }

        public Builder medBeregningsgrunnlagPrStatusOgAndelList(List<BeregningsgrunnlagPrStatusOgAndel> val) {
            beregningsgrunnlagPrStatusOgAndelList = val;
            return this;
        }

        public BeregningsgrunnlagPeriode build() {
            return new BeregningsgrunnlagPeriode(this);
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
        var that = (BeregningsgrunnlagPeriode) o;
        return Objects.equals(dagsats, that.dagsats) && Objects.equals(bruttoPrÅr, that.bruttoPrÅr) && Objects.equals(avkortetPrÅr, that.avkortetPrÅr)
            && Objects.equals(periodeÅrsakKoder, that.periodeÅrsakKoder) && Objects.equals(periode, that.periode) && Objects.equals(
            beregningsgrunnlagPrStatusOgAndelList, that.beregningsgrunnlagPrStatusOgAndelList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dagsats, bruttoPrÅr, avkortetPrÅr, periodeÅrsakKoder, periode, beregningsgrunnlagPrStatusOgAndelList);
    }

    @Override
    public String toString() {
        return "BeregningsgrunnlagPeriode{" + "dagsats=" + dagsats + ", bruttoPrÅr=" + bruttoPrÅr + ", avkortetPrÅr=" + avkortetPrÅr
            + ", periodeÅrsakKoder=" + periodeÅrsakKoder + ", periode=" + periode + ", beregningsgrunnlagPrStatusOgAndelList="
            + beregningsgrunnlagPrStatusOgAndelList + '}';
    }
}
