package no.nav.foreldrepenger.melding.beregningsgrunnlag;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import no.nav.foreldrepenger.melding.inntektarbeidytelse.RelatertYtelseType;
import no.nav.foreldrepenger.melding.opptjening.OpptjeningAktivitetType;
import no.nav.foreldrepenger.melding.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.melding.typer.DatoIntervall;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;

public class BeregningsgrunnlagPrStatusOgAndel {
    private Long dagsatsArbeidsgiver;
    private Long dagsatsBruker;
    private String aktivitetStatus; // Kodeliste.AktivitetStatus
    private String orginalDagsatsFraTilstøtendeYtelse;
    private BigDecimal bruttoPrÅr;
    private BigDecimal besteberegningPrÅr;
    private BigDecimal overstyrtPrÅr;
    private BigDecimal pgiSnitt;
    private BigDecimal pgi1;
    private BigDecimal pgi2;
    private BigDecimal pgi3;
    private Boolean nyIArbeidslivet;
    private String opptjeningAktivitetType; // Kodeliste.OpptjeningAktivitetType
    private RelatertYtelseType ytelse;
    private OpptjeningAktivitetType arbeidsforholdType;
    private DatoIntervall beregningsperiode;
    private BGAndelArbeidsforhold bgAndelArbeidsforhold;

    public Long getDagsatsArbeidsgiver() {
        return dagsatsArbeidsgiver;
    }

    public Long getDagsatsBruker() {
        return dagsatsBruker;
    }

    public String getAktivitetStatus() {
        return aktivitetStatus;
    }

    public String getOrginalDagsatsFraTilstøtendeYtelse() {
        return orginalDagsatsFraTilstøtendeYtelse;
    }

    public BigDecimal getBruttoPrÅr() {
        return bruttoPrÅr;
    }

    public BigDecimal getBesteberegningPrÅr() {
        return besteberegningPrÅr;
    }

    public BigDecimal getOverstyrtPrÅr() {
        return overstyrtPrÅr;
    }

    public BigDecimal getPgiSnitt() {
        return pgiSnitt;
    }

    public BigDecimal getPgi1() {
        return pgi1;
    }

    public BigDecimal getPgi2() {
        return pgi2;
    }

    public BigDecimal getPgi3() {
        return pgi3;
    }

    public String getOpptjeningAktivitetType() {
        return opptjeningAktivitetType;
    }

    public RelatertYtelseType getYtelse() {
        return ytelse;
    }

    public OpptjeningAktivitetType getArbeidsforholdType() {
        return arbeidsforholdType;
    }

    public Optional<BGAndelArbeidsforhold> getBgAndelArbeidsforhold() {
        return Optional.ofNullable(bgAndelArbeidsforhold);
    }

    public boolean gjelderSammeArbeidsforhold(Arbeidsgiver arbeidsgiver, ArbeidsforholdRef arbeidsforholdRef) {
        return gjelderSammeArbeidsforhold(Optional.ofNullable(arbeidsgiver), Optional.ofNullable(arbeidsforholdRef));
    }

    private boolean gjelderSammeArbeidsforhold(Optional<Arbeidsgiver> arbeidsgiver, Optional<ArbeidsforholdRef> arbeidsforholdRef) {
        if (!Objects.equals(getAktivitetStatus(), AktivitetStatus.ARBEIDSTAKER)) {
            return false;
        }
        if (!this.getBgAndelArbeidsforhold().flatMap(BGAndelArbeidsforhold::getArbeidsforholdRef).isPresent() || !arbeidsforholdRef.isPresent()) {
            return Objects.equals(this.getBgAndelArbeidsforhold().flatMap(BGAndelArbeidsforhold::getArbeidsgiver), arbeidsgiver);
        }
        return Objects.equals(this.getBgAndelArbeidsforhold().flatMap(BGAndelArbeidsforhold::getArbeidsforholdRef), arbeidsforholdRef);
    }

    public Long getDagsats() {
        if (dagsatsBruker == null) {
            return dagsatsArbeidsgiver;
        }
        if (dagsatsArbeidsgiver == null) {
            return dagsatsBruker;
        }
        return dagsatsBruker + dagsatsArbeidsgiver;
    }

    public LocalDate getBeregningsperiodeFom() {
        return beregningsperiode != null ? beregningsperiode.getFomDato() : null;
    }

    public LocalDate getBeregningsperiodeTom() {
        return beregningsperiode != null ? beregningsperiode.getTomDato() : null;
    }

    public Boolean getNyIArbeidslivet() {
        return nyIArbeidslivet;
    }
}
