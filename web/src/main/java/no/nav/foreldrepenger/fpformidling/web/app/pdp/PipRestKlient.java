package no.nav.foreldrepenger.fpformidling.web.app.pdp;

import java.net.URI;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.vedtak.felles.integrasjon.rest.RestClient;
import no.nav.vedtak.felles.integrasjon.rest.RestClientConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestRequest;
import no.nav.vedtak.felles.integrasjon.rest.TokenFlow;
import no.nav.vedtak.sikkerhet.abac.pipdata.AbacPipDto;

@ApplicationScoped
@RestClientConfig(tokenConfig = TokenFlow.STS_CC, endpointProperty = "fpsak.rest.base.url", endpointDefault = "http://fpsak")
// TODO: forsøk å fikse hack i autotest som setter nais-cluster-name til localhost-fss. Da kan man bruke application = FpApplication.FPSAK
public class PipRestKlient {

    private static final Logger LOGGER = LoggerFactory.getLogger(PipRestKlient.class);

    private static final String PIP_PATH = "/api/pip";

    private RestClient restClient;
    private URI contextPath;

    public PipRestKlient() {
        //CDI
    }

    @Inject
    public PipRestKlient(RestClient restClient) {
        this.restClient = restClient;
        this.contextPath = RestConfig.endpointFromAnnotation(PipRestKlient.class);
    }


    public AbacPipDto hentPipdataForBehandling(UUID behandlingUUid) {
        try {
            var uri = UriBuilder.fromUri(contextPath)
                    .path(PIP_PATH)
                    .path("/pipdata-for-behandling-appintern")
                    .queryParam("behandlingUuid", behandlingUUid.toString())
                    .build();
            return restClient.sendReturnOptional(RestRequest.newGET(uri, TokenFlow.STS_CC, null), AbacPipDto.class)
                    .orElseThrow(IllegalStateException::new);
        } catch (IllegalArgumentException| UriBuilderException e) {
            LOGGER.error("Feil ved oppretting av URI.", e);
        }
        return null;
    }

}
