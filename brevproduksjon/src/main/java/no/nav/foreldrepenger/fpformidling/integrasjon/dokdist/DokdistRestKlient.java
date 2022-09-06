package no.nav.foreldrepenger.fpformidling.integrasjon.dokdist;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.fpformidling.integrasjon.http.JavaHttpKlient;
import no.nav.foreldrepenger.fpformidling.integrasjon.oauth2.ClientConfiguration;
import no.nav.foreldrepenger.fpformidling.integrasjon.oauth2.OAuth2Token;
import no.nav.foreldrepenger.fpformidling.typer.JournalpostId;
import no.nav.foreldrepenger.konfig.Environment;
import no.nav.foreldrepenger.konfig.KonfigVerdi;
import no.nav.security.token.support.client.core.ClientProperties;
import no.nav.vedtak.exception.IntegrasjonException;
import no.nav.vedtak.log.mdc.MDCOperations;
import no.nav.vedtak.sikkerhet.context.SubjectHandler;
import no.nav.vedtak.sikkerhet.oidc.token.TokenProvider;

@ApplicationScoped
class DokdistRestKlient extends JavaHttpKlient implements Dokdist {
    public static final String DOKDIST_CLIENT_ID = "dokdist";

    private static final Logger LOG = LoggerFactory.getLogger(DokdistRestKlient.class);

    private final String dokdistRestBaseUri;
    private final OAuth2Token tokenService;
    private final ClientConfiguration clientProps;

    @Inject
    public DokdistRestKlient(@KonfigVerdi("dokdist.rest.base.url") String endpoint,
                             OAuth2Token tokenService,
                             ClientConfiguration clientProperties) {
        this.dokdistRestBaseUri = endpoint;
        this.tokenService = tokenService;
        this.clientProps = clientProperties;
    }

    public Dokdist.Resultat distribuerJournalpost(DistribuerJournalpostRequest dto) {
        var endpoint = URI.create(dokdistRestBaseUri + "/distribuerjournalpost");
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
        if (!Environment.current().isProd()) {
            return Optional.ofNullable(tokenService.getAccessToken(getClientProps()).getAccessToken());
        } else {
            return Optional.ofNullable(TokenProvider.getStsSystemToken().token());
        }
    }

    private ClientProperties getClientProps() {
        return clientProps.registrations().get(DOKDIST_CLIENT_ID);
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
