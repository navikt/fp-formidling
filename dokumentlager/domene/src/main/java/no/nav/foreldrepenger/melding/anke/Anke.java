package no.nav.foreldrepenger.melding.anke;

import java.util.UUID;

public class Anke {

    // Er skrelt ned til minimum. Se fpsak for fullt innholt. Ta inn mer ved behov.
    private AnkeVurdering ankeVurdering;
    private String fritekstTilBrev;
    private AnkeVurderingOmgjør ankeVurderingOmgjoer;
    private UUID påAnketKlageBehandlingUuid;

    public static Builder ny() {
        return new Builder();
    }

    public AnkeVurdering getAnkeVurdering() {
        return ankeVurdering;
    }

    public String getFritekstTilBrev() {
        return fritekstTilBrev;
    }

    public AnkeVurderingOmgjør getAnkeVurderingOmgjoer() {
        return ankeVurderingOmgjoer;
    }

    public UUID getPåAnketKlageBehandlingUuid() {
        return påAnketKlageBehandlingUuid;
    }

    public static final class Builder {
        private Anke kladd;

        public Builder() {
            kladd = new Anke();
        }

        public Builder medAnkeVurdering(AnkeVurdering ankeVurdering) {
            kladd.ankeVurdering = ankeVurdering;
            return this;
        }

        public Builder medFritekstTilBrev(String val) {
            kladd.fritekstTilBrev = val;
            return this;
        }

        public Builder medAnkeVurderingOmgjoer(AnkeVurderingOmgjør val) {
            kladd.ankeVurderingOmgjoer = val;
            return this;
        }

        public Builder medPaAnketBehandlingUuid(UUID påAnketKlageBehandlingUuid) {
            kladd.påAnketKlageBehandlingUuid = påAnketKlageBehandlingUuid;
            return this;
        }

        public Anke build() {
            return kladd;
        }
    }
}
