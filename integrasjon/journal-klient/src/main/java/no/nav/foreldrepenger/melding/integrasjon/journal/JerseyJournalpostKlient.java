package no.nav.foreldrepenger.melding.integrasjon.journal;

import static javax.ws.rs.client.Entity.json;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

import java.net.URI;

import javax.enterprise.context.Dependent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.konfig.KonfigVerdi;
import no.nav.foreldrepenger.melding.integrasjon.journal.dto.FerdigstillJournalpostRequest;
import no.nav.foreldrepenger.melding.integrasjon.journal.dto.OpprettJournalpostRequest;
import no.nav.foreldrepenger.melding.integrasjon.journal.dto.OpprettJournalpostResponse;
import no.nav.foreldrepenger.melding.integrasjon.journal.dto.TilknyttVedleggRequest;
import no.nav.foreldrepenger.melding.integrasjon.journal.dto.TilknyttVedleggResponse;
import no.nav.foreldrepenger.melding.typer.JournalpostId;
import no.nav.vedtak.felles.integrasjon.rest.jersey.AbstractJerseyOidcRestClient;
import no.nav.vedtak.felles.integrasjon.rest.jersey.Jersey;

@Jersey
@Dependent
public class JerseyJournalpostKlient extends AbstractJerseyOidcRestClient implements Journalpost {
    private static final String FORSØK_FERDIGSTILL = "forsoekFerdigstill";
    private static final Logger LOG = LoggerFactory.getLogger(JerseyJournalpostKlient.class);
    private static final String TILKNYTT_VEDLEGG = "/{id}/tilknyttVedlegg";
    private static final String FERDIGSTILL = "/{id}/ferdigstill";

    private static final String DEFAULT_URI = "http://dokarkiv.default/rest/journalpostapi/v1/journalpost";
    private static final String DEFAULT_PROXY_URI = "http://dokarkivproxy.default/rest/journalpostapi/v1/journalpost";

    private final URI dokarkivUrl;
    private final URI dokarkivProxyUrl;

    public JerseyJournalpostKlient(@KonfigVerdi(value = "journalpost_rest_v1.url", defaultVerdi = DEFAULT_URI) URI dokarkivUrl,
            @KonfigVerdi(value = "journalpost_rest_proxy_v1.url", defaultVerdi = DEFAULT_PROXY_URI) URI dokarkivProxyUrl) {
        this.dokarkivUrl = dokarkivUrl;
        this.dokarkivProxyUrl = dokarkivProxyUrl;
    }

    @Override
    public OpprettJournalpostResponse opprettJournalpost(OpprettJournalpostRequest req, boolean ferdigstill) {
        LOG.trace("Oppretter journalpost {}", req);
        var res = invoke(client.target(dokarkivUrl)
                .queryParam(FORSØK_FERDIGSTILL, ferdigstill)
                .request(APPLICATION_JSON_TYPE)
                .buildPost(json(req)), OpprettJournalpostResponse.class);
        LOG.info("Oppretter journalpost {} OK", res.getJournalpostId());
        return res;
    }

    @Override
    public void ferdigstillJournalpost(JournalpostId id) {
        LOG.trace("Ferdigstiller journalpost {}", id);
        patch(client.target(dokarkivUrl)
                .path(FERDIGSTILL)
                .resolveTemplate("id", id.getVerdi())
                .getUri(), new FerdigstillJournalpostRequest("9999"));
        LOG.info("Ferdigstilt journalpost OK");
    }

    @Override
    public void tilknyttVedlegg(TilknyttVedleggRequest req, JournalpostId til) {
        LOG.trace("Tilknytter vedlegg {}", til.getVerdi());
        var res = invoke(client.target(dokarkivProxyUrl)
                .path(TILKNYTT_VEDLEGG)
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
