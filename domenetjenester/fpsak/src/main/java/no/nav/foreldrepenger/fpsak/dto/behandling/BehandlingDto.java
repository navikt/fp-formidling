package no.nav.foreldrepenger.fpsak.dto.behandling;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BehandlingDto {

    private UUID uuid;
    private Integer versjon;
    private KodeDto type;
    private KodeDto status;
    private Long fagsakId;
    private LocalDateTime opprettet;
    private LocalDateTime avsluttet;
    private LocalDateTime endret;
    private String endretAvBrukernavn;
    private String behandlendeEnhetId;
    private String behandlendeEnhetNavn;
    private Boolean erAktivPapirsoknad;

    private Boolean behandlingPaaVent;
    private Boolean behandlingKoet;
    private String ansvarligSaksbehandler;
    private String ansvarligBeslutter;
    private LocalDate fristBehandlingPaaVent;
    private Boolean behandlingHenlagt;
    private BehandlingsresultatDto behandlingsresultat;
    private KodeDto sprakkode;
    private boolean toTrinnsBehandling;
    private List<BehandlingResourceLinkDto> links = new ArrayList<>();
    private List<BehandlingResourceLinkDto> formidlingRessurser = new ArrayList<>();
    private AsyncPollingStatus taskStatus;
    private String venteArsakKode;
    private List<BehandlingÅrsakDto> behandlingÅrsaker = new ArrayList<>();
    private LocalDate originalVedtaksDato;

    public Integer getVersjon() {
        return versjon;
    }

    public void setVersjon(Integer versjon) {
        this.versjon = versjon;
    }

    public Long getFagsakId() {
        return fagsakId;
    }

    public void setFagsakId(Long fagsakId) {
        this.fagsakId = fagsakId;
    }

    public KodeDto getType() {
        return type;
    }

    public void setType(KodeDto type) {
        this.type = type;
    }

    public KodeDto getStatus() {
        return status;
    }

    public void setStatus(KodeDto status) {
        this.status = status;
    }

    public String getBehandlendeEnhetId() {
        return behandlendeEnhetId;
    }

    public void setBehandlendeEnhetId(String behandlendeEnhetId) {
        this.behandlendeEnhetId = behandlendeEnhetId;
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

    public Boolean getBehandlingPaaVent() {
        return behandlingPaaVent;
    }

    public void setBehandlingPaaVent(Boolean behandlingPaaVent) {
        this.behandlingPaaVent = behandlingPaaVent;
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

    public LocalDate getFristBehandlingPaaVent() {
        return fristBehandlingPaaVent;
    }

    public void setFristBehandlingPaaVent(LocalDate fristBehandlingPaaVent) {
        this.fristBehandlingPaaVent = fristBehandlingPaaVent;
    }

    public LocalDateTime getAvsluttet() {
        return avsluttet;
    }

    public void setAvsluttet(LocalDateTime avsluttet) {
        this.avsluttet = avsluttet;
    }

    public Boolean getBehandlingHenlagt() {
        return behandlingHenlagt;
    }

    public void setBehandlingHenlagt(Boolean behandlingHenlagt) {
        this.behandlingHenlagt = behandlingHenlagt;
    }

    public BehandlingsresultatDto getBehandlingsresultat() {
        return behandlingsresultat;
    }

    public void setBehandlingsresultat(BehandlingsresultatDto behandlingsresultat) {
        this.behandlingsresultat = behandlingsresultat;
    }

    public KodeDto getSprakkode() {
        return sprakkode;
    }

    public void setSprakkode(KodeDto sprakkode) {
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

    public AsyncPollingStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(AsyncPollingStatus taskStatus) {
        this.taskStatus = taskStatus;
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

    public boolean isErAktivPapirsoknad() {
        return erAktivPapirsoknad;
    }

    public void setErAktivPapirsoknad(boolean erAktivPapirsoknad) {
        this.erAktivPapirsoknad = erAktivPapirsoknad;
    }

    public Boolean getBehandlingKoet() {
        return behandlingKoet;
    }

    public void setBehandlingKoet(Boolean behandlingKoet) {
        this.behandlingKoet = behandlingKoet;
    }

    public String getVenteArsakKode() {
        return venteArsakKode;
    }

    public void setVenteArsakKode(String venteArsakKode) {
        this.venteArsakKode = venteArsakKode;
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
                ", versjon=" + versjon +
                ", type=" + type +
                ", status=" + status +
                ", fagsakId=" + fagsakId +
                ", opprettet=" + opprettet +
                ", avsluttet=" + avsluttet +
                ", endret=" + endret +
                ", behandlendeEnhetId='" + behandlendeEnhetId + '\'' +
                ", behandlendeEnhetNavn='" + behandlendeEnhetNavn + '\'' +
                ", erAktivPapirsoknad=" + erAktivPapirsoknad +
                ", behandlingPaaVent=" + behandlingPaaVent +
                ", behandlingKoet=" + behandlingKoet +
                ", ansvarligSaksbehandler='" + ansvarligSaksbehandler + '\'' +
                ", ansvarligBeslutter='" + ansvarligBeslutter + '\'' +
                ", fristBehandlingPaaVent=" + fristBehandlingPaaVent +
                ", behandlingHenlagt=" + behandlingHenlagt +
                ", behandlingsresultat=" + behandlingsresultat +
                ", sprakkode=" + sprakkode +
                ", toTrinnsBehandling=" + toTrinnsBehandling +
                ", links=" + links +
                ", taskStatus='" + taskStatus + '\'' +
                ", venteArsakKode='" + venteArsakKode + '\'' +
                ", behandlingÅrsaker=" + behandlingÅrsaker + '\'' +
                ", originalVedtaksDato=" + originalVedtaksDato +
                '}';
    }
}
