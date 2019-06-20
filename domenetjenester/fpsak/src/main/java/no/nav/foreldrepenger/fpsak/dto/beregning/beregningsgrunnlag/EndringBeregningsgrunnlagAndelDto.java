package no.nav.foreldrepenger.fpsak.dto.beregning.beregningsgrunnlag;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class EndringBeregningsgrunnlagAndelDto extends FaktaOmBeregningAndelDto {

    private static final int MÅNEDER_I_1_ÅR = 12;
    private BigDecimal fordelingForrigeBehandling;
    private BigDecimal fordelingForrigeBehandlingPrAar;
    private BigDecimal refusjonskrav = BigDecimal.ZERO;
    private BigDecimal beregnetPrMnd;
    private BigDecimal beregnetPrAar;
    private BigDecimal belopFraInntektsmelding;
    private BigDecimal fastsattForrige;
    private BigDecimal fastsattForrigePrAar;
    private BigDecimal refusjonskravFraInntektsmelding;
    private BigDecimal snittIBeregningsperiodenPrMnd;

    public EndringBeregningsgrunnlagAndelDto() {
    }

    public BigDecimal getSnittIBeregningsperiodenPrMnd() {
        return snittIBeregningsperiodenPrMnd;
    }

    public void setSnittIBeregningsperiodenPrMnd(BigDecimal snittIBeregningsperiodenPrMnd) {
        this.snittIBeregningsperiodenPrMnd = snittIBeregningsperiodenPrMnd;
    }

    public BigDecimal getBelopFraInntektsmelding() {
        return belopFraInntektsmelding;
    }

    public void setBelopFraInntektsmelding(BigDecimal belopFraInntektsmelding) {
        this.belopFraInntektsmelding = belopFraInntektsmelding;
    }

    public BigDecimal getFordelingForrigeBehandling() {
        return fordelingForrigeBehandling;
    }

    public void setFordelingForrigeBehandling(BigDecimal fordelingForrigeBehandling) {
        this.fordelingForrigeBehandling = fordelingForrigeBehandling;
    }

    public BigDecimal getRefusjonskrav() {
        return refusjonskrav;
    }

    public void setRefusjonskrav(BigDecimal refusjonskrav) {
        this.refusjonskrav = refusjonskrav;
    }

    public void initialiserVerdierForBeregnet(BigDecimal beregnetPrAar) {
        this.beregnetPrAar = beregnetPrAar;
        this.beregnetPrMnd = beregnetPrAar != null ? beregnetPrAar.divide(BigDecimal.valueOf(MÅNEDER_I_1_ÅR), 0, RoundingMode.HALF_UP) : null;
    }

    public BigDecimal getBeregnetPrMnd() {
        return beregnetPrMnd;
    }

    public BigDecimal getFastsattForrige() {
        return fastsattForrige;
    }

    public BigDecimal getRefusjonskravFraInntektsmelding() {
        return refusjonskravFraInntektsmelding;
    }

    public void setRefusjonskravFraInntektsmelding(BigDecimal refusjonskravFraInntektsmelding) {
        this.refusjonskravFraInntektsmelding = refusjonskravFraInntektsmelding;
    }

    public BigDecimal getFordelingForrigeBehandlingPrAar() {
        return fordelingForrigeBehandlingPrAar;
    }

    public void setFordelingForrigeBehandlingPrAar(BigDecimal fordelingForrigeBehandlingPrAar) {
        this.fordelingForrigeBehandlingPrAar = fordelingForrigeBehandlingPrAar;
    }

    public BigDecimal getBeregnetPrAar() {
        return beregnetPrAar;
    }

    public void setBeregnetPrAar(BigDecimal beregnetPrAar) {
        this.beregnetPrAar = beregnetPrAar;
    }

    public BigDecimal getFastsattForrigePrAar() {
        return fastsattForrigePrAar;
    }

    public void setBeregnetPrMnd(BigDecimal beregnetPrMnd) {
        this.beregnetPrMnd = beregnetPrMnd;
    }

    public void setFastsattForrige(BigDecimal fastsattForrige) {
        this.fastsattForrige = fastsattForrige;
    }

    public void setFastsattForrigePrAar(BigDecimal fastsattForrigePrAar) {
        this.fastsattForrigePrAar = fastsattForrigePrAar;
    }
}
