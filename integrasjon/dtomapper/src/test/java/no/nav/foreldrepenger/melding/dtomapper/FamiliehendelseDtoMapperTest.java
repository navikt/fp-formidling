package no.nav.foreldrepenger.melding.dtomapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.AvklartBarnDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.AvklartDataAdopsjonDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.AvklartDataFodselDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.AvklartDataOmsorgDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.FamiliehendelseDto;

public class FamiliehendelseDtoMapperTest {
    private static final LocalDate FØRSTE_JANUAR = LocalDate.of(2018, 1, 1);

    @Test
    public void utledTermindato() {
        assertThat(FamiliehendelseDtoMapper.finnTermindato(lagDtoMedTermindato(FØRSTE_JANUAR)).get()).isEqualTo(FØRSTE_JANUAR);
    }


    @Test
    public void finnAntallbarnFødt() {
        assertThat(FamiliehendelseDtoMapper.utledAntallBarnFraDto(lagFødselDtoMedBarnTerminOgFødsel(FØRSTE_JANUAR, 1, 2))).isEqualTo(2);
        assertThat(FamiliehendelseDtoMapper.utledAntallBarnFraDto(lagFødselDtoMedBarnTerminOgFødsel(FØRSTE_JANUAR, 3, 0))).isEqualTo(3);
        assertThat(FamiliehendelseDtoMapper.utledAntallBarnFraDto(lagFødselDtoMedBarnTerminOgFødsel(FØRSTE_JANUAR, 3, 3))).isEqualTo(3);
        assertThat(FamiliehendelseDtoMapper.utledAntallBarnFraDto(lagFødselDtoMedBarnTerminOgFødsel(FØRSTE_JANUAR, 3, 1))).isEqualTo(1);
    }


    @Test
    public void finnTermindato() {
        int antallBarnFødsel = 1;
        int antallBarnAdopsjon = 2;
        int antallBarnOmsorg = 3;
        assertThat(FamiliehendelseDtoMapper.utledAntallBarnFraDto(lagFødselDtoMedBarnOgTermindato(FØRSTE_JANUAR, false, antallBarnFødsel))).isEqualTo(antallBarnFødsel);
        assertThat(FamiliehendelseDtoMapper.utledAntallBarnFraDto(lagFødselDtoMedBarnOgTermindato(FØRSTE_JANUAR, true, antallBarnFødsel))).isEqualTo(antallBarnFødsel);
        assertThat(FamiliehendelseDtoMapper.utledAntallBarnFraDto(lagAdopsjonsDtoMedAntallBarn(antallBarnAdopsjon))).isEqualTo(antallBarnAdopsjon);
        assertThat(FamiliehendelseDtoMapper.utledAntallBarnFraDto(lagOmsorgDtoMedAntallBarn(antallBarnOmsorg)));
    }

    private FamiliehendelseDto lagAdopsjonsDtoMedAntallBarn(int antallBarn) {
        AvklartDataAdopsjonDto dto = new AvklartDataAdopsjonDto();
        Map<Integer, LocalDate> adopsjonFodelsedatoer = new HashMap<>();
        for (int i = 0; i < antallBarn; i++) {
            adopsjonFodelsedatoer.put(i, LocalDate.now());
        }
        dto.setAdopsjonFodelsedatoer(adopsjonFodelsedatoer);
        return dto;
    }

    private FamiliehendelseDto lagOmsorgDtoMedAntallBarn(int antallBarn) {
        AvklartDataOmsorgDto dto = new AvklartDataOmsorgDto();
        dto.setAntallBarnTilBeregning(antallBarn);
        return dto;
    }


    private FamiliehendelseDto lagFødselDtoMedBarnTerminOgFødsel(LocalDate termindato, int antallBarnTermin, int antallbarnFødsel) {
        AvklartDataFodselDto dto = new AvklartDataFodselDto();
        dto.setTermindato(termindato);
        dto.setAntallBarnTermin(antallBarnTermin);
        for (int i = 0; i < antallbarnFødsel; i++) {
            dto.getAvklartBarn().add(new AvklartBarnDto(LocalDate.now(), null));
        }
        return dto;
    }

    private FamiliehendelseDto lagFødselDtoMedBarnOgTermindato(LocalDate termindato, boolean termin, int antallBarn) {
        AvklartDataFodselDto dto = new AvklartDataFodselDto();
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
