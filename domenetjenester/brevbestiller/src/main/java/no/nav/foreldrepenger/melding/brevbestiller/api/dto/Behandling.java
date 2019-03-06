package no.nav.foreldrepenger.melding.brevbestiller.api.dto;

import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingDto;

public class Behandling {
    private String ansvarligSaksbehandler;
    private String ansvarligBeslutter;
    private Boolean toTrinnsBehandling;
    private Behandlingsresultat behandlingsresultat;

    public Behandling(BehandlingDto dto) {
        this.ansvarligSaksbehandler = dto.getAnsvarligSaksbehandler();
//        this.ansvarligBeslutter = ansvarligBeslutter;
        this.toTrinnsBehandling = dto.getToTrinnsBehandling();
        this.behandlingsresultat = new Behandlingsresultat(dto.getBehandlingsresultat());
    }

    public Behandlingsresultat getBehandlingsresultat() {
        return behandlingsresultat;
    }

    public String getAnsvarligSaksbehandler() {
        return ansvarligSaksbehandler;
    }

    public String getAnsvarligBeslutter() {
        return ansvarligBeslutter;
    }

    public Boolean getToTrinnsBehandling() {
        return toTrinnsBehandling;
    }
}
