package no.nav.foreldrepenger.fpformidling.integrasjon.oauth2;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;

import no.nav.foreldrepenger.konfig.Environment;
import no.nav.security.token.support.client.core.ClientAuthenticationProperties;
import no.nav.security.token.support.client.core.ClientProperties;
import no.nav.security.token.support.client.core.OAuth2GrantType;

@ApplicationScoped
public class ClientConfigurationService implements OAuth2ClientProps {

    private static final String REGISTRATION_PREFIX = "no.nav.security.jwt.client.registration.";
    private static final Environment ENV = Environment.current();
    private static final String TOKEN_X_CLIENT_JWK = ENV.getProperty("token.x.private.jwk");
    private static final String TOKEN_X_CLIENT_ID = ENV.getProperty("token.x.client.id");
    private static final URI STS_WELL_KNOWN_URI = ENV.getProperty("oidc.sts.well.known.url", URI.class);
    private static final URI STS_TOKEN_ENDPOINT_URI = ENV.getProperty("oidc.sts.token.endpoint", URI.class);
    private static final String SYSTEMPASSORD = ENV.getProperty("systembruker.password");
    private static final String SYSTEMBRUKER = ENV.getProperty("systembruker.username");
    private static final URI TOKEN_X_TOKEN_ENDPOINT_URI = ENV.getProperty("token.x.token.endpoint", URI.class);
    private static final URI TOKEN_X_WELL_KNOWN_URI = ENV.getProperty("token.x.well.known.url", URI.class);
    private static final URI AZURE_TOKEN_ENDPOINT_URI = ENV.getProperty("azure.openid.config.token.endpoint", URI.class);
    private static final URI AZURE_WELL_KNOWN_URI = ENV.getProperty("azure.app.well.known.url", URI.class);
    private static final String AZURE_CLIENT_JWK = ENV.getProperty("azure.app.jwk");
    private static final String AZURE_CLIENT_ID = ENV.getProperty("azure.app.client.id");
    private static final String AZURE_CLIENT_SECRET = ENV.getProperty("azure.app.client.secret");

    private volatile ClientConfiguration clientConfiguration;

    ClientConfigurationService() {
        registrer();
    }

