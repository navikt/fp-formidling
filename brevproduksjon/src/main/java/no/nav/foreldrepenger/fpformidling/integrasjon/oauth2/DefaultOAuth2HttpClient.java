package no.nav.foreldrepenger.fpformidling.integrasjon.oauth2;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectReader;

import no.nav.foreldrepenger.konfig.Environment;
import no.nav.security.token.support.client.core.http.OAuth2HttpClient;
import no.nav.security.token.support.client.core.http.OAuth2HttpRequest;
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenResponse;
import no.nav.vedtak.exception.IntegrasjonException;
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.mapper.json.DefaultJsonMapper;

@ApplicationScoped
public class DefaultOAuth2HttpClient implements OAuth2HttpClient {

    private static final ObjectReader READER = DefaultJsonMapper.getObjectMapper().readerFor(OAuth2AccessTokenResponse.class);
    private static final Environment ENV = Environment.current();
    private static final URI AZUREAD_TOKEN_ENDPOINT = ENV.getProperty("azure.openid.config.token.endpoint", URI.class);

    private final HttpClient client;
    private final HttpClient clientProxy;

    @Inject
    public DefaultOAuth2HttpClient() {
        var useProxySelector = Optional.ofNullable(!ENV.isLocal() ? null : ENV.getProperty("http.proxy", URI.class))
                .map(p -> new InetSocketAddress(p.getHost(), p.getPort()))
                .map(ProxySelector::of)
                .orElse(HttpClient.Builder.NO_PROXY);

        var clientBuilder = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .followRedirects(HttpClient.Redirect.NEVER);

        this.client = clientBuilder.proxy(HttpClient.Builder.NO_PROXY).build();
        this.clientProxy = clientBuilder.proxy(useProxySelector).build();
    }

    @Override
    public OAuth2AccessTokenResponse post(OAuth2HttpRequest oAuth2HttpRequest) {
        try {
            var requestBuilder = HttpRequest.newBuilder()
                    .uri(oAuth2HttpRequest.getTokenEndpointUrl())
                    .header("Cache-Control", "no-cache")
                    .timeout(Duration.ofSeconds(5))
                    .POST(ofFormData(oAuth2HttpRequest.getFormParameters()));
            addHeaders(requestBuilder, oAuth2HttpRequest.getOAuth2HttpHeaders().headers());

            HttpResponse<String> response;
            if (oAuth2HttpRequest.getTokenEndpointUrl().equals(AZUREAD_TOKEN_ENDPOINT)) {
                response = clientProxy.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString(UTF_8));
            } else {
                response = client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString(UTF_8));
            }

            if (response == null || response.body() == null) {
                throw new TekniskException("F-157385", "Kunne ikke hente token. Har fått ingen svar.");
            }
            if ((response.statusCode() < HttpURLConnection.HTTP_OK && response.statusCode() >= HttpURLConnection.HTTP_MULT_CHOICE)) {
                throw new IntegrasjonException("REST-FEIL", String.format("[HTTP %s] Uventet respons fra %s: %s",
                        response.statusCode(), oAuth2HttpRequest.getTokenEndpointUrl(), response.body()));
            }
            return READER.readValue(response.body());
        } catch (JsonProcessingException e) {
            throw new TekniskException("F-208314", "Kunne ikke deserialisere objekt til JSON", e);
        } catch (IOException e) {
            throw new TekniskException("F-432937", "IOException ved kommunikasjon med server", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TekniskException("F-432938", "InterruptedException ved henting av token", e);
        }
    }

    private static void addHeaders(HttpRequest.Builder builder, Map<String, List<String>> headers) {
        headers.forEach((key, value) -> builder.header(key, String.join(",", value)));
    }

    private static HttpRequest.BodyPublisher ofFormData(Map<String, String> formParameters) {
        var formdata = formParameters.entrySet()
                .stream()
                .map(it -> it.getKey() + "=" + it.getValue())
                .collect(Collectors.joining("&"));
        return HttpRequest.BodyPublishers.ofString(formdata);
    }
}
