package no.nav.foreldrepenger.fpformidling.integrasjon.journal.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FeiletDokument {
    @JsonProperty("kildeJournalpostId")
    private String kildeJournalpostId;
    @JsonProperty("dokumentInfoId")
    private String dokumentInfoId;
    @JsonProperty("arsakKode")
    private String arsakKode;

    public FeiletDokument(@JsonProperty("kildeJournalpostId")String kildeJournalpostId,
                          @JsonProperty("dokumentInfoId")String dokumentInfoId,
                          @JsonProperty("arsakKode")String arsakKode) {
        this.kildeJournalpostId = kildeJournalpostId;
        this.dokumentInfoId = dokumentInfoId;
        this.arsakKode = arsakKode;
    }

    public String getKildeJournalpostId() { return kildeJournalpostId; }
    public String getDokumentInfoId() {
        return dokumentInfoId;
    }
    public String getArsakKode() {
        return arsakKode;
    }

    @Override
    public String toString() {
        return "FeileDokument{" +
                "kildeJournalpostId='" + kildeJournalpostId + '\'' +
                ", dokumentInfoId='" + dokumentInfoId + '\'' +
                ", arsakKode='" + arsakKode + '\'' +
                '}';
    }
}
