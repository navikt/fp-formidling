package no.nav.foreldrepenger.fpformidling.integrasjon.journal.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TilknyttVedleggRequest(List<DokumentTilknytt> dokument) {

    public static record DokumentTilknytt(String kildeJournalpostId, String dokumentInfoId) {
    }
}
