package no.nav.foreldrepenger.fpformidling.beregningsgrunnlag;

import no.nav.foreldrepenger.fpformidling.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.fpformidling.virksomhet.Arbeidsgiver;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

public record BGAndelArbeidsforhold(Arbeidsgiver arbeidsgiver, ArbeidsforholdRef arbeidsforholdRef, BigDecimal naturalytelseBortfaltPrÅr,
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        var that = (BGAndelArbeidsforhold) o;
        return Objects.equals(arbeidsgiver, that.arbeidsgiver) && Objects.equals(arbeidsforholdRef, that.arbeidsforholdRef) && Objects.equals(
            naturalytelseBortfaltPrÅr, that.naturalytelseBortfaltPrÅr) && Objects.equals(naturalytelseTilkommetPrÅr, that.naturalytelseTilkommetPrÅr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(arbeidsgiver, arbeidsforholdRef, naturalytelseBortfaltPrÅr, naturalytelseTilkommetPrÅr);
    }

    @Override
    public String toString() {
        return "BGAndelArbeidsforhold{" + "arbeidsgiver=" + arbeidsgiver + ", arbeidsforholdRef=" + arbeidsforholdRef + ", naturalytelseBortfaltPrÅr="
            + naturalytelseBortfaltPrÅr + ", naturalytelseTilkommetPrÅr=" + naturalytelseTilkommetPrÅr + '}';
    }
}
