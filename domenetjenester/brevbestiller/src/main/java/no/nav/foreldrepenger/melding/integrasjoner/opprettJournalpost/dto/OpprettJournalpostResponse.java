package no.nav.foreldrepenger.melding.integrasjoner.opprettJournalpost.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class OpprettJournalpostResponse {
    @JsonProperty("journalpostId")
    private String journalpostId;
    @JsonProperty("journalpostferdigstilt")
    private boolean journalpostferdigstilt;
    @JsonProperty("dokumenter")
    private List<DokumentOpprettResponse> dokumenter;

    @JsonCreator
    public OpprettJournalpostResponse(@JsonProperty("journalpostId") String journalpostId,
                                      @JsonProperty("journalpostferdigstilt") boolean journalpostferdigstilt,
                                      @JsonProperty("dokumenter") List<DokumentOpprettResponse> dokumenter) {
        this.journalpostId = journalpostId;
        this.journalpostferdigstilt = journalpostferdigstilt;
        this.dokumenter = dokumenter;
    }

    public String getJournalpostId() {
        return journalpostId;
    }

    public boolean erFerdigstilt() {
        return journalpostferdigstilt;
    }

    public List<DokumentOpprettResponse> getDokumenter() {
        return dokumenter;
    }

    @Override
    public String toString() {
        return "OpprettJournalpostResponse{" +
                "journalpostId='" + journalpostId + '\'' +
                ", journalpostferdigstilt='" + journalpostferdigstilt + '\'' +
                ", dokumenter=" + dokumenter +
                '}';
    }
// json anotering - kan man angi for alle et sted, fant dette forslaget:
// ObjectMapper mapper = new ObjectMapper();
//mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
//mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
}
