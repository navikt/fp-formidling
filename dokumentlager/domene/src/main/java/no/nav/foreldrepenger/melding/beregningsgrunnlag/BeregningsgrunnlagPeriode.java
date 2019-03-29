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
}
