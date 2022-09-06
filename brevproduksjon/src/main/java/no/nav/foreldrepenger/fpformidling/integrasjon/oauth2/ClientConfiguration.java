package no.nav.foreldrepenger.fpformidling.integrasjon.oauth2;

import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import no.nav.security.token.support.client.core.ClientProperties;

public record ClientConfiguration(@NotEmpty @Valid Map<String, ClientProperties> registrations) {}
