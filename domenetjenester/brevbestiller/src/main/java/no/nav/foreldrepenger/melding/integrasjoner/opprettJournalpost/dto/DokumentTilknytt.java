package no.nav.foreldrepenger.melding.integrasjoner.opprettJournalpost.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class DokumentTilknytt {
    @JsonProperty("kildeJournalpostId")
    private String kildeJournalpostId;
    @JsonProperty("dokumentInfoId")
    private String dokumentInfoId;

    public DokumentTilknytt(@JsonProperty("kildeJournalpostId")String kildeJournalpostId,
                            @JsonProperty("dokumentInfoId")String dokumentInfoId) {
        this.kildeJournalpostId = kildeJournalpostId;
        this.dokumentInfoId = dokumentInfoId;
    }

    public String getKildeJournalpostId() {
        return kildeJournalpostId;
    }

    public String getDokumentInfoId() {
        return dokumentInfoId;
    }

    @Override
    public String toString() {
        return "DokumentTilknytt{" +
                ", kildeJournalpostId='" + kildeJournalpostId + '\'' +
                ", dokumentInfoId='" + dokumentInfoId + '\'' +
                '}';
    }
}
