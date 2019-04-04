package no.nav.foreldrepenger.melding.datamapper.domene;

import javax.enterprise.context.ApplicationScoped;

import no.nav.foreldrepenger.fpsak.BehandlingRestKlient;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelse;

@ApplicationScoped
public class FamiliehendelseMapper {

    private BehandlingRestKlient behandlingRestKlient;

    public FamiliehendelseMapper() {
        //CDI
    }

    public FamiliehendelseMapper(BehandlingRestKlient behandlingRestKlient) {
        this.behandlingRestKlient = behandlingRestKlient;
    }

    public FamilieHendelse hentFamiliehendelse(Behandling behandling) {
        return FamilieHendelse.fraDto(behandlingRestKlient.hentFamiliehendelse(behandling.getResourceLinkDtos()));
    }


}
