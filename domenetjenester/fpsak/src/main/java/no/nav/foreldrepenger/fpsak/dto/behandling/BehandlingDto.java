package no.nav.foreldrepenger.fpsak.dto.behandling;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BehandlingDto {
    private Long id;
    private Integer versjon;
    private KodeDto type;
    private KodeDto status;
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
    private String ansvarligBeslutter;
    private LocalDate fristBehandlingPaaVent;
    private Boolean behandlingHenlagt;
    private BehandlingsresultatDto behandlingsresultat;
    private KodeDto sprakkode;
    private boolean toTrinnsBehandling;
    private Long originalBehandlingId;
    private List<BehandlingResourceLinkDto> links = new ArrayList<>();
    private String taskStatus;
    private String venteArsakKode;
    private List<BehandlingÅrsakDto> behandlingArsaker = new ArrayList<>();

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

    public Boolean getErAktivPapirsoknad() {
        return erAktivPapirsoknad;
    }

    public void setErAktivPapirsoknad(Boolean erAktivPapirsoknad) {
        this.erAktivPapirsoknad = erAktivPapirsoknad;
    }

    @Override
    public String toString() {
        return "BehandlingDto{" +
                "id=" + id +
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
                ", originalBehandlingId=" + originalBehandlingId +
                ", links=" + links +
                ", taskStatus='" + taskStatus + '\'' +
                ", venteArsakKode='" + venteArsakKode + '\'' +
                ", behandlingArsaker=" + behandlingArsaker +
                '}';
    }
}
