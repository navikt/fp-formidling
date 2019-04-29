package no.nav.foreldrepenger.melding.dtomapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpsak.dto.fagsak.FagsakDto;
import no.nav.foreldrepenger.melding.fagsak.Fagsak;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.foreldrepenger.melding.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.melding.typer.PersonIdent;
import no.nav.foreldrepenger.tps.TpsTjeneste;

@ApplicationScoped
class FagsakDtoMapper {

    private KodeverkRepository kodeverkRepository;
    private TpsTjeneste tpsTjeneste;

    @Inject
    public FagsakDtoMapper(KodeverkRepository kodeverkRepository,
                           TpsTjeneste tpsTjeneste) {
        this.kodeverkRepository = kodeverkRepository;
        this.tpsTjeneste = tpsTjeneste;
    }

    public FagsakDtoMapper() {
    }

    public Fagsak mapFagsakFraDto(FagsakDto fagsakDto) {
        return Fagsak.ny()
                .medSaksnummer(String.valueOf(fagsakDto.getSaksnummer()))
                .medBrukerRolle(kodeverkRepository.finn(RelasjonsRolleType.class, fagsakDto.getRelasjonsRolleType().getKode()))
                .medPersoninfo(tpsTjeneste.hentBrukerForFnr(PersonIdent.fra(fagsakDto.getPerson().getPersonnummer())).orElseThrow(IllegalStateException::new))
                .build();
    }

}
