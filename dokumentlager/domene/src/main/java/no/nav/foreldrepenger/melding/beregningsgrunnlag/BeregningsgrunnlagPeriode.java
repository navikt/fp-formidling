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
    private String periodeÅrsak; //Kode beregningsgrunnlagPeriodeÅrsaker.periodeÅrsak
    private DatoIntervall periode;
    private List<BeregningsgrunnlagPrStatusOgAndel> beregningsgrunnlagPrStatusOgAndelList = new ArrayList<>();

    private BeregningsgrunnlagPeriode() {
    }

    public Long getDagsats() {
        return dagsats;
    }

    public BigDecimal getBruttoPrÅr() {
        return bruttoPrÅr;
    }

    public String getPeriodeÅrsak() {
        return periodeÅrsak;
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

    public static class Builder {
        private Long dagsats;
        private BigDecimal bruttoPrÅr;
        private String periodeÅrsak; //Kode beregningsgrunnlagPeriodeÅrsaker.periodeÅrsak
        private DatoIntervall periode;
        private List<BeregningsgrunnlagPrStatusOgAndel> beregningsgrunnlagPrStatusOgAndelList = new ArrayList<>();

        public Builder medDagsats(Long dagsats) {
            this.dagsats = dagsats;
            return this;
        }

        public Builder medBruttoPrÅr(BigDecimal bruttoPrÅr) {
            this.bruttoPrÅr = bruttoPrÅr;
            return this;
        }

        public Builder medPeriodeÅrsak(String periodeÅrsak) {
            this.periodeÅrsak = periodeÅrsak;
            return this;
        }

        public Builder medPeriode(DatoIntervall periode) {
            this.periode = periode;
            return this;
        }

        public Builder leggTilAndel(BeregningsgrunnlagPrStatusOgAndel andel) {
            this.beregningsgrunnlagPrStatusOgAndelList.add(andel);
            return this;
        }

        public BeregningsgrunnlagPeriode build() {
            BeregningsgrunnlagPeriode beregningsgrunnlagPeriode = new BeregningsgrunnlagPeriode();
            beregningsgrunnlagPeriode.dagsats = this.dagsats;
            beregningsgrunnlagPeriode.bruttoPrÅr = this.bruttoPrÅr;
            beregningsgrunnlagPeriode.periodeÅrsak = this.periodeÅrsak;
            beregningsgrunnlagPeriode.periode = this.periode;
            beregningsgrunnlagPeriode.beregningsgrunnlagPrStatusOgAndelList = this.beregningsgrunnlagPrStatusOgAndelList;
            return beregningsgrunnlagPeriode;
        }
    }
}
