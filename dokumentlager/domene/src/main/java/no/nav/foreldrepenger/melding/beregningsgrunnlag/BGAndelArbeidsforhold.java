package no.nav.foreldrepenger.melding.beregningsgrunnlag;

import java.util.Optional;

import no.nav.foreldrepenger.melding.opptjening.OpptjeningAktivitetType;
import no.nav.foreldrepenger.melding.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;
import no.nav.foreldrepenger.melding.virksomhet.Virksomhet;

public class BGAndelArbeidsforhold {
    private Arbeidsgiver arbeidsgiver;
    private ArbeidsforholdRef arbeidsforholdRef;
    private OpptjeningAktivitetType arbeidsforholdType;

    public BGAndelArbeidsforhold(Arbeidsgiver arbeidsgiver, ArbeidsforholdRef arbeidsforholdRef, OpptjeningAktivitetType arbeidsforholdType) {
        this.arbeidsgiver = arbeidsgiver;
        this.arbeidsforholdRef = arbeidsforholdRef;
        this.arbeidsforholdType = arbeidsforholdType;
    }

    public OpptjeningAktivitetType getArbeidsforholdType() {
        return arbeidsforholdType;
    }

    public Optional<Arbeidsgiver> getArbeidsgiver() {
        return Optional.ofNullable(arbeidsgiver);
    }

    public Optional<ArbeidsforholdRef> getArbeidsforholdRef() {
        return Optional.ofNullable(arbeidsforholdRef);
    }

    public Optional<Virksomhet> getVirksomhet() {
        return Optional.ofNullable(arbeidsgiver).map(Arbeidsgiver::getVirksomhet);
    }
}
