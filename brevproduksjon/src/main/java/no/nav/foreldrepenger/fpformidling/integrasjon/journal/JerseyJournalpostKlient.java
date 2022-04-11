package no.nav.foreldrepenger.fpformidling.integrasjon.journal;

import static javax.ws.rs.client.Entity.json;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

import java.net.URI;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.fpformidling.integrasjon.journal.dto.FerdigstillJournalpostRequest;
import no.nav.foreldrepenger.fpformidling.integrasjon.journal.dto.OpprettJournalpostRequest;
import no.nav.foreldrepenger.fpformidling.integrasjon.journal.dto.OpprettJournalpostResponse;
import no.nav.foreldrepenger.fpformidling.integrasjon.journal.dto.TilknyttVedleggRequest;
import no.nav.foreldrepenger.fpformidling.integrasjon.journal.dto.TilknyttVedleggResponse;
import no.nav.foreldrepenger.fpformidling.typer.JournalpostId;
import no.nav.foreldrepenger.konfig.KonfigVerdi;
import no.nav.vedtak.felles.integrasjon.rest.jersey.AbstractJerseyOidcRestClient;
import no.nav.vedtak.felles.integrasjon.rest.jersey.Jersey;

@Jersey
@Dependent
public class JerseyJournalpostKlient extends AbstractJerseyOidcRestClient implements Journalpost {
    private static final Logger LOG = LoggerFactory.getLogger(JerseyJournalpostKlient.class);

    private final URI dokarkivUrl;
    private final URI dokarkivProxyUrl;

    @Inject
    public JerseyJournalpostKlient(@KonfigVerdi(value = "journalpost.rest.v1.url") URI dokarkivUrl,
            @KonfigVerdi(value = "journalpost.rest.proxy.v1.url") URI dokarkivProxyUrl) {
        this.dokarkivUrl = dokarkivUrl;
        this.dokarkivProxyUrl = dokarkivProxyUrl;
    }

    @Override
    public OpprettJournalpostResponse opprettJournalpost(OpprettJournalpostRequest req, boolean ferdigstill) {
        LOG.trace("Oppretter journalpost {}", req);
        var res = invoke(client.target(dokarkivUrl)
                .queryParam("forsoekFerdigstill", ferdigstill)
                .request(APPLICATION_JSON_TYPE)
                .buildPost(json(req)), OpprettJournalpostResponse.class);
        LOG.info("Oppretter journalpost {} OK", res.getJournalpostId());
        return res;
    }

    @Override
    public void ferdigstillJournalpost(JournalpostId id) {
        LOG.trace("Ferdigstiller journalpost {}", id);
        patch(client.target(dokarkivUrl)
                .path("/{id}/ferdigstill")
                .resolveTemplate("id", id.getVerdi())
                .getUri(), new FerdigstillJournalpostRequest("9999"));
        LOG.info("Ferdigstilt journalpost OK");
    }

    @Override
    public void tilknyttVedlegg(TilknyttVedleggRequest req, JournalpostId til) {
        LOG.trace("Tilknytter vedlegg {}", til.getVerdi());
        var res = invoke(client.target(dokarkivProxyUrl)
                .path("/{id}/tilknyttVedlegg")
                .resolveTemplate("id", til.getVerdi())
                .request(APPLICATION_JSON_TYPE)
                .buildPut(json(req)), TilknyttVedleggResponse.class);
        if (!res.getFeiledeDokumenter().isEmpty()) {
            throw new IllegalStateException("Følgende vedlegg feilet " + res.getFeiledeDokumenter() + " for journalpost " + til);
        }
        LOG.info("Vedlegg tilknyttet {} OK", til.getVerdi());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [dokarkivUrl=" + dokarkivUrl + ", dokarkivProxyUrl=" + dokarkivProxyUrl + "]";
    }

}
