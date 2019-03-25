package no.nav.foreldrepenger.fpsak.dto.beregning.beregningsgrunnlag;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BrukersAndelDto extends FaktaOmBeregningAndelDto {

    @JsonProperty("fastsattBelopPrMnd")
    private BigDecimal fastsattBelopPrMnd;

    public BigDecimal getFastsattBelopPrMnd() {
        return fastsattBelopPrMnd;
    }

    public void setFastsattBelopPrMnd(BigDecimal fastsattBelopPrMnd) {
        this.fastsattBelopPrMnd = fastsattBelopPrMnd;
    }
}
