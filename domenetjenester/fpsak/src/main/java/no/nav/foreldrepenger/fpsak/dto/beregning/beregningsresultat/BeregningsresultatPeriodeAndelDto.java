package no.nav.foreldrepenger.fpsak.dto.beregning.beregningsresultat;

import java.math.BigDecimal;
import java.time.LocalDate;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

public class BeregningsresultatPeriodeAndelDto {
    private String arbeidsgiverNavn;
    private String arbeidsgiverOrgnr;
    private Integer refusjon;
    private Integer tilSoker;
    private UttakDto uttak;
    private BigDecimal utbetalingsgrad;
    private LocalDate sisteUtbetalingsdato;
    private KodeDto aktivitetStatus;
    private String arbeidsforholdId;
    private KodeDto arbeidsforholdType;

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

    public void setArbeidsgiverNavn(String arbeidsgiverNavn) {
        this.arbeidsgiverNavn = arbeidsgiverNavn;
    }

    public void setArbeidsgiverOrgnr(String arbeidsgiverOrgnr) {
        this.arbeidsgiverOrgnr = arbeidsgiverOrgnr;
    }

    public void setRefusjon(Integer refusjon) {
        this.refusjon = refusjon;
    }

    public void setTilSoker(Integer tilSoker) {
        this.tilSoker = tilSoker;
    }

    public void setUttak(UttakDto uttak) {
        this.uttak = uttak;
    }

    public void setUtbetalingsgrad(BigDecimal utbetalingsgrad) {
        this.utbetalingsgrad = utbetalingsgrad;
    }

    public void setSisteUtbetalingsdato(LocalDate sisteUtbetalingsdato) {
        this.sisteUtbetalingsdato = sisteUtbetalingsdato;
    }

    public void setAktivitetStatus(KodeDto aktivitetStatus) {
        this.aktivitetStatus = aktivitetStatus;
    }

    public void setArbeidsforholdId(String arbeidsforholdId) {
        this.arbeidsforholdId = arbeidsforholdId;
    }

    public void setArbeidsforholdType(KodeDto arbeidsforholdType) {
        this.arbeidsforholdType = arbeidsforholdType;
    }
}
