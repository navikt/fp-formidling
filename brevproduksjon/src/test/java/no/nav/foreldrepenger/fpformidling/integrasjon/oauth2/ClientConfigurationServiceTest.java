package no.nav.foreldrepenger.fpformidling.integrasjon.oauth2;

import static org.assertj.core.api.Assertions.assertThat;

import javax.ws.rs.core.UriBuilder;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;

import no.nav.security.token.support.client.core.OAuth2GrantType;

class ClientConfigurationServiceTest {

    private static final String SECURITY_REG_PREFIX = "no.nav.security.jwt.client.registration.";
    private static final String DOKDIST = "dokdist";
    private static final String JOURNAL = "journal";
    private static final String CLIENT_STS = "client-sts";

    @BeforeAll
    public static void setupSystemPropertyForTest() {
        System.setProperty("systembruker.username", "test-bruker");
        System.setProperty("systembruker.password", "test-passord");
        System.setProperty("oidc.sts.token.endpoint", "http://localhost:8080/sts/token");
        System.setProperty(SECURITY_REG_PREFIX + DOKDIST + ".well-known-url", "http://localhost:8080/azuread/.well-known/openid-configuration");
        System.setProperty(SECURITY_REG_PREFIX + DOKDIST + ".well-known-url", "http://localhost:8080/azuread/.well-known/openid-configuration");
        System.setProperty(SECURITY_REG_PREFIX + DOKDIST + ".token-endpoint-url", "http://localhost:8080/azuread/token");
        System.setProperty(SECURITY_REG_PREFIX + DOKDIST + ".resource-url", "http://fpsak/fpsak/api");
        System.setProperty(SECURITY_REG_PREFIX + DOKDIST + ".grant-type", "client_credentials");
        System.setProperty(SECURITY_REG_PREFIX + DOKDIST + ".scope", "scope1, scope2");
        System.setProperty(SECURITY_REG_PREFIX + DOKDIST + ".authentication.client-auth-method", "client_secret_post");
        System.setProperty(SECURITY_REG_PREFIX + DOKDIST + ".authentication.client-id", "fpformidling");
        System.setProperty(SECURITY_REG_PREFIX + DOKDIST + ".authentication.client-secret", "some-secret");

        System.setProperty(SECURITY_REG_PREFIX + JOURNAL + ".well-known-url", "http://localhost:8080/azuread/.well-known/openid-configuration");
        System.setProperty(SECURITY_REG_PREFIX + JOURNAL + ".token-endpoint-url", "http://localhost:8080/azuread/token");
        System.setProperty(SECURITY_REG_PREFIX + JOURNAL + ".resource-url", "http://fpsak/fpsak/api");
        System.setProperty(SECURITY_REG_PREFIX + JOURNAL + ".grant-type", "urn:ietf:params:oauth:grant-type:token-exchange");
        System.setProperty(SECURITY_REG_PREFIX + JOURNAL + ".scope", "scope1, scope2");
        System.setProperty(SECURITY_REG_PREFIX + JOURNAL + ".authentication.client-auth-method", "client_secret_basic");
        System.setProperty(SECURITY_REG_PREFIX + JOURNAL + ".authentication.client-id", "fpformidling");
        System.setProperty(SECURITY_REG_PREFIX + JOURNAL + ".authentication.client-secret", "some-secret");
        System.setProperty(SECURITY_REG_PREFIX + JOURNAL + ".token-exchange.audience", "fpsak");
        System.setProperty(SECURITY_REG_PREFIX + JOURNAL + ".token-exchange.resource", "søknad");

        System.setProperty(SECURITY_REG_PREFIX + CLIENT_STS + ".id-provider", "STS");
    }


