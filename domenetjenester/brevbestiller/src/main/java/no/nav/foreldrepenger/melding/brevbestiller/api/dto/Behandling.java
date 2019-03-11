package no.nav.foreldrepenger.melding.brevbestiller.api.dto;

import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingDto;

public class Behandling {
    private long id;
    private Long saksnummer;
    private String type;
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
        this.type = dto.getType().kode;
        this.behandlendeEnhetNavn = dto.getBehandlendeEnhetNavn();
        if (dto.getBehandlingsresultat() != null) {
            this.behandlingsresultat = new Behandlingsresultat(dto.getBehandlingsresultat());
        }
        this.behandlendeEnhetNavn = dto.getBehandlendeEnhetNavn();
    }

    public String getType() {
        return type;
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

    public Boolean isToTrinnsBehandling() {
        return toTrinnsBehandling;
    }

    public Long getSaksnummer() {
        return saksnummer;
    }

    public void setSaksnummer(Long saksnummer) {
        this.saksnummer = saksnummer;
    }

    public long getId() {
        return id;
    }
}
