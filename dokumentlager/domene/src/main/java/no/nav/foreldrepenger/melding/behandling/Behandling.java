package no.nav.foreldrepenger.melding.behandling;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.melding.fagsak.Fagsak;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;

public class Behandling {
    private Behandlingsresultat behandlingsresultat;
    private List<BehandlingResourceLink> resourceLinker;

    //Felter brukt i brev
    private long id;
    private UUID uuid;
    private BehandlingType behandlingType;
    private Integer behandlingstidFristUker;
    private LocalDateTime opprettetDato;
    private LocalDateTime avsluttet;
    private List<BehandlingÅrsak> behandlingÅrsaker;
    private String ansvarligSaksbehandler;
    private boolean toTrinnsBehandling;
    private String behandlendeEnhetNavn;
    private String ansvarligBeslutter;
    private Fagsak fagsak;
    private BehandlingStatus status;
    private String endretAv;
    private Språkkode språkkode;
    private LocalDate originalVedtaksDato;

    private Behandling(Builder builder) {
        id = builder.id;
        uuid = builder.uuid;
        behandlendeEnhetNavn = builder.behandlendeEnhetNavn;
        behandlingsresultat = builder.behandlingsresultat;
        resourceLinker = builder.resourceLinker;
        behandlingType = builder.behandlingType;
        behandlingstidFristUker = builder.behandlingstidFristUker;
        opprettetDato = builder.opprettetDato;
        avsluttet = builder.avsluttet;
        behandlingÅrsaker = builder.behandlingÅrsaker;
        ansvarligSaksbehandler = builder.ansvarligSaksbehandler;
        toTrinnsBehandling = builder.toTrinnsBehandling;
        ansvarligBeslutter = builder.ansvarligBeslutter;
        fagsak = builder.fagsak;
        status = builder.status;
        endretAv = builder.endretAv;
        språkkode = builder.språkkode;
        originalVedtaksDato = builder.originalVedtaksDato;
    }

    public LocalDateTime getAvsluttet() {
        return avsluttet;
    }

    public String getBehandlendeEnhetNavn() {
        return behandlendeEnhetNavn;
    }

    public Behandlingsresultat getBehandlingsresultat() {
        return behandlingsresultat;
    }

    public BehandlingType getBehandlingType() {
        return behandlingType;
    }

    public Integer getBehandlingstidFristUker() {
        return behandlingstidFristUker;
    }

    public LocalDateTime getOpprettetDato() {
        return opprettetDato;
    }

    public List<BehandlingÅrsak> getBehandlingÅrsaker() {
        return behandlingÅrsaker;
    }

    public String getAnsvarligSaksbehandler() {
        return ansvarligSaksbehandler;
    }

    public String getAnsvarligBeslutter() {
        return ansvarligBeslutter;
    }

    public boolean isToTrinnsBehandling() {
        return toTrinnsBehandling;
    }

    public UUID getUuid() {
        return uuid;
    }

    public long getId() {
        return id;
    }

    public List<BehandlingResourceLink> getResourceLinker() {
        return resourceLinker;
    }

    public Språkkode getSpråkkode() {
        return språkkode;
    }

    public LocalDate getOriginalVedtaksDato() {
        return originalVedtaksDato;
    }



    public boolean erFørstegangssøknad() {
        return BehandlingType.FØRSTEGANGSSØKNAD.equals(getBehandlingType());
    }

    public BehandlingStatus getStatus() {
        return status;
    }

    public boolean erRevurdering() {
        return BehandlingType.REVURDERING.equals(getBehandlingType());
    }

    public boolean erKlage() {
        return BehandlingType.KLAGE.equals(getBehandlingType());
    }

    public Fagsak getFagsak() {
        return fagsak;
    }

    public boolean erSaksbehandlingAvsluttet() {
        if (behandlingsresultat == null) {
            return false;
        }
        return erAvsluttet() || erUnderIverksettelse() || erHenlagt();
    }

    private boolean erHenlagt() {
        return getBehandlingsresultat().isBehandlingHenlagt();
    }

    public boolean erUnderIverksettelse() {
        return Objects.equals(BehandlingStatus.IVERKSETTER_VEDTAK, getStatus());
    }

    public boolean erAvsluttet() {
        return Objects.equals(BehandlingStatus.AVSLUTTET, getStatus());
    }

    public String getEndretAv() {
        return endretAv;
    }

    public void leggtilFagsak(Fagsak fagsak) {
        if (this.fagsak == null) {
            this.fagsak = fagsak;
        }
    }

    public boolean harFagsak() {
        return fagsak != null;
    }

    public boolean erManueltOpprettet() {
        return getBehandlingÅrsaker().stream()
                .map(BehandlingÅrsak::getManueltOpprettet)
                .collect(Collectors.toList())
                .contains(true);
    }

    public static Behandling.Builder builder() {
        return new Behandling.Builder();
    }

