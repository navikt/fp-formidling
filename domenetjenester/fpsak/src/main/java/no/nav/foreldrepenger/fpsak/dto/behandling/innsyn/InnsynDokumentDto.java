package no.nav.foreldrepenger.fpsak.dto.behandling.innsyn;

class InnsynDokumentDto {

    private boolean fikkInnsyn;

    private String journalpostId;

    private String dokumentId;

    public boolean isFikkInnsyn() {
        return fikkInnsyn;
    }

    public void setFikkInnsyn(boolean fikkInnsyn) {
        this.fikkInnsyn = fikkInnsyn;
    }

    public String getJournalpostId() {
        return journalpostId;
    }

    public void setJournalpostId(String journalpostId) {
        this.journalpostId = journalpostId;
    }

    public String getDokumentId() {
        return dokumentId;
    }

    public void setDokumentId(String dokumentId) {
        this.dokumentId = dokumentId;
    }
}
