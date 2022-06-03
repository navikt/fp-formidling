package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper;

import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.soknad.SoknadBackendDto;
import no.nav.foreldrepenger.fpformidling.søknad.Søknad;

public class SøknadDtoMapper {
    public static Søknad mapSøknadFraDto(SoknadBackendDto dto) {
        return new Søknad(dto.mottattDato(), dto.oppgittAleneomsorg());
    }
}
