package no.nav.foreldrepenger.melding.dtomapper;

import no.nav.foreldrepenger.fpsak.dto.personopplysning.VergeDto;
import no.nav.foreldrepenger.melding.verge.Verge;

public class VergeDtoMapper {

    public static Verge mapVergeFraDto(VergeDto dto) {
        return new Verge(dto.getSokerErKontaktPerson(), dto.getVergeErKontaktPerson(), dto.getFnr());
    }
}
