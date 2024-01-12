package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper;

import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentTypeId;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling.MottattDokumentDto;
import no.nav.foreldrepenger.fpformidling.domene.mottattdokument.MottattDokument;

import java.util.List;

public class MottattDokumentDtoMapper {

    private MottattDokumentDtoMapper() {
    }

    public static List<MottattDokument> mapMottattedokumenterFraDto(List<MottattDokumentDto> dtoListe) {
        return dtoListe.stream().map(MottattDokumentDtoMapper::mapMottattDokumentFraDto).toList();
    }

    private static MottattDokument mapMottattDokumentFraDto(MottattDokumentDto dto) {
        return new MottattDokument(dto.mottattDato(), new DokumentTypeId(dto.dokumentTypeId()), dto.dokumentKategori());
    }

}
