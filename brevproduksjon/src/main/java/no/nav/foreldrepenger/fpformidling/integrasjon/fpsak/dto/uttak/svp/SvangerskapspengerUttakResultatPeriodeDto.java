package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.uttak.svp;

import java.math.BigDecimal;
import java.time.LocalDate;

import no.nav.foreldrepenger.fpformidling.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.fpformidling.uttak.svp.PeriodeIkkeOppfyltÅrsak;

public class SvangerskapspengerUttakResultatPeriodeDto {
    private BigDecimal utbetalingsgrad;
    private PeriodeResultatType periodeResultatType;
    private PeriodeIkkeOppfyltÅrsak periodeIkkeOppfyltÅrsak;
    private LocalDate fom;
    private LocalDate tom;

    public SvangerskapspengerUttakResultatPeriodeDto() {
    }

    public BigDecimal getUtbetalingsgrad() {
        return utbetalingsgrad;
    }

    public PeriodeResultatType getPeriodeResultatType() {
        return periodeResultatType;
    }

    public PeriodeIkkeOppfyltÅrsak getPeriodeIkkeOppfyltÅrsak() {
        return periodeIkkeOppfyltÅrsak;
    }

    public LocalDate getFom() {
        return fom;
    }

    public LocalDate getTom() {
        return tom;
    }

}
