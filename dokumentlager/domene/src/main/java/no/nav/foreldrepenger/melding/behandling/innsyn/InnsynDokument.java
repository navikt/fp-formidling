package no.nav.foreldrepenger.melding.behandling.innsyn;

import no.nav.foreldrepenger.melding.typer.JournalpostId;

public class InnsynDokument {

    private JournalpostId journalpostId;

    private String dokumentId;


    public InnsynDokument(JournalpostId journalpostId, String dokumentId) {
        this.journalpostId = journalpostId;
        this.dokumentId = dokumentId;
    }

    public JournalpostId getJournalpostId() {
        return journalpostId;
    }

    public String getDokumentId() {
        return dokumentId;
    }
}
