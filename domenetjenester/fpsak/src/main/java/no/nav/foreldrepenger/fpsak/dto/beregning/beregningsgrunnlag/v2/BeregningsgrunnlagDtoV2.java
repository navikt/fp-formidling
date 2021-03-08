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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_ABSENT, content = JsonInclude.Include.NON_EMPTY)
@JsonAutoDetect(fieldVisibility = NONE, getterVisibility = NONE, setterVisibility = NONE, isGetterVisibility = NONE, creatorVisibility = NONE)
public class BeregningsgrunnlagDtoV2 {

    @JsonProperty(value = "aktivitetstatusListe")
    @NotNull
    @Valid
    @Size(min = 1)
    private List<KodeDto> aktivitetstatusListe;

    @JsonProperty(value = "hjemmel")
    @NotNull
    @Valid
    private KodeDto hjemmel;

    @JsonProperty(value = "grunnbeløp")
    @Valid
    @Digits(integer = 8, fraction = 2)
    @DecimalMin("0.00")
    @DecimalMax("10000000.00")
    private BigDecimal grunnbeløp;

    @JsonProperty(value = "beregningsgrunnlagperioder")
    @Valid
    @NotNull
    @Size(min = 1)
    private List<BeregningsgrunnlagPeriodeDtoV2> beregningsgrunnlagperioder;

    @JsonProperty(value = "erBesteberegnet")
    @Valid
    private boolean erBesteberegnet;

    public BeregningsgrunnlagDtoV2() {
    }

    public BeregningsgrunnlagDtoV2(@NotNull @Valid @Size(min = 1) List<KodeDto> aktivitetstatusListe,
                                   @NotNull @Valid KodeDto hjemmel,
                                   @Valid @Digits(integer = 8, fraction = 2) @DecimalMin("0.00") @DecimalMax("10000000.00") BigDecimal grunnbeløp,
                                   @Valid @NotNull @Size(min = 1) List<BeregningsgrunnlagPeriodeDtoV2> beregningsgrunnlagperioder,
                                   @Valid boolean erBesteberegnet) {
        this.aktivitetstatusListe = aktivitetstatusListe;
        this.hjemmel = hjemmel;
        this.grunnbeløp = grunnbeløp;
        this.beregningsgrunnlagperioder = beregningsgrunnlagperioder;
        this.erBesteberegnet = erBesteberegnet;
    }

    public List<KodeDto> getAktivitetstatusListe() {
        return aktivitetstatusListe;
    }

    public KodeDto getHjemmel() {
        return hjemmel;
    }

    public BigDecimal getGrunnbeløp() {
        return grunnbeløp;
    }

    public List<BeregningsgrunnlagPeriodeDtoV2> getBeregningsgrunnlagperioder() {
        return beregningsgrunnlagperioder;
    }

    public boolean isErBesteberegnet() {
        return erBesteberegnet;
    }
}
