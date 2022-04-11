package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper;

import no.nav.foreldrepenger.fpformidling.fagsak.Dekningsgrad;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.ytelsefordeling.YtelseFordelingDto;
import no.nav.foreldrepenger.fpformidling.ytelsefordeling.YtelseFordeling;

public final class YtelseFordelingDtoMapper {

    private YtelseFordelingDtoMapper() {
    }

    public static YtelseFordeling mapYtelseFordelingFraDto(YtelseFordelingDto dto) {
        return new YtelseFordeling(new Dekningsgrad(dto.getGjeldendeDekningsgrad()));
    }
}
