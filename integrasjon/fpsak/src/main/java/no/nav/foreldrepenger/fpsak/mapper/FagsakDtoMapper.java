package no.nav.foreldrepenger.fpsak.mapper;

import no.nav.foreldrepenger.fpformidling.fagsak.FagsakBackend;
import no.nav.foreldrepenger.fpformidling.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.fpformidling.typer.AktørId;
import no.nav.foreldrepenger.fpsak.dto.fagsak.FagsakDto;

public class FagsakDtoMapper {

    public FagsakDtoMapper() {
    }

    public static FagsakBackend mapFagsakBackendFraDto(FagsakDto fagsakDto) {
        return FagsakBackend.ny()
                .medSaksnummer(fagsakDto.getSaksnummer())
                .medBrukerRolle(RelasjonsRolleType.fraKode(fagsakDto.getRelasjonsRolleType().getKode()))
                .medAktørId(new AktørId(fagsakDto.getAktoerId()))
                .build();
    }

}
