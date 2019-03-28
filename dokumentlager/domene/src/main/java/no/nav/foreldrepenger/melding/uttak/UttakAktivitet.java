package no.nav.foreldrepenger.melding.uttak;

import java.util.Optional;

import no.nav.foreldrepenger.melding.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;

public class UttakAktivitet {
    private UttakArbeidType uttakArbeidType;
    private Arbeidsgiver arbeidsgiver;
    private ArbeidsforholdRef arbeidsforholdRef;

    public UttakArbeidType getUttakArbeidType() {
        return uttakArbeidType;
    }

    public Optional<Arbeidsgiver> getArbeidsgiver() {
        return Optional.ofNullable(arbeidsgiver);
    }

    public Optional<ArbeidsforholdRef> getArbeidsforholdRef() {
        return Optional.ofNullable(arbeidsforholdRef);
    }
}
