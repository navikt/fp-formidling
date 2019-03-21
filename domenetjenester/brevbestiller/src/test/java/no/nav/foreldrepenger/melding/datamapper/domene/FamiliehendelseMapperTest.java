package no.nav.foreldrepenger.melding.datamapper.domene;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Test;

import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.AvklartDataAdopsjonDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.AvklartDataFodselDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.AvklartDataOmsorgDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.FamiliehendelseDto;
import no.nav.foreldrepenger.melding.brevbestiller.XmlUtil;

public class FamiliehendelseMapperTest {

    LocalDate førsteJanuar = LocalDate.of(2018, 1, 1);

    @Test
    public void utledAntallBarnFraDto() {
        XMLGregorianCalendar førsteJanuarXml = XmlUtil.finnDatoVerdiAvUtenTidSone(førsteJanuar);
        assertThat(FamiliehendelseMapper.finnTermindato(lagDtoMedTermindato(førsteJanuar)).get()).isEqualTo(førsteJanuarXml);
    }

    private FamiliehendelseDto lagDtoMedTermindato(LocalDate termindato) {
        return lagFødselDtoMedBarnOgTermindato(termindato, false, 0);
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

    @Test
    public void finnTermindato() {
        int antallBarnFødsel = 1;
        int antallBarnAdopsjon = 2;
        int antallBarnOmsorg = 3;
        assertThat(FamiliehendelseMapper.utledAntallBarnFraDto(lagFødselDtoMedBarnOgTermindato(førsteJanuar, false, antallBarnFødsel))).isEqualTo(antallBarnFødsel);
        assertThat(FamiliehendelseMapper.utledAntallBarnFraDto(lagFødselDtoMedBarnOgTermindato(førsteJanuar, true, antallBarnFødsel))).isEqualTo(antallBarnFødsel);
        assertThat(FamiliehendelseMapper.utledAntallBarnFraDto(lagAdopsjonsDtoMedAntallBarn(antallBarnAdopsjon))).isEqualTo(antallBarnAdopsjon);
        assertThat(FamiliehendelseMapper.utledAntallBarnFraDto(lagOmsorgDtoMedAntallBarn(antallBarnOmsorg)));
    }
}