package no.nav.foreldrepenger.melding.aksjonspunkt;

public class Aksjonspunkt {
    private AksjonspunktDefinisjon aksjonspunktDefinisjon;

    private Aksjonspunkt(Builder builder) {
        aksjonspunktDefinisjon = builder.aksjonspunktDefinisjon;
    }

    public static Builder ny() {
        return new Builder();
    }

    public AksjonspunktDefinisjon getAksjonspunktDefinisjon() {
        return aksjonspunktDefinisjon;
    }

    public static final class Builder {
        private AksjonspunktDefinisjon aksjonspunktDefinisjon;

        private Builder() {
        }

        public Builder medAksjonspunktDefinisjon(AksjonspunktDefinisjon aksjonspunktDefinisjon) {
            this.aksjonspunktDefinisjon = aksjonspunktDefinisjon;
            return this;
        }

        public Aksjonspunkt build() {
            return new Aksjonspunkt(this);
        }
    }
}
