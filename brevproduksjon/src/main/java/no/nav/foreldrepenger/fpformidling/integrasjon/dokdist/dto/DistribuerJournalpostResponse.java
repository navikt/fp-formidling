package no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class DistribuerJournalpostResponse {
    @JsonProperty("bestillingsId")
    private String bestillingsId;

    public DistribuerJournalpostResponse(@JsonProperty("bestillingsId") String bestillingsId) {
        this.bestillingsId = bestillingsId;
    }

    public String getBestillingsId() {
        return bestillingsId;
    }

    @Override
    public String toString() {
        return "DistribuerJournalpostResponse{" +
                "bestillingsId=" + bestillingsId +
                '}';
    }
}