package no.nav.foreldrepenger.fpformidling.integrasjon.dokdist;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.net.HttpURLConnection;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.fpformidling.integrasjon.http.JavaClient;
import no.nav.foreldrepenger.fpformidling.integrasjon.http.JavaHttpKlient;
import no.nav.foreldrepenger.fpformidling.typer.JournalpostId;
import no.nav.foreldrepenger.konfig.KonfigVerdi;
import no.nav.vedtak.exception.IntegrasjonException;
import no.nav.vedtak.log.mdc.MDCOperations;
import no.nav.vedtak.sikkerhet.context.SubjectHandler;
import no.nav.vedtak.sikkerhet.oidc.token.TokenProvider;

@ApplicationScoped
@JavaClient
class JavaDokdistRestKlient extends JavaHttpKlient implements Dokdist {

    private static final Logger LOG = LoggerFactory.getLogger(JavaDokdistRestKlient.class);

    private final String dokdistRestBaseUri;

    @Inject
    public JavaDokdistRestKlient(@KonfigVerdi("dokdist.rest.base.url") String endpoint) {
        this.dokdistRestBaseUri = endpoint;
    }

    public Dokdist.Resultat distribuerJournalpost(DistribuerJournalpostRequest dto) {
        var endpoint = UriBuilder.fromUri(dokdistRestBaseUri).path("/distribuerjournalpost").build();
        var request = getRequestBuilder()
                .uri(endpoint)
                .POST(HttpRequest.BodyPublishers.ofString(toJson(dto), UTF_8))
                .build();

        var journalpostId = new JournalpostId(dto.journalpostId());
        var batchId = dto.batchId();
        LOG.info("Bestiller distribusjon av {} med batchId {}", journalpostId, batchId);
        var response = sendStringRequest(request);

        if (response.statusCode() == HttpURLConnection.HTTP_BAD_REQUEST) {
            return consumeError(response, journalpostId);
        } else if (response.statusCode() == HttpURLConnection.HTTP_CONFLICT) {
            return getResultFunction(journalpostId).apply(response);
        }
        return handleResponse(response, getResultFunction(journalpostId));
    }

    private Function<HttpResponse<String>, Resultat> getResultFunction(JournalpostId journalpostId) {
        return httpResponse -> {
            var status = httpResponse.statusCode();
            var bestillingsId = fromJson(httpResponse.body(), DistribuerJournalpostResponse.class).bestillingsId();
            LOG.info("[HTTP {}] Distribuert {} med bestillingsId {}", status, journalpostId, bestillingsId);
            return Resultat.OK;
        };
    }

    private Resultat consumeError(HttpResponse<String> response, JournalpostId journalpostId) {
        var message = fromJson(response.body(), ErrorResponse.class).message();
        var statusCode = response.statusCode();
        var endpoint = response.uri();
        LOG.warn("[HTTP {}] Brevdistribusjon feilet: Fikk svar '{}'.", statusCode, message);
        if (message.contains("Mottaker har ukjent adresse")) {
            LOG.warn("[HTTP {}] Brevdistribusjon feilet. Bruker mangler adresse. Sjekk med fag om GOSYS oppgaven er opprettet for {}",
                    statusCode, journalpostId);
            return Resultat.MANGLER_ADRESSE;
        } else {
            throw new IntegrasjonException("FP-468815", String.format("[HTTP %s] Uventet respons fra %s, med melding: %s", statusCode, endpoint, message));
        }
    }

    @Override
    public Optional<String> getAuthorization() {
        return Optional.ofNullable(TokenProvider.getStsSystemToken().token());
    }

    @Override
    public Optional<String> getConsumerId() {
        return Optional.ofNullable(SubjectHandler.getSubjectHandler().getConsumerId());
    }

    @Override
    public String getCallId() {
        return MDCOperations.getCallId();
    }
}
