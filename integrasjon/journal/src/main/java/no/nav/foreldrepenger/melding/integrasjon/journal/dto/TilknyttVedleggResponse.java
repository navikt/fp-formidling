package no.nav.foreldrepenger.melding.integrasjon.journal.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class TilknyttVedleggResponse {
    @JsonProperty("feiledeDokumenter")
    List<FeiletDokument> feiledeDokumenter;

    public TilknyttVedleggResponse (@JsonProperty("feiledeDokumenter")List<FeiletDokument> feiledeDokumenter) {
        this.feiledeDokumenter = feiledeDokumenter;
    }

    public List<FeiletDokument> getFeiledeDokumenter() { return feiledeDokumenter; }

    @Override
    public String toString() {
        return "TilknyttVedleggResponse{" +
                "feiledeDokumenter=" + feiledeDokumenter +
                '}';
    }
}
