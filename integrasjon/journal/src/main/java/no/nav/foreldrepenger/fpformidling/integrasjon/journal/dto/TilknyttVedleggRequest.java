package no.nav.foreldrepenger.fpformidling.integrasjon.journal.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class TilknyttVedleggRequest {
    @JsonProperty("dokument")
    private List<DokumentTilknytt> dokument;

    public TilknyttVedleggRequest(@JsonProperty("dokument") List<DokumentTilknytt> dokument) {
        this.dokument = dokument;
    }

    public List<DokumentTilknytt> getDokument() {
        return dokument;
    }

    @Override
    public String toString() {
        return "TilknyttVedleggRequest{" +
                "dokument=" + dokument +
                '}';
    }
}
