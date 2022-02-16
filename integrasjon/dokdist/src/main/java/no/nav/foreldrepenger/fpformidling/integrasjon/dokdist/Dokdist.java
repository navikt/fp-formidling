package no.nav.foreldrepenger.fpformidling.integrasjon.dokdist;

import java.util.UUID;

import no.nav.foreldrepenger.fpformidling.typer.JournalpostId;

public interface Dokdist {

    void distribuerJournalpost(JournalpostId journalpostId, UUID bestillingUuid);

}
