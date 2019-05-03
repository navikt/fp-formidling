package no.nav.foreldrepenger.melding.dtomapper;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpsak.dto.behandling.innsyn.InnsynDokumentDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.innsyn.InnsynsbehandlingDto;
import no.nav.foreldrepenger.melding.behandling.innsyn.Innsyn;
import no.nav.foreldrepenger.melding.behandling.innsyn.InnsynDokument;
import no.nav.foreldrepenger.melding.behandling.innsyn.InnsynResultatType;
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
        return new Innsyn(kodeverkRepository.finn(InnsynResultatType.class, dto.getInnsynResultatType().getKode()),
                mapInnsyndokumenterFraDto(dto.getDokumenter()));
    }

    private List<InnsynDokument> mapInnsyndokumenterFraDto(List<InnsynDokumentDto> dokumenter) {
        ArrayList<InnsynDokument> dokumentliste = new ArrayList<>();
        dokumenter.stream().filter(InnsynDokumentDto::isFikkInnsyn).map(this::mapDokumentFraDto).forEach(dokumentliste::add);
        return dokumentliste;
    }

    private InnsynDokument mapDokumentFraDto(InnsynDokumentDto dto) {
        return new InnsynDokument(dto.getJournalpostId(), dto.getDokumentId());
    }
}
