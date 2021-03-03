package no.nav.foreldrepenger.fpsak.dto.beregning.beregningsgrunnlag.v2;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_ABSENT, content = JsonInclude.Include.NON_EMPTY)
@JsonAutoDetect(fieldVisibility = NONE, getterVisibility = NONE, setterVisibility = NONE, isGetterVisibility = NONE, creatorVisibility = NONE)
public class BeregningsgrunnlagPeriodeDtoV2 {

    @JsonProperty("dagsats")
    @Valid
    @Digits(integer = 8, fraction = 2)
    @DecimalMin("0.00")
    @DecimalMax("10000.00")
    private Long dagsats;

    @JsonProperty(value = "bruttoPrÅr")
    @Valid
    @Digits(integer = 8, fraction = 2)
    @DecimalMin("0.00")
    @DecimalMax("10000000.00")
    private BigDecimal bruttoPrÅr;

    @JsonProperty(value = "avkortetPrÅr")
    @Valid
    @Digits(integer = 8, fraction = 2)
    @DecimalMin("0.00")
    @DecimalMax("10000000.00")
    private BigDecimal avkortetPrÅr;

    @JsonProperty(value = "periodeårsaker")
    @Valid
    private List<KodeDto> periodeårsaker;

    @JsonProperty(value = "beregningsgrunnlagperiodeFom")
    @Valid
    private LocalDate beregningsgrunnlagperiodeFom;

    @JsonProperty(value = "beregningsgrunnlagperiodeTom")
    @Valid
    private LocalDate beregningsgrunnlagperiodeTom;

    @JsonProperty(value = "beregningsgrunnlagandeler")
    @Valid
    @Size(min = 1)
    private List<BeregningsgrunnlagAndelDtoV2> beregningsgrunnlagandeler;

    public BeregningsgrunnlagPeriodeDtoV2() {
    }

    public BeregningsgrunnlagPeriodeDtoV2(@Valid @Digits(integer = 8, fraction = 2) @DecimalMin("0.00") @DecimalMax("10000.00") Long dagsats,
                                          @Valid @Digits(integer = 8, fraction = 2) @DecimalMin("0.00") @DecimalMax("10000000.00") BigDecimal bruttoPrÅr,
                                          @Valid @Digits(integer = 8, fraction = 2) @DecimalMin("0.00") @DecimalMax("10000000.00") BigDecimal avkortetPrÅr,
                                          @Valid List<KodeDto> periodeårsaker,
                                          @Valid LocalDate beregningsgrunnlagperiodeFom,
                                          @Valid LocalDate beregningsgrunnlagperiodeTom,
                                          @Valid @Size(min = 1) List<BeregningsgrunnlagAndelDtoV2> beregningsgrunnlagandeler) {
        this.dagsats = dagsats;
        this.bruttoPrÅr = bruttoPrÅr;
        this.avkortetPrÅr = avkortetPrÅr;
        this.periodeårsaker = periodeårsaker;
        this.beregningsgrunnlagperiodeFom = beregningsgrunnlagperiodeFom;
        this.beregningsgrunnlagperiodeTom = beregningsgrunnlagperiodeTom;
        this.beregningsgrunnlagandeler = beregningsgrunnlagandeler;
    }

    public Long getDagsats() {
        return dagsats;
    }

    public BigDecimal getBruttoPrÅr() {
        return bruttoPrÅr;
    }

    public BigDecimal getAvkortetPrÅr() {
        return avkortetPrÅr;
    }

    public List<KodeDto> getPeriodeårsaker() {
        return periodeårsaker;
    }

    public LocalDate getBeregningsgrunnlagperiodeFom() {
        return beregningsgrunnlagperiodeFom;
    }

    public LocalDate getBeregningsgrunnlagperiodeTom() {
        return beregningsgrunnlagperiodeTom;
    }

    public List<BeregningsgrunnlagAndelDtoV2> getBeregningsgrunnlagandeler() {
        return beregningsgrunnlagandeler;
    }
}
