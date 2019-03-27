package no.nav.foreldrepenger.melding.datamapper.domene;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpsak.BehandlingRestKlient;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.brevbestiller.api.dto.Søknad;

@ApplicationScoped
public class SøknadMapper {

    private SøknadMapper søknadMapper;
    private BehandlingRestKlient behandlingRestKlient;

    public SøknadMapper() {
        //CDI
    }

    @Inject
    public SøknadMapper(SøknadMapper søknadMapper,
                        BehandlingRestKlient behandlingRestKlient) {
        this.søknadMapper = søknadMapper;
        this.behandlingRestKlient = behandlingRestKlient;
    }

    public Søknad hentSøknad(Behandling behandling) {
        return new Søknad(behandlingRestKlient.hentSoknad(behandling.getResourceLinkDtos()));
    }
}
