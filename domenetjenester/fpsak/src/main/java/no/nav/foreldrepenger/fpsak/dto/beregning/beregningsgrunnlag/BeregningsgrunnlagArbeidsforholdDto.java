package no.nav.foreldrepenger.fpsak.dto.beregning.beregningsgrunnlag;

import java.math.BigDecimal;
import java.time.LocalDate;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

public class BeregningsgrunnlagArbeidsforholdDto {

    private String arbeidsgiverNavn;
    private String arbeidsgiverId;
    private LocalDate startdato;
    private LocalDate opphoersdato;
    private String arbeidsforholdId;
    private KodeDto arbeidsforholdType;
    private Long aktørId;
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

    public String getArbeidsgiverNavn() {
        return arbeidsgiverNavn;
    }

    public void setArbeidsgiverNavn(String arbeidsgiverNavn) {
        this.arbeidsgiverNavn = arbeidsgiverNavn;
    }

    public String getArbeidsgiverId() {
        return arbeidsgiverId;
    }

    public void setArbeidsgiverId(String arbeidsgiverId) {
        this.arbeidsgiverId = arbeidsgiverId;
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

    public void setAktørId(Long aktørId) {
        this.aktørId = aktørId;
    }

    public Long getAktørId() {
        return aktørId;
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
