package no.nav.foreldrepenger.fpsak.dto.uttak.saldo;

public class StønadskontoDto {

    private String stonadskontotype;
    private int maxDager;
    private int saldo;
    private KontoUtvidelser kontoUtvidelser;

    public StønadskontoDto() {
    }

    public String getStonadskontotype() {
        return stonadskontotype;
    }

    public int getMaxDager() {
        return maxDager;
    }

    public int getSaldo() {
        return saldo;
    }

    public KontoUtvidelser getKontoUtvidelser() {
        return kontoUtvidelser;
    }
}
