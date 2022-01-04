package no.nav.foreldrepenger.fpsak.mapper;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentKategori;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentTypeId;
import no.nav.foreldrepenger.fpformidling.mottattdokument.MottattDokument;
import no.nav.foreldrepenger.fpsak.dto.behandling.MottattDokumentDto;

public class MottattDokumentDtoMapper {

    public static List<MottattDokument> mapMottattedokumenterFraDto(List<MottattDokumentDto> dtoListe) {
        return dtoListe.stream().map(MottattDokumentDtoMapper::mapMottattDokumentFraDto).collect(Collectors.toList());
    }

    private static MottattDokument mapMottattDokumentFraDto(MottattDokumentDto dto) {
        return new MottattDokument(dto.getMottattDato(), new DokumentTypeId(dto.getDokumentTypeId().getKode()),
                DokumentKategori.fraKode(dto.getDokumentKategori().getKode()));
    }

}
