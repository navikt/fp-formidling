package no.nav.foreldrepenger.melding.datamapper.domene;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpsak.BehandlingRestKlient;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.vilkår.Vilkår;

@ApplicationScoped
public class VilkårMapper {

    private BehandlingRestKlient behandlingRestKlient;

    public VilkårMapper() {
        //CDI
    }

    @Inject
    public VilkårMapper(BehandlingRestKlient behandlingRestKlient) {
        this.behandlingRestKlient = behandlingRestKlient;
    }

    public Vilkår hentVilkår(Behandling behandling) {
        return new Vilkår(behandlingRestKlient.hentVilkår(behandling.getResourceLinkDtos()));
    }
}