    private void registrer() {
        this.clientConfiguration = new ClientConfiguration(new HashMap<>());

        var properties = ENV.getPropertiesWithPrefix(REGISTRATION_PREFIX);
        var clients = findAllClients(properties.keySet());

        clients.forEach(client -> {
            var prefix = REGISTRATION_PREFIX + client;
            var idProvider = ENV.getProperty(prefix + ".id-provider");

            var propertiesBuilder = ClientProperties.builder();

            if (ClientType.STS.name().equals(idProvider)) {
                propertiesBuilder
                        .wellKnownUrl(STS_WELL_KNOWN_URI)
                        .tokenEndpointUrl(STS_TOKEN_ENDPOINT_URI)
                        .grantType(OAuth2GrantType.CLIENT_CREDENTIALS)
                        .scope(List.of("oidc"))
                        .authentication(ClientAuthenticationProperties.builder()
                                .clientAuthMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                                .clientId(SYSTEMBRUKER)
                                .clientSecret(SYSTEMPASSORD)
                                .build());
            } else if (ClientType.TOKENX.name().equals(idProvider)) {
                propertiesBuilder
                        .tokenEndpointUrl(ENV.getProperty(prefix + ".token-endpoint-url", URI.class, TOKEN_X_TOKEN_ENDPOINT_URI))
                        .wellKnownUrl(ENV.getProperty(prefix + ".well-known-url", URI.class, TOKEN_X_WELL_KNOWN_URI))
                        .grantType(
                                new OAuth2GrantType(ENV.getProperty(prefix + ".grant-type", OAuth2GrantType.TOKEN_EXCHANGE.value())))
                        .authentication(ClientAuthenticationProperties.builder()
                                .clientAuthMethod(ClientAuthenticationMethod.PRIVATE_KEY_JWT)
                                .clientId(hentProperty(prefix + ".authentication.client-id", TOKEN_X_CLIENT_ID))
                                .clientJwk(hentProperty(prefix + ".authentication.client-jwk", TOKEN_X_CLIENT_JWK))
                                .build());
                mapTokenExchange(prefix, propertiesBuilder);

            } else if (ClientType.AZURE.name().equals(idProvider)) {
                propertiesBuilder
                        .tokenEndpointUrl(ENV.getProperty(prefix + ".token-endpoint-url", URI.class, AZURE_TOKEN_ENDPOINT_URI))
                        .wellKnownUrl(ENV.getProperty(prefix + ".well-known-url", URI.class, AZURE_WELL_KNOWN_URI))
                        .grantType(new OAuth2GrantType(ENV.getRequiredProperty(prefix + ".grant-type")));
                var authMethod = ClientAuthenticationMethod.parse(ENV.getProperty(prefix + ".authentication.client-auth-method"));
                propertiesBuilder.authentication(ClientAuthenticationProperties.builder()
                        .clientAuthMethod(authMethod != null ? authMethod : ClientAuthenticationMethod.CLIENT_SECRET_POST)
                        .clientId(hentProperty(prefix + ".authentication.client-id", AZURE_CLIENT_ID))
                        .clientSecret(hentProperty(prefix + ".authentication.client-secret", AZURE_CLIENT_SECRET))
                        .clientJwk(hentProperty(prefix + ".authentication.client-jwk", AZURE_CLIENT_JWK))
                        .build());
                mapScope(prefix, propertiesBuilder);

            } else {
                var grantType = new OAuth2GrantType(ENV.getProperty(prefix + ".grant-type"));
                propertiesBuilder
                        .tokenEndpointUrl(ENV.getProperty(prefix + ".token-endpoint-url", URI.class))
                        .wellKnownUrl(ENV.getProperty(prefix + ".well-known-url", URI.class))
                        .resourceUrl(ENV.getProperty(prefix + ".resource-url", URI.class))
                        .grantType(grantType)
                        .authentication(ClientAuthenticationProperties.builder()
                            .clientAuthMethod(ClientAuthenticationMethod.parse(ENV.getProperty(prefix + ".authentication.client-auth-method")))
                            .clientId(ENV.getProperty(prefix + ".authentication.client-id"))
                            .clientSecret(ENV.getProperty(prefix + ".authentication.client-secret"))
                            .clientJwk(ENV.getProperty(prefix + ".authentication.client-jwk"))
                            .build());
                if (OAuth2GrantType.TOKEN_EXCHANGE.equals(grantType)) {
                    mapTokenExchange(prefix, propertiesBuilder);
                }
                mapScope(prefix, propertiesBuilder);
            }

            this.clientConfiguration.registrations().put(client, propertiesBuilder.build());
        });
    }

    private static void mapTokenExchange(String prefix, ClientProperties.ClientPropertiesBuilder propertiesBuilder) {
        propertiesBuilder.tokenExchange(ClientProperties.TokenExchangeProperties.builder()
            .audience(ENV.getRequiredProperty(prefix + ".token-exchange.audience"))
            .resource(ENV.getProperty(prefix + ".token-exchange.resource"))
            .build());
    }

    private static void mapScope(String prefix, ClientProperties.ClientPropertiesBuilder propertiesBuilder) {
        var scope = ENV.getProperty(prefix + ".scope");
        if (scope != null) {
            propertiesBuilder.scope(Arrays.stream(scope.split(",")).map(String::trim).toList());
        }
    }

    private static String hentProperty(String nøkkel, String defaultVerdi) {
        return ENV.getProperty(nøkkel, defaultVerdi);
    }

    private Set<String> findAllClients(Set<Object> keySet) {
        return keySet.stream()
                .map(it -> ((String) it).replace(REGISTRATION_PREFIX, ""))
                .map(it -> it.split("\\.")[0])
                .collect(Collectors.toSet());
    }

    @Produces
    public ClientConfiguration clientProperties() {
        if (clientConfiguration == null) {
            registrer();
        }
        return clientConfiguration;
    }

    private enum ClientType {
        STS, TOKENX, AZURE;
    }

}
