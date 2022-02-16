package no.nav.foreldrepenger.fpformidling.integrasjon.dokdist;

import static javax.ws.rs.client.Entity.json;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Fagsystem.FPSAK;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.client.WebTarget;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.dto.DistribuerJournalpostRequest;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.dto.DistribuerJournalpostResponse;
import no.nav.foreldrepenger.fpformidling.typer.JournalpostId;
import no.nav.foreldrepenger.konfig.KonfigVerdi;
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.felles.integrasjon.rest.jersey.AbstractJerseyOidcRestClient;
import no.nav.vedtak.felles.integrasjon.rest.jersey.Jersey;

@Dependent
@Jersey
public class JerseyDokdistKlient extends AbstractJerseyOidcRestClient implements Dokdist {
    private static final Logger LOG = LoggerFactory.getLogger(JerseyDokdistKlient.class);
    private final WebTarget target;

    @Inject
    public JerseyDokdistKlient(
            @KonfigVerdi(value = "dokdist.rest.base.url", defaultVerdi = "some sensible default") URI baseUri) {
        this.target = client
                .target(baseUri)
                .path("/distribuerjournalpost");
    }

    @Override
    public void distribuerJournalpost(JournalpostId id, UUID bestillingUuid) {
        LOG.trace("Distribuerer journalpost {} til {}", id, target.getUri());
        Optional.ofNullable(
                invoke(target
                        .request(APPLICATION_JSON_TYPE)
                        .buildPost(json(new DistribuerJournalpostRequest(id, FPSAK, bestillingUuid))), DistribuerJournalpostResponse.class))
                .ifPresentOrElse(v -> LOG.info("Distribuert {} med bestillingsId {}", id, v.getBestillingsId()),
                        () -> { throw new TekniskException("FPFORMIDLING-647352", String.format("Fikk tomt svar ved kall til dokdist for %s.", id)); });
        LOG.info("Distribuerert journalpost {} til {} OK", id, target.getUri());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [target=" + target.getUri() + "]";
    }
}
