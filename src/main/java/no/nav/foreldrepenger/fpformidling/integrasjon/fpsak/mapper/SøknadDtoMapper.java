package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper;

import no.nav.foreldrepenger.fpformidling.domene.søknad.Søknad;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.soknad.SoknadBackendDto;

public class SøknadDtoMapper {

    private SøknadDtoMapper() {
    }

    public static Søknad mapSøknadFraDto(SoknadBackendDto dto) {
        return new Søknad(dto.mottattDato(), dto.oppgittAleneomsorg());
    }
}
