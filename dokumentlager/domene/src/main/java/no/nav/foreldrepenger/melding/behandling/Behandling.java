package no.nav.foreldrepenger.melding.behandling;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingResourceLinkDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingÅrsakDto;

public class Behandling {
    private Long saksnummer;
    private String behandlendeEnhetNavn;
    private Behandlingsresultat behandlingsresultat;
    private List<BehandlingResourceLinkDto> resourceLinkDtos;

    //Felter brukt i brev
    private long id;
    private Long originalBehandlingId;
    private String behandlingType;
    private Integer behandlingstidFristUker;
    private LocalDate opprettetDato;
    private List<BehandlingÅrsak> behandlingÅrsaker = new ArrayList<>();
    private String ansvarligSaksbehandler;
    private Boolean toTrinnsBehandling;
    private String ansvarligBeslutter;

    public Behandling(BehandlingDto dto) {
        this.id = dto.getId();
        this.originalBehandlingId = dto.getOriginalBehandlingId();
        this.ansvarligSaksbehandler = dto.getAnsvarligSaksbehandler();
//        this.ansvarligBeslutter = ansvarligBeslutter;
        this.toTrinnsBehandling = dto.getToTrinnsBehandling();
        this.behandlingType = dto.getType().kode;
        this.behandlendeEnhetNavn = dto.getBehandlendeEnhetNavn();
        if (dto.getBehandlingsresultat() != null) {
            this.behandlingsresultat = new Behandlingsresultat(dto.getBehandlingsresultat());
        }
        this.behandlendeEnhetNavn = dto.getBehandlendeEnhetNavn();
        this.resourceLinkDtos = dto.getLinks();

        for (BehandlingÅrsakDto årsakDto : dto.getBehandlingArsaker()) {
            behandlingÅrsaker.add(new BehandlingÅrsak(årsakDto));
        }
    }

    public List<BehandlingÅrsak> getBehandlingÅrsaker() {
        return behandlingÅrsaker;
    }

    public String getBehandlingType() {
        return behandlingType;
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

    public List<BehandlingResourceLinkDto> getResourceLinkDtos() {
        return resourceLinkDtos;
    }

    public Long getOriginalBehandlingId() {
        return originalBehandlingId;
    }

    public void setOriginalBehandlingId(Long originalBehandlingId) {
        this.originalBehandlingId = originalBehandlingId;
    }
}
