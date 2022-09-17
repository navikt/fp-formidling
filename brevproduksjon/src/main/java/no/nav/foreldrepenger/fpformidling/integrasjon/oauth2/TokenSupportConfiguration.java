package no.nav.foreldrepenger.fpformidling.integrasjon.oauth2;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import no.nav.security.token.support.client.core.context.JwtBearerTokenResolver;
import no.nav.security.token.support.client.core.http.OAuth2HttpClient;
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService;

@ApplicationScoped
public class TokenSupportConfiguration {
    @Produces
    private static final JwtBearerTokenResolver JWT_BEARER_TOKEN_RESOLVER = new JwtBearerTokenResolverService();
    @Produces
    private static final OAuth2HttpClient O_AUTH_2_HTTP_CLIENT = new DefaultOAuth2HttpClient();
    @Produces
    private static final ClientConfiguration CLIENT_CONFIGURATION = new ClientConfigurationService().clientProperties();
    private DefaultOAuth2AccessTokenService oAuth2AccessTokenService;

    @Produces
    public OAuth2AccessTokenService oAuth2AccessTokenService() {
        if (oAuth2AccessTokenService == null) {
            oAuth2AccessTokenService = new DefaultOAuth2AccessTokenService(JWT_BEARER_TOKEN_RESOLVER, O_AUTH_2_HTTP_CLIENT);
        }
        return oAuth2AccessTokenService;
    }
}
