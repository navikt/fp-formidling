package no.nav.foreldrepenger.fpsak.mapper;

import no.nav.foreldrepenger.fpformidling.søknad.Søknad;
import no.nav.foreldrepenger.fpformidling.ytelsefordeling.OppgittRettighet;
import no.nav.foreldrepenger.fpsak.dto.soknad.SoknadBackendDto;

public class SøknadDtoMapper {
    public static Søknad mapSøknadFraDto(SoknadBackendDto dto) {
        boolean aleneomsorgForBarnet = dto.getOppgittRettighet() != null && dto.getOppgittRettighet().isAleneomsorgForBarnet();
        return new Søknad(dto.getMottattDato(), dto.getSoknadsdato(), new OppgittRettighet(aleneomsorgForBarnet));
    }
}
