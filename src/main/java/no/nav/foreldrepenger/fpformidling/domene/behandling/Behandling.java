package no.nav.foreldrepenger.fpformidling.domene.behandling;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakBackend;
import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.domene.verge.Verge;
import no.nav.foreldrepenger.fpformidling.domene.vilkår.Avslagsårsak;
import no.nav.foreldrepenger.fpformidling.domene.vilkår.Vilkår;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingÅrsakType;

public class Behandling {
    private Behandlingsresultat behandlingsresultat;
    private List<BehandlingResourceLink> resourceLinker;

    //Felter brukt i brev
    private UUID uuid; //
    private BehandlingType behandlingType; //
    private LocalDateTime opprettetDato; //
    private LocalDateTime avsluttet; //
    private List<BehandlingÅrsak> behandlingÅrsaker;
    private boolean toTrinnsBehandling; //

    private String behandlendeEnhetId; //
    private FagsakBackend fagsak;
    private BehandlingStatus status; //
    private Språkkode språkkode; //
    private boolean harAvklartAnnenForelderRett; //
    private List<Vilkår> vilkår;

    private UUID originalBehandlingUuid; //
    private Avslagsårsak medlemskapOpphørsårsak; //
    private LocalDate medlemskapFom; //

    private Verge verge;

    private Behandling() {
    }

    public LocalDateTime getAvsluttet() {
        return avsluttet;
    }

    public Behandlingsresultat getBehandlingsresultat() {
        return behandlingsresultat;
    }

    public BehandlingType getBehandlingType() {
        return behandlingType;
    }

    public LocalDateTime getOpprettet() {
        return opprettetDato;
    }

    public List<BehandlingÅrsak> getBehandlingÅrsaker() {
        return behandlingÅrsaker;
    }

    public boolean isToTrinnsBehandling() {
        return toTrinnsBehandling;
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean getHarAvklartAnnenForelderRett() {
        return harAvklartAnnenForelderRett;
    }

    public List<Vilkår> getVilkår() {
        return vilkår == null ? List.of() : vilkår;
    }

    public List<BehandlingResourceLink> getResourceLinker() {
        return resourceLinker;
    }

    public Språkkode getSpråkkode() {
        return språkkode;
    }

    public boolean harBehandlingÅrsak(BehandlingÅrsakType behandlingÅrsak) {
        return getBehandlingÅrsaker().stream().map(BehandlingÅrsak::getBehandlingÅrsakType).anyMatch(behandlingÅrsak::equals);
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

    public boolean erAnke() {
        return BehandlingType.ANKE.equals(getBehandlingType());
    }

    public boolean erInnsyn() {
        return BehandlingType.INNSYN.equals(getBehandlingType());
    }

    public FagsakBackend getFagsak() {
        return fagsak;
    }

    public String getBehandlendeEnhetId() {
        return behandlendeEnhetId;
    }

    public boolean utenMinsterett() {
        return Optional.ofNullable(getBehandlingsresultat()).map(Behandlingsresultat::utenMinsterett).orElse(true);
    }

    public UUID getOriginalBehandlingUuid() {
        return originalBehandlingUuid;
    }

    public Verge verge() {
        return verge;
    }

    public void setVerge(Verge verge) {
        this.verge = verge;
    }

    public Avslagsårsak getMedlemskapOpphørsårsak() {
        return medlemskapOpphørsårsak;
    }

    public LocalDate getMedlemskapFom() {
        return medlemskapFom;
    }

    public static Behandling.Builder builder() {
        return new Behandling.Builder();
    }

    public static class Builder {
        private Behandling kladd;

        public Builder() {
            this.kladd = new Behandling();
            this.kladd.resourceLinker = new ArrayList<>();
            this.kladd.behandlingÅrsaker = new ArrayList<>();
        }

        public Behandling.Builder medBehandlendeEnhetId(String behandlendeEnhetId) {
            this.kladd.behandlendeEnhetId = behandlendeEnhetId;
            return this;
        }

        public Behandling.Builder medBehandlingsresultat(Behandlingsresultat behandlingsresultat) {
            this.kladd.behandlingsresultat = behandlingsresultat;
            return this;
        }

        public Behandling.Builder leggTilResourceLink(BehandlingResourceLink resourceLink) {
            this.kladd.resourceLinker.add(resourceLink);
            return this;
        }

        public Behandling.Builder medUuid(UUID uuid) {
            this.kladd.uuid = uuid;
            return this;
        }

        public Behandling.Builder medBehandlingType(BehandlingType behandlingType) {
            this.kladd.behandlingType = behandlingType;
            return this;
        }

        public Behandling.Builder medOpprettet(LocalDateTime opprettetDato) {
            this.kladd.opprettetDato = opprettetDato;
            return this;
        }

        public Behandling.Builder medAvsluttet(LocalDateTime avsluttet) {
            this.kladd.avsluttet = avsluttet;
            return this;
        }

        public Behandling.Builder medBehandlingÅrsaker(List<BehandlingÅrsak> behandlingÅrsaker) {
            this.kladd.behandlingÅrsaker.addAll(behandlingÅrsaker);
            return this;
        }

        public Behandling.Builder medToTrinnsBehandling(boolean toTrinnsBehandling) {
            this.kladd.toTrinnsBehandling = toTrinnsBehandling;
            return this;
        }

        public Behandling.Builder medStatus(BehandlingStatus status) {
            this.kladd.status = status;
            return this;
        }

        public Behandling.Builder medFagsak(FagsakBackend fagsak) {
            this.kladd.fagsak = fagsak;
            return this;
        }

        public Behandling.Builder medSpråkkode(Språkkode språkkode) {
            this.kladd.språkkode = språkkode;
            return this;
        }

        public Behandling.Builder medOriginalBehandlingUuid(UUID uuid) {
            this.kladd.originalBehandlingUuid = uuid;
            return this;
        }

        public Behandling.Builder medHarAvklartAnnenForelderRett(boolean harAvklartAnnenForelderRett) {
            this.kladd.harAvklartAnnenForelderRett = harAvklartAnnenForelderRett;
            return this;
        }

        public Behandling.Builder medMedlemskapOpphørsårsak(Avslagsårsak medlemskapOpphørsårsak) {
            this.kladd.medlemskapOpphørsårsak = medlemskapOpphørsårsak;
            return this;
        }

        public Behandling.Builder medMedlemskapFom(LocalDate medlemskapFom) {
            this.kladd.medlemskapFom = medlemskapFom;
            return this;
        }

        public Behandling.Builder medVilkår(List<Vilkår> vilkår) {
            this.kladd.vilkår = vilkår;
            return this;
        }

        public Behandling.Builder medVerge(Verge verge) {
            this.kladd.verge = verge;
            return this;
        }

        public Behandling build() {
            return this.kladd;
        }
    }
}
