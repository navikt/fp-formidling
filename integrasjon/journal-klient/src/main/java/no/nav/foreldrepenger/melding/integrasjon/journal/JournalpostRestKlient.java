package no.nav.foreldrepenger.melding.integrasjon.journal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.foreldrepenger.melding.integrasjon.journal.dto.FerdigstillJournalpostRequest;
import no.nav.foreldrepenger.melding.integrasjon.journal.dto.OpprettJournalpostRequest;
import no.nav.foreldrepenger.melding.integrasjon.journal.dto.OpprettJournalpostResponse;
import no.nav.foreldrepenger.melding.integrasjon.journal.dto.TilknyttVedleggRequest;
import no.nav.foreldrepenger.melding.integrasjon.journal.dto.TilknyttVedleggResponse;
import no.nav.foreldrepenger.melding.typer.JournalpostId;
import no.nav.vedtak.felles.integrasjon.rest.OidcRestClient;
import no.nav.vedtak.konfig.KonfigVerdi;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;

@ApplicationScoped
public class JournalpostRestKlient {
    private static final String DEFAULT_URI = "http://dokarkiv.default/rest/journalpostapi/v1/journalpost";
    private static final String DEFAULT_PROXY_URI = "http://dokarkivproxy.default/rest/journalpostapi/v1/journalpost";
    private static final Logger LOG = LoggerFactory.getLogger(JournalpostRestKlient.class);
    private static final String STATUS_OK = "OK";

    private URI endpoint;
    private URI endpointProxy;
    private OidcRestClient restKlient;
    private OidcRestClient restKlientProxy;

    JournalpostRestKlient() {
        // CDI
    }

    @Inject
    public JournalpostRestKlient(@KonfigVerdi(value = "journalpost_rest_v1.url", defaultVerdi = DEFAULT_URI) URI endpoint, OidcRestClient restKlient,
                                 @KonfigVerdi(value = "journalpost_rest_proxy_v1.url", defaultVerdi = DEFAULT_PROXY_URI) URI endpointProxy, OidcRestClient restKlientProxy) {
        this.endpoint = endpoint;
        this.restKlient = restKlient;
        this.endpointProxy = endpointProxy;
        this.restKlientProxy = restKlientProxy;
    }

    public OpprettJournalpostResponse opprettJournalpost(OpprettJournalpostRequest request, boolean ferdigstill) {
        try {
            var uri = new URIBuilder(endpoint).addParameter("forsoekFerdigstill", ""+ferdigstill).build();
            return restKlient.post(uri, request, OpprettJournalpostResponse.class);
        } catch (URISyntaxException e) {
            throw JournalpostFeil.FACTORY.klarteIkkeOppretteUriForNyJournalpost(request.getSak().getArkivsaksnummer(), e).toException();
        }
    }

    public void tilknyttVedlegg(TilknyttVedleggRequest request, JournalpostId journalpostIdTil) {
        try {
            String tilknyttPath = String.format("/%s/tilknyttVedlegg", journalpostIdTil.getVerdi());
            var uri = new URIBuilder(endpointProxy + tilknyttPath).build();
            ObjectMapper mapper = new ObjectMapper();

            String response = restKlientProxy.put(uri, request);

            TilknyttVedleggResponse tilknyttVedleggResponse = mapper.readValue(response, TilknyttVedleggResponse.class);

            if (!tilknyttVedleggResponse.getFeiledeDokumenter().isEmpty()) {
                throw new IllegalStateException("Følgende vedlegg feilet " + tilknyttVedleggResponse.toString() + " for journalpost " + journalpostIdTil);
            } else {
                LOG.info("Vedlegg tilknyttet {} OK", journalpostIdTil);
            }
        } catch (URISyntaxException | JsonProcessingException e) {
            throw JournalpostFeil.FACTORY.klarteIkkeOppretteUriForTilknytningAvVedlegg(journalpostIdTil, request.toString(), e).toException();
        }
    }

    public void ferdigstillJournalpost(JournalpostId journalpostId) {
        try {
            LOG.info("Ferdigstiller journalpost {}", journalpostId);
            String tilknyttPath = String.format("/%s/ferdigstill", journalpostId.getVerdi());
            var uri = new URIBuilder(endpoint + tilknyttPath).build();
            restKlient.patch(uri, new FerdigstillJournalpostRequest("9999"));
        } catch (Exception e) {
            throw JournalpostFeil.FACTORY.klarteIkkeFerdigstilleJournalpost(journalpostId, e).toException();
        }
    }
}