    @Test
    void finnesClientConfig() {
        var clientConfig = new ClientConfigurationService();

        var props = clientConfig.clientProperties();
        assertThat(props).isNotNull();
        assertThat(props.registrations()).isNotEmpty();
        assertThat(props.registrations()).hasSize(3);

        assertThat(props.registrations().get(DOKDIST)).isNotNull();
        assertThat(props.registrations().get(DOKDIST).getResourceUrl()).isEqualTo(UriBuilder.fromUri("http://fpsak/fpsak/api").build());
        assertThat(props.registrations().get(DOKDIST).getTokenEndpointUrl()).isEqualTo(UriBuilder.fromUri("http://localhost:8080/azuread/token").build());
        assertThat(props.registrations().get(DOKDIST).getWellKnownUrl()).isEqualTo(UriBuilder.fromUri("http://localhost:8080/azuread/.well-known/openid-configuration").build());
        assertThat(props.registrations().get(DOKDIST).getGrantType()).isEqualTo(OAuth2GrantType.CLIENT_CREDENTIALS);
        assertThat(props.registrations().get(DOKDIST).getScope()).isNotEmpty();
        assertThat(props.registrations().get(DOKDIST).getScope()).hasSize(2);
        assertThat(props.registrations().get(DOKDIST).getScope()).containsExactlyInAnyOrder("scope1", "scope2");
        assertThat(props.registrations().get(DOKDIST).getAuthentication()).isNotNull();
        assertThat(props.registrations().get(DOKDIST).getAuthentication().getClientId()).isEqualTo("fpformidling");
        assertThat(props.registrations().get(DOKDIST).getAuthentication().getClientAuthMethod()).isEqualTo(
                ClientAuthenticationMethod.CLIENT_SECRET_POST);
        assertThat(props.registrations().get(DOKDIST).getAuthentication().getClientSecret()).isEqualTo("some-secret");
        assertThat(props.registrations().get(DOKDIST).getAuthentication().getClientRsaKey()).isNull();
        assertThat(props.registrations().get(DOKDIST).getTokenExchange()).isNull();

        assertThat(props.registrations().get(JOURNAL)).isNotNull();
        assertThat(props.registrations().get(JOURNAL).getResourceUrl()).isEqualTo(UriBuilder.fromUri("http://fpsak/fpsak/api").build());
        assertThat(props.registrations().get(JOURNAL).getTokenEndpointUrl()).isEqualTo(UriBuilder.fromUri("http://localhost:8080/azuread/token").build());
        assertThat(props.registrations().get(JOURNAL).getWellKnownUrl()).isEqualTo(UriBuilder.fromUri("http://localhost:8080/azuread/.well-known/openid-configuration").build());
        assertThat(props.registrations().get(JOURNAL).getGrantType()).isEqualTo(OAuth2GrantType.TOKEN_EXCHANGE);
        assertThat(props.registrations().get(JOURNAL).getScope()).isNotEmpty();
        assertThat(props.registrations().get(JOURNAL).getScope()).hasSize(2);
        assertThat(props.registrations().get(JOURNAL).getScope()).containsExactlyInAnyOrder("scope1", "scope2");
        assertThat(props.registrations().get(JOURNAL).getAuthentication()).isNotNull();
        assertThat(props.registrations().get(JOURNAL).getAuthentication().getClientId()).isEqualTo("fpformidling");
        assertThat(props.registrations().get(JOURNAL).getAuthentication().getClientAuthMethod()).isEqualTo(
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
        assertThat(props.registrations().get(JOURNAL).getAuthentication().getClientSecret()).isEqualTo("some-secret");
        assertThat(props.registrations().get(JOURNAL).getAuthentication().getClientRsaKey()).isNull();
        assertThat(props.registrations().get(JOURNAL).getTokenExchange().getAudience()).isEqualTo("fpsak");
        assertThat(props.registrations().get(JOURNAL).getTokenExchange().getResource()).isEqualTo("søknad");

        // STS preset
        assertThat(props.registrations().get(CLIENT_STS)).isNotNull();
        assertThat(props.registrations().get(CLIENT_STS).getTokenEndpointUrl()).isNotNull();
        assertThat(props.registrations().get(CLIENT_STS).getAuthentication()).isNotNull();
        assertThat(props.registrations().get(CLIENT_STS).getAuthentication().getClientId()).isEqualTo("test-bruker");
        assertThat(props.registrations().get(CLIENT_STS).getAuthentication().getClientSecret()).isEqualTo("test-passord");

    }

}
