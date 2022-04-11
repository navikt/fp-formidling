package no.nav.foreldrepenger.fpformidling.integrasjon.journal.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class DokumentOpprettResponse {
    @JsonProperty("dokumentInfoId")
    private String dokumentInfoId;

    public DokumentOpprettResponse(@JsonProperty("dokumentInfoId")String dokumentInfoId) {
        this.dokumentInfoId = dokumentInfoId;
    }

    public String getDokumentInfoId() {
        return dokumentInfoId;
    }

    @Override
    public String toString() {
        return "DokumentOpprettResponse{" +
                "dokumentInfoId='" + dokumentInfoId + '\'' +
                '}';
    }
}
