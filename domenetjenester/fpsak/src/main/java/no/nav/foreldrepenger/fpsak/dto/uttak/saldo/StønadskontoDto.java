package no.nav.foreldrepenger.fpsak.dto.uttak.saldo;

import java.util.List;

public class StønadskontoDto {

    private String stonadskontotype;
    private int maxDager;
    private int saldo;
    private List<AktivitetSaldoDto> aktivitetSaldoDtoList;
    private boolean gyldigForbruk;

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

    public List<AktivitetSaldoDto> getAktivitetSaldoDtoList() {
        return aktivitetSaldoDtoList;
    }

    public boolean isGyldigForbruk() {
        return gyldigForbruk;
    }
}
