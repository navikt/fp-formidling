package no.nav.foreldrepenger.melding.beregningsgrunnlag;

import java.util.Optional;

import no.nav.foreldrepenger.melding.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;
import no.nav.foreldrepenger.melding.virksomhet.Virksomhet;

public class BGAndelArbeidsforhold {
    private Arbeidsgiver arbeidsgiver;
    private ArbeidsforholdRef arbeidsforholdRef;

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
