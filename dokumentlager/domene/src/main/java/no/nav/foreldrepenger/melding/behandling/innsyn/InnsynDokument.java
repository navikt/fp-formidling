package no.nav.foreldrepenger.melding.behandling.innsyn;

public class InnsynDokument {

    private String journalpostId;

    private String dokumentId;


    public InnsynDokument(String journalpostId, String dokumentId) {
        this.journalpostId = journalpostId;
        this.dokumentId = dokumentId;
    }

    public String getJournalpostId() {
        return journalpostId;
    }

    public String getDokumentId() {
        return dokumentId;
    }
}
