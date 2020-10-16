package no.nav.foreldrepenger.melding.dtomapper;

import no.nav.foreldrepenger.fpsak.dto.personopplysning.PersonopplysningDto;
import no.nav.foreldrepenger.melding.personopplysning.Personopplysning;

public class PersonopplysningDtoMapper {
    public Personopplysning mapPersonopplysningFraDto(PersonopplysningDto dto) {
        return new Personopplysning(dto.getFnr(), dto.getAktoerId(), dto.getNavn(), dto.getHarVerge());
    }
}
