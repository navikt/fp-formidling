package no.nav.foreldrepenger.melding.behandling;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingResourceLinkDto;
import no.nav.foreldrepenger.melding.fagsak.Fagsak;

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
    private Fagsak fagsak;

    private Behandling(Builder builder) {
        id = builder.id;
        saksnummer = builder.saksnummer;
        behandlendeEnhetNavn = builder.behandlendeEnhetNavn;
        behandlingsresultat = builder.behandlingsresultat;
        resourceLinkDtos = builder.resourceLinkDtos;
        originalBehandlingId = builder.originalBehandlingId;
        behandlingType = builder.behandlingType;
        behandlingstidFristUker = builder.behandlingstidFristUker;
        opprettetDato = builder.opprettetDato;
        behandlingÅrsaker = builder.behandlingÅrsaker;
        ansvarligSaksbehandler = builder.ansvarligSaksbehandler;
        toTrinnsBehandling = builder.toTrinnsBehandling;
        ansvarligBeslutter = builder.ansvarligBeslutter;
        fagsak = builder.fagsak;
    }

    public static Behandling fraDto(BehandlingDto dto) {
        Behandling.Builder builder = Behandling.builder();
        builder.medId(dto.getId())
                .medOriginalBehandling(dto.getOriginalBehandlingId())
                .medAnsvarligSaksbehandler(dto.getAnsvarligSaksbehandler())
                .medToTrinnsBehandling(dto.getToTrinnsBehandling())
                .medBehandlingType(dto.getType().kode)
                .medBehandlendeEnhetNavn(dto.getBehandlendeEnhetNavn());

        dto.getLinks().forEach(builder::leggTilResourceLink);
        dto.getBehandlingArsaker().stream().map(BehandlingÅrsak::new).forEach(builder::leggTilBehandlingÅrsak);

        if (dto.getBehandlingsresultat() != null) {
            builder.medBehandlingsresultat(new Behandlingsresultat(dto.getBehandlingsresultat()));
        }

        return builder.build();
    }

    public String getBehandlendeEnhetNavn() {
        return behandlendeEnhetNavn;
    }

    public Behandlingsresultat getBehandlingsresultat() {
        return behandlingsresultat;
    }

    public String getBehandlingType() {
        return behandlingType;
    }

    public Integer getBehandlingstidFristUker() {
        return behandlingstidFristUker;
    }

    public LocalDate getOpprettetDato() {
        return opprettetDato;
    }

    public List<BehandlingÅrsak> getBehandlingÅrsaker() {
        return behandlingÅrsaker;
    }

    public String getAnsvarligSaksbehandler() {
        return ansvarligSaksbehandler;
    }

    public Boolean getToTrinnsBehandling() {
        return toTrinnsBehandling;
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

    public long getId() {
        return id;
    }

    public List<BehandlingResourceLinkDto> getResourceLinkDtos() {
        return resourceLinkDtos;
    }

    public Long getOriginalBehandlingId() {
        return originalBehandlingId;
    }

    public boolean erRevurdering() {
        return BehandlingType.REVURDERING.equals(getBehandlingType());
    }

    public Fagsak getFagsak() {
        return fagsak;
    }

    public String getFagsakYtelseType() {
        return getFagsak().getYtelseType();
    }

    public Optional<Behandling> getOriginalBehandling() {
        return getBehandlingÅrsaker().stream()
                .filter(Objects::nonNull)
                .map(BehandlingÅrsak::getOriginalBehandling)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    public String getRelasjonsRolleType() {
        return getFagsak().getRelasjonsRolleType();
    }

    public static Behandling.Builder builder() {
        return new Behandling.Builder();
    }

    public static class Builder {
        private Long saksnummer;
        private String behandlendeEnhetNavn;
        private Behandlingsresultat behandlingsresultat;
        private List<BehandlingResourceLinkDto> resourceLinkDtos = new ArrayList<>();

        private long id;
        private Long originalBehandlingId;
        private String behandlingType;
        private Integer behandlingstidFristUker;
        private LocalDate opprettetDato;
        private List<BehandlingÅrsak> behandlingÅrsaker = new ArrayList<>();
        private String ansvarligSaksbehandler;
        private Boolean toTrinnsBehandling;
        private String ansvarligBeslutter;
        private Fagsak fagsak;

        public Behandling.Builder medSaksnummer(Long saksnummer) {
            this.saksnummer = saksnummer;
            return this;
        }

        public Behandling.Builder medBehandlendeEnhetNavn(String behandlendeEnhetNavn) {
            this.behandlendeEnhetNavn = behandlendeEnhetNavn;
            return this;
        }

        public Behandling.Builder medBehandlingsresultat(Behandlingsresultat behandlingsresultat) {
            this.behandlingsresultat = behandlingsresultat;
            return this;
        }

        public Behandling.Builder leggTilResourceLink(BehandlingResourceLinkDto resourceLinkDto) {
            this.resourceLinkDtos.add(resourceLinkDto);
            return this;
        }

        public Behandling.Builder medId(long id) {
            this.id = id;
            return this;
        }

        public Behandling.Builder medBehandlingType(String behandlingType) {
            this.behandlingType = behandlingType;
            return this;
        }

        public Behandling.Builder medOriginalBehandling(Long originalBehandlingId) {
            this.originalBehandlingId = originalBehandlingId;
            return this;
        }

        public Behandling.Builder medBehandlingstidFristUker(Integer behandlingstidFristUker) {
            this.behandlingstidFristUker = behandlingstidFristUker;
            return this;
        }

        public Behandling.Builder medOpprettetDato(LocalDate opprettetDato) {
            this.opprettetDato = opprettetDato;
            return this;
        }

        public Behandling.Builder leggTilBehandlingÅrsak(BehandlingÅrsak behandlingÅrsak) {
            this.behandlingÅrsaker.add(behandlingÅrsak);
            return this;
        }

        public Behandling.Builder medAnsvarligSaksbehandler(String ansvarligSaksbehandler) {
            this.ansvarligSaksbehandler = ansvarligSaksbehandler;
            return this;
        }

        public Behandling.Builder medToTrinnsBehandling(Boolean toTrinnsBehandling) {
            this.toTrinnsBehandling = toTrinnsBehandling;
            return this;
        }

        public Behandling.Builder medAnsvarligBeslutter(String ansvarligBeslutter) {
            this.ansvarligBeslutter = ansvarligBeslutter;
            return this;
        }

        public Behandling.Builder medFagsak(Fagsak fagsak) {
            this.fagsak = fagsak;
            return this;
        }

        public Behandling build() {
            return new Behandling(this);
        }

    }


}
