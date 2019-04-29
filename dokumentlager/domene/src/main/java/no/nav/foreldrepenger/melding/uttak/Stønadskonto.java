package no.nav.foreldrepenger.melding.uttak;

public class Stønadskonto {
    private Integer maxDager;
    private StønadskontoType stønadskontoType;
    private int saldo;

    public Stønadskonto(Integer maxDager, StønadskontoType stønadskontoType, int saldo) {
        this.maxDager = maxDager;
        this.stønadskontoType = stønadskontoType;
        this.saldo = saldo;
    }

    public Integer getMaxDager() {
        return maxDager;
    }

    public StønadskontoType getStønadskontoType() {
        return stønadskontoType;
    }

    public int getSaldo() {
        return saldo;
    }
}
