package no.nav.foreldrepenger.fpformidling.integrasjon.http;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.mapper.json.DefaultJsonMapper;
import no.nav.vedtak.sikkerhet.oidc.token.OpenIDToken;

public abstract class JavaHttpKlient implements RequestKonfig, AuthKonfig {
    private static final String DEFAULT_NAV_CONSUMERID = "Nav-Consumer-Id";
    private static final String DEFAULT_NAV_CALLID = "Nav-Callid";
    public static final String ALT_NAV_CALL_ID = "nav-call-id";

    protected static final String OIDC_AUTH_HEADER_PREFIX = OpenIDToken.OIDC_DEFAULT_TOKEN_TYPE;

    private static final HttpClient client;
    static {
        client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).connectTimeout(Duration.ofSeconds(10)).build();
    }

    protected HttpRequest.Builder getRequestBuilder() {
        var requestBuilder = HttpRequest.newBuilder()
                .header(ACCEPT, getAccept())
                .header(CONTENT_TYPE, getContentType())
                .header(DEFAULT_NAV_CALLID, getCallId())
                .header(ALT_NAV_CALL_ID, getCallId())
                .timeout(Duration.ofSeconds(getTimeout()));
                getAuthorization().map(token -> requestBuilder.header(AUTHORIZATION, OIDC_AUTH_HEADER_PREFIX + token));
                getConsumerId().map(consumer -> requestBuilder.header(DEFAULT_NAV_CONSUMERID, consumer));
        return requestBuilder;
    }

    protected HttpResponse<String> sendRequest(HttpRequest request) {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
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

    @Override
    public abstract Optional<String> getAuthorization();
    @Override
    public abstract Optional<String> getConsumerId();
    @Override
    public abstract String getCallId();
}
