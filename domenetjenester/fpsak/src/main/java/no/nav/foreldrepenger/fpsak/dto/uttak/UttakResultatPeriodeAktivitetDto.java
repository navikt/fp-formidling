package no.nav.foreldrepenger.fpsak.dto.uttak;

import java.math.BigDecimal;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

public class UttakResultatPeriodeAktivitetDto {

    private KodeDto stønadskontoType;
    private BigDecimal trekkdager;
    private BigDecimal prosentArbeid;
    private String arbeidsforholdId;
    private ArbeidsgiverDto arbeidsgiver;
    private BigDecimal utbetalingsgrad;
    private KodeDto uttakArbeidType;
    private boolean gradering;

    private UttakResultatPeriodeAktivitetDto() {
    }

    public KodeDto getStønadskontoType() {
        return stønadskontoType;
    }

    public BigDecimal getTrekkdager() {
        return trekkdager;
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

    public KodeDto getUttakArbeidType() {
        return uttakArbeidType;
    }

    public ArbeidsgiverDto getArbeidsgiver() {
        return arbeidsgiver;
    }

    public boolean isGradering() {
        return gradering;
    }
}
