package no.nav.foreldrepenger.fpformidling.beregningsgrunnlag;

import java.math.BigDecimal;
import java.util.Optional;

import no.nav.foreldrepenger.fpformidling.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.fpformidling.virksomhet.Arbeidsgiver;

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

    public BigDecimal getNaturalytelseBortfaltPrÅr() {
        return naturalytelseBortfaltPrÅr;
    }

    public BigDecimal getNaturalytelseTilkommetPrÅr() {
        return naturalytelseTilkommetPrÅr;
    }
}
