package no.nav.foreldrepenger.fpsak.dto.beregning.beregningsgrunnlag;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE, fieldVisibility=Visibility.ANY)
public class BeregningsgrunnlagPrStatusOgAndelATDto extends BeregningsgrunnlagPrStatusOgAndelDto {

    @JsonProperty("bortfaltNaturalytelse")
    private BigDecimal bortfaltNaturalytelse;


    public BeregningsgrunnlagPrStatusOgAndelATDto() {
        super();
        // trengs for deserialisering av JSON
    }

    public BigDecimal getBortfaltNaturalytelse() {
        return bortfaltNaturalytelse;
    }

    public void setBortfaltNaturalytelse(BigDecimal bortfaltNaturalytelse) {
        this.bortfaltNaturalytelse = bortfaltNaturalytelse;
    }
}
