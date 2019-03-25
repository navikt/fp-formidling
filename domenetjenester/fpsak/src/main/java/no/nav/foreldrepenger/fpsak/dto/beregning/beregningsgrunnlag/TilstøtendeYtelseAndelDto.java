package no.nav.foreldrepenger.fpsak.dto.beregning.beregningsgrunnlag;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TilstøtendeYtelseAndelDto extends FaktaOmBeregningAndelDto {


    @JsonProperty("fordelingForrigeYtelse")
    private BigDecimal fordelingForrigeYtelse;

    @JsonProperty("refusjonskrav")
    private BigDecimal refusjonskrav;

    @JsonProperty("fastsattPrAar")
    private BigDecimal fastsattPrAar;


    public TilstøtendeYtelseAndelDto () {
        // Hibernate
    }

    public BigDecimal getFordelingForrigeYtelse() {
        return fordelingForrigeYtelse;
    }

    public void setFordelingForrigeYtelse(BigDecimal fordelingForrigeYtelse) {
        this.fordelingForrigeYtelse = fordelingForrigeYtelse;
    }

    public BigDecimal getRefusjonskrav() {
        return refusjonskrav;
    }

    public void setRefusjonskrav(BigDecimal refusjonskrav) {
        this.refusjonskrav = refusjonskrav;
    }

    public BigDecimal getFastsattPrAar() {
        return fastsattPrAar;
    }

    public void setFastsattPrAar(BigDecimal fastsattPrAar) {
        this.fastsattPrAar = fastsattPrAar;
    }

}
