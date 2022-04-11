package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.beregningsgrunnlag.v2;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

import java.math.BigDecimal;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_ABSENT, content = JsonInclude.Include.NON_EMPTY)
@JsonAutoDetect(fieldVisibility = NONE, getterVisibility = NONE, setterVisibility = NONE, isGetterVisibility = NONE, creatorVisibility = NONE)
public class BgAndelArbeidsforholdDtoV2 {

    @JsonProperty(value = "arbeidsgiverIdent")
    @Valid
    @NotNull
    private String arbeidsgiverIdent;

    @JsonProperty(value = "arbeidsforholdRef")
    @Valid
    private String arbeidsforholdRef;

    @JsonProperty(value = "naturalytelseBortfaltPrÅr")
    @Valid
    @Digits(integer = 8, fraction = 2)
    @DecimalMin("0.00")
    @DecimalMax("10000000.00")
    private BigDecimal naturalytelseBortfaltPrÅr;

    @JsonProperty(value = "naturalytelseTilkommetPrÅr")
    @Valid
    @Digits(integer = 8, fraction = 2)
    @DecimalMin("0.00")
    @DecimalMax("10000000.00")
    private BigDecimal naturalytelseTilkommetPrÅr;

    public BgAndelArbeidsforholdDtoV2() {
    }

    public BgAndelArbeidsforholdDtoV2(@Valid @NotNull String arbeidsgiverIdent,
                                      @Valid String arbeidsforholdRef,
                                      @Valid @Digits(integer = 8, fraction = 2) @DecimalMin("0.00") @DecimalMax("10000000.00") BigDecimal naturalytelseBortfaltPrÅr,
                                      @Valid @Digits(integer = 8, fraction = 2) @DecimalMin("0.00") @DecimalMax("10000000.00") BigDecimal naturalytelseTilkommetPrÅr) {
        this.arbeidsgiverIdent = arbeidsgiverIdent;
        this.arbeidsforholdRef = arbeidsforholdRef;
        this.naturalytelseBortfaltPrÅr = naturalytelseBortfaltPrÅr;
        this.naturalytelseTilkommetPrÅr = naturalytelseTilkommetPrÅr;
    }

    public String getArbeidsgiverIdent() {
        return arbeidsgiverIdent;
    }

    public String getArbeidsforholdRef() {
        return arbeidsforholdRef;
    }

    public BigDecimal getNaturalytelseBortfaltPrÅr() {
        return naturalytelseBortfaltPrÅr;
    }

    public BigDecimal getNaturalytelseTilkommetPrÅr() {
        return naturalytelseTilkommetPrÅr;
    }
}
