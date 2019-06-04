package no.nav.foreldrepenger.melding.aksjonspunkt;

public class Aksjonspunkt {
    private AksjonspunktDefinisjon aksjonspunktDefinisjon;
    private AksjonspunktStatus aksjonspunktStatus;

    private Aksjonspunkt(Builder builder) {

        aksjonspunktDefinisjon = builder.aksjonspunktDefinisjon;
        aksjonspunktStatus = builder.aksjonspunktStatus;
    }

    public static Builder ny() {
        return new Builder();
    }

    public AksjonspunktDefinisjon getAksjonspunktDefinisjon() {
        return aksjonspunktDefinisjon;
    }

    public AksjonspunktStatus getAksjonspunktStatus() {
        return aksjonspunktStatus;
    }

    public static final class Builder {
        private AksjonspunktDefinisjon aksjonspunktDefinisjon;
        private AksjonspunktStatus aksjonspunktStatus;

        private Builder() {
        }

        public Builder medAksjonspunktDefinisjon(AksjonspunktDefinisjon aksjonspunktDefinisjon) {
            this.aksjonspunktDefinisjon = aksjonspunktDefinisjon;
            return this;
        }

        public Builder medAksjonspunktStatus(AksjonspunktStatus aksjonspunktStatus) {
            this.aksjonspunktStatus = aksjonspunktStatus;
            return this;
        }

        public Aksjonspunkt build() {
            return new Aksjonspunkt(this);
        }
    }
}
