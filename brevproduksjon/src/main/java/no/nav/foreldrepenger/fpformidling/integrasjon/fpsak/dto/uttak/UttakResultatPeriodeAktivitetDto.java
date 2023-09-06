package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.uttak;

import java.math.BigDecimal;

import no.nav.foreldrepenger.fpformidling.uttak.fp.StønadskontoType;
import no.nav.foreldrepenger.fpformidling.uttak.fp.UttakArbeidType;

public class UttakResultatPeriodeAktivitetDto {

    private StønadskontoType stønadskontoType;
    private BigDecimal trekkdagerDesimaler;
    private BigDecimal prosentArbeid;
    private String arbeidsforholdId;
    private String arbeidsgiverReferanse;
    private BigDecimal utbetalingsgrad;
    private UttakArbeidType uttakArbeidType;
    private boolean gradering;

    private UttakResultatPeriodeAktivitetDto() {
    }

    public StønadskontoType getStønadskontoType() {
        return stønadskontoType;
    }

    public BigDecimal getTrekkdagerDesimaler() {
        return trekkdagerDesimaler;
    }

    public BigDecimal getProsentArbeid() {
        return prosentArbeid;
    }

    public String getArbeidsforholdId() {
        return arbeidsforholdId;
    }

    public BigDecimal getUtbetalingsgrad() {
        return utbetalingsgrad;
    }

    public UttakArbeidType getUttakArbeidType() {
        return uttakArbeidType;
    }

    public String getArbeidsgiverReferanse() {
        return arbeidsgiverReferanse;
    }

    public boolean isGradering() {
        return gradering;
    }
}
