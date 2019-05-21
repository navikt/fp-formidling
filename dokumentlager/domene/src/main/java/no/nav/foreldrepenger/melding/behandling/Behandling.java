package no.nav.foreldrepenger.melding.behandling;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.melding.fagsak.Fagsak;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.melding.typer.Saksnummer;

public class Behandling {
    private Behandlingsresultat behandlingsresultat;
    private List<BehandlingResourceLink> resourceLinker;

    //Felter brukt i brev
    private long id;
    private UUID uuid;
    private Behandling originalBehandling;
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

    private Behandling(Builder builder) {
        id = builder.id;
        uuid = builder.uuid;
        behandlendeEnhetNavn = builder.behandlendeEnhetNavn;
        behandlingsresultat = builder.behandlingsresultat;
        resourceLinker = builder.resourceLinker;
        originalBehandling = builder.originalBehandling;
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

    //TODO - Hack
    public UUID getUuid() {
        return uuid != null ? uuid : resourceLinker.stream()
                .map(BehandlingResourceLink::getRequestPayload)
                .filter(Objects::nonNull)
                .map(BehandlingRelLinkPayload::getBehandlingUuid)
                .filter(Objects::nonNull)
                .findFirst()
                .map(UUID::fromString)
                .orElse(null);
    }

    public long getId() {
        return id;
    }

    public List<BehandlingResourceLink> getResourceLinker() {
        return resourceLinker;
    }

    public Behandling getOriginalBehandling() {
        return originalBehandling;
    }

    public Språkkode getSpråkkode() {
        return språkkode;
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

    public Saksnummer getSaksnummer() {
        return getFagsak().getSaksnummer();
    }

    public RelasjonsRolleType getRelasjonsRolleType() {
        return getFagsak().getRelasjonsRolleType();
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

    public boolean erManueltOpprettet() {
        return getBehandlingÅrsaker().stream()
                .map(BehandlingÅrsak::getManueltOpprettet)
                .collect(Collectors.toList())
                .contains(true);
    }

    public static Behandling.Builder builder() {
        return new Behandling.Builder();
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

        public Behandling.Builder medOriginalBehandling(Behandling originalBehandling) {
            this.originalBehandling = originalBehandling;
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

        public Behandling build() {
            return new Behandling(this);
        }
    }
}
