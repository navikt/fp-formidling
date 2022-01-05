package no.nav.foreldrepenger.fpsak.dto.uttak.saldo;

import java.util.Map;

public class SaldoerDto {
    private Map<String, StønadskontoDto> stonadskontoer;
    private int tapteDagerFpff;

    public SaldoerDto() {
    }

    public Map<String, StønadskontoDto> getStonadskontoer() {
        return stonadskontoer;
    }

    public int getTapteDagerFpff() {
        return tapteDagerFpff;
    }
}
