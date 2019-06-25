package no.nav.foreldrepenger.melding.uttak;

public class Stønadskonto {
    private Integer maxDager;
    private StønadskontoType stønadskontoType;
    private int saldo;
    private int prematurDager;
    private int flerbarnsDager;

    public Stønadskonto(Integer maxDager, StønadskontoType stønadskontoType, int saldo, int prematurDager, int flerbarnsDager) {
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

    public int getPrematurDager() {
        return prematurDager;
    }

    public int getFlerbarnsDager() {
        return flerbarnsDager;
    }
}
