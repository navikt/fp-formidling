package no.nav.foreldrepenger.melding.dtomapper;

import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.fpsak.dto.behandling.innsyn.InnsynDokumentDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.innsyn.InnsynsbehandlingDto;
import no.nav.foreldrepenger.melding.behandling.innsyn.Innsyn;
import no.nav.foreldrepenger.melding.behandling.innsyn.InnsynDokument;
import no.nav.foreldrepenger.melding.behandling.innsyn.InnsynResultatType;
import no.nav.foreldrepenger.melding.typer.JournalpostId;

public class InnsynDtoMapper {

    public static Innsyn mapInnsynFraDto(InnsynsbehandlingDto dto) {
        return new Innsyn(InnsynResultatType.fraKode(dto.getInnsynResultatType().getKode()),
                mapInnsyndokumenterFraDto(dto.getDokumenter()));
    }

    private static List<InnsynDokument> mapInnsyndokumenterFraDto(List<InnsynDokumentDto> dokumenter) {
        ArrayList<InnsynDokument> dokumentliste = new ArrayList<>();
        dokumenter.stream().filter(InnsynDokumentDto::isFikkInnsyn).map(InnsynDtoMapper::mapDokumentFraDto).forEach(dokumentliste::add);
        return dokumentliste;
    }

    private static InnsynDokument mapDokumentFraDto(InnsynDokumentDto dto) {
        return new InnsynDokument(new JournalpostId(dto.getJournalpostId()), dto.getDokumentId());
    }
}
