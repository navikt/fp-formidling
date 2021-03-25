package no.nav.foreldrepenger.melding.integrasjon.dokdist;

import no.nav.foreldrepenger.melding.integrasjon.dokdist.dto.DistribuerJournalpostRequest;
import no.nav.foreldrepenger.melding.integrasjon.dokdist.dto.DistribuerJournalpostResponse;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.Fagsystem;
import no.nav.foreldrepenger.melding.typer.JournalpostId;
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.felles.integrasjon.rest.jersey.AbstractJerseyOidcRestClient;
import no.nav.vedtak.felles.integrasjon.rest.jersey.Jersey;
import no.nav.vedtak.konfig.KonfigVerdi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.net.URI;
import java.util.UUID;

import static javax.ws.rs.client.Entity.json;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

@ApplicationScoped
@Jersey
public class JerseyDokdistClient extends AbstractJerseyOidcRestClient implements Dokdist {
    private static final Logger LOGGER = LoggerFactory.getLogger(DokdistRestKlient.class);
    private static final String DOKDIST_REST_BASE_URL = "dokdist_rest_base.url";
    private static final String DISTRIBUERJOURNALPOST = "/distribuerjournalpost";
    private final URI baseUri;

    @Inject
    public JerseyDokdistClient(@KonfigVerdi(DOKDIST_REST_BASE_URL) URI baseUri) {
        this.baseUri = baseUri;
    }

    @Override
    public void distribuerJournalpost(JournalpostId journalpostId) {
        try {
            LOGGER.info("Distribuerer dokument for journalpostId {}", journalpostId);
            var response = client.target(baseUri)
                    .path(DISTRIBUERJOURNALPOST)
                    .request(APPLICATION_JSON_TYPE)
                    .buildPost(json(lagRequest(journalpostId)))
                    .invoke(DistribuerJournalpostResponse.class);

            if (!response.getBestillingsId().isEmpty()) {
                LOGGER.info("Distribuert {} med bestillingsId {}", journalpostId, response);
            } else {
                throw new TekniskException("FPFORMIDLING-647352", String.format("Fikk tomt svar ved kall til dokdist for %s.", journalpostId));
            }
        } catch (Exception e) {
            LOGGER.warn("Fikk feil ved kall til dokdist for {} med feimelding: {}", journalpostId, e.getMessage());
            throw new TekniskException("FPFORMIDLING-647353", String.format("Fikk feil ved kall til dokdist for %s ", journalpostId), e);
        }
}

    private DistribuerJournalpostRequest lagRequest(JournalpostId journalpostId) {
        return new DistribuerJournalpostRequest(journalpostId.getVerdi(), UUID.randomUUID().toString(), Fagsystem.FPSAK.getOffisiellKode(),
                Fagsystem.FPSAK.getKode());
    }

}