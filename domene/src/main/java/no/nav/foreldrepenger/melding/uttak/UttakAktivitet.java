package no.nav.foreldrepenger.melding.uttak;

import java.util.Optional;

import no.nav.foreldrepenger.melding.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;

public class UttakAktivitet {
    private UttakArbeidType uttakArbeidType;
    private Arbeidsgiver arbeidsgiver;
    private ArbeidsforholdRef arbeidsforholdRef;

    private UttakAktivitet(Builder builder) {
        uttakArbeidType = builder.uttakArbeidType;
        arbeidsgiver = builder.arbeidsgiver;
        arbeidsforholdRef = builder.arbeidsforholdRef;
    }

    public static Builder ny() {
        return new Builder();
    }

    public UttakArbeidType getUttakArbeidType() {
        return uttakArbeidType;
    }

    public Optional<Arbeidsgiver> getArbeidsgiver() {
        return Optional.ofNullable(arbeidsgiver);
    }

    public Optional<ArbeidsforholdRef> getArbeidsforholdRef() {
        return Optional.ofNullable(arbeidsforholdRef);
    }


    public static final class Builder {
        private UttakArbeidType uttakArbeidType;
        private Arbeidsgiver arbeidsgiver;
        private ArbeidsforholdRef arbeidsforholdRef;

        private Builder() {
        }

        public Builder medUttakArbeidType(UttakArbeidType uttakArbeidType) {
            this.uttakArbeidType = uttakArbeidType;
            return this;
        }

        public Builder medArbeidsgiver(Arbeidsgiver arbeidsgiver) {
            this.arbeidsgiver = arbeidsgiver;
            return this;
        }

        public Builder medArbeidsforholdRef(ArbeidsforholdRef arbeidsforholdRef) {
            this.arbeidsforholdRef = arbeidsforholdRef;
            return this;
        }

        public UttakAktivitet build() {
            return new UttakAktivitet(this);
        }
    }
}
