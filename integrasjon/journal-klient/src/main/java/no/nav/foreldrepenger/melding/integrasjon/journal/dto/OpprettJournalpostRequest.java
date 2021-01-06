package no.nav.foreldrepenger.melding.integrasjon.journal.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@SuppressWarnings("java:S107")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class OpprettJournalpostRequest {
    @JsonProperty("journalposttype")
    private String journalpostType = "UTGAAENDE";
    @JsonProperty("avsenderMottaker")
    private AvsenderMottaker avsenderMottaker;
    @JsonProperty("bruker")
    private Bruker bruker;
    @JsonProperty("tema")
    private String tema;
    @JsonProperty("behandlingstema")
    private String behandlingstema;
    @JsonProperty("tittel")
    private String tittel;
    @JsonProperty("kanal")
    private String kanal = null;
    @JsonProperty("journalfoerendeEnhet")
    private String journalfoerendeEnhet;
    @JsonProperty("eksternReferanseId")
    private String eksternReferanseId = null;
    @JsonProperty("sak")
    private Sak sak;
    @JsonProperty("dokumenter")
    private List<DokumentOpprettRequest> dokumenter;

    public OpprettJournalpostRequest(@JsonProperty("avsenderMottaker")AvsenderMottaker avsenderMottaker,
                                     @JsonProperty("bruker")Bruker bruker,
                                     @JsonProperty("tema")String tema,
                                     @JsonProperty("behandlingstema")String behandlingstema,
                                     @JsonProperty("tittel")String tittel,
                                     @JsonProperty("journalfoerendeEnhet")String journalfoerendeEnhet,
                                     @JsonProperty("sak")Sak sak,
                                     @JsonProperty("dokumenter")List<DokumentOpprettRequest> dokumenter) {
        this.avsenderMottaker = avsenderMottaker;
        this.bruker = bruker;
        this.tema = tema;
        this.behandlingstema = behandlingstema;
        this.tittel = tittel;
        this.journalfoerendeEnhet = journalfoerendeEnhet;
        this.sak = sak;
        this.dokumenter = dokumenter;
    }

    public String getTittel() {
        return tittel;
    }

    public String getKanal() {
        return kanal;
    }

    public String getTema() {
        return tema;
    }

    public String getBehandlingstema() {
        return behandlingstema;
    }

    public String getJournalfoerendeEnhet() {
        return journalfoerendeEnhet;
    }

    public String getEksternReferanseId() {
        return eksternReferanseId;
    }

    public Sak getSak() {
        return sak;
    }

    public Bruker getBruker() {
        return bruker;
    }

    public AvsenderMottaker getAvsenderMottaker() {
        return avsenderMottaker;
    }

    public List<DokumentOpprettRequest> getDokumenter() {
        return dokumenter;
    }

    @Override
    public String toString() {
        return "OpprettJournalpostRequest{" +
                "journalpostType='" + journalpostType + '\'' +
                ", avsenderMottaker=" + avsenderMottaker +
                ", bruker=" + bruker +
                ", tema='" + tema + '\'' +
                ", behandlingstema='" + behandlingstema + '\'' +
                "  tittel='" + tittel + '\'' +
                ", kanal='" + kanal + '\'' +
                ", journalfoerendeEnhet='" + journalfoerendeEnhet + '\'' +
                ", eksternReferanseId='" + eksternReferanseId + '\'' +
                ", sak=" + sak +
                ", dokumenter=" + dokumenter +
                '}';
    }
}
