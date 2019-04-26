package no.nav.foreldrepenger.fpsak.dto.beregning.beregningsgrunnlag;

import java.time.LocalDate;
import java.util.List;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

public class BeregningsgrunnlagDto {

    private LocalDate skjaeringstidspunktBeregning;
    private List<KodeDto> aktivitetStatus;
    private List<BeregningsgrunnlagPeriodeDto> beregningsgrunnlagPeriode;
    private SammenligningsgrunnlagDto sammenligningsgrunnlag;
    private String ledetekstBrutto;
    private String ledetekstAvkortet;
    private String ledetekstRedusert;
    private Double halvG;
    private FaktaOmBeregningDto faktaOmBeregning;
    private List<BeregningsgrunnlagPrStatusOgAndelDto> andelerMedGraderingUtenBG;
    private KodeDto hjemmel;

    public BeregningsgrunnlagDto() {
        // trengs for deserialisering av JSON
    }

    public LocalDate getSkjaeringstidspunktBeregning() {
        return skjaeringstidspunktBeregning;
    }

    public List<KodeDto> getAktivitetStatus() {
        return aktivitetStatus;
    }

    public List<BeregningsgrunnlagPeriodeDto> getBeregningsgrunnlagPeriode() {
        return beregningsgrunnlagPeriode;
    }

    public String getLedetekstBrutto() {
        return ledetekstBrutto;
    }

    public String getLedetekstAvkortet() {
        return ledetekstAvkortet;
    }

    public String getLedetekstRedusert() {
        return ledetekstRedusert;
    }

    public SammenligningsgrunnlagDto getSammenligningsgrunnlag() {
        return sammenligningsgrunnlag;
    }

    public Double getHalvG() {
        return halvG;
    }

    public FaktaOmBeregningDto getFaktaOmBeregning() {
        return faktaOmBeregning;
    }

    public KodeDto getHjemmel() {
        return hjemmel;
    }

    public void setSkjaeringstidspunktBeregning(LocalDate skjaeringstidspunktBeregning) {
        this.skjaeringstidspunktBeregning = skjaeringstidspunktBeregning;
    }

    public void setAktivitetStatus(List<KodeDto> aktivitetStatus) {
        this.aktivitetStatus = aktivitetStatus;
    }

    public void setBeregningsgrunnlagPeriode(List<BeregningsgrunnlagPeriodeDto> perioder) {
        this.beregningsgrunnlagPeriode = perioder;
    }

    public void setSammenligningsgrunnlag(SammenligningsgrunnlagDto sammenligningsgrunnlag) {
        this.sammenligningsgrunnlag = sammenligningsgrunnlag;
    }

    public void setLedetekstBrutto(String ledetekstBrutto) {
        this.ledetekstBrutto = ledetekstBrutto;
    }

    public void setLedetekstAvkortet(String ledetekstAvkortet) {
        this.ledetekstAvkortet = ledetekstAvkortet;
    }

    public void setLedetekstRedusert(String ledetekstRedusert) {
        this.ledetekstRedusert = ledetekstRedusert;
    }

    public void setHalvG(Double halvG) {
        this.halvG = halvG;
    }

    public void setFaktaOmBeregning(FaktaOmBeregningDto faktaOmBeregning) {
        this.faktaOmBeregning = faktaOmBeregning;
    }

    public List<BeregningsgrunnlagPrStatusOgAndelDto> getAndelerMedGraderingUtenBG() {
        return andelerMedGraderingUtenBG;
    }

    public void setAndelerMedGraderingUtenBG(List<BeregningsgrunnlagPrStatusOgAndelDto> andelerMedGraderingUtenBG) {
        this.andelerMedGraderingUtenBG = andelerMedGraderingUtenBG;
    }

    public void setHjemmel(KodeDto hjemmel) {
        this.hjemmel = hjemmel;
    }
}
