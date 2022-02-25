package no.nav.foreldrepenger.fpsak.dto.behandling;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.fpformidling.behandling.BehandlingStatus;
import no.nav.foreldrepenger.fpformidling.behandling.BehandlingType;
import no.nav.foreldrepenger.fpformidling.geografisk.Språkkode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BehandlingDto {

    private UUID uuid;
    private BehandlingType type;
    private BehandlingStatus status;
    private LocalDateTime opprettet;
    private LocalDateTime avsluttet;
    private LocalDateTime endret;
    private String endretAvBrukernavn;
    private String behandlendeEnhetNavn;
    private String ansvarligSaksbehandler;
    private String ansvarligBeslutter;
    private BehandlingsresultatDto behandlingsresultat;
    private Språkkode sprakkode;
    private boolean toTrinnsBehandling;
    private List<BehandlingResourceLinkDto> links = new ArrayList<>();
    private List<BehandlingResourceLinkDto> formidlingRessurser = new ArrayList<>();
    private List<BehandlingÅrsakDto> behandlingÅrsaker = new ArrayList<>();
    private LocalDate originalVedtaksDato;


    public BehandlingType getType() {
        return type;
    }

    public void setType(BehandlingType type) {
        this.type = type;
    }

    public BehandlingStatus getStatus() {
        return status;
    }

    public void setStatus(BehandlingStatus status) {
        this.status = status;
    }

    public String getBehandlendeEnhetNavn() {
        return behandlendeEnhetNavn;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setBehandlendeEnhetNavn(String behandlendeEnhetNavn) {
        this.behandlendeEnhetNavn = behandlendeEnhetNavn;
    }

    public String getAnsvarligSaksbehandler() {
        return ansvarligSaksbehandler;
    }

    public void setAnsvarligSaksbehandler(String ansvarligSaksbehandler) {
        this.ansvarligSaksbehandler = ansvarligSaksbehandler;
    }

    public String getAnsvarligBeslutter() {
        return ansvarligBeslutter;
    }

    public void setAnsvarligBeslutter(String ansvarligBeslutter) {
        this.ansvarligBeslutter = ansvarligBeslutter;
    }

    public LocalDateTime getAvsluttet() {
        return avsluttet;
    }

    public void setAvsluttet(LocalDateTime avsluttet) {
        this.avsluttet = avsluttet;
    }

    public BehandlingsresultatDto getBehandlingsresultat() {
        return behandlingsresultat;
    }

    public void setBehandlingsresultat(BehandlingsresultatDto behandlingsresultat) {
        this.behandlingsresultat = behandlingsresultat;
    }

    public Språkkode getSprakkode() {
        return sprakkode;
    }

    public void setSprakkode(Språkkode sprakkode) {
        this.sprakkode = sprakkode;
    }

    public boolean getToTrinnsBehandling() {
        return toTrinnsBehandling;
    }

    public void setToTrinnsBehandling(Boolean toTrinnsBehandling) {
        this.toTrinnsBehandling = toTrinnsBehandling;
    }

    public List<BehandlingResourceLinkDto> getLinks() {
        return links;
    }

    public void setLinks(List<BehandlingResourceLinkDto> links) {
        this.links = links;
    }

    public LocalDateTime getOpprettet() {
        return opprettet;
    }

    public void setOpprettet(LocalDateTime opprettet) {
        this.opprettet = opprettet;
    }

    public LocalDateTime getEndret() {
        return endret;
    }

    public void setEndret(LocalDateTime endret) {
        this.endret = endret;
    }

    public List<BehandlingÅrsakDto> getBehandlingÅrsaker() {
        return behandlingÅrsaker;
    }

    public void setBehandlingÅrsaker(List<BehandlingÅrsakDto> behandlingÅrsaker) {
        this.behandlingÅrsaker = behandlingÅrsaker;
    }

    public String getEndretAvBrukernavn() {
        return endretAvBrukernavn;
    }

    public void setEndretAvBrukernavn(String endretAvBrukernavn) {
        this.endretAvBrukernavn = endretAvBrukernavn;
    }

    public LocalDate getOriginalVedtaksDato() {
        return originalVedtaksDato;
    }

    public void setOriginalVedtaksDato(LocalDate originalVedtaksDato) {
        this.originalVedtaksDato = originalVedtaksDato;
    }

    public List<BehandlingResourceLinkDto> getFormidlingRessurser() {
        return formidlingRessurser;
    }

    public void setFormidlingRessurser(List<BehandlingResourceLinkDto> formidlingRessurser) {
        this.formidlingRessurser = formidlingRessurser;
    }

    @Override
    public String toString() {
        return "BehandlingDto{" +
                "id=" + uuid +
                ", type=" + type +
                ", status=" + status +
                ", opprettet=" + opprettet +
                ", avsluttet=" + avsluttet +
                ", endret=" + endret +
                ", behandlendeEnhetNavn='" + behandlendeEnhetNavn + '\'' +
                ", ansvarligSaksbehandler='" + ansvarligSaksbehandler + '\'' +
                ", ansvarligBeslutter='" + ansvarligBeslutter + '\'' +
                ", behandlingsresultat=" + behandlingsresultat +
                ", sprakkode=" + sprakkode +
                ", toTrinnsBehandling=" + toTrinnsBehandling +
                ", links=" + links +
                ", behandlingÅrsaker=" + behandlingÅrsaker + '\'' +
                ", originalVedtaksDato=" + originalVedtaksDato +
                '}';
    }
}
