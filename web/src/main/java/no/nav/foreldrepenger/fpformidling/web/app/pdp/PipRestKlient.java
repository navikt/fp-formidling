package no.nav.foreldrepenger.fpformidling.web.app.pdp;

import java.net.URISyntaxException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.fpformidling.web.app.pdp.dto.PipDto;
import no.nav.foreldrepenger.konfig.KonfigVerdi;
import no.nav.vedtak.felles.integrasjon.rest.SystemUserOidcRestClient;

@ApplicationScoped
//For Future Use
public class PipRestKlient {

    private static final Logger LOGGER = LoggerFactory.getLogger(PipRestKlient.class);

    private SystemUserOidcRestClient oidcRestClient;
    private String endpointFpsakRestBase;

    public PipRestKlient() {
        //CDI
    }

    @Inject
    public PipRestKlient(SystemUserOidcRestClient oidcRestClient,
                         @KonfigVerdi("fpsak.rest.base.url") String endpointFpsakRestBase) {
        this.oidcRestClient = oidcRestClient;
        this.endpointFpsakRestBase = endpointFpsakRestBase;
    }

    public PipDto hentPipdataForBehandling(String behandlingUUid) {
        try {
            URIBuilder pipUriBuilder = new URIBuilder(endpointFpsakRestBase + "/fpsak/api/pip/pipdata-for-behandling");
            pipUriBuilder.setParameter("behandlingUuid", behandlingUUid);
            return oidcRestClient.getReturnsOptional(pipUriBuilder.build(), PipDto.class)
                    .orElseThrow(IllegalStateException::new);
        } catch (URISyntaxException e) {
            LOGGER.error("Feil ved oppretting av URI.", e);
        }
        return null;
    }

}
