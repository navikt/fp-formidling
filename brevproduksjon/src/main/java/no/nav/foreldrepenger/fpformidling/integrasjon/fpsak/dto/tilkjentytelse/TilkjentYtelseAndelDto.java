package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.tilkjentytelse;

import java.math.BigDecimal;

import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.AktivitetStatus;

public class TilkjentYtelseAndelDto {
    private String arbeidsgiverReferanse;
    private Integer refusjon;
    private Integer tilSoker;
    private AktivitetStatus aktivitetStatus;
    private String arbeidsforholdId;
    private BigDecimal stillingsprosent;

    public TilkjentYtelseAndelDto() {
    }

    public String getArbeidsgiverReferanse() {
        return arbeidsgiverReferanse;
    }

    public Integer getRefusjon() {
        return refusjon;
    }

    public Integer getTilSoker() {
        return tilSoker;
    }

    public AktivitetStatus getAktivitetStatus() {
        return aktivitetStatus;
    }

    public String getArbeidsforholdId() {
        return arbeidsforholdId;
    }

    public BigDecimal getStillingsprosent() {
        return stillingsprosent;
    }
}
