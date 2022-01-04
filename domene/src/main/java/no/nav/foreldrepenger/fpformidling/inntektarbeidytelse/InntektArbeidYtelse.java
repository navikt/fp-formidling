package no.nav.foreldrepenger.fpformidling.inntektarbeidytelse;

import java.util.ArrayList;
import java.util.List;

public class InntektArbeidYtelse {

    List<Inntektsmelding> inntektsmeldinger = new ArrayList<>();

    private InntektArbeidYtelse(Builder builder) {
        inntektsmeldinger = builder.inntektsmeldinger;
    }

    public static Builder ny() {
        return new Builder();
    }

    public List<Inntektsmelding> getInntektsmeldinger() {
        return inntektsmeldinger;
    }


    public static final class Builder {
        private List<Inntektsmelding> inntektsmeldinger = new ArrayList<>();

        private Builder() {
        }

        public Builder medInntektsmeldinger(List<Inntektsmelding> inntektsmeldinger) {
            this.inntektsmeldinger = inntektsmeldinger;
            return this;
        }

        public InntektArbeidYtelse build() {
            return new InntektArbeidYtelse(this);
        }
    }
}
