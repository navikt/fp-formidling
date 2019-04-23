package no.nav.foreldrepenger.fpsak.dto.uttak;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

public class UttakResultatPeriodeAktivitetDto {

    private KodeDto stønadskontoType;
    private Integer trekkdager;
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

    @Deprecated
    //TODO Denne endres snart til desimaltall
    public Integer getTrekkdager() {
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

    @JsonIgnore
    boolean isGradering() {
        return gradering;
    }
}
