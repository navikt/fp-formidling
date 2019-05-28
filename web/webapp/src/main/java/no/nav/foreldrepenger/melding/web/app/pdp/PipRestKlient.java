package no.nav.foreldrepenger.melding.web.app.pdp;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.melding.typer.AktørId;
import no.nav.vedtak.felles.integrasjon.rest.OidcRestClient;
import no.nav.vedtak.felles.integrasjon.rest.SystemUserOidcRestClient;
import no.nav.vedtak.konfig.KonfigVerdi;

@ApplicationScoped
//For Future Use
public class PipRestKlient {

    private static final Logger LOGGER = LoggerFactory.getLogger(PipRestKlient.class);

    private static final String FPSAK_REST_BASE_URL = "fpsak_rest_base.url";
    private static final String PIP_BEHANDLING_ENDPOINT = "/fpsak/api/pip/aktoer-for-behandling";

    private SystemUserOidcRestClient oidcRestClient;
    private String endpointFpsakRestBase;

    public PipRestKlient() {
        //CDI
    }

    @Inject
    public PipRestKlient(SystemUserOidcRestClient oidcRestClient,
                         @KonfigVerdi(FPSAK_REST_BASE_URL) String endpointFpsakRestBase) {
        this.oidcRestClient = oidcRestClient;
        this.endpointFpsakRestBase = endpointFpsakRestBase;
    }

    public List<AktørId> hentAktørerForBehandling(String behandlingUUid) {
        List<AktørId> aktører = new ArrayList<>();
        try {
            URIBuilder pipUriBuilder = new URIBuilder(endpointFpsakRestBase + PIP_BEHANDLING_ENDPOINT);
            pipUriBuilder.setParameter("behandlingUuid", behandlingUUid);
            aktører = oidcRestClient.getReturnsOptional(pipUriBuilder.build(), AktørId[].class)
                    .map(Arrays::asList)
                    .orElseThrow(IllegalStateException::new);
        } catch (URISyntaxException e) {
            LOGGER.error("Feil ved oppretting av URI.", e);
        }
        return aktører;
    }

}
