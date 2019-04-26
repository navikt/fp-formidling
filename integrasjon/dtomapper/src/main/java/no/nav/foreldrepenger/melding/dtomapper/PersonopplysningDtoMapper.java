package no.nav.foreldrepenger.melding.dtomapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpsak.dto.personopplysning.PersonopplysningDto;
import no.nav.foreldrepenger.melding.aktør.NavBrukerKjønn;
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
        return new Personopplysning(dto.getFnr(), dto.getAktoerId(), kodeverkRepository.finn(NavBrukerKjønn.class,
                dto.getNavBrukerKjonn().getKode()), dto.getNavn(), dto.getHarVerge());
    }
}
