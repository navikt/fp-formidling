package no.nav.foreldrepenger.fpsak;

import java.util.Optional;

import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingIdDto;

public interface BehandlingRestKlient {
    Optional<BehandlingDto> hentBehandling(BehandlingIdDto behandlingIdDto);
}
