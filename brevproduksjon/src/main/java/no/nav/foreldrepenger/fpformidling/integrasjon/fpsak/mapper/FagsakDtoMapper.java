package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper;

import no.nav.foreldrepenger.fpformidling.fagsak.FagsakBackend;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.fagsak.FagsakDto;
import no.nav.foreldrepenger.fpformidling.typer.AktørId;

public class FagsakDtoMapper {

    public FagsakDtoMapper() {
    }

    public static FagsakBackend mapFagsakBackendFraDto(FagsakDto fagsakDto) {
        return FagsakBackend.ny()
                .medSaksnummer(fagsakDto.getSaksnummer())
                .medBrukerRolle(fagsakDto.getRelasjonsRolleType())
                .medAktørId(new AktørId(fagsakDto.getAktoerId()))
                .build();
    }

}
