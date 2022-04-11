package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.ytelsefordeling.YtelseFordelingDto;
import no.nav.foreldrepenger.fpformidling.ytelsefordeling.YtelseFordeling;

public class YtelseFordelingDtoMapperTest {

    @Test
    public void skal_mappe_dekningsgrad() {
        int dekningsgrad = 80;
        YtelseFordelingDto dto = new YtelseFordelingDto.Builder().medGjeldendeDekningsgrad(dekningsgrad).build();
        YtelseFordeling ytelseFordeling = YtelseFordelingDtoMapper.mapYtelseFordelingFraDto(dto);
        assertThat(ytelseFordeling.dekningsgrad().getVerdi()).isEqualTo(dekningsgrad);
    }
}
