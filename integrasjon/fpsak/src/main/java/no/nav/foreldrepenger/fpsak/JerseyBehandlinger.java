package no.nav.foreldrepenger.fpsak;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;

import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.konfig.KonfigVerdi;
import no.nav.foreldrepenger.melding.behandling.BehandlingResourceLink;
import no.nav.vedtak.felles.integrasjon.rest.jersey.AbstractJerseyRestClient;
import no.nav.vedtak.felles.integrasjon.rest.jersey.Jersey;

@Dependent
@Jersey
public class JerseyBehandlinger extends AbstractJerseyRestClient implements Behandlinger {

    private static final String FPSAK_REST_BASE_URL = "fpsak_rest_base.url";
    private static final String HENT_BEHANLDING_ENDPOINT = "/fpsak/api/formidling/ressurser";
    private static final String SAKSNUMMER = "saksnummer";
    private static final String BEHANDLING_ID = "behandlingId";
    private final URI baseUri;

    @Inject
    public JerseyBehandlinger(@KonfigVerdi(FPSAK_REST_BASE_URL) URI baseUri) {
        this.baseUri = baseUri;
    }

    @Override
    public BehandlingDto hentBehandling(UUID behandlingId) {
        return Optional.ofNullable(client.target(baseUri)
                .path(HENT_BEHANLDING_ENDPOINT)
                .queryParam(BEHANDLING_ID, behandlingId)
                .request(APPLICATION_JSON_TYPE)
                .get(BehandlingDto.class))
                .orElseThrow(() -> new IllegalStateException("Klarte ikke hente behandling: " + behandlingId));
    }

    @Override
    public <T> Optional<T> hentDtoFraLink(BehandlingResourceLink link, Class<T> clazz) {
        if ("POST".equals(link.getType())) {
            return Optional.ofNullable(invoke(client.target(baseUri)
                    .path(link.getHref())
                    .request(APPLICATION_JSON_TYPE)
                    .buildPost(Entity.json(link.getRequestPayload())), clazz));
        }
        return Optional.ofNullable(invoke(invocation(link), clazz));
    }

    private Invocation invocation(BehandlingResourceLink link) {
        var target = client.target(baseUri).path(link.getHref());
        var payload = link.getRequestPayload();
        if (payload != null) {
            if (payload.saksnummer() != null) {
                target.queryParam(SAKSNUMMER, payload.saksnummer());
            }
            if (payload.behandlingUuid() != null) {
                target.queryParam(BEHANDLING_ID, payload.behandlingUuid());
            }
        }
        return target
                .request(APPLICATION_JSON_TYPE)
                .buildGet();
    }
}