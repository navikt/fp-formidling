package no.nav.foreldrepenger.fpformidling.server.abac;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriBuilderException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.vedtak.felles.integrasjon.rest.FpApplication;
import no.nav.vedtak.felles.integrasjon.rest.RestClient;
import no.nav.vedtak.felles.integrasjon.rest.RestClientConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestRequest;
import no.nav.vedtak.felles.integrasjon.rest.TokenFlow;
import no.nav.vedtak.sikkerhet.abac.pipdata.AbacPipDto;

@ApplicationScoped
@RestClientConfig(tokenConfig = TokenFlow.AZUREAD_CC, application = FpApplication.FPSAK)
public class PipRestKlient {

    private static final Logger LOG = LoggerFactory.getLogger(PipRestKlient.class);

    private static final String PIP_PATH = "/api/pip";

    private final RestClient restClient;
    private final RestConfig restConfig;

    public PipRestKlient() {
        this.restClient = RestClient.client();
        this.restConfig = RestConfig.forClient(this.getClass());
    }


    public AbacPipDto hentPipdataForBehandling(UUID behandlingUUid) {
        try {
            var uri = UriBuilder.fromUri(restConfig.fpContextPath())
                .path(PIP_PATH)
                .path("/pipdata-for-behandling-appintern")
                .queryParam("behandlingUuid", behandlingUUid.toString())
                .build();
            return restClient.sendReturnOptional(RestRequest.newGET(uri, restConfig), AbacPipDto.class).orElseThrow(IllegalStateException::new);
        } catch (IllegalArgumentException | UriBuilderException e) {
            LOG.error("Feil ved oppretting av URI.", e);
        }
        return null;
    }

}
