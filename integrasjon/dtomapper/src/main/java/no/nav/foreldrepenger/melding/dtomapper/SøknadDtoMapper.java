package no.nav.foreldrepenger.melding.dtomapper;

import no.nav.foreldrepenger.fpsak.dto.soknad.SoknadDto;
import no.nav.foreldrepenger.melding.søknad.Søknad;
import no.nav.foreldrepenger.melding.ytelsefordeling.OppgittRettighet;

public class SøknadDtoMapper {
    public static Søknad mapSøknadFraDto(SoknadDto dto) {
        boolean aleneomsorgForBarnet = dto.getOppgittRettighet() != null && dto.getOppgittRettighet().isAleneomsorgForBarnet();
        return new Søknad(dto.getMottattDato(), dto.getSoknadsdato(), new OppgittRettighet(aleneomsorgForBarnet));
    }
}
