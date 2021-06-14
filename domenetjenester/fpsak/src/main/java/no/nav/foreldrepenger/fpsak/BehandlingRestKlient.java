package no.nav.foreldrepenger.fpsak;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.konfig.KonfigVerdi;
import no.nav.foreldrepenger.melding.behandling.BehandlingRelLinkPayload;
import no.nav.foreldrepenger.melding.behandling.BehandlingResourceLink;
import no.nav.vedtak.felles.integrasjon.rest.OidcRestClient;

@Dependent
public class BehandlingRestKlient implements Behandlinger {
    private static final Logger LOGGER = LoggerFactory.getLogger(BehandlingRestKlient.class);
    private static final String FPSAK_REST_BASE_URL = "fpsak_rest_base.url";
    private static final String HENT_BEHANLDING_ENDPOINT = "/fpsak/api/formidling/ressurser";
    private static final String SAKSNUMMER = "saksnummer";
    private static final String BEHANDLING_ID = "behandlingId";

    private final OidcRestClient oidcRestClient;
    private final String endpointFpsakRestBase;

    @Inject
    public BehandlingRestKlient(OidcRestClient oidcRestClient,
            @KonfigVerdi(FPSAK_REST_BASE_URL) String endpointFpsakRestBase) {
        this.oidcRestClient = oidcRestClient;
        this.endpointFpsakRestBase = endpointFpsakRestBase;
    }

    @Override
    public BehandlingDto hentBehandling(UUID behandlingId) {
        Optional<BehandlingDto> behandling = Optional.empty();
        try {
            var behandlingUriBuilder = new URIBuilder(endpointFpsakRestBase + HENT_BEHANLDING_ENDPOINT);
            behandlingUriBuilder.setParameter(BEHANDLING_ID, behandlingId.toString());
            behandling = oidcRestClient.getReturnsOptional(behandlingUriBuilder.build(), BehandlingDto.class);
        } catch (URISyntaxException e) {
            LOGGER.error("Feil ved oppretting av URI.", e);
        }
        return behandling.orElseThrow(() -> new IllegalStateException("Klarte ikke hente behandling: " + behandlingId));
    }

    @Override
    public <T> Optional<T> hentDtoFraLink(BehandlingResourceLink link, Class<T> clazz) {

        if ("POST".equals(link.getType())) {
            return oidcRestClient.postReturnsOptional(URI.create(endpointFpsakRestBase + link.getHref()), link.getRequestPayload(), clazz);
        }
        return oidcRestClient.getReturnsOptional(saksnummerRequest(endpointFpsakRestBase + link.getHref(), link.getRequestPayload()), clazz);
    }

    private static URI saksnummerRequest(String endpoint, BehandlingRelLinkPayload payload) {
        try {
            var uriBuilder = new URIBuilder(endpoint);
            if (payload != null) {
                // Hvis payloaden er null, er GET parameterne antagelivis allerede satt i urlen
                if (payload.saksnummer() != null) {
                    uriBuilder.addParameter(SAKSNUMMER, String.valueOf(payload.saksnummer()));
                }
                if (payload.behandlingUuid() != null) {
                    uriBuilder.addParameter(BEHANDLING_ID, String.valueOf(payload.behandlingUuid()));
                }
            }
            return uriBuilder.build();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [oidcRestClient=" + oidcRestClient + ", endpointFpsakRestBase=" + endpointFpsakRestBase + "]";
    }
}
