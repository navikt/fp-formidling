package no.nav.foreldrepenger.fpsak;

import static org.terracotta.modules.ehcache.ToolkitInstanceFactoryImpl.LOGGER;

import java.net.URISyntaxException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.http.client.utils.URIBuilder;

import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingIdDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.innsyn.InnsynsbehandlingDto;
import no.nav.vedtak.felles.integrasjon.rest.OidcRestClient;
import no.nav.vedtak.konfig.KonfigVerdi;

@ApplicationScoped
public class InnsynRestKlientImpl implements InnsynRestKlient {

    //TODO burde kunne sentraliseres
    private static final String FPSAK_REST_BASE_URL = "fpsak_rest_base.url";
    private static final String HENT_INNSYN_ENDPOINT = "/fpsak/api/behandling/innsyn";

    private String endpointFpsakRestBase;
    private OidcRestClient oidcRestClient;


    public InnsynRestKlientImpl() {
    }

    @Inject
    public InnsynRestKlientImpl(OidcRestClient oidcRestClient,
                                @KonfigVerdi(FPSAK_REST_BASE_URL) String endpointFpsakRestBase) {
        this.endpointFpsakRestBase = endpointFpsakRestBase;
        this.oidcRestClient = oidcRestClient;
    }

    @Override
    public InnsynsbehandlingDto hentInnsynsbehandling(BehandlingIdDto behandlingIdDto) {
        try {
            URIBuilder innsynURIBuilder = new URIBuilder(endpointFpsakRestBase + HENT_INNSYN_ENDPOINT);
            innsynURIBuilder.setParameter("behandlingId", String.valueOf(behandlingIdDto.getBehandlingId()));
            return oidcRestClient.getReturnsOptional(innsynURIBuilder.build(), InnsynsbehandlingDto.class)
                    .orElseThrow(() -> {
                        throw new IllegalStateException("Klarer ikke hente innsyn: " + behandlingIdDto.getBehandlingId());
                    });
        } catch (URISyntaxException e) {
            LOGGER.error("Feil ved oppretting av URI.", e);
        }
        throw new IllegalStateException("Klarer ikke hente innsyn: " + behandlingIdDto.getBehandlingId());
    }
}
