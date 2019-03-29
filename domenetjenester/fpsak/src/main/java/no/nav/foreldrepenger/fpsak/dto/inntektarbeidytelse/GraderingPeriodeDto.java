package no.nav.foreldrepenger.fpsak.dto.inntektarbeidytelse;

import java.math.BigDecimal;
import java.time.LocalDate;

public class GraderingPeriodeDto {
    private LocalDate fom;
    private LocalDate tom;
    private BigDecimal arbeidsprosent;

    public LocalDate getFom() {
        return fom;
    }

    public LocalDate getTom() {
        return tom;
    }

    public BigDecimal getArbeidsprosent() {
        return arbeidsprosent;
    }
}
