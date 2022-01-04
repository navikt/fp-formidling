package no.nav.foreldrepenger.fpsak.mapper;

import no.nav.foreldrepenger.fpformidling.fagsak.Dekningsgrad;
import no.nav.foreldrepenger.fpformidling.ytelsefordeling.YtelseFordeling;
import no.nav.foreldrepenger.fpsak.dto.ytelsefordeling.YtelseFordelingDto;

public final class YtelseFordelingDtoMapper {

    private YtelseFordelingDtoMapper() {
    }

    public static YtelseFordeling mapYtelseFordelingFraDto(YtelseFordelingDto dto) {
        return new YtelseFordeling(new Dekningsgrad(dto.getGjeldendeDekningsgrad()));
    }
}
