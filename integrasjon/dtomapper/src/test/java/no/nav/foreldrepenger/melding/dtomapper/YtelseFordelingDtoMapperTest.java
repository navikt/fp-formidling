package no.nav.foreldrepenger.melding.dtomapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

import no.nav.foreldrepenger.fpsak.dto.PeriodeDto;
import no.nav.foreldrepenger.fpsak.dto.ytelsefordeling.YtelseFordelingDto;
import no.nav.foreldrepenger.melding.ytelsefordeling.YtelseFordeling;

public class YtelseFordelingDtoMapperTest {

    @Test
    public void skal_sette_ingen_periode_med_aleneomsorg_når_liste_er_tom() {
        YtelseFordelingDto dto = new YtelseFordelingDto.Builder().medAleneOmsorgPerioder(Collections.emptyList()).build();
        YtelseFordeling ytelseFordeling = YtelseFordelingDtoMapper.mapYtelseFordelingFraDto(dto);
        assertThat(ytelseFordeling.isHarPerioderMedAleneomsorg()).isFalse();
    }

    @Test
    public void skal_sette_ingen_periode_med_aleneomsorg_når_liste_er_null() {
        YtelseFordelingDto dto = new YtelseFordelingDto.Builder().medAleneOmsorgPerioder(null).build();
        YtelseFordeling ytelseFordeling = YtelseFordelingDtoMapper.mapYtelseFordelingFraDto(dto);
        assertThat(ytelseFordeling.isHarPerioderMedAleneomsorg()).isFalse();
    }

    @Test
    public void skal_sette_periode_med_aleneomsorg() {
        YtelseFordelingDto dto = new YtelseFordelingDto.Builder().medAleneOmsorgPerioder(List.of(new PeriodeDto())).build();
        YtelseFordeling ytelseFordeling = YtelseFordelingDtoMapper.mapYtelseFordelingFraDto(dto);
        assertThat(ytelseFordeling.isHarPerioderMedAleneomsorg()).isTrue();
    }
}
