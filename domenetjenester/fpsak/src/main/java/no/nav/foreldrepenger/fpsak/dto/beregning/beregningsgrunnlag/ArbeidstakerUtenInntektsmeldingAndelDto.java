package no.nav.foreldrepenger.fpsak.dto.beregning.beregningsgrunnlag;

import java.math.BigDecimal;

public class ArbeidstakerUtenInntektsmeldingAndelDto extends FaktaOmBeregningAndelDto {

    private Boolean mottarYtelse;
    private BigDecimal inntektPrMnd;

    public Boolean getMottarYtelse() {
        return mottarYtelse;
    }

    public void setMottarYtelse(boolean mottarYtelse) {
        this.mottarYtelse = mottarYtelse;
    }

    public BigDecimal getInntektPrMnd() {
        return inntektPrMnd;
    }

    public void setInntektPrMnd(BigDecimal inntektPrMnd) {
        this.inntektPrMnd = inntektPrMnd;
    }
}
