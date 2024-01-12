package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling.familiehendelse.AvklartBarnDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling.familiehendelse.AvklartDataAdopsjonDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling.familiehendelse.AvklartDataFodselDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling.familiehendelse.AvklartDataOmsorgDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling.familiehendelse.FamiliehendelseDto;

class FamiliehendelseDtoMapperTest {
    private static final LocalDate FØRSTE_JANUAR = LocalDate.of(2018, 1, 1);

    @Test
    void utledTermindato() {
        Assertions.assertThat(FamiliehendelseDtoMapper.finnTermindato(lagDtoMedTermindato(FØRSTE_JANUAR))).contains(FØRSTE_JANUAR);
    }

    @Test
    void finnAntallbarnFødt() {
        assertThat(FamiliehendelseDtoMapper.utledAntallBarnFraDto(lagFødselDtoMedBarnTerminOgFødsel(FØRSTE_JANUAR, 1, 2))).isEqualTo(2);
        assertThat(FamiliehendelseDtoMapper.utledAntallBarnFraDto(lagFødselDtoMedBarnTerminOgFødsel(FØRSTE_JANUAR, 3, 0))).isEqualTo(3);
        assertThat(FamiliehendelseDtoMapper.utledAntallBarnFraDto(lagFødselDtoMedBarnTerminOgFødsel(FØRSTE_JANUAR, 3, 3))).isEqualTo(3);
        assertThat(FamiliehendelseDtoMapper.utledAntallBarnFraDto(lagFødselDtoMedBarnTerminOgFødsel(FØRSTE_JANUAR, 3, 1))).isEqualTo(1);
    }

    @Test
    void finnAntallDødeBarn() {
        assertThat(FamiliehendelseDtoMapper.utledAntallDødeBarnFraDto(lagFødselDtoMedBarnTerminOgFødsel(FØRSTE_JANUAR, 1, 2))).isZero();
        assertThat(FamiliehendelseDtoMapper.utledAntallDødeBarnFraDto(lagFødselDtoMedFødselOgDødsdato(3, 2))).isEqualTo(2);
        assertThat(FamiliehendelseDtoMapper.utledAntallDødeBarnFraDto(lagFødselDtoMedFødselOgDødsdato(3, 3))).isEqualTo(3);
        assertThat(FamiliehendelseDtoMapper.utledAntallDødeBarnFraDto(lagFødselDtoMedFødselOgDødsdato(3, 0))).isZero();
    }

    @Test
    void finnTermindato() {
        var antallBarnFødsel = 1;
        var antallBarnAdopsjon = 2;
        var antallBarnOmsorg = 3;
        assertThat(FamiliehendelseDtoMapper.utledAntallBarnFraDto(lagFødselDtoMedBarnOgTermindato(FØRSTE_JANUAR, false, antallBarnFødsel))).isEqualTo(
            antallBarnFødsel);
        assertThat(FamiliehendelseDtoMapper.utledAntallBarnFraDto(lagFødselDtoMedBarnOgTermindato(FØRSTE_JANUAR, true, antallBarnFødsel))).isEqualTo(
            antallBarnFødsel);
        assertThat(FamiliehendelseDtoMapper.utledAntallBarnFraDto(lagAdopsjonsDtoMedAntallBarn(antallBarnAdopsjon))).isEqualTo(antallBarnAdopsjon);
        assertThat(FamiliehendelseDtoMapper.utledAntallBarnFraDto(lagOmsorgDtoMedAntallBarn(antallBarnOmsorg))).isEqualTo(antallBarnOmsorg);
    }

    private FamiliehendelseDto lagAdopsjonsDtoMedAntallBarn(int antallBarn) {
        var dto = new AvklartDataAdopsjonDto();
        Map<Integer, LocalDate> adopsjonFodelsedatoer = new HashMap<>();
        for (var i = 0; i < antallBarn; i++) {
            adopsjonFodelsedatoer.put(i, LocalDate.now());
        }
        dto.setAdopsjonFodelsedatoer(adopsjonFodelsedatoer);
        return dto;
    }

    private FamiliehendelseDto lagOmsorgDtoMedAntallBarn(int antallBarn) {
        var dto = new AvklartDataOmsorgDto();
        dto.setAntallBarnTilBeregning(antallBarn);
        return dto;
    }

    private FamiliehendelseDto lagFødselDtoMedBarnTerminOgFødsel(LocalDate termindato, int antallBarnTermin, int antallbarnFødsel) {
        var dto = new AvklartDataFodselDto();
        dto.setTermindato(termindato);
        dto.setAntallBarnTermin(antallBarnTermin);

        for (var i = 0; i < antallbarnFødsel; i++) {
            dto.getAvklartBarn().add(new AvklartBarnDto(LocalDate.now(), null));
        }
        return dto;
    }

    private FamiliehendelseDto lagFødselDtoMedFødselOgDødsdato(int antallbarnFødsel, int antallDødeBarn) {
        var dto = new AvklartDataFodselDto();

        for (var i = 0; i < antallbarnFødsel; i++) {
            dto.getAvklartBarn().add(new AvklartBarnDto(LocalDate.now(), null));
        }

        for (var i = 0; i < antallDødeBarn; i++) {
            dto.getAvklartBarn().get(i).setDodsdato(LocalDate.now());
        }

        return dto;
    }

    private FamiliehendelseDto lagFødselDtoMedBarnOgTermindato(LocalDate termindato, boolean termin, int antallBarn) {
        var dto = new AvklartDataFodselDto();
        dto.setTermindato(termindato);
        if (termin) {
            dto.setAntallBarnTermin(antallBarn);
        } else {
            dto.getAvklartBarn().add(new AvklartBarnDto(LocalDate.now(), null));
        }
        return dto;
    }

    private FamiliehendelseDto lagDtoMedTermindato(LocalDate termindato) {
        return lagFødselDtoMedBarnOgTermindato(termindato, false, 0);
    }
}
