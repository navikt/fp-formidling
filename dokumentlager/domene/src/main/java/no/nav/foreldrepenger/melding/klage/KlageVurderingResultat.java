package no.nav.foreldrepenger.melding.klage;

public class KlageVurderingResultat {

    private KlageVurdering KlageVurdering;

    private KlageVurderingResultat(Builder builder) {
        KlageVurdering = builder.KlageVurdering;
    }

    public static Builder ny() {
        return new Builder();
    }

    public KlageVurdering getKlageVurdering() {
        return KlageVurdering;
    }

    public static final class Builder {
        private KlageVurdering KlageVurdering;

        private Builder() {
        }

        public Builder medKlageVurdering(KlageVurdering val) {
            KlageVurdering = val;
            return this;
        }

        public KlageVurderingResultat build() {
            return new KlageVurderingResultat(this);
        }
    }
}
