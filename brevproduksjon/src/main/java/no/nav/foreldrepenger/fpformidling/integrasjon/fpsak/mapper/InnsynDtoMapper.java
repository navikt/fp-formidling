package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper;

import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.fpformidling.behandling.innsyn.Innsyn;
import no.nav.foreldrepenger.fpformidling.behandling.innsyn.InnsynDokument;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling.innsyn.InnsynDokumentDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling.innsyn.InnsynsbehandlingDto;
import no.nav.foreldrepenger.fpformidling.typer.JournalpostId;

public class InnsynDtoMapper {

    public static Innsyn mapInnsynFraDto(InnsynsbehandlingDto dto) {
        return new Innsyn(dto.getInnsynResultatType(),
                mapInnsyndokumenterFraDto(dto.getDokumenter()));
    }

    private static List<InnsynDokument> mapInnsyndokumenterFraDto(List<InnsynDokumentDto> dokumenter) {
        var dokumentliste = new ArrayList<InnsynDokument>();
        dokumenter.stream().filter(InnsynDokumentDto::isFikkInnsyn).map(InnsynDtoMapper::mapDokumentFraDto).forEach(dokumentliste::add);
        return dokumentliste;
    }

    private static InnsynDokument mapDokumentFraDto(InnsynDokumentDto dto) {
        return new InnsynDokument(new JournalpostId(dto.getJournalpostId()), dto.getDokumentId());
    }
}
