package no.nav.foreldrepenger.melding.brevbestiller.api.dto;

import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingsresultatDto;

public class Behandlingsresultat {

    private String behandligResultatType;

    public Behandlingsresultat(BehandlingsresultatDto behandlingsresultatDto) {
        this.behandligResultatType = behandlingsresultatDto.getType().kode;
    }

    public String getBehandligResultatType() {
        return behandligResultatType;
    }
}
