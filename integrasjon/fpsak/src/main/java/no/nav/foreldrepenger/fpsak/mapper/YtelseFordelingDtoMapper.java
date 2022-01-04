package no.nav.foreldrepenger.fpsak.mapper;

import no.nav.foreldrepenger.fpsak.dto.ytelsefordeling.YtelseFordelingDto;
import no.nav.foreldrepenger.melding.fagsak.Dekningsgrad;
import no.nav.foreldrepenger.melding.ytelsefordeling.YtelseFordeling;

public final class YtelseFordelingDtoMapper {

    private YtelseFordelingDtoMapper() {
    }

    public static YtelseFordeling mapYtelseFordelingFraDto(YtelseFordelingDto dto) {
        return new YtelseFordeling(new Dekningsgrad(dto.getGjeldendeDekningsgrad()));
    }
}
