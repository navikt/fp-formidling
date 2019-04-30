package no.nav.foreldrepenger.melding.dtomapper;

import javax.enterprise.context.ApplicationScoped;

import no.nav.foreldrepenger.fpsak.dto.ytelsefordeling.YtelseFordelingDto;
import no.nav.foreldrepenger.melding.fagsak.Dekningsgrad;
import no.nav.foreldrepenger.melding.ytelsefordeling.YtelseFordeling;

@ApplicationScoped
public class YtelseFordelingDtoMapper {

    public static YtelseFordeling mapYtelseFordelingFraDto(YtelseFordelingDto dto) {
        boolean annenForelderHarRett = false;
        boolean harPeriodeMedAleneomsorg = false;
        if (dto.getAnnenforelderHarRettDto() != null && dto.getAnnenforelderHarRettDto().getAnnenforelderHarRett() != null) {
            annenForelderHarRett = dto.getAnnenforelderHarRettDto().getAnnenforelderHarRett();
        }
        if (dto.getAleneOmsorgPerioder() != null) {
            harPeriodeMedAleneomsorg = !dto.getAleneOmsorgPerioder().isEmpty();
        }
        return new YtelseFordeling(new Dekningsgrad(dto.getGjeldendeDekningsgrad()), annenForelderHarRett, harPeriodeMedAleneomsorg);
    }

}
