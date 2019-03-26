package no.nav.foreldrepenger.fpsak.dto.beregning.beregningsresultat;

public class BeregningsresultatEngangsstønadDto {

    private Long beregnetTilkjentYtelse;
    private Long satsVerdi;
    private Integer antallBarn;

    public BeregningsresultatEngangsstønadDto() {
    }

    public Long getBeregnetTilkjentYtelse() {
        return beregnetTilkjentYtelse;
    }

    public void setBeregnetTilkjentYtelse(Long beregnetTilkjentYtelse) {
        this.beregnetTilkjentYtelse = beregnetTilkjentYtelse;
    }

    public Long getSatsVerdi() {
        return satsVerdi;
    }

    public void setSatsVerdi(Long satsVerdi) {
        this.satsVerdi = satsVerdi;
    }

    public Integer getAntallBarn() {
        return antallBarn;
    }

    public void setAntallBarn(Integer antallBarn) {
        this.antallBarn = antallBarn;
    }
}
