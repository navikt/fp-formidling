package no.nav.foreldrepenger.fpsak.dto.beregning.beregningsgrunnlag;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY)
public class BeregningsgrunnlagPeriodeDto {

    @JsonProperty("beregningsgrunnlagPeriodeFom")
    private LocalDate beregningsgrunnlagPeriodeFom;

    @JsonProperty("beregningsgrunnlagPeriodeTom")
    private LocalDate beregningsgrunnlagPeriodeTom;

    @JsonProperty("beregnetPrAar")
    private BigDecimal beregnetPrAar;

    @JsonProperty("bruttoPrAar")
    private BigDecimal bruttoPrAar;

    @JsonProperty("bruttoInkludertBortfaltNaturalytelsePrAar")
    private BigDecimal bruttoInkludertBortfaltNaturalytelsePrAar;

    @JsonProperty("avkortetPrAar")
    private BigDecimal avkortetPrAar;

    @JsonProperty("redusertPrAar")
    private BigDecimal redusertPrAar;

    @JsonProperty("periodeAarsaker")
    private Set<KodeDto> periodeAarsaker = new HashSet<>();

    @JsonProperty("dagsats")
    private Long dagsats;

    @JsonProperty("beregningsgrunnlagPrStatusOgAndel")
    private List<BeregningsgrunnlagPrStatusOgAndelDto> beregningsgrunnlagPrStatusOgAndel;

    public BeregningsgrunnlagPeriodeDto() {
        // trengs for deserialisering av JSON
    }

    public LocalDate getBeregningsgrunnlagPeriodeFom() {
        return beregningsgrunnlagPeriodeFom;
    }

    public LocalDate getBeregningsgrunnlagPeriodeTom() {
        return beregningsgrunnlagPeriodeTom;
    }

    public BigDecimal getBeregnetPrAar() {
        return beregnetPrAar;
    }

    public BigDecimal getBruttoPrAar() {
        return bruttoPrAar;
    }

    public BigDecimal getBruttoInkludertBortfaltNaturalytelsePrAar() {
        return bruttoInkludertBortfaltNaturalytelsePrAar;
    }

    public BigDecimal getAvkortetPrAar() {
        return avkortetPrAar;
    }

    public BigDecimal getRedusertPrAar() {
        return redusertPrAar;
    }

    public Long getDagsats() {
        return dagsats;
    }

    public List<BeregningsgrunnlagPrStatusOgAndelDto> getBeregningsgrunnlagPrStatusOgAndel() {
        return beregningsgrunnlagPrStatusOgAndel;
    }

    public void setBeregningsgrunnlagPeriodeFom(LocalDate beregningsgrunnlagPeriodeFom) {
        this.beregningsgrunnlagPeriodeFom = beregningsgrunnlagPeriodeFom;
    }

    public void setBeregningsgrunnlagPeriodeTom(LocalDate beregningsgrunnlagPeriodeTom) {
        this.beregningsgrunnlagPeriodeTom = beregningsgrunnlagPeriodeTom;
    }

    public void setBeregnetPrAar(BigDecimal beregnetPrAar) {
        this.beregnetPrAar = beregnetPrAar;
    }

    public void setBruttoPrAar(BigDecimal bruttoPrAar) {
        this.bruttoPrAar = bruttoPrAar;
    }

    public void setBruttoInkludertBortfaltNaturalytelsePrAar(BigDecimal bruttoInkludertBortfaltNaturalytelsePrAar) {
        this.bruttoInkludertBortfaltNaturalytelsePrAar = bruttoInkludertBortfaltNaturalytelsePrAar;
    }

    public void setAvkortetPrAar(BigDecimal avkortetPrAar) {
        this.avkortetPrAar = avkortetPrAar;
    }

    public void setRedusertPrAar(BigDecimal redusertPrAar) {
        this.redusertPrAar = redusertPrAar;
    }

    public void setElementer(List<BeregningsgrunnlagPrStatusOgAndelDto> elementer) {
        this.beregningsgrunnlagPrStatusOgAndel = elementer;
    }

    public void setDagsats(Long dagsats) {
        this.dagsats = dagsats;
    }

    public void leggTilPeriodeAarsak(KodeDto periodeAarsak) {
        periodeAarsaker.add(periodeAarsak);
    }

    public void leggTilPeriodeAarsaker(List<KodeDto> periodeAarsaker) {
        for (KodeDto aarsak : periodeAarsaker) {
            leggTilPeriodeAarsak(aarsak);
        }
    }

    public void setPeriodeAarsaker(Set<KodeDto> periodeAarsaker) {
        this.periodeAarsaker = periodeAarsaker;
    }

    public Set<KodeDto> getPeriodeAarsaker() {
        return periodeAarsaker;
    }
}
