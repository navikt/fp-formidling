package no.nav.foreldrepenger.fpsak;

import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingIdDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.innsyn.InnsynsbehandlingDto;

public interface InnsynRestKlient {

    InnsynsbehandlingDto hentInnsynsbehandling(BehandlingIdDto behandlingIdDto);

}
