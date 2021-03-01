package no.nav.foreldrepenger.melding.dtomapper;

import no.nav.foreldrepenger.fpsak.dto.fagsak.FagsakBackendDto;
import no.nav.foreldrepenger.melding.fagsak.FagsakBackend;
import no.nav.foreldrepenger.melding.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.melding.typer.AktørId;

public class FagsakDtoMapper {

    public FagsakDtoMapper() {
    }

    public static FagsakBackend mapFagsakBackendFraDto(FagsakBackendDto fagsakDto) {
        return FagsakBackend.ny()
                .medSaksnummer(fagsakDto.getSaksnummerString())
                .medBrukerRolle(RelasjonsRolleType.fraKode(fagsakDto.getRelasjonsRolleType().getKode()))
                .medAktørId(new AktørId(fagsakDto.getAktoerId()))
                .build();
    }

}
