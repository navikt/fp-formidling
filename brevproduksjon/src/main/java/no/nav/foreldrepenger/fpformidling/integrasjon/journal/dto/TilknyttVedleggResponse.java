package no.nav.foreldrepenger.fpformidling.integrasjon.journal.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TilknyttVedleggResponse(List<FeiletDokument> feiledeDokumenter) {

    public static record FeiletDokument(String kildeJournalpostId, String dokumentInfoId, String arsakKode) {
    }
}
