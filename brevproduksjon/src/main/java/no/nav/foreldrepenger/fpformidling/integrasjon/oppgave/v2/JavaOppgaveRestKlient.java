package no.nav.foreldrepenger.fpformidling.integrasjon.oppgave.v2;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.fpformidling.integrasjon.http.JavaClient;
import no.nav.foreldrepenger.fpformidling.integrasjon.http.JavaHttpKlient;
import no.nav.foreldrepenger.konfig.KonfigVerdi;
import no.nav.vedtak.exception.IntegrasjonException;
import no.nav.vedtak.exception.ManglerTilgangException;
import no.nav.vedtak.log.mdc.MDCOperations;
import no.nav.vedtak.sikkerhet.context.SubjectHandler;
import no.nav.vedtak.sikkerhet.oidc.token.TokenProvider;

@ApplicationScoped
@JavaClient
class JavaOppgaveRestKlient extends JavaHttpKlient implements Oppgaver {

    private static final Logger LOG = LoggerFactory.getLogger(JavaOppgaveRestKlient.class);

    public static final String HEADER_CORRELATION_ID = "X-Correlation-ID";

    private final String oppgaveBaseUri;

    @Inject
    public JavaOppgaveRestKlient(@KonfigVerdi("oppgave.rs.uri") String endpoint) {
        this.oppgaveBaseUri = endpoint;
    }

    @Override
    public Oppgave opprettetOppgave(OpprettOppgaveRequest oppgave) {
        var endpoint = URI.create(oppgaveBaseUri);
        var request = getRequestBuilder()
                .uri(endpoint)
                .header(HEADER_CORRELATION_ID, getCallId())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(oppgave), UTF_8))
                .build();

        var response = sendRequest(request);
        int status = response.statusCode();
        return handleResponse(status, response, endpoint.toString());
    }

    private Oppgave handleResponse(int status, HttpResponse<String> response, String endpoint) {
        if ((status >= HttpStatus.SC_OK && status < HttpStatus.SC_MULTIPLE_CHOICES)) {
            var okResponse = fromJson(response.body(), Oppgave.class);
            LOG.info("[HTTP {}] Oppgave med id: {} opprettet.", status, okResponse.id());
            return okResponse;
        } else if (status == HttpStatus.SC_BAD_REQUEST) {
            var errorResponse = fromJson(response.body(), ErrorResponse.class);
            LOG.info("[HTTP {}] Oppretting av oppgave feilet: Fikk svar '{}'.", status, errorResponse.feilmelding());
            throw new IntegrasjonException("F-468815", String.format("[HTTP %s] Uventet respons fra %s, med melding: %s", status, endpoint, errorResponse.feilmelding()));
        } else if (status == HttpStatus.SC_UNAUTHORIZED) {
            var errorResponse = fromJson(response.body(), ErrorResponse.class);
            throw new ManglerTilgangException("F-468815", String.format("[HTTP %s] Feilet mot %s pga <%s>", status, endpoint, errorResponse.feilmelding()));
        } else if (status == HttpStatus.SC_FORBIDDEN) {
            throw new ManglerTilgangException("F-468815", String.format("[HTTP %s] Feilet mot %s", status, endpoint));
        } else {
            throw new IntegrasjonException("F-468815", String.format("[HTTP %s] Uventet respons fra %s", status, endpoint));
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
