package no.nav.foreldrepenger.melding.dtomapper;

import javax.enterprise.context.ApplicationScoped;

import no.nav.foreldrepenger.fpsak.dto.ytelsefordeling.YtelseFordelingDto;
import no.nav.foreldrepenger.melding.fagsak.Dekningsgrad;
import no.nav.foreldrepenger.melding.ytelsefordeling.YtelseFordeling;

@ApplicationScoped
public class YtelseFordelingDtoMapper {

    public static YtelseFordeling mapYtelseFordelingFraDto(YtelseFordelingDto dto) {
        return new YtelseFordeling(new Dekningsgrad(dto.getGjeldendeDekningsgrad()));
    }

}