    public static Behandling.Builder builder(Behandling copy) {
        return new Behandling.Builder(copy);
    }

    public static class Builder {
        private String behandlendeEnhetNavn;
        private Behandlingsresultat behandlingsresultat;
        private List<BehandlingResourceLink> resourceLinker = new ArrayList<>();

        private long id;
        private UUID uuid;
        private Behandling originalBehandling;
        private BehandlingType behandlingType;
        private Integer behandlingstidFristUker;
        private LocalDateTime opprettetDato;
        private LocalDateTime avsluttet;
        private List<BehandlingÅrsak> behandlingÅrsaker = new ArrayList<>();
        private String ansvarligSaksbehandler;
        private boolean toTrinnsBehandling;
        private String ansvarligBeslutter;
        private Fagsak fagsak;
        private BehandlingStatus status;
        private String endretAv;
        private Språkkode språkkode;
        private LocalDate originalVedtaksDato;

        public Builder() {
        }

        public Builder(Behandling copy) {
            this.behandlingsresultat = copy.getBehandlingsresultat();
            this.resourceLinker = copy.getResourceLinker();
            this.id = copy.getId();
            this.uuid = copy.getUuid();
            this.behandlingType = copy.getBehandlingType();
            this.behandlingstidFristUker = copy.getBehandlingstidFristUker();
            this.opprettetDato = copy.getOpprettetDato();
            this.avsluttet = copy.getAvsluttet();
            this.behandlingÅrsaker = copy.getBehandlingÅrsaker();
            this.ansvarligSaksbehandler = copy.getAnsvarligSaksbehandler();
            this.toTrinnsBehandling = copy.isToTrinnsBehandling();
            this.behandlendeEnhetNavn = copy.getBehandlendeEnhetNavn();
            this.ansvarligBeslutter = copy.getAnsvarligBeslutter();
            this.fagsak = copy.getFagsak();
            this.status = copy.getStatus();
            this.endretAv = copy.getEndretAv();
            this.språkkode = copy.getSpråkkode();
            this.originalVedtaksDato = copy.getOriginalVedtaksDato();
        }

        public Behandling.Builder medBehandlendeEnhetNavn(String behandlendeEnhetNavn) {
            this.behandlendeEnhetNavn = behandlendeEnhetNavn;
            return this;
        }

        public Behandling.Builder medBehandlingsresultat(Behandlingsresultat behandlingsresultat) {
            this.behandlingsresultat = behandlingsresultat;
            return this;
        }

        public Behandling.Builder leggTilResourceLink(BehandlingResourceLink resourceLink) {
            this.resourceLinker.add(resourceLink);
            return this;
        }

        public Behandling.Builder medId(long id) {
            this.id = id;
            return this;
        }

        public Behandling.Builder medUuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Behandling.Builder medBehandlingType(BehandlingType behandlingType) {
            this.behandlingType = behandlingType;
            return this;
        }

        public Behandling.Builder medBehandlingstidFristUker(Integer behandlingstidFristUker) {
            this.behandlingstidFristUker = behandlingstidFristUker;
            return this;
        }

        public Behandling.Builder medOpprettetDato(LocalDateTime opprettetDato) {
            this.opprettetDato = opprettetDato;
            return this;
        }

        public Behandling.Builder medAvsluttet(LocalDateTime avsluttet) {
            this.avsluttet = avsluttet;
            return this;
        }

        public Behandling.Builder medBehandlingÅrsaker(List<BehandlingÅrsak> behandlingÅrsaker) {
            this.behandlingÅrsaker.addAll(behandlingÅrsaker);
            return this;
        }

        public Behandling.Builder medAnsvarligSaksbehandler(String ansvarligSaksbehandler) {
            this.ansvarligSaksbehandler = ansvarligSaksbehandler;
            return this;
        }

        public Behandling.Builder medToTrinnsBehandling(boolean toTrinnsBehandling) {
            this.toTrinnsBehandling = toTrinnsBehandling;
            return this;
        }

        public Behandling.Builder medAnsvarligBeslutter(String ansvarligBeslutter) {
            this.ansvarligBeslutter = ansvarligBeslutter;
            return this;
        }

        public Behandling.Builder medStatus(BehandlingStatus status) {
            this.status = status;
            return this;
        }

        public Behandling.Builder medFagsak(Fagsak fagsak) {
            this.fagsak = fagsak;
            return this;
        }

        public Behandling.Builder medEndretAv(String endretAv) {
            this.endretAv = endretAv;
            return this;
        }

        public Behandling.Builder medSpråkkode(Språkkode språkkode) {
            this.språkkode = språkkode;
            return this;
        }

        public Behandling.Builder medOriginalVedtaksDato(LocalDate originalVedtaksDato) {
            this.originalVedtaksDato = originalVedtaksDato;
            return this;
        }



        public Behandling build() {
            return new Behandling(this);
        }
    }
}
