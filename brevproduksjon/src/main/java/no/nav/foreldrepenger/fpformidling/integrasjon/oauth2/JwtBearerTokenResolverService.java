package no.nav.foreldrepenger.fpformidling.integrasjon.oauth2;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import no.nav.security.token.support.client.core.context.JwtBearerTokenResolver;
import no.nav.security.token.support.core.context.TokenValidationContextHolder;
import no.nav.security.token.support.core.jwt.JwtToken;
import no.nav.security.token.support.jaxrs.JaxrsTokenValidationContextHolder;

class JwtBearerTokenResolverService implements JwtBearerTokenResolver {

    private final TokenValidationContextHolder tokenValidationContextHolder = JaxrsTokenValidationContextHolder.getHolder();

    public JwtBearerTokenResolverService() {
    }

    @Override
    public Optional<String> token() {
        var tokenValidationContext = tokenValidationContextHolder.getTokenValidationContext();
        if (tokenValidationContext.hasValidToken()) {
            return tokenValidationContext.getFirstValidToken().map(JwtToken::getTokenAsString);
        }
        return Optional.empty();
    }
}
