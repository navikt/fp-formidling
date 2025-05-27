package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import no.nav.foreldrepenger.fpformidling.domene.behandling.BehandlingStatus;
import no.nav.foreldrepenger.fpformidling.domene.behandling.BehandlingType;
import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.domene.vilkår.Avslagsårsak;
import no.nav.foreldrepenger.fpformidling.domene.vilkår.VilkårType;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.fagsak.FagsakDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.personopplysning.VergeDto;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingÅrsakType;

public class BehandlingDto {

    private UUID uuid;
    private BehandlingType type;
    private BehandlingStatus status;
    private LocalDateTime opprettet;
    private LocalDateTime avsluttet;
    private String behandlendeEnhetId;
    private Språkkode språkkode;
    private boolean toTrinnsBehandling;
    private boolean harAvklartAnnenForelderRett;
    private UUID originalBehandlingUuid;
    private Avslagsårsak medlemskapOpphørsårsak;
    private LocalDate medlemskapFom;
    private BehandlingsresultatDto behandlingsresultat;
    private FagsakDto fagsak;
    private VergeDto verge;

    private List<BehandlingÅrsakType> behandlingÅrsaker = new ArrayList<>();
    private List<VilkårType> vilkår;
    private List<BehandlingResourceLinkDto> links = new ArrayList<>();

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

    public Språkkode getSpråkkode() {
        return språkkode;
    }

    public void setSpråkkode(Språkkode språkkode) {
        this.språkkode = språkkode;
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

    public List<BehandlingÅrsakType> getBehandlingÅrsaker() {
        return behandlingÅrsaker;
    }

    public void setBehandlingÅrsaker(List<BehandlingÅrsakType> behandlingÅrsaker) {
        this.behandlingÅrsaker = behandlingÅrsaker;
    }

    public boolean getHarAvklartAnnenForelderRett() {
        return harAvklartAnnenForelderRett;
    }

    public void setHarAvklartAnnenForelderRett(boolean harAvklartAnnenForelderRett) {
        this.harAvklartAnnenForelderRett = harAvklartAnnenForelderRett;
    }

    public List<VilkårType> getVilkår() {
        return vilkår;
    }

    public void setVilkår(List<VilkårType> vilkår) {
        this.vilkår = vilkår;
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

    public FagsakDto fagsak() {
        return fagsak;
    }

    public void setFagsak(FagsakDto fagsak) {
        this.fagsak = fagsak;
    }

    public VergeDto verge() {
        return verge;
    }

    public void setVerge(VergeDto verge) {
        this.verge = verge;
    }

    @Override
    public String toString() {
        return "BehandlingDto{" + "id=" + uuid + ", type=" + type + ", status=" + status + ", opprettet=" + opprettet + ", avsluttet=" + avsluttet
            + ", behandlendeEnhetId='" + behandlendeEnhetId + ", behandlingsresultat=" + behandlingsresultat + ", sprakkode=" + språkkode
            + ", toTrinnsBehandling=" + toTrinnsBehandling + ", links=" + links + ", behandlingÅrsaker=" + behandlingÅrsaker
            + '}';
    }
}
