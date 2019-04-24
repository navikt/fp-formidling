package no.nav.foreldrepenger.melding.datamapper.domene;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpsak.BehandlingRestKlient;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatES;

@ApplicationScoped
public class BeregningsresultatESMapper {
    private BehandlingRestKlient behandlingRestKlient;

    public BeregningsresultatESMapper() {
        //CDI
    }

    @Inject
    public BeregningsresultatESMapper(BehandlingRestKlient behandlingRestKlient) {
        this.behandlingRestKlient = behandlingRestKlient;
    }

    public BeregningsresultatES hentBeregningsresultatES(Behandling behandling) {
        return new BeregningsresultatES(behandlingRestKlient.hentBeregningsresultatEngangsstønad(behandling.getResourceLinkDtos()));
    }
}
