package no.nav.foreldrepenger.melding.beregningsgrunnlag;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import no.nav.foreldrepenger.melding.typer.DatoIntervall;

public class BeregningsgrunnlagPeriode {
    private Long dagsats;
    private BigDecimal bruttoPrÅr;
    private BigDecimal redusertPrÅr;
    private BigDecimal avkortetPrÅr;
    private List<String> periodeÅrsakKoder;
    private DatoIntervall periode;
    private List<BeregningsgrunnlagPrStatusOgAndel> beregningsgrunnlagPrStatusOgAndelList = new ArrayList<>();

    private BeregningsgrunnlagPeriode() {
    }

    private BeregningsgrunnlagPeriode(Builder builder) {
        dagsats = builder.dagsats;
        bruttoPrÅr = builder.bruttoPrÅr;
        redusertPrÅr = builder.redusertPrÅr;
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

    public List<String> getPeriodeÅrsakKoder() {
        return periodeÅrsakKoder;
    }

    public BigDecimal getRedusertPrÅr() {
        return redusertPrÅr;
    }

    public BigDecimal getAvkortetPrÅr() { return avkortetPrÅr;
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
        private BigDecimal redusertPrÅr;
        private BigDecimal avkortetPrÅr;
        private List<String> periodeÅrsaker;
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

        public Builder medRedusertPrÅr(BigDecimal val) {
            redusertPrÅr = val;
            return this;
        }

        public Builder medAvkortetPrÅr(BigDecimal val) {
            avkortetPrÅr = val;
            return this;
        }

        public Builder medperiodeÅrsaker(List<String> val) {
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
}
