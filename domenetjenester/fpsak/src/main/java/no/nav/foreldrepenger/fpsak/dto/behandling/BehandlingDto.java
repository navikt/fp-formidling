package no.nav.foreldrepenger.fpsak.dto.behandling;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.Kode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BehandlingDto {
    private Long id;
    private Integer versjon;
    private Kode type;
    private Kode status;
    private Long fagsakId;
    private LocalDateTime opprettet;
    private LocalDateTime avsluttet;
    private LocalDateTime endret;
    private String behandlendeEnhetId;
    private String behandlendeEnhetNavn;
    private Boolean erAktivPapirsoknad;

    private Boolean behandlingPaaVent;
    private Boolean behandlingKoet;
    private String ansvarligSaksbehandler;
    private LocalDate fristBehandlingPaaVent;
    private Boolean behandlingHenlagt;
    private BehandlingsresultatDto behandlingsresultat;
    private Kode sprakkode;
    private Boolean toTrinnsBehandling;
    private Long originalBehandlingId;
    private List<BehandlingResourceLinkDto> links;
    private String taskStatus;
    private String venteArsakKode;
    private List<BehandlingÅrsakDto> behandlingArsaker;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Long getOriginalBehandlingId() {
        return originalBehandlingId;
    }

    public void setOriginalBehandlingId(Long originalBehandlingId) {
        this.originalBehandlingId = originalBehandlingId;
    }

    public Kode getType() {
        return type;
    }

    public void setType(Kode type) {
        this.type = type;
    }

    public Kode getStatus() {
        return status;
    }

    public void setStatus(Kode status) {
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

    public Kode getSprakkode() {
        return sprakkode;
    }

    public void setSprakkode(Kode sprakkode) {
        this.sprakkode = sprakkode;
    }

    public Boolean getToTrinnsBehandling() {
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

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
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

    public List<BehandlingÅrsakDto> getBehandlingArsaker() {
        return behandlingArsaker;
    }

    public void setBehandlingArsaker(List<BehandlingÅrsakDto> behandlingArsaker) {
        this.behandlingArsaker = behandlingArsaker;
    }

    @Override
    public String toString() {
        return "BehandlingDto{" +
                "id=" + id +
                ", versjon=" + versjon +
                ", type=" + (type != null ? type.toString() : null) +
                ", status=" + (status != null ? status.toString() : null) +
                ", fagsakId=" + fagsakId +
                ", behandlendeEnhetId='" + behandlendeEnhetId + '\'' +
                ", behandlendeEnhetNavn='" + behandlendeEnhetNavn + '\'' +
                ", behandlingPaaVent=" + behandlingPaaVent +
                ", ansvarligSaksbehandler='" + ansvarligSaksbehandler + '\'' +
                ", fristBehandlingPaaVent=" + fristBehandlingPaaVent +
                ", avsluttet=" + avsluttet +
                ", behandlingHenlagt=" + behandlingHenlagt +
                ", behandlingsresultat=" + (behandlingsresultat != null ? behandlingsresultat.toString() : null) +
                ", sprakkode=" + (sprakkode != null ? sprakkode.toString() : null) +
                ", toTrinnsBehandling=" + toTrinnsBehandling +
                ", originalBehandlingId=" + originalBehandlingId +
                ", links=" + (links != null ? links.toString() : null) +
                ", taskStatus='" + taskStatus + '\'' +
                ", behandlingKoet='" + behandlingKoet + '\'' +
                ", venteArsakKode='" + venteArsakKode + '\'' +
                ", behandlingArsaker='" + (behandlingArsaker != null ? behandlingArsaker.toString() : null) + '\'' +
                '}';
    }
}
