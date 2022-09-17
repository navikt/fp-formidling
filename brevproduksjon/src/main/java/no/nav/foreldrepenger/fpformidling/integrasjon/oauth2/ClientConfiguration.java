package no.nav.foreldrepenger.fpformidling.integrasjon.oauth2;

import java.util.Map;

import javax.enterprise.inject.Produces;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import no.nav.security.token.support.client.core.ClientProperties;
import no.nav.security.token.support.client.core.context.JwtBearerTokenResolver;
import no.nav.security.token.support.core.api.Protected;

public record ClientConfiguration(@NotEmpty @Valid Map<String, ClientProperties> registrations) {
}
