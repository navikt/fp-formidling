package no.nav.foreldrepenger.melding.behandling;

import java.util.Optional;

import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingÅrsakDto;

public class BehandlingÅrsak {
    private String behandlingÅrsakType;
    private Boolean manueltOpprettet;
    private Behandling originalBehandling;

    public BehandlingÅrsak(BehandlingÅrsakDto dto) {
        this.behandlingÅrsakType = dto.getBehandlingArsakType().kode;
        this.manueltOpprettet = dto.getManueltOpprettet();
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
}
