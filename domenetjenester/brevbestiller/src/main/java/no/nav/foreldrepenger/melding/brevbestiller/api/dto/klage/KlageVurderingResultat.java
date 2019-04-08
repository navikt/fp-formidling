package no.nav.foreldrepenger.melding.brevbestiller.api.dto.klage;

import no.nav.foreldrepenger.fpsak.dto.klage.KlageVurderingResultatDto;

public class KlageVurderingResultat {

    private String KlageVurdering;

    private KlageVurderingResultat(Builder builder) {
        KlageVurdering = builder.KlageVurdering;
    }

    public static KlageVurderingResultat fraDto(KlageVurderingResultatDto dto) {
        Builder builder = ny();
        builder.medKlageVurdering(dto.getKlageVurdering());
        return builder.build();
    }

    public static Builder ny() {
        return new Builder();
    }

    public String getKlageVurdering() {
        return KlageVurdering;
    }


    public static final class Builder {
        private String KlageVurdering;

        private Builder() {
        }

        public Builder medKlageVurdering(String val) {
            KlageVurdering = val;
            return this;
        }

        public KlageVurderingResultat build() {
            return new KlageVurderingResultat(this);
        }
    }
}
