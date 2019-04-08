package no.nav.foreldrepenger.melding.brevbestiller.api.dto.klage;

import no.nav.foreldrepenger.fpsak.dto.klage.KlagebehandlingDto;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;

public class Klage {
    private KlageVurderingResultat klageVurderingResultatNFP;
    private KlageVurderingResultat klageVurderingResultatNK;
    private KlageFormkravResultat formkravKA;
    private KlageFormkravResultat formkravNFP;

    private Klage(Builder builder) {
        klageVurderingResultatNFP = builder.klageVurderingResultatNFP;
        klageVurderingResultatNK = builder.klageVurderingResultatNK;
        formkravKA = builder.formkravKA;
        formkravNFP = builder.formkravNFP;
    }

    public static Klage fraDto(KlagebehandlingDto dto, KodeverkRepository kodeverkRepository) {
        Builder builder = ny();
        if (dto.getKlageFormkravResultatNFP() != null) {
            builder.medFormkravNFP(KlageFormkravResultat.fraDto(dto.getKlageFormkravResultatNFP(), kodeverkRepository));
        }
        if (dto.getKlageFormkravResultatKA() != null) {
            builder.medFormkravKA(KlageFormkravResultat.fraDto(dto.getKlageFormkravResultatKA(), kodeverkRepository));
        }
        if (dto.getKlageVurderingResultatNFP() != null) {
            builder.medKlageVurderingResultatNFP(KlageVurderingResultat.fraDto(dto.getKlageVurderingResultatNFP()));
        }
        if (dto.getKlageVurderingResultatNK() != null) {
            builder.medKlageVurderingResultatNK(KlageVurderingResultat.fraDto(dto.getKlageVurderingResultatNK()));
        }
        return builder.build();
    }

    public static Builder ny() {
        return new Builder();
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
        if (klageVurderingResultatNFP != null) {
            return klageVurderingResultatNFP;
        } else if (klageVurderingResultatNK != null) {
            return klageVurderingResultatNK;
        }
        return null;
    }

    public static final class Builder {
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

        public Klage build() {
            return new Klage(this);
        }
    }
}
