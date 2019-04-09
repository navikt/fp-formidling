package no.nav.foreldrepenger.melding.klage;

import java.util.ArrayList;
import java.util.List;

public class KlageFormkravResultat {
    private String begrunnelse;
    private List<KlageAvvistÅrsak> avvistÅrsaker = new ArrayList<>();

    private KlageFormkravResultat() {
    }

    private KlageFormkravResultat(Builder builder) {
        begrunnelse = builder.begrunnelse;
        avvistÅrsaker = builder.avvistÅrsaker;
    }

    public static Builder ny() {
        return new Builder();
    }

    public String getBegrunnelse() {
        return begrunnelse;
    }

    public List<KlageAvvistÅrsak> getAvvistÅrsaker() {
        return avvistÅrsaker;
    }

    public static final class Builder {
        private String begrunnelse;
        private List<KlageAvvistÅrsak> avvistÅrsaker;

        private Builder() {
        }

        public Builder medBegrunnelse(String val) {
            begrunnelse = val;
            return this;
        }

        public Builder medAvvistÅrsaker(List<KlageAvvistÅrsak> val) {
            avvistÅrsaker = val;
            return this;
        }

        public KlageFormkravResultat build() {
            return new KlageFormkravResultat(this);
        }
    }
}
