package no.nav.foreldrepenger.melding.beregningsgrunnlag;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import no.nav.foreldrepenger.melding.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.melding.typer.DatoIntervall;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;

public class BeregningsgrunnlagPrStatusOgAndel {
    private int dagsatsArbeidsgiver;
    private int dagsatsBruker;
    private String aktivitetStatus; // Kodeliste.AktivitetStatus
    private String orginalDagsatsFraTilstøtendeYtelse;
    private int bruttoPrÅr;
    private int besteberegningPrÅr;
    private int overstyrtPrÅr;
    private int pgiSnitt;
    private int pgi1;
    private int pgi2;
    private int pgi3;
    private String opptjeningAktivitetType; // Kodeliste.OpptjeningAktivitetType
    private BGAndelArbeidsforhold bgAndelArbeidsforhold;

    private DatoIntervall beregningsperiode;

    public int getDagsatsArbeidsgiver() {
        return dagsatsArbeidsgiver;
    }

    public int getDagsatsBruker() {
        return dagsatsBruker;
    }

    public String getAktivitetStatus() {
        return aktivitetStatus;
    }

    public String getOrginalDagsatsFraTilstøtendeYtelse() {
        return orginalDagsatsFraTilstøtendeYtelse;
    }

    public int getBruttoPrÅr() {
        return bruttoPrÅr;
    }

    public int getBesteberegningPrÅr() {
        return besteberegningPrÅr;
    }

    public int getOverstyrtPrÅr() {
        return overstyrtPrÅr;
    }

    public int getPgiSnitt() {
        return pgiSnitt;
    }

    public int getPgi1() {
        return pgi1;
    }

    public int getPgi2() {
        return pgi2;
    }

    public int getPgi3() {
        return pgi3;
    }

    public String getOpptjeningAktivitetType() {
        return opptjeningAktivitetType;
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
}
