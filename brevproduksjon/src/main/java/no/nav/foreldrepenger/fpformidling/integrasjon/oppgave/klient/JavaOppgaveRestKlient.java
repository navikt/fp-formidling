package no.nav.foreldrepenger.fpformidling.integrasjon.oppgave.klient;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.fpformidling.integrasjon.http.JavaClient;
import no.nav.foreldrepenger.fpformidling.integrasjon.http.JavaHttpKlient;
import no.nav.foreldrepenger.konfig.KonfigVerdi;
import no.nav.vedtak.exception.IntegrasjonException;
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

        return handleResponse(sendStringRequest(request), getResultFunction(), getErrorConsumer());
    }

    private Function<HttpResponse<String>, Oppgave> getResultFunction() {
        return httpResponse -> {
            var oppgave = fromJson(httpResponse.body(), Oppgave.class);
            LOG.info("[HTTP {}] Oppgave med id: {} opprettet.", httpResponse.statusCode(), oppgave.id());
            return oppgave;
        };
    }
    private Consumer<HttpResponse<String>> getErrorConsumer() {
        return httpResponse -> {
            var feilmelding = fromJson(httpResponse.body(), ErrorResponse.class).feilmelding();
            LOG.info("[HTTP {}] Oppretting av oppgave feilet: Fikk svar '{}'.", httpResponse.statusCode(), feilmelding);
            throw new IntegrasjonException("FP-468820", String.format("[HTTP %s] Uventet respons fra %s, med melding: %s", httpResponse.statusCode(),
                    httpResponse.uri(), feilmelding));
        };
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
