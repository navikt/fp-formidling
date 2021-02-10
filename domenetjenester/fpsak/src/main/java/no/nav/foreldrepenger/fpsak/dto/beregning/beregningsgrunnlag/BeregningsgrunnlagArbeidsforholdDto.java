package no.nav.foreldrepenger.fpsak.dto.beregning.beregningsgrunnlag;

import java.math.BigDecimal;
import java.time.LocalDate;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

public class BeregningsgrunnlagArbeidsforholdDto {

    private String arbeidsgiverIdent;
    private LocalDate startdato;
    private LocalDate opphoersdato;
    private String arbeidsforholdId;
    private KodeDto arbeidsforholdType;
    private BigDecimal naturalytelseBortfaltPrÅr;
    private BigDecimal naturalytelseTilkommetPrÅr;

    public BeregningsgrunnlagArbeidsforholdDto() {
        //Deserialisering
    }

    public String getArbeidsforholdId() {
        return arbeidsforholdId;
    }

    public void setArbeidsforholdId(String arbeidsforholdId) {
        this.arbeidsforholdId = arbeidsforholdId;
    }

    public String getArbeidsgiverIdent() {
        return arbeidsgiverIdent;
    }

    public void setArbeidsgiverIdent(String arbeidsgiverIdent) {
        this.arbeidsgiverIdent = arbeidsgiverIdent;
    }

    public LocalDate getStartdato() {
        return startdato;
    }

    public void setStartdato(LocalDate startdato) {
        this.startdato = startdato;
    }

    public LocalDate getOpphoersdato() {
        return opphoersdato;
    }

    public void setOpphoersdato(LocalDate opphoersdato) {
        this.opphoersdato = opphoersdato;
    }

    public KodeDto getArbeidsforholdType() {
        return arbeidsforholdType;
    }

    public void setArbeidsforholdType(KodeDto arbeidsforholdType) {
        this.arbeidsforholdType = arbeidsforholdType;
    }

    public BigDecimal getNaturalytelseBortfaltPrÅr() {
        return naturalytelseBortfaltPrÅr;
    }

    public void setNaturalytelseBortfaltPrÅr(BigDecimal naturalytelseBortfaltPrÅr) {
        this.naturalytelseBortfaltPrÅr = naturalytelseBortfaltPrÅr;
    }

    public BigDecimal getNaturalytelseTilkommetPrÅr() {
        return naturalytelseTilkommetPrÅr;
    }

    public void setNaturalytelseTilkommetPrÅr(BigDecimal naturalytelseTilkommetPrÅr) {
        this.naturalytelseTilkommetPrÅr = naturalytelseTilkommetPrÅr;
    }
}
