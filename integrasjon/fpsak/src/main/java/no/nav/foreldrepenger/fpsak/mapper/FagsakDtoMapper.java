package no.nav.foreldrepenger.fpsak.mapper;

import no.nav.foreldrepenger.fpsak.dto.fagsak.FagsakDto;
import no.nav.foreldrepenger.melding.fagsak.FagsakBackend;
import no.nav.foreldrepenger.melding.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.melding.typer.AktørId;

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
