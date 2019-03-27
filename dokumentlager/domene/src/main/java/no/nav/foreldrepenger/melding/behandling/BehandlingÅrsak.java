package no.nav.foreldrepenger.melding.behandling;

import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingÅrsakDto;

public class BehandlingÅrsak {
    private String BehandlingÅrsakType;
    private Boolean manueltOpprettet;

    public BehandlingÅrsak(BehandlingÅrsakDto dto) {
        this.BehandlingÅrsakType = dto.getBehandlingArsakType().kode;
        this.manueltOpprettet = dto.getManueltOpprettet();
    }

    public String getBehandlingÅrsakType() {
        return BehandlingÅrsakType;
    }

    public Boolean getManueltOpprettet() {
        return manueltOpprettet;
    }
}
