package no.nav.foreldrepenger.melding.datamapper.domene;

import java.util.Optional;

import javax.xml.datatype.XMLGregorianCalendar;

import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.AvklartDataAdopsjonDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.AvklartDataFodselDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.AvklartDataOmsorgDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.FamiliehendelseDto;
import no.nav.foreldrepenger.melding.brevbestiller.XmlUtil;

public class FamiliehendelseMapper {

    public static int utledAntallBarnFraDto(FamiliehendelseDto familiehendelseDto) {
        if (familiehendelseDto instanceof AvklartDataAdopsjonDto) {
            return ((AvklartDataAdopsjonDto) familiehendelseDto).getAdopsjonFodelsedatoer().size();
        } else if (familiehendelseDto instanceof AvklartDataFodselDto) {
            return utledAntallBarnFødsel((AvklartDataFodselDto) familiehendelseDto);
        } else if (familiehendelseDto instanceof AvklartDataOmsorgDto) {
            return ((AvklartDataOmsorgDto) familiehendelseDto).getAntallBarnTilBeregning();
        }
        throw new IllegalStateException("Familihendelse er av ukjent type");
    }

    public static Optional<XMLGregorianCalendar> finnTermindato(FamiliehendelseDto dto) {
        if (dto instanceof AvklartDataFodselDto) {
            if (((AvklartDataFodselDto) dto).getTermindato() != null) {
                return Optional.of(XmlUtil.finnDatoVerdiAvUtenTidSone(((AvklartDataFodselDto) dto).getTermindato()));
            }
        }
        return Optional.empty();
    }

    private static int utledAntallBarnFødsel(AvklartDataFodselDto familiehendelseDto) {
        int sum = 0;
        if (familiehendelseDto.getAntallBarnTermin() != null) {
            sum += familiehendelseDto.getAntallBarnTermin();
        }
        if (familiehendelseDto.getAntallBarnFødt() != null) {
            sum += familiehendelseDto.getAntallBarnFødt();
        }
        return sum;
    }

}
