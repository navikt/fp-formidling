package no.nav.foreldrepenger.melding.brevbestiller.api;

import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingDto;

public interface BrevBestillerApplikasjonTjeneste {
    byte[] forhandsvisBrev(BehandlingDto behandlingDto);
}
