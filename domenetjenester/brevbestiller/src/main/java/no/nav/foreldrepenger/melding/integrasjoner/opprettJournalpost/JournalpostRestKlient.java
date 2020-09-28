package no.nav.foreldrepenger.melding.integrasjoner.opprettJournalpost;

import no.nav.foreldrepenger.melding.integrasjoner.opprettJournalpost.dto.JournalPostData;
import no.nav.foreldrepenger.melding.integrasjoner.opprettJournalpost.dto.OpprettetJournalpostResponse;
import no.nav.vedtak.felles.integrasjon.rest.OidcRestClient;
import no.nav.vedtak.konfig.KonfigVerdi;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.net.URI;

@ApplicationScoped
public class JournalpostRestKlient {
    private static final String DEFAULT_URI = "http://dokarkiv.default/rest/journalpostapi/v1/journalpost";

    private static final Logger LOG = LoggerFactory.getLogger(JournalpostRestKlient.class);

    private URI endpoint;
    private OidcRestClient restKlient;

    JournalpostRestKlient() {
        // CDI
    }

    @Inject
    public JournalpostRestKlient(@KonfigVerdi(value = "journalpost_rest_v1.url", defaultVerdi = DEFAULT_URI) URI endpoint, OidcRestClient restKlient) {
        this.endpoint = endpoint;
        this.restKlient = restKlient;
    }

    public OpprettetJournalpostResponse opprettJournalpost(JournalPostData request, boolean ferdigstill) {
        try {
            var opprett = new URIBuilder(endpoint).addParameter("forsoekFerdigstill", "true").build();
            return restKlient.post(opprett, request, OpprettetJournalpostResponse.class);
        } catch (Exception e) {
            LOG.info("Fpformidling JournalpostRestKlient feilet å journalføre for {}", request, e);
            return null;
        }
    }
}
