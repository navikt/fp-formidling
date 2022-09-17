package no.nav.foreldrepenger.fpformidling.integrasjon.oauth2;

import javax.inject.Inject;

import no.nav.security.token.support.client.core.OAuth2CacheFactory;
import no.nav.security.token.support.client.core.context.JwtBearerTokenResolver;
import no.nav.security.token.support.client.core.http.OAuth2HttpClient;
import no.nav.security.token.support.client.core.oauth2.ClientCredentialsTokenClient;
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService;
import no.nav.security.token.support.client.core.oauth2.OnBehalfOfTokenClient;

class DefaultOAuth2AccessTokenService extends OAuth2AccessTokenService {

    @Inject
    public DefaultOAuth2AccessTokenService(JwtBearerTokenResolver tokenResolver, OAuth2HttpClient oAuth2HttpClient) {
        // bruker ikke tokenX her så den klienten kan være null.
        super(tokenResolver, new OnBehalfOfTokenClient(oAuth2HttpClient), new ClientCredentialsTokenClient(oAuth2HttpClient), null);
        setClientCredentialsGrantCache(OAuth2CacheFactory.accessTokenResponseCache(100, 3600));
        setOnBehalfOfGrantCache(OAuth2CacheFactory.accessTokenResponseCache(100, 3600));
    }
}
