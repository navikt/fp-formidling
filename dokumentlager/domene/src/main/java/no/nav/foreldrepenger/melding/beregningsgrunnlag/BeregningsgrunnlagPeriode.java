package no.nav.foreldrepenger.melding.beregningsgrunnlag;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import no.nav.foreldrepenger.melding.typer.DatoIntervall;

public class BeregningsgrunnlagPeriode {
    private int dagsats;
    private int bruttoPrÅr;
    private String periodeÅrsak; //Kode beregningsgrunnlagPeriodeÅrsaker.periodeÅrsak
    private DatoIntervall periode;
    private List<BeregningsgrunnlagPrStatusOgAndel> beregningsgrunnlagPrStatusOgAndelList = new ArrayList<>();

    public int getDagsats() {
        return dagsats;
    }

    public int getBruttoPrÅr() {
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
