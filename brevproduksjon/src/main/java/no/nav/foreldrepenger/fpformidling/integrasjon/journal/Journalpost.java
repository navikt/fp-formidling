package no.nav.foreldrepenger.fpformidling.integrasjon.journal;

import no.nav.foreldrepenger.fpformidling.integrasjon.journal.dto.OpprettJournalpostRequest;
import no.nav.foreldrepenger.fpformidling.integrasjon.journal.dto.OpprettJournalpostResponse;
import no.nav.foreldrepenger.fpformidling.integrasjon.journal.dto.TilknyttVedleggRequest;
import no.nav.foreldrepenger.fpformidling.typer.JournalpostId;

public interface Journalpost {

    OpprettJournalpostResponse opprettJournalpost(OpprettJournalpostRequest request, boolean ferdigstill);

    void ferdigstillJournalpost(JournalpostId journalpostId);

    void tilknyttVedlegg(TilknyttVedleggRequest request, JournalpostId journalpostIdTil);

}
