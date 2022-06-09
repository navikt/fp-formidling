package no.nav.foreldrepenger.fpformidling.integrasjon.dokdist;

import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.dto.Distribusjonstype;
import no.nav.foreldrepenger.fpformidling.typer.JournalpostId;

public interface Dokdist {

    void distribuerJournalpost(JournalpostId journalpostId, String bestillingId, Distribusjonstype distribusjonstype);

}
