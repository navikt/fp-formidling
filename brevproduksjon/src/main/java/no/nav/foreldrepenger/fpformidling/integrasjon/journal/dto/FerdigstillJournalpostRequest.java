package no.nav.foreldrepenger.fpformidling.integrasjon.journal.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FerdigstillJournalpostRequest {
    @JsonProperty("journalfoerendeEnhet")
    private String journalfoerendeEnhet;

    public FerdigstillJournalpostRequest(@JsonProperty("journalfoerendeEnhet") String journalfoerendeEnhet) {
        this.journalfoerendeEnhet = journalfoerendeEnhet;
    }

    public String getJournalfoerendeEnhet() {
        return journalfoerendeEnhet;
    }

    @Override
    public String toString() {
        return "FerdigstillJournalpostRequest{" +
                "journalfoerendeEnhet=" + journalfoerendeEnhet +
                '}';
    }
}
