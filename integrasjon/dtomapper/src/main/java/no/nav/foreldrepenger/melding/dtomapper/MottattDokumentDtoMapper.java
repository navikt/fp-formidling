package no.nav.foreldrepenger.melding.dtomapper;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import no.nav.foreldrepenger.fpsak.dto.behandling.MottattDokumentDto;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentKategori;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentTypeId;
import no.nav.foreldrepenger.melding.mottattdokument.MottattDokument;

@ApplicationScoped
public class MottattDokumentDtoMapper {

    public MottattDokumentDtoMapper() {
        //CDI
    }

    public List<MottattDokument> mapMottattedokumenterFraDto(List<MottattDokumentDto> dtoListe) {
        return dtoListe.stream().map(this::mapMottattDokumentFraDto).collect(Collectors.toList());
    }

    private MottattDokument mapMottattDokumentFraDto(MottattDokumentDto dto) {
        return new MottattDokument(dto.getMottattDato(), new DokumentTypeId(dto.getDokumentTypeId().getKode()),
                DokumentKategori.fraKode(dto.getDokumentKategori().getKode()));
    }

}
