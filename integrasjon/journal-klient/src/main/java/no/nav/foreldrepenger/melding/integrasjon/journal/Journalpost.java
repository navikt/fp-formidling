package no.nav.foreldrepenger.melding.integrasjon.journal;

import no.nav.foreldrepenger.melding.integrasjon.journal.dto.OpprettJournalpostRequest;
import no.nav.foreldrepenger.melding.integrasjon.journal.dto.OpprettJournalpostResponse;
import no.nav.foreldrepenger.melding.integrasjon.journal.dto.TilknyttVedleggRequest;
import no.nav.foreldrepenger.melding.typer.JournalpostId;

public interface Journalpost {

    OpprettJournalpostResponse opprettJournalpost(OpprettJournalpostRequest request, boolean ferdigstill);

    void ferdigstillJournalpost(JournalpostId journalpostId);

    void tilknyttVedlegg(TilknyttVedleggRequest request, JournalpostId journalpostIdTil);

}
