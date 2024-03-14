package no.nav.foreldrepenger.fpformidling.domene.behandling;

import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingÅrsakType;

public class BehandlingÅrsak {
    private BehandlingÅrsakType behandlingÅrsakType;

    private BehandlingÅrsak(Builder builder) {
        behandlingÅrsakType = builder.behandlingÅrsakType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public BehandlingÅrsakType getBehandlingÅrsakType() {
        return behandlingÅrsakType;
    }


    public static class Builder {
        private BehandlingÅrsakType behandlingÅrsakType;

        private Builder() {
        }

        public Builder medBehandlingÅrsakType(BehandlingÅrsakType behandlingÅrsakType) {
            this.behandlingÅrsakType = behandlingÅrsakType;
            return this;
        }

        public BehandlingÅrsak build() {
            return new BehandlingÅrsak(this);
        }
    }
}
