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
    private AktivitetStatus aktivitetStatus;
    private Long originalDagsatsFraTilstøtendeYtelse;
    private BigDecimal bruttoPrÅr;
    private BigDecimal avkortetPrÅr;
    private BigDecimal besteberegningPrÅr;
    private BigDecimal overstyrtPrÅr;
    private BigDecimal pgiSnitt;
    private BigDecimal pgi1;
    private BigDecimal pgi2;
    private BigDecimal pgi3;
    private Boolean nyIArbeidslivet;
    private RelatertYtelseType ytelse;
    private OpptjeningAktivitetType arbeidsforholdType;
    private DatoIntervall beregningsperiode;
    private BGAndelArbeidsforhold bgAndelArbeidsforhold;

    private BeregningsgrunnlagPrStatusOgAndel(Builder builder) {
        dagsatsArbeidsgiver = builder.dagsatsArbeidsgiver;
        dagsatsBruker = builder.dagsatsBruker;
        aktivitetStatus = builder.aktivitetStatus;
        originalDagsatsFraTilstøtendeYtelse = builder.originalDagsatsFraTilstøtendeYtelse;
        bruttoPrÅr = builder.bruttoPrÅr;
        avkortetPrÅr = builder.avkortetPrÅr;
        besteberegningPrÅr = builder.besteberegningPrÅr;
        overstyrtPrÅr = builder.overstyrtPrÅr;
        pgiSnitt = builder.pgiSnitt;
        pgi1 = builder.pgi1;
        pgi2 = builder.pgi2;
        pgi3 = builder.pgi3;
        nyIArbeidslivet = builder.nyIArbeidslivet;
        ytelse = builder.ytelse;
        arbeidsforholdType = builder.arbeidsforholdType;
        beregningsperiode = builder.beregningsperiode;
        bgAndelArbeidsforhold = builder.bgAndelArbeidsforhold;
    }

    public static Builder ny() {
        return new Builder();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Long getDagsatsArbeidsgiver() {
        return dagsatsArbeidsgiver;
    }

    public Long getDagsatsBruker() {
        return dagsatsBruker;
    }

    public AktivitetStatus getAktivitetStatus() {
        return aktivitetStatus;
    }

    public Long getOriginalDagsatsFraTilstøtendeYtelse() {
        return originalDagsatsFraTilstøtendeYtelse;
    }

    public BigDecimal getBruttoPrÅr() {
        return bruttoPrÅr;
    }

    public BigDecimal getAvkortetPrÅr() {
        return avkortetPrÅr;
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


    public static final class Builder {
        private Long dagsatsArbeidsgiver;
        private Long dagsatsBruker;
        private AktivitetStatus aktivitetStatus;
        private Long originalDagsatsFraTilstøtendeYtelse;
        private BigDecimal bruttoPrÅr;
        private BigDecimal avkortetPrÅr;
        private BigDecimal besteberegningPrÅr;
        private BigDecimal overstyrtPrÅr;
        private BigDecimal pgiSnitt;
        private BigDecimal pgi1;
        private BigDecimal pgi2;
        private BigDecimal pgi3;
        private Boolean nyIArbeidslivet;
        private RelatertYtelseType ytelse;
        private OpptjeningAktivitetType arbeidsforholdType;
        private DatoIntervall beregningsperiode;
        private BGAndelArbeidsforhold bgAndelArbeidsforhold;

        private Builder() {
        }

        public Builder medDagsatsArbeidsgiver(Long dagsatsArbeidsgiver) {
            this.dagsatsArbeidsgiver = dagsatsArbeidsgiver;
            return this;
        }

        public Builder medDagsatsBruker(Long dagsatsBruker) {
            this.dagsatsBruker = dagsatsBruker;
            return this;
        }

        public Builder medAktivitetStatus(AktivitetStatus aktivitetStatus) {
            this.aktivitetStatus = aktivitetStatus;
            return this;
        }

        public Builder medOriginalDagsatsFraTilstøtendeYtelse(Long originalDagsatsFraTilstøtendeYtelse) {
            this.originalDagsatsFraTilstøtendeYtelse = originalDagsatsFraTilstøtendeYtelse;
            return this;
        }

        public Builder medBruttoPrÅr(BigDecimal bruttoPrÅr) {
            this.bruttoPrÅr = bruttoPrÅr;
            return this;
        }

        public Builder medAvkortetPrÅr(BigDecimal avkortetPrÅr) {
            this.avkortetPrÅr = avkortetPrÅr;
            return this;
        }

        public Builder medBesteberegningPrÅr(BigDecimal besteberegningPrÅr) {
            this.besteberegningPrÅr = besteberegningPrÅr;
            return this;
        }

        public Builder medOverstyrtPrÅr(BigDecimal overstyrtPrÅr) {
            this.overstyrtPrÅr = overstyrtPrÅr;
            return this;
        }

        public Builder medPgiSnitt(BigDecimal pgiSnitt) {
            this.pgiSnitt = pgiSnitt;
            return this;
        }

        public Builder medPgi1(BigDecimal pgi1) {
            this.pgi1 = pgi1;
            return this;
        }

        public Builder medPgi2(BigDecimal pgi2) {
            this.pgi2 = pgi2;
            return this;
        }

        public Builder medPgi3(BigDecimal pgi3) {
            this.pgi3 = pgi3;
            return this;
        }

        public Builder medNyIArbeidslivet(Boolean nyIArbeidslivet) {
            this.nyIArbeidslivet = nyIArbeidslivet;
            return this;
        }

        public Builder medYtelse(RelatertYtelseType ytelse) {
            this.ytelse = ytelse;
            return this;
        }

        public Builder medArbeidsforholdType(OpptjeningAktivitetType arbeidsforholdType) {
            this.arbeidsforholdType = arbeidsforholdType;
            return this;
        }

        public Builder medBeregningsperiode(DatoIntervall beregningsperiode) {
            this.beregningsperiode = beregningsperiode;
            return this;
        }

        public Builder medBgAndelArbeidsforhold(BGAndelArbeidsforhold bgAndelArbeidsforhold) {
            this.bgAndelArbeidsforhold = bgAndelArbeidsforhold;
            return this;
        }

        public BeregningsgrunnlagPrStatusOgAndel build() {
            return new BeregningsgrunnlagPrStatusOgAndel(this);
        }
    }
}
