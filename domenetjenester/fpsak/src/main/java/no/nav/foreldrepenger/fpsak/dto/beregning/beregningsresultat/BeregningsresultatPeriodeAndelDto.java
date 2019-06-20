package no.nav.foreldrepenger.fpsak.dto.beregning.beregningsresultat;

import java.math.BigDecimal;
import java.time.LocalDate;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

public class BeregningsresultatPeriodeAndelDto {
    private String arbeidsgiverNavn;
    private String arbeidsgiverOrgnr;
    private String aktørId;
    private Integer refusjon;
    private Integer tilSoker;
    private UttakDto uttak;
    private BigDecimal utbetalingsgrad;
    private LocalDate sisteUtbetalingsdato;
    private KodeDto aktivitetStatus;
    private String arbeidsforholdId;
    private KodeDto arbeidsforholdType;
    private BigDecimal stillingsprosent;

    public BeregningsresultatPeriodeAndelDto() {
    }

    public String getAktørId() {
        return aktørId;
    }

    public String getArbeidsgiverNavn() {
        return arbeidsgiverNavn;
    }

    public String getArbeidsgiverOrgnr() {
        return arbeidsgiverOrgnr;
    }

    public Integer getRefusjon() {
        return refusjon;
    }

    public Integer getTilSoker() {
        return tilSoker;
    }

    public UttakDto getUttak() {
        return uttak;
    }

    public BigDecimal getUtbetalingsgrad() {
        return utbetalingsgrad;
    }

    public LocalDate getSisteUtbetalingsdato() {
        return sisteUtbetalingsdato;
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
