package no.nav.foreldrepenger.fpformidling.integrasjon.dokdist;

import javax.validation.constraints.NotNull;

import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.dto.Distribusjonstype;
import no.nav.foreldrepenger.fpformidling.typer.JournalpostId;

public interface Dokdist {
    Resultat distribuerJournalpost(JournalpostId journalpostId, @NotNull String bestillingId, @NotNull Distribusjonstype distribusjonstype);

    enum Resultat {OK, MANGLER_ADRESSE }
}
