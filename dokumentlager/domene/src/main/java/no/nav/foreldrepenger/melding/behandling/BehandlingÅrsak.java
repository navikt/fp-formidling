package no.nav.foreldrepenger.melding.behandling;

import java.util.Optional;

public class BehandlingÅrsak {
    private String behandlingÅrsakType;
    private Boolean manueltOpprettet;
    private Behandling originalBehandling;

    private BehandlingÅrsak(Builder builder) {
        behandlingÅrsakType = builder.behandlingÅrsakType;
        manueltOpprettet = builder.manueltOpprettet;
        originalBehandling = builder.originalBehandling;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getBehandlingÅrsakType() {
        return behandlingÅrsakType;
    }

    public Boolean getManueltOpprettet() {
        return manueltOpprettet;
    }

    public Optional<Behandling> getOriginalBehandling() {
        return Optional.ofNullable(originalBehandling);
    }

    public static class Builder {
        private String behandlingÅrsakType;
        private Boolean manueltOpprettet;
        private Behandling originalBehandling;

        private Builder() {
        }

        public Builder medBehandlingÅrsakType(String behandlingÅrsakType) {
            this.behandlingÅrsakType = behandlingÅrsakType;
            return this;
        }

        public Builder medManueltOpprettet(Boolean manueltOpprettet) {
            this.manueltOpprettet = manueltOpprettet;
            return this;
        }

        public Builder medOriginalBehandling(Behandling originalBehandling) {
            this.originalBehandling = originalBehandling;
            return this;
        }

        public BehandlingÅrsak build() {
            return new BehandlingÅrsak(this);
        }
    }
}
