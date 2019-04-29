package no.nav.foreldrepenger.fpsak.dto.uttak.saldo;

public class AktivitetSaldoDto {

    private AktivitetIdentifikatorDto aktivitetIdentifikator;
    private int saldo;

    public AktivitetSaldoDto() {
    }

    public AktivitetIdentifikatorDto getAktivitetIdentifikator() {
        return aktivitetIdentifikator;
    }

    public int getSaldo() {
        return saldo;
    }
}
