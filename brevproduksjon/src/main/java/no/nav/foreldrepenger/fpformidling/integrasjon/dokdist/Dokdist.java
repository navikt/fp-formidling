package no.nav.foreldrepenger.fpformidling.integrasjon.dokdist;

import javax.validation.constraints.NotNull;

import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.dto.DistribuerJournalpostRequest;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.dto.Distribusjonstype;
import no.nav.foreldrepenger.fpformidling.typer.JournalpostId;

public interface Dokdist {

    Resultat distribuerJournalpost(DistribuerJournalpostRequest dto);

    enum Resultat {OK, MANGLER_ADRESSE }
}
