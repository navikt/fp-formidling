package no.nav.foreldrepenger.fpsak.dto.beregning.beregningsgrunnlag;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY)
public class BeregningsgrunnlagPrStatusOgAndelDto {

    @JsonProperty("aktivitetStatus")
    private KodeDto aktivitetStatus;

    @JsonProperty("beregningsperiodeFom")
    private LocalDate beregningsperiodeFom;

    @JsonProperty("beregningsperiodeTom")
    private LocalDate beregningsperiodeTom;

    @JsonProperty("beregnetPrAar")
    private BigDecimal beregnetPrAar;

    @JsonProperty("overstyrtPrAar")
    private BigDecimal overstyrtPrAar;

    @JsonProperty("bruttoPrAar")
    private BigDecimal bruttoPrAar;

    @JsonProperty("avkortetPrAar")
    private BigDecimal avkortetPrAar;

    @JsonProperty("redusertPrAar")
    private BigDecimal redusertPrAar;

    @JsonProperty("erTidsbegrensetArbeidsforhold")
    private Boolean erTidsbegrensetArbeidsforhold;

    @JsonProperty("erNyIArbeidslivet")
    private Boolean erNyIArbeidslivet;

    @JsonProperty("lonnsendringIBeregningsperioden")
    private Boolean lonnsendringIBeregningsperioden;

    @JsonProperty("andelsnr")
    private Long andelsnr;

    @JsonProperty("besteberegningPrAar")
    private BigDecimal besteberegningPrAar;

    @JsonProperty("arbeidsforhold")
    private BeregningsgrunnlagArbeidsforholdDto arbeidsforhold;

    @JsonProperty("fastsattAvSaksbehandler")
    private Boolean fastsattAvSaksbehandler;

    @JsonProperty("dagsats")
    private Long dagsats;

    @JsonProperty("originalDagsatsFraTilstøtendeYtelse")
    private Long originalDagsatsFraTilstøtendeYtelse;


    public BeregningsgrunnlagPrStatusOgAndelDto() {
        // trengs for deserialisering av JSON
    }

    @JsonGetter
    public LocalDate getBeregningsgrunnlagFom() {
        return beregningsperiodeFom;
    }

    @JsonGetter
    public LocalDate getBeregningsgrunnlagTom() {
        return beregningsperiodeTom;
    }

    public BigDecimal getBeregnetPrAar() {
        return beregnetPrAar;
    }

    public BigDecimal getOverstyrtPrAar() {
        return overstyrtPrAar;
    }

    public BigDecimal getBruttoPrAar() {
        return bruttoPrAar;
    }

    public KodeDto getAktivitetStatus() {
        return aktivitetStatus;
    }

    public BigDecimal getAvkortetPrAar() {
        return avkortetPrAar;
    }

    public BigDecimal getRedusertPrAar() {
        return redusertPrAar;
    }

    public Boolean getErTidsbegrensetArbeidsforhold() {
        return erTidsbegrensetArbeidsforhold;
    }

    public Boolean getErNyIArbeidslivet() {
        return erNyIArbeidslivet;
    }

    public Long getAndelsnr() {
        return andelsnr;
    }

    public Boolean getLonnsendringIBeregningsperioden() {
        return lonnsendringIBeregningsperioden;
    }

    public BigDecimal getBesteberegningPrAar() {
        return besteberegningPrAar;
    }

    public BeregningsgrunnlagArbeidsforholdDto getArbeidsforhold() {
        return arbeidsforhold;
    }

    public void setArbeidsforhold(BeregningsgrunnlagArbeidsforholdDto arbeidsforhold) {
        this.arbeidsforhold = arbeidsforhold;
    }

    public void setBesteberegningPrAar(BigDecimal besteberegningPrAar) {
        this.besteberegningPrAar = besteberegningPrAar;
    }

    public void setLonnsendringIBeregningsperioden(Boolean lonnsendringIBeregningsperioden) {
        this.lonnsendringIBeregningsperioden = lonnsendringIBeregningsperioden;
    }

    public void setBeregningsperiodeFom(LocalDate beregningsperiodeFom) {
        this.beregningsperiodeFom = beregningsperiodeFom;
    }

    public void setBeregningsperiodeTom(LocalDate beregningsperiodeTom) {
        this.beregningsperiodeTom = beregningsperiodeTom;
    }

    public void setBeregnetPrAar(BigDecimal beregnetPrAar) {
        this.beregnetPrAar = beregnetPrAar;
    }

    public void setOverstyrtPrAar(BigDecimal overstyrtPrAar) {
        this.overstyrtPrAar = overstyrtPrAar;
    }

    public void setBruttoPrAar(BigDecimal bruttoPrAar) {
        this.bruttoPrAar = bruttoPrAar;
    }

    public void setAktivitetStatus(KodeDto aktivitetStatus) {
        this.aktivitetStatus = aktivitetStatus;
    }

    public void setAvkortetPrAar(BigDecimal avkortetPrAar) {
        this.avkortetPrAar = avkortetPrAar;
    }

    public void setRedusertPrAar(BigDecimal redusertPrAar) {
        this.redusertPrAar = redusertPrAar;
    }

    public void setErTidsbegrensetArbeidsforhold(Boolean erTidsbegrensetArbeidsforhold) {
        this.erTidsbegrensetArbeidsforhold = erTidsbegrensetArbeidsforhold;
    }

    public void setAndelsnr(Long andelsnr) {
        this.andelsnr = andelsnr;
    }

    public void setErNyIArbeidslivet(Boolean erNyIArbeidslivet) {
        this.erNyIArbeidslivet = erNyIArbeidslivet;
    }

    public Boolean getFastsattAvSaksbehandler() {
        return fastsattAvSaksbehandler;
    }

    public void setFastsattAvSaksbehandler(Boolean fastsattAvSaksbehandler) {
        this.fastsattAvSaksbehandler = fastsattAvSaksbehandler;
    }

    public Long getDagsats() {
        return dagsats;
    }

    public void setDagsats(Long dagsats) {
        this.dagsats = dagsats;
    }

    public Long getOriginalDagsatsFraTilstøtendeYtelse() {
        return originalDagsatsFraTilstøtendeYtelse;
    }

    public void setOriginalDagsatsFraTilstøtendeYtelse(Long originalDagsatsFraTilstøtendeYtelse) {
        this.originalDagsatsFraTilstøtendeYtelse = originalDagsatsFraTilstøtendeYtelse;
    }
}
