package no.nav.foreldrepenger.fpsak.dto.beregning.beregningsresultat;

import java.math.BigDecimal;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

public class BeregningsresultatPeriodeAndelDto {
    private String arbeidsgiverReferanse;
    private Integer refusjon;
    private Integer tilSoker;
    private KodeDto aktivitetStatus;
    private String arbeidsforholdId;
    private KodeDto arbeidsforholdType;
    private BigDecimal stillingsprosent;

    public BeregningsresultatPeriodeAndelDto() {
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
