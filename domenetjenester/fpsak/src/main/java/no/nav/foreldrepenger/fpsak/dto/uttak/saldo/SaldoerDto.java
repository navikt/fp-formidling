package no.nav.foreldrepenger.fpsak.dto.uttak.saldo;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

public class SaldoerDto {
    private Optional<LocalDate> maksDatoUttak;
    private Map<String, StønadskontoDto> stonadskontoer;
    private int tapteDagerFpff;

    public SaldoerDto() {
    }

    public Optional<LocalDate> getMaksDatoUttak() {
        return maksDatoUttak;
    }

    public Map<String, StønadskontoDto> getStonadskontoer() {
        return stonadskontoer;
    }

    public int getTapteDagerFpff() {
        return tapteDagerFpff;
    }
}
