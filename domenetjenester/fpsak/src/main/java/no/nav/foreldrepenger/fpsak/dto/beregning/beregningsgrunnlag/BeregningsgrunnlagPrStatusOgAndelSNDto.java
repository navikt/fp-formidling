package no.nav.foreldrepenger.fpsak.dto.beregning.beregningsgrunnlag;

import java.math.BigDecimal;

public class BeregningsgrunnlagPrStatusOgAndelSNDto extends BeregningsgrunnlagPrStatusOgAndelDto {
    private BigDecimal pgiSnitt;
    private BigDecimal pgi1;
    private BigDecimal pgi2;
    private BigDecimal pgi3;

    public BeregningsgrunnlagPrStatusOgAndelSNDto() {
        super();
        // trengs for deserialisering av JSON
    }

    public BigDecimal getPgiSnitt() {
        return pgiSnitt;
    }

    public void setPgiSnitt(BigDecimal pgiSnitt) {
        this.pgiSnitt = pgiSnitt;
    }

    public BigDecimal getPgi1() {
        return pgi1;
    }

    public void setPgi1(BigDecimal pgi1) {
        this.pgi1 = pgi1;
    }

    public BigDecimal getPgi2() {
        return pgi2;
    }

    public void setPgi2(BigDecimal pgi2) {
        this.pgi2 = pgi2;
    }

    public BigDecimal getPgi3() {
        return pgi3;
    }

    public void setPgi3(BigDecimal pgi3) {
        this.pgi3 = pgi3;
    }
}
