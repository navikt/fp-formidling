package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper;

import no.nav.foreldrepenger.fpformidling.fagsak.FagsakBackend;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.fagsak.FagsakDto;
import no.nav.foreldrepenger.fpformidling.typer.AktørId;

public class FagsakDtoMapper {

    public FagsakDtoMapper() {
    }

    public static FagsakBackend mapFagsakBackendFraDto(FagsakDto fagsakDto) {
        return FagsakBackend.ny()
                .medSaksnummer(fagsakDto.saksnummer())
                .medBrukerRolle(fagsakDto.relasjonsRolleType())
                .medAktørId(new AktørId(fagsakDto.aktoerId()))
                .medDekningsgrad(fagsakDto.dekningsgrad())
                .build();
    }

}
