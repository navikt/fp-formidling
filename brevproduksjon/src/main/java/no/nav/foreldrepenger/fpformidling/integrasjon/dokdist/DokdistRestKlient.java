package no.nav.foreldrepenger.fpformidling.integrasjon.dokdist;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.vedtak.exception.IntegrasjonException;
import no.nav.vedtak.exception.ManglerTilgangException;
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.felles.integrasjon.rest.RestClient;
import no.nav.vedtak.felles.integrasjon.rest.RestClientConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestRequest;
import no.nav.vedtak.felles.integrasjon.rest.TokenFlow;
import no.nav.vedtak.mapper.json.DefaultJsonMapper;

@ApplicationScoped
@RestClientConfig(tokenConfig = TokenFlow.ADAPTIVE, endpointProperty = "dokdist.rest.base.url", endpointDefault = "http://dokdistfordeling.teamdokumenthandtering/rest/v1", scopesProperty = "dokdist.scopes", scopesDefault = "api://prod-fss.teamdokumenthandtering.saf/.default")
class DokdistRestKlient implements Dokdist {

    private static final Logger LOG = LoggerFactory.getLogger(DokdistRestKlient.class);
    private static final String MOTTAKER_HAR_UKJENT_ADRESSE = "Mottaker har ukjent adresse";

    private final RestClient restClient;
    private final RestConfig restConfig;

    private final URI dokdistEndpoint;

    public DokdistRestKlient() {
        this(RestClient.client());
    }

    DokdistRestKlient(RestClient restClient) {
        this.restClient = restClient;
        this.restConfig = RestConfig.forClient(DokdistRestKlient.class);
        this.dokdistEndpoint = UriBuilder.fromUri(restConfig.endpoint()).path("/distribuerjournalpost").build();
    }

    public Resultat distribuerJournalpost(DistribuerJournalpostRequest dto) {
        var request = RestRequest.newPOSTJson(dto, dokdistEndpoint, restConfig);

        var journalpostId = dto.journalpostId();
        var batchId = dto.batchId();
        LOG.info("Bestiller distribusjon av {} med batchId {}", journalpostId, batchId);
        var response = restClient.sendReturnUnhandled(request);

        var statusCode = response.statusCode();
        if (statusCode == HttpURLConnection.HTTP_BAD_REQUEST) {
            return consumeError(response, journalpostId);
        } else if (response.body() == null || statusCode == HttpURLConnection.HTTP_NO_CONTENT) {
            LOG.info("[HTTP {}] Ingen resultat fra {}", statusCode, dokdistEndpoint);
            return null;
        } else if ((statusCode >= HttpURLConnection.HTTP_OK && statusCode < HttpURLConnection.HTTP_MULT_CHOICE)
            || statusCode == HttpURLConnection.HTTP_CONFLICT) {
            var bestillingsId = DefaultJsonMapper.fromJson(response.body(), DistribuerJournalpostResponse.class).bestillingsId();
            LOG.info("[HTTP {}] Distribuert {} med bestillingsId {}", statusCode, journalpostId, bestillingsId);
            return Resultat.OK;
        } else if (statusCode == HttpURLConnection.HTTP_GONE) {
            return Resultat.MANGLER_ADRESSE;
        } else if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED || statusCode == HttpURLConnection.HTTP_FORBIDDEN) {
            throw new ManglerTilgangException("NO-AUTH", String.format("[HTTP %s] Mangler tilgang. Feilet mot %s", statusCode, dokdistEndpoint));
        } else if (statusCode == HttpURLConnection.HTTP_NOT_FOUND) {
            throw new TekniskException("NOT-FOUND", String.format("[HTTP %s] Feilet mot %s.", statusCode, dokdistEndpoint));
        }
        throw new IntegrasjonException("REST-FEIL", String.format("[HTTP %s] Uventet respons fra %s", statusCode, dokdistEndpoint));
    }

    private Resultat consumeError(HttpResponse<String> response, String journalpostId) {
        var message = DefaultJsonMapper.fromJson(response.body(), ErrorResponse.class).message();
        var statusCode = response.statusCode();
        var endpoint = response.uri();
        LOG.info("[HTTP {}] Brevdistribusjon feilet: Fikk svar '{}'.", statusCode, message);
        if (message.contains(MOTTAKER_HAR_UKJENT_ADRESSE)) {
            LOG.info("[HTTP {}] Brevdistribusjon feilet. Bruker mangler adresse. Oppretter en GOSYS oppgave for journalpost: {}", statusCode,
                journalpostId);
            return Resultat.MANGLER_ADRESSE;
        } else {
            throw new IntegrasjonException("FP-468815",
                String.format("[HTTP %s] Uventet respons fra %s, med melding: %s", statusCode, endpoint, message));
        }
    }

}
