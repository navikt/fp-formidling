package no.nav.foreldrepenger.melding.integrasjoner.opprettJournalpost.dto;

import com.fasterxml.jackson.annotation.*;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)

public class OpprettetJournalpostResponse {
    @JsonProperty("journalpostId")
    private String journalpostId;
    @JsonProperty("journalpostferdigstilt")
    private boolean journalpostferdigstilt;
    @JsonProperty("dokumenter")
    private List<Dokument> dokumenter;

    @JsonCreator
    public OpprettetJournalpostResponse(@JsonProperty("journalpostId") String journalpostId,
                                        @JsonProperty("journalpostferdigstilt") boolean journalpostferdigstilt,
                                        @JsonProperty("dokumenter") List<Dokument> dokumenter) {
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

    public List<Dokument> getDokumenter() {
        return dokumenter;
    }

    @Override
    public String toString() {
        return "OpprettetJournalpostResponse{" +
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
