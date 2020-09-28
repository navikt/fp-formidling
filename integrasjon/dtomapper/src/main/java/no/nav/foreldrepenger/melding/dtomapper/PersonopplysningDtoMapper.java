package no.nav.foreldrepenger.melding.dtomapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpsak.dto.personopplysning.PersonopplysningDto;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.foreldrepenger.melding.personopplysning.Personopplysning;

@ApplicationScoped
public class PersonopplysningDtoMapper {
    private KodeverkRepository kodeverkRepository;

    @Inject
    public PersonopplysningDtoMapper(KodeverkRepository kodeverkRepository) {
        this.kodeverkRepository = kodeverkRepository;
    }

    public PersonopplysningDtoMapper() {
    }


    public Personopplysning mapPersonopplysningFraDto(PersonopplysningDto dto) {
        return new Personopplysning(dto.getFnr(), dto.getAktoerId(), dto.getNavn(), dto.getHarVerge());
    }
}
