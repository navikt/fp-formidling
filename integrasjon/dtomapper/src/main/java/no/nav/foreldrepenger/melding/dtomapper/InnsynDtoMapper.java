package no.nav.foreldrepenger.melding.dtomapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpsak.dto.behandling.innsyn.InnsynsbehandlingDto;
import no.nav.foreldrepenger.melding.behandling.Innsyn;
import no.nav.foreldrepenger.melding.behandling.InnsynResultatType;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;

@ApplicationScoped
public class InnsynDtoMapper {

    private KodeverkRepository kodeverkRepository;

    public InnsynDtoMapper() {
        //CDI
    }

    @Inject
    public InnsynDtoMapper(KodeverkRepository kodeverkRepository) {
        this.kodeverkRepository = kodeverkRepository;
    }

    public Innsyn mapInnsynFraDto(InnsynsbehandlingDto dto) {
        return new Innsyn(kodeverkRepository.finn(InnsynResultatType.class, dto.getInnsynResultatType().getKode()));
    }
}
