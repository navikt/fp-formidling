package no.nav.foreldrepenger.fpformidling.integrasjon.dokdist;

import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.dto.DistribuerJournalpostRequest;

/**
 * Brukes til distribuering av journalførte dokumenter.
 * Swagger dokumentasjon: <a href="https://dokdistfordeling-q1.dev.adeo.no/swagger-ui/index.html">Swagger API dokumentasjon i dev.</a>
 */
public interface Dokdist {

    Resultat distribuerJournalpost(DistribuerJournalpostRequest dto);

    enum Resultat {OK, MANGLER_ADRESSE }
}
