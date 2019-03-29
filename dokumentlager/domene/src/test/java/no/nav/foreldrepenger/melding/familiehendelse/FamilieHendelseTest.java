package no.nav.foreldrepenger.melding.familiehendelse;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.AvklartDataAdopsjonDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.AvklartDataFodselDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.AvklartDataOmsorgDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.FamiliehendelseDto;

public class FamilieHendelseTest {

    private static final LocalDate FØRSTE_JANUAR = LocalDate.of(2018, 1, 1);

    @Test
    public void utledTermindato() {
        assertThat(FamilieHendelse.finnTermindato(lagDtoMedTermindato(FØRSTE_JANUAR)).get()).isEqualTo(FØRSTE_JANUAR);
    }

    @Test
    public void finnTermindato() {
        int antallBarnFødsel = 1;
        int antallBarnAdopsjon = 2;
        int antallBarnOmsorg = 3;
        assertThat(FamilieHendelse.utledAntallBarnFraDto(lagFødselDtoMedBarnOgTermindato(FØRSTE_JANUAR, false, antallBarnFødsel))).isEqualTo(antallBarnFødsel);
        assertThat(FamilieHendelse.utledAntallBarnFraDto(lagFødselDtoMedBarnOgTermindato(FØRSTE_JANUAR, true, antallBarnFødsel))).isEqualTo(antallBarnFødsel);
        assertThat(FamilieHendelse.utledAntallBarnFraDto(lagAdopsjonsDtoMedAntallBarn(antallBarnAdopsjon))).isEqualTo(antallBarnAdopsjon);
        assertThat(FamilieHendelse.utledAntallBarnFraDto(lagOmsorgDtoMedAntallBarn(antallBarnOmsorg)));
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

    private FamiliehendelseDto lagFødselDtoMedBarnOgTermindato(LocalDate termindato, boolean termin, int antallBarn) {
        AvklartDataFodselDto dto = new AvklartDataFodselDto();
        dto.setTermindato(termindato);
        if (termin) {
            dto.setAntallBarnTermin(antallBarn);
        } else {
            dto.setAntallBarnFødt(antallBarn);
        }
        return dto;
    }


    private FamiliehendelseDto lagDtoMedTermindato(LocalDate termindato) {
        return lagFødselDtoMedBarnOgTermindato(termindato, false, 0);
    }

}
