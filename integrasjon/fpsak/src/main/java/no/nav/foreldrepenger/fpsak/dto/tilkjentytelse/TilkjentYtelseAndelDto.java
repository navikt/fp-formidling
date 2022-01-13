package no.nav.foreldrepenger.fpsak.dto.tilkjentytelse;

import java.math.BigDecimal;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

public class TilkjentYtelseAndelDto {
    private KodeDto aktivitetStatus;
    private String arbeidsgiverReferanse;
    private KodeDto arbeidsforholdType;
    private String arbeidsforholdId;
    private Integer utbetalesTilArbeidsgiver;
    private BigDecimal stillingsprosent;
    private Integer utbetalesTilBruker;

    public TilkjentYtelseAndelDto() {
    }

    public String getArbeidsgiverReferanse() {
        return arbeidsgiverReferanse;
    }

    public Integer getUtbetalesTilArbeidsgiver() {
        return utbetalesTilArbeidsgiver;
    }

    public Integer getUtbetalesTilBruker() {
        return utbetalesTilBruker;
    }

    public KodeDto getAktivitetStatus() {
        return aktivitetStatus;
    }

    public String getArbeidsforholdId() {
        return arbeidsforholdId;
    }

    public KodeDto getArbeidsforholdType() {
        return arbeidsforholdType;
    }

    public BigDecimal getStillingsprosent() {
        return stillingsprosent;
    }
}
