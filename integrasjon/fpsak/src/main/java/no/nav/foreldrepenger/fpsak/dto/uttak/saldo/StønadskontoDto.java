package no.nav.foreldrepenger.fpsak.dto.uttak.saldo;

import no.nav.foreldrepenger.fpformidling.uttak.StønadskontoType;

public class StønadskontoDto {

    private StønadskontoType stonadskontotype;
    private int maxDager;
    private int saldo;
    private KontoUtvidelser kontoUtvidelser;

    public StønadskontoDto() {
    }

    public StønadskontoType getStonadskontotype() {
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
