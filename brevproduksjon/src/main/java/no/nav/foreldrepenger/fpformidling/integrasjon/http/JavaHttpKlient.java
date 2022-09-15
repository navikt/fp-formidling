package no.nav.foreldrepenger.fpformidling.integrasjon.http;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.vedtak.exception.IntegrasjonException;
import no.nav.vedtak.exception.ManglerTilgangException;
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.mapper.json.DefaultJsonMapper;
import no.nav.vedtak.sikkerhet.oidc.token.OpenIDToken;

public abstract class JavaHttpKlient implements RequestKonfig, AuthKonfig {
    private static final Logger LOG = LoggerFactory.getLogger(JavaHttpKlient.class);
    private static final String DEFAULT_NAV_CONSUMERID = "Nav-Consumer-Id";
    private static final String DEFAULT_NAV_CALLID = "Nav-Callid";
    public static final String ALT_NAV_CALL_ID = "nav-call-id";

    protected static final String OIDC_AUTH_HEADER_PREFIX = OpenIDToken.OIDC_DEFAULT_TOKEN_TYPE;

    private static final HttpClient client;
    static {
        client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).proxy(HttpClient.Builder.NO_PROXY).connectTimeout(Duration.ofSeconds(10)).build();
    }

    protected HttpRequest.Builder getRequestBuilder() {
        var requestBuilder = HttpRequest.newBuilder()
                .header(ACCEPT, getAccept())
                .header(CONTENT_TYPE, getContentType())
                .header(DEFAULT_NAV_CALLID, getCallId())
                .header(ALT_NAV_CALL_ID, getCallId())
                .timeout(Duration.ofSeconds(getTimeout()));
                getAuthorization().ifPresent(token -> requestBuilder.header(AUTHORIZATION, OIDC_AUTH_HEADER_PREFIX + token));
                getConsumerId().ifPresent(consumer -> requestBuilder.header(DEFAULT_NAV_CONSUMERID, consumer));
        return requestBuilder;
    }

    protected HttpResponse<String> sendStringRequest(HttpRequest request) {
        return send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
    }

    protected HttpResponse<byte[]> sendByteArrayRequest(HttpRequest request) {
        return send(request, HttpResponse.BodyHandlers.ofByteArray());
    }

    protected <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> responseHandler) {
        try {
            return client.send(request, responseHandler);
        } catch (IOException e) {
            throw new TekniskException("F-432937", String.format("Kunne ikke sende request mot %s", request.uri().toString()), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TekniskException("F-432938", "InterruptedException ved henting av data.", e);
        }
    }

    protected String toJson(Object dto) {
        return DefaultJsonMapper.toJson(dto);
    }

    protected <T> T fromJson(String json, Class<T> clazz) {
        return DefaultJsonMapper.fromJson(json, clazz);
    }

    protected <T, R> R handleResponse(HttpResponse<T> response, Function<HttpResponse<T>, R> responseFunction, Consumer<HttpResponse<T>> errorConsumer) {
        var statusCode = response.statusCode();
        var endpoint = response.uri();
        if (response.body() == null || statusCode == HttpURLConnection.HTTP_NO_CONTENT) {
            LOG.info("[HTTP {}] Ingen resultat fra {}", statusCode, endpoint);
            return null;
        }
        if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED || statusCode == HttpURLConnection.HTTP_FORBIDDEN) {
            throw new ManglerTilgangException("NO-AUTH", String.format("[HTTP %s] Mangler tilgang. Feilet mot %s", statusCode, endpoint));
        }
        if ((statusCode >= HttpURLConnection.HTTP_OK && statusCode < HttpURLConnection.HTTP_MULT_CHOICE)) {
            return responseFunction.apply(response);
        }
        if (statusCode == HttpURLConnection.HTTP_NOT_FOUND) {
            throw new TekniskException("NOT-FOUND", String.format("[HTTP %s] Feilet mot %s.", statusCode, endpoint));
        }
        if (statusCode == HttpURLConnection.HTTP_BAD_REQUEST && errorConsumer != null) {
            errorConsumer.accept(response);
        }
        throw new IntegrasjonException("REST-FEIL", String.format("[HTTP %s] Uventet respons fra %s", statusCode, endpoint));
    }

    protected <T, R> R handleResponse(HttpResponse<T> response, Function<HttpResponse<T>, R> responseFunction) {
        return handleResponse(response, responseFunction, null);
    }
}
