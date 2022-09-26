package no.nav.foreldrepenger.fpformidling.beregningsgrunnlag;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import no.nav.foreldrepenger.fpformidling.opptjening.OpptjeningAktivitetType;
import no.nav.foreldrepenger.fpformidling.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.fpformidling.typer.DatoIntervall;
import no.nav.foreldrepenger.fpformidling.virksomhet.Arbeidsgiver;

public class BeregningsgrunnlagPrStatusOgAndel {
    private Long dagsats;
    private AktivitetStatus aktivitetStatus;
    private BigDecimal bruttoPrÅr;
    private BigDecimal avkortetPrÅr;
    private Boolean nyIArbeidslivet;
    private OpptjeningAktivitetType arbeidsforholdType;
    private DatoIntervall beregningsperiode;
    private BGAndelArbeidsforhold bgAndelArbeidsforhold;
    private Boolean erTilkommetAndel;

    private BeregningsgrunnlagPrStatusOgAndel(Builder builder) {
        dagsats = builder.dagsats;
        aktivitetStatus = builder.aktivitetStatus;
        bruttoPrÅr = builder.bruttoPrÅr;
        avkortetPrÅr = builder.avkortetPrÅr;
        nyIArbeidslivet = builder.nyIArbeidslivet;
        arbeidsforholdType = builder.arbeidsforholdType;
        beregningsperiode = builder.beregningsperiode;
        bgAndelArbeidsforhold = builder.bgAndelArbeidsforhold;
        erTilkommetAndel = builder.erTilkommetAndel;
    }

    public static Builder ny() {
        return new Builder();
    }

    public Long getDagsats() {
        return dagsats;
    }
    public void setDagsats(long sats) {
        this.dagsats = dagsats + sats;
    }

    public AktivitetStatus getAktivitetStatus() {
        return aktivitetStatus;
    }

    public BigDecimal getBruttoPrÅr() {
        return bruttoPrÅr;
    }

    public BigDecimal getAvkortetPrÅr() {
        return avkortetPrÅr;
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
        if (this.getBgAndelArbeidsforhold().flatMap(BGAndelArbeidsforhold::getArbeidsforholdRef).isEmpty() || arbeidsforholdRef.isEmpty()) {
            return Objects.equals(this.getBgAndelArbeidsforhold().flatMap(BGAndelArbeidsforhold::getArbeidsgiver), arbeidsgiver);
        }
        return Objects.equals(this.getBgAndelArbeidsforhold().flatMap(BGAndelArbeidsforhold::getArbeidsforholdRef), arbeidsforholdRef);
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

    public Boolean getErTilkommetAndel() { return erTilkommetAndel; }

    public static final class Builder {
        private Long dagsats;
        private AktivitetStatus aktivitetStatus;
        private BigDecimal bruttoPrÅr;
        private BigDecimal avkortetPrÅr;
        private Boolean nyIArbeidslivet;
        private OpptjeningAktivitetType arbeidsforholdType;
        private DatoIntervall beregningsperiode;
        private BGAndelArbeidsforhold bgAndelArbeidsforhold;
        private Boolean erTilkommetAndel;

        private Builder() {
        }

        public Builder medDagsats(Long dagsats) {
            this.dagsats = dagsats;
            return this;
        }

        public Builder medAktivitetStatus(AktivitetStatus aktivitetStatus) {
            this.aktivitetStatus = aktivitetStatus;
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

        public Builder medNyIArbeidslivet(Boolean nyIArbeidslivet) {
            this.nyIArbeidslivet = nyIArbeidslivet;
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

        public Builder medErTilkommetAndel(Boolean erTilkommetAndel) {
            this.erTilkommetAndel = erTilkommetAndel;
            return this;
        }

        public BeregningsgrunnlagPrStatusOgAndel build() {
            return new BeregningsgrunnlagPrStatusOgAndel(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (BeregningsgrunnlagPrStatusOgAndel) o;
        return Objects.equals(dagsats, that.dagsats) && aktivitetStatus == that.aktivitetStatus && Objects.equals(bruttoPrÅr, that.bruttoPrÅr) && Objects.equals(avkortetPrÅr, that.avkortetPrÅr) && Objects.equals(nyIArbeidslivet, that.nyIArbeidslivet) && arbeidsforholdType == that.arbeidsforholdType && Objects.equals(beregningsperiode, that.beregningsperiode) && Objects.equals(bgAndelArbeidsforhold, that.bgAndelArbeidsforhold) && Objects.equals(erTilkommetAndel, that.erTilkommetAndel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dagsats, aktivitetStatus, bruttoPrÅr, avkortetPrÅr, nyIArbeidslivet, arbeidsforholdType, beregningsperiode, bgAndelArbeidsforhold, erTilkommetAndel);
    }

    @Override
    public String toString() {
        return "BeregningsgrunnlagPrStatusOgAndel{" +
                "dagsats=" + dagsats +
                ", aktivitetStatus=" + aktivitetStatus +
                ", bruttoPrÅr=" + bruttoPrÅr +
                ", avkortetPrÅr=" + avkortetPrÅr +
                ", nyIArbeidslivet=" + nyIArbeidslivet +
                ", arbeidsforholdType=" + arbeidsforholdType +
                ", beregningsperiode=" + beregningsperiode +
                ", bgAndelArbeidsforhold=" + bgAndelArbeidsforhold +
                ", erTilkommetAndel=" + erTilkommetAndel +
                '}';
    }
}
