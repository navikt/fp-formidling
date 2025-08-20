package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.fpformidling.domene.behandling.BehandlingStatus;
import no.nav.foreldrepenger.fpformidling.domene.behandling.BehandlingType;
import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.domene.uttak.Rettigheter;
import no.nav.foreldrepenger.fpformidling.domene.vilkår.Avslagsårsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling.vilkår.VilkårDto;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BehandlingDto {

    private UUID uuid;
    private BehandlingType type;
    private BehandlingStatus status;
    private LocalDateTime opprettet;
    private LocalDateTime avsluttet;
    private String behandlendeEnhetId;
    private BehandlingsresultatDto behandlingsresultat;
    private Språkkode sprakkode;
    private boolean toTrinnsBehandling;
    private List<BehandlingResourceLinkDto> links = new ArrayList<>();
    private List<BehandlingResourceLinkDto> formidlingRessurser = new ArrayList<>();
    private List<BehandlingÅrsakDto> behandlingÅrsaker = new ArrayList<>();
    private List<VilkårDto> vilkår;
    private UUID originalBehandlingUuid;
    private Avslagsårsak medlemskapOpphørsårsak;
    private LocalDate medlemskapFom;
    private Rettigheter rettigheter;

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

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
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

    public List<BehandlingÅrsakDto> getBehandlingÅrsaker() {
        return behandlingÅrsaker;
    }

    public void setBehandlingÅrsaker(List<BehandlingÅrsakDto> behandlingÅrsaker) {
        this.behandlingÅrsaker = behandlingÅrsaker;
    }

    public List<VilkårDto> getVilkår() {
        return vilkår;
    }

    public void setVilkår(List<VilkårDto> vilkår) {
        this.vilkår = vilkår;
    }

    public List<BehandlingResourceLinkDto> getFormidlingRessurser() {
        return formidlingRessurser;
    }

    public void setFormidlingRessurser(List<BehandlingResourceLinkDto> formidlingRessurser) {
        this.formidlingRessurser = formidlingRessurser;
    }

    public String getBehandlendeEnhetId() {
        return behandlendeEnhetId;
    }

    public void setBehandlendeEnhetId(String behandlendeEnhetId) {
        this.behandlendeEnhetId = behandlendeEnhetId;
    }

    public UUID getOriginalBehandlingUuid() {
        return originalBehandlingUuid;
    }

    public void setOriginalBehandlingUuid(UUID originalBehandlingUuid) {
        this.originalBehandlingUuid = originalBehandlingUuid;
    }

    public Avslagsårsak getMedlemskapOpphørsårsak() {
        return medlemskapOpphørsårsak;
    }

    public void setMedlemskapOpphørsårsak(Avslagsårsak medlemskapOpphørsårsak) {
        this.medlemskapOpphørsårsak = medlemskapOpphørsårsak;
    }

    public LocalDate getMedlemskapFom() {
        return medlemskapFom;
    }

    public void setMedlemskapFom(LocalDate medlemskapFom) {
        this.medlemskapFom = medlemskapFom;
    }

    public Rettigheter getRettigheter() {
        return rettigheter;
    }

    public void setRettigheter(Rettigheter rettigheter) {
        this.rettigheter = rettigheter;
    }

    @Override
    public String toString() {
        return "BehandlingDto{" + "id=" + uuid + ", type=" + type + ", status=" + status + ", opprettet=" + opprettet + ", avsluttet=" + avsluttet
            + ", behandlendeEnhetId='" + behandlendeEnhetId + ", behandlingsresultat=" + behandlingsresultat + ", sprakkode=" + sprakkode
            + ", toTrinnsBehandling=" + toTrinnsBehandling + ", links=" + links + ", behandlingÅrsaker=" + behandlingÅrsaker
            + '}';
    }
}
