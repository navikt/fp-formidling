package no.nav.foreldrepenger.melding.dtomapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpsak.dto.fagsak.FagsakDto;
import no.nav.foreldrepenger.melding.aktør.Personinfo;
import no.nav.foreldrepenger.melding.fagsak.Fagsak;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.foreldrepenger.melding.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.melding.typer.PersonIdent;

@ApplicationScoped
class FagsakDtoMapper {

    private KodeverkRepository kodeverkRepository;

    @Inject
    public FagsakDtoMapper(KodeverkRepository kodeverkRepository) {
        this.kodeverkRepository = kodeverkRepository;
    }

    public FagsakDtoMapper() {
    }

    public Fagsak mapFagsakFraDto(FagsakDto fagsakDto) {
        return Fagsak.ny()
                .medSaksnummer(String.valueOf(fagsakDto.getSaksnummer()))
                .medBrukerRolle(kodeverkRepository.finn(RelasjonsRolleType.class, fagsakDto.getRelasjonsRolleType().getKode()))
                .medPersonInfo(byggPersonInfo(fagsakDto))
                .build();
    }

    private Personinfo byggPersonInfo(FagsakDto fagsakDto) {
        return new Personinfo.Builder()
                .medPersonIdent(PersonIdent.fra(fagsakDto.getPerson().getPersonnummer()))
                .build();
    }

}
