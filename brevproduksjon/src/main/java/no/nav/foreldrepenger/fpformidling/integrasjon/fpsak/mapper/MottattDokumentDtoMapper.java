package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper;

import java.util.List;

import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentTypeId;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling.MottattDokumentDto;
import no.nav.foreldrepenger.fpformidling.mottattdokument.MottattDokument;

public class MottattDokumentDtoMapper {

    public static List<MottattDokument> mapMottattedokumenterFraDto(List<MottattDokumentDto> dtoListe) {
        return dtoListe.stream().map(MottattDokumentDtoMapper::mapMottattDokumentFraDto).toList();
    }

    private static MottattDokument mapMottattDokumentFraDto(MottattDokumentDto dto) {
        return new MottattDokument(dto.mottattDato(), new DokumentTypeId(dto.dokumentTypeId()),
                dto.dokumentKategori());
    }

}
