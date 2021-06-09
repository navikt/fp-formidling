package no.nav.foreldrepenger.melding.beregningsgrunnlag;

import java.math.BigDecimal;
import java.util.Optional;

import no.nav.foreldrepenger.melding.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;

public record BGAndelArbeidsforhold(Arbeidsgiver arbeidsgiver,
        ArbeidsforholdRef arbeidsforholdRef,
        BigDecimal naturalytelseBortfaltPrÅr,
        BigDecimal naturalytelseTilkommetPrÅr) {

    public Optional<Arbeidsgiver> getArbeidsgiver() {
        return Optional.ofNullable(arbeidsgiver());
    }

    public Optional<ArbeidsforholdRef> getArbeidsforholdRef() {
        return Optional.ofNullable(arbeidsforholdRef());
    }
}
