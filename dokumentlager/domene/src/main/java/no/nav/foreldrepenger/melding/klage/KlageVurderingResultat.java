package no.nav.foreldrepenger.melding.klage;

public class KlageVurderingResultat {

    private KlageVurdering KlageVurdering;
    private String fritekstTilBrev;

    private KlageVurderingResultat(Builder builder) {
        KlageVurdering = builder.KlageVurdering;
        fritekstTilBrev = builder.fritekstTilBrev;
    }

    public static Builder ny() {
        return new Builder();
    }

    public KlageVurdering getKlageVurdering() {
        return KlageVurdering;
    }

    public String getFritekstTilBrev() {
        return fritekstTilBrev;
    }

    public static final class Builder {
        private KlageVurdering KlageVurdering;
        private String fritekstTilBrev;

        private Builder() {
        }

        public Builder medKlageVurdering(KlageVurdering val) {
            KlageVurdering = val;
            return this;
        }

        public Builder medFritekstTilbrev(String fritekstTilBrev) {
            this.fritekstTilBrev = fritekstTilBrev;
            return this;
        }

        public KlageVurderingResultat build() {
            return new KlageVurderingResultat(this);
        }
    }
}
