package no.nav.foreldrepenger.melding.uttak.svp;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;

public class SvpUttakResultatArbeidsforhold {

    private ArbeidsforholdIkkeOppfyltÅrsak arbeidsforholdIkkeOppfyltÅrsak;

    private Arbeidsgiver arbeidsgiver;

    private List<SvpUttakResultatPeriode> perioder;

    private SvpUttakResultatArbeidsforhold(Builder builder) {
        arbeidsforholdIkkeOppfyltÅrsak = builder.arbeidsforholdIkkeOppfyltÅrsak;
        arbeidsgiver = builder.arbeidsgiver;
        perioder = builder.perioder;
    }

    public ArbeidsforholdIkkeOppfyltÅrsak getArbeidsforholdIkkeOppfyltÅrsak() {
        return arbeidsforholdIkkeOppfyltÅrsak;
    }

    public Arbeidsgiver getArbeidsgiver() {
        return arbeidsgiver;
    }

    public List<SvpUttakResultatPeriode> getPerioder() {
        return perioder.stream().sorted().collect(Collectors.toList());
    }

    public static Builder ny() {
        return new Builder();
    }

    public static final class Builder {
        private ArbeidsforholdIkkeOppfyltÅrsak arbeidsforholdIkkeOppfyltÅrsak = ArbeidsforholdIkkeOppfyltÅrsak.INGEN;
        private Arbeidsgiver arbeidsgiver;
        private List<SvpUttakResultatPeriode> perioder = new ArrayList<>();

        public Builder() {
        }

        public Builder medArbeidsforholdIkkeOppfyltÅrsak(ArbeidsforholdIkkeOppfyltÅrsak arbeidsforholdIkkeOppfyltÅrsak) {
            if (arbeidsforholdIkkeOppfyltÅrsak != null) {
                this.arbeidsforholdIkkeOppfyltÅrsak = arbeidsforholdIkkeOppfyltÅrsak;
            }
            return this;
        }

        public Builder medArbeidsgiver(Arbeidsgiver arbeidsgiver) {
            this.arbeidsgiver = arbeidsgiver;
            return this;
        }

        public Builder medPeriode(SvpUttakResultatPeriode periode) {
            this.perioder.add(periode);
            return this;
        }

        public SvpUttakResultatArbeidsforhold build() {
            return new SvpUttakResultatArbeidsforhold(this);
        }
    }
}
