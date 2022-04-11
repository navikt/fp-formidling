package no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Fagsystem;
import no.nav.foreldrepenger.fpformidling.typer.JournalpostId;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class DistribuerJournalpostRequest {
    @JsonProperty("journalpostId")
    private String journalpostId;
    @JsonProperty("batchId")
    private String batchId;
    @JsonProperty("bestillendeFagsystem")
    private String bestillendeFagsystem;
    @JsonProperty("dokumentProdApp")
    private String dokumentProdApp;

    @JsonCreator
    public DistribuerJournalpostRequest(@JsonProperty("journalpostId") String journalpostId,
            @JsonProperty("batchId") String batchId,
            @JsonProperty("bestillendeFagsystem") String bestillendeFagsystem,
            @JsonProperty("dokumentProdApp") String dokumentProdApp) {
        this.journalpostId = journalpostId;
        this.batchId = batchId;
        this.bestillendeFagsystem = bestillendeFagsystem;
        this.dokumentProdApp = dokumentProdApp;
    }

    public DistribuerJournalpostRequest(JournalpostId id, Fagsystem fagsystem, String bestillingId) {
        this(id.getVerdi(), bestillingId, fagsystem.getOffisiellKode(), fagsystem.getKode());
    }

    public String getJournalpostId() {
        return journalpostId;
    }

    public String getBatchId() {
        return batchId;
    }

    public String getBestillendeFagsystem() {
        return bestillendeFagsystem;
    }

    public String getDokumentProdApp() {
        return dokumentProdApp;
    }

    @Override
    public String toString() {
        return "DistribuerJournalpostRequest{" +
                "journalpostId=" + journalpostId +
                ", batchId=" + batchId +
                ", bestillendeFagsystem=" + bestillendeFagsystem +
                ", dokumentProdApp=" + dokumentProdApp +
                '}';
    }
}
