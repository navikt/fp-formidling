package no.nav.foreldrepenger.melding.integrasjon.dokdist;

import no.nav.foreldrepenger.melding.typer.JournalpostId;

public interface Dokdist {

    void distribuerJournalpost(JournalpostId journalpostId);

}