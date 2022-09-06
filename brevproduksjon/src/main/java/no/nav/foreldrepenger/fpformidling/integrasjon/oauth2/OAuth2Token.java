package no.nav.foreldrepenger.fpformidling.integrasjon.oauth2;

import no.nav.security.token.support.client.core.ClientProperties;
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenResponse;

public interface OAuth2Token {
    OAuth2AccessTokenResponse getAccessToken(ClientProperties clientProperties);
}
