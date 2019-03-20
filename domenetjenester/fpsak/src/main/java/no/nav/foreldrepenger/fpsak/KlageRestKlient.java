package no.nav.foreldrepenger.fpsak;

import java.util.Optional;

import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingIdDto;
import no.nav.foreldrepenger.fpsak.dto.klage.KlagebehandlingDto;

public interface KlageRestKlient {

    Optional<KlagebehandlingDto> hentKlagebehandling(BehandlingIdDto behandlingIdDto);
}
