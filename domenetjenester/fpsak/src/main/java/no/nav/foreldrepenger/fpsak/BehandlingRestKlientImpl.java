package no.nav.foreldrepenger.fpsak;

import java.net.URISyntaxException;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingIdDto;
import no.nav.vedtak.felles.integrasjon.rest.OidcRestClient;
import no.nav.vedtak.konfig.KonfigVerdi;

@ApplicationScoped
public class BehandlingRestKlientImpl implements BehandlingRestKlient {
    private static final Logger LOGGER = LoggerFactory.getLogger(BehandlingRestKlientImpl.class);
    private static final String FPSAK_REST_BASE_URL = "fpsak_rest_base.url";
    private static final String HENT_BEHANLDING_ENDPOINT = "/fpsak/api/behandlinger";

    private OidcRestClient oidcRestClient;
    private String endpointFpsakRestBase;

    public BehandlingRestKlientImpl() {//For CDI
    }

    @Inject
    public BehandlingRestKlientImpl(OidcRestClient oidcRestClient,
                                    @KonfigVerdi(FPSAK_REST_BASE_URL) String endpointFpsakRestBase) {
        this.oidcRestClient = oidcRestClient;
        this.endpointFpsakRestBase = endpointFpsakRestBase;
    }

    @Override
    public Optional<BehandlingDto> hentBehandling(BehandlingIdDto behandlingIdDto) {
        Optional<BehandlingDto> behandling = Optional.empty();
        try {
            URIBuilder uriBuilder = new URIBuilder(endpointFpsakRestBase + HENT_BEHANLDING_ENDPOINT);
            uriBuilder.setParameter("behandlingId", String.valueOf(behandlingIdDto.getBehandlingId()));

            behandling = oidcRestClient.getReturnsOptional(uriBuilder.build(), BehandlingDto.class);
        } catch (URISyntaxException e) {
            LOGGER.error("Feil ved oppretting av URI.", e);
        }
        return behandling;
    }
}
