package no.nav.foreldrepenger.melding.integrasjoner.opprettJournalpost.dto;

import com.fasterxml.jackson.annotation.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class AvsenderMottaker {
    @JsonProperty("id")
    private String id;
    @JsonProperty("navn")
    private String navn;
    @JsonProperty("idType")
    private AvsenderMottakerIdType idType;

    @JsonCreator
    public AvsenderMottaker(@JsonProperty("id")String id,
                            @JsonProperty("navn")String navn,
                            @JsonProperty("idType")AvsenderMottakerIdType idType) {
       this.id = id;
       this.navn = navn;
       this.idType = idType;
   }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AvsenderMottakerIdType getIdType() {
        return idType;
    }

    public String getNavn() {
        return navn;
    }

    @Override
    public String toString() {
        return "AvsenderMottaker{" +
                "idType=" + idType +
                "navn" + navn +
                '}';
    }
}
