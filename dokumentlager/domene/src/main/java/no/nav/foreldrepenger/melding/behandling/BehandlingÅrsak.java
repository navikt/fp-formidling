package no.nav.foreldrepenger.melding.behandling;

import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingÅrsakDto;

public class BehandlingÅrsak {
    private String behandlingArsakType;
    private Boolean manueltOpprettet;

    public BehandlingÅrsak(BehandlingÅrsakDto dto) {
        this.behandlingArsakType = dto.getBehandlingArsakType().kode;
        this.manueltOpprettet = dto.getManueltOpprettet();
    }
}
