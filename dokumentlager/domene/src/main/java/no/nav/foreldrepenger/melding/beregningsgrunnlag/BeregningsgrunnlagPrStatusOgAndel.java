package no.nav.foreldrepenger.melding.beregningsgrunnlag;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndelDto;
import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndelSNDto;
import no.nav.foreldrepenger.melding.inntektarbeidytelse.RelatertYtelseType;
import no.nav.foreldrepenger.melding.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.melding.typer.DatoIntervall;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;

public class BeregningsgrunnlagPrStatusOgAndel {
    private Long dagsatsArbeidsgiver;
    private Long dagsatsBruker;
    private String aktivitetStatus; // Kodeliste.AktivitetStatus
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
    private String arbeidsforholdType;
    private DatoIntervall beregningsperiode;
    private BGAndelArbeidsforhold bgAndelArbeidsforhold;

    private BeregningsgrunnlagPrStatusOgAndel(Builder builder) {
        this.dagsatsArbeidsgiver = builder.dagsatsArbeidsgiver;
        this.dagsatsBruker = builder.dagsatsBruker;
        this.aktivitetStatus = builder.aktivitetStatus;
        this.originalDagsatsFraTilstøtendeYtelse = builder.originalDagsatsFraTilstøtendeYtelse;
        this.bruttoPrÅr = builder.bruttoPrÅr;
        this.avkortetPrÅr = builder.avkortetPrÅr;
        this.besteberegningPrÅr = builder.besteberegningPrÅr;
        this.overstyrtPrÅr = builder.overstyrtPrÅr;
        this.pgiSnitt = builder.pgiSnitt;
        this.pgi1 = builder.pgi1;
        this.pgi2 = builder.pgi2;
        this.pgi3 = builder.pgi3;
        this.nyIArbeidslivet = builder.nyIArbeidslivet;
        this.ytelse = builder.ytelse;
        this.arbeidsforholdType = builder.arbeidsforholdType;
        this.beregningsperiode = builder.beregningsperiode;
        this.bgAndelArbeidsforhold = builder.bgAndelArbeidsforhold;
    }

    public static BeregningsgrunnlagPrStatusOgAndel fraDto(BeregningsgrunnlagPrStatusOgAndelDto dto) {
        Builder builder = BeregningsgrunnlagPrStatusOgAndel.ny();
        BGAndelArbeidsforhold bgAndelArbeidsforhold = BGAndelArbeidsforhold.fraDto(dto.getArbeidsforhold());
        builder.aktivitetStatus(dto.getAktivitetStatus().kode)
                .bruttoPrÅr(dto.getBruttoPrAar())
                .avkortetPrÅr(dto.getAvkortetPrAar())
                .besteberegningPrÅr(dto.getBesteberegningPrAar())
                .overstyrtPrÅr(dto.getOverstyrtPrAar())
                .nyIArbeidslivet(dto.getErNyIArbeidslivet())
                //TODO - mangler verdi
                .originalDagsatsFraTilstøtendeYtelse(null)
                //TODO - mangler verdi
                .dagsatsArbeidsgiver(null)
                //TODO - mangler verdi
                .dagsatsBruker(null)
                .beregningsperiode(avklarBeregningsperiode(dto))
                .bgAndelArbeidsforhold(bgAndelArbeidsforhold)
                .arbeidsforholdType(bgAndelArbeidsforhold.getArbeidsforholdType());
        if (dto instanceof BeregningsgrunnlagPrStatusOgAndelSNDto) {
            BeregningsgrunnlagPrStatusOgAndelSNDto snDto = (BeregningsgrunnlagPrStatusOgAndelSNDto) dto;
            builder.pgi1(snDto.getPgi1())
                    .pgi2(snDto.getPgi2())
                    .pgi3(snDto.getPgi3())
                    .pgiSnitt(snDto.getPgiSnitt());
        }
        return builder.build();
    }

    private static DatoIntervall avklarBeregningsperiode(BeregningsgrunnlagPrStatusOgAndelDto dto) {
        if (dto.getBeregningsgrunnlagTom() == null) {
            return DatoIntervall.fraOgMed(dto.getBeregningsgrunnlagFom());
        }
        return DatoIntervall.fraOgMedTilOgMed(dto.getBeregningsgrunnlagFom(), dto.getBeregningsgrunnlagTom());
    }

    public static Builder ny() {
        return new Builder();
    }

    public Long getDagsatsArbeidsgiver() {
        return dagsatsArbeidsgiver;
    }

    public Long getDagsatsBruker() {
        return dagsatsBruker;
    }

    public String getAktivitetStatus() {
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

    //OpptjeningAktivitetType
    public String getArbeidsforholdType() {
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


    //Generert builder
    public static final class Builder {
        private Long dagsatsArbeidsgiver;
        private Long dagsatsBruker;
        private String aktivitetStatus;
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
        private String arbeidsforholdType;
        private DatoIntervall beregningsperiode;
        private BGAndelArbeidsforhold bgAndelArbeidsforhold;

        private Builder() {
        }

        public Builder dagsatsArbeidsgiver(Long val) {
            dagsatsArbeidsgiver = val;
            return this;
        }

        public Builder dagsatsBruker(Long val) {
            dagsatsBruker = val;
            return this;
        }

        public Builder aktivitetStatus(String val) {
            aktivitetStatus = val;
            return this;
        }

        public Builder originalDagsatsFraTilstøtendeYtelse(Long val) {
            originalDagsatsFraTilstøtendeYtelse = val;
            return this;
        }

        public Builder bruttoPrÅr(BigDecimal val) {
            bruttoPrÅr = val;
            return this;
        }

        public Builder avkortetPrÅr(BigDecimal val) {
            avkortetPrÅr = val;
            return this;
        }

        public Builder besteberegningPrÅr(BigDecimal val) {
            besteberegningPrÅr = val;
            return this;
        }

        public Builder overstyrtPrÅr(BigDecimal val) {
            overstyrtPrÅr = val;
            return this;
        }

        public Builder pgiSnitt(BigDecimal val) {
            pgiSnitt = val;
            return this;
        }

        public Builder pgi1(BigDecimal val) {
            pgi1 = val;
            return this;
        }

        public Builder pgi2(BigDecimal val) {
            pgi2 = val;
            return this;
        }

        public Builder pgi3(BigDecimal val) {
            pgi3 = val;
            return this;
        }

        public Builder nyIArbeidslivet(Boolean val) {
            nyIArbeidslivet = val;
            return this;
        }

        public Builder ytelse(RelatertYtelseType val) {
            ytelse = val;
            return this;
        }

        public Builder arbeidsforholdType(String val) {
            arbeidsforholdType = val;
            return this;
        }

        public Builder beregningsperiode(DatoIntervall val) {
            beregningsperiode = val;
            return this;
        }

        public Builder bgAndelArbeidsforhold(BGAndelArbeidsforhold val) {
            bgAndelArbeidsforhold = val;
            return this;
        }

        public BeregningsgrunnlagPrStatusOgAndel build() {
            return new BeregningsgrunnlagPrStatusOgAndel(this);
        }
    }
}
