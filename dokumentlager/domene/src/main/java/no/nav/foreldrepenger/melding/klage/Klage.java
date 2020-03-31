package no.nav.foreldrepenger.melding.klage;

import no.nav.foreldrepenger.melding.behandling.BehandlingType;

public class Klage {
    private BehandlingType påklagdBehandlingType;
    private KlageVurderingResultat klageVurderingResultatNFP;
    private KlageVurderingResultat klageVurderingResultatNK;
    private KlageFormkravResultat formkravKA;
    private KlageFormkravResultat formkravNFP;

    private Klage(Builder builder) {
        påklagdBehandlingType = builder.påklagdBehandlingType;
        klageVurderingResultatNFP = builder.klageVurderingResultatNFP;
        klageVurderingResultatNK = builder.klageVurderingResultatNK;
        formkravKA = builder.formkravKA;
        formkravNFP = builder.formkravNFP;
    }

    public static Builder ny() {
        return new Builder();
    }

    public BehandlingType getPåklagdBehandlingType() {
        return påklagdBehandlingType;
    }

    public KlageFormkravResultat getFormkravKA() {
        return formkravKA;
    }

    public KlageFormkravResultat getFormkravNFP() {
        return formkravNFP;
    }

    public KlageVurderingResultat getKlageVurderingResultatNFP() {
        return klageVurderingResultatNFP;
    }

    public KlageVurderingResultat getKlageVurderingResultatNK() {
        return klageVurderingResultatNK;
    }

    public KlageVurderingResultat getGjeldendeKlageVurderingsresultat() {
        if (klageVurderingResultatNK != null) {
            return klageVurderingResultatNK;
        } else if (klageVurderingResultatNFP != null) {
            return klageVurderingResultatNFP;
        }
        return null;
    }

    public static final class Builder {
        private BehandlingType påklagdBehandlingType;
        private KlageVurderingResultat klageVurderingResultatNFP;
        private KlageVurderingResultat klageVurderingResultatNK;
        private KlageFormkravResultat formkravKA;
        private KlageFormkravResultat formkravNFP;

        private Builder() {
        }

        public Builder medKlageVurderingResultatNFP(KlageVurderingResultat val) {
            klageVurderingResultatNFP = val;
            return this;
        }

        public Builder medKlageVurderingResultatNK(KlageVurderingResultat val) {
            klageVurderingResultatNK = val;
            return this;
        }

        public Builder medFormkravKA(KlageFormkravResultat val) {
            formkravKA = val;
            return this;
        }

        public Builder medFormkravNFP(KlageFormkravResultat val) {
            formkravNFP = val;
            return this;
        }

        public Builder medPåklagdBehandlingType(BehandlingType val) {
            påklagdBehandlingType = val;
            return this;
        }

        public Klage build() {
            return new Klage(this);
        }
    }
}
