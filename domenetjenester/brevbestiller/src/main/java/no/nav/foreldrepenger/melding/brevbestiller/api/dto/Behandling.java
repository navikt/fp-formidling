package no.nav.foreldrepenger.melding.brevbestiller.api.dto;

import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingDto;

public class Behandling {
    private long id;
    private String ansvarligSaksbehandler;
    private String ansvarligBeslutter;
    private String behandlendeEnhetNavn;
    private Boolean toTrinnsBehandling;
    private Behandlingsresultat behandlingsresultat;

    public Behandling(BehandlingDto dto) {
        this.ansvarligSaksbehandler = dto.getAnsvarligSaksbehandler();
//        this.ansvarligBeslutter = ansvarligBeslutter;
        this.toTrinnsBehandling = dto.getToTrinnsBehandling();
        this.id = dto.getId();
        this.behandlendeEnhetNavn = dto.getBehandlendeEnhetNavn();
        if (dto.getBehandlingsresultat() != null) {
            this.behandlingsresultat = new Behandlingsresultat(dto.getBehandlingsresultat());
        }
    }

    public String getBehandlendeEnhetNavn() {
        return behandlendeEnhetNavn;
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

    public long getId() {
        return id;
    }
}
