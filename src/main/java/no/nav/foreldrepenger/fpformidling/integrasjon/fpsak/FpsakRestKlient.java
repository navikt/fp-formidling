package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak;

import java.net.URI;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.UriBuilder;
import no.nav.foreldrepenger.kontrakter.formidling.v3.DokumentKvitteringDto;
import no.nav.vedtak.felles.integrasjon.rest.FpApplication;
import no.nav.vedtak.felles.integrasjon.rest.RestClient;
import no.nav.vedtak.felles.integrasjon.rest.RestClientConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestRequest;
import no.nav.vedtak.felles.integrasjon.rest.TokenFlow;

@ApplicationScoped
@RestClientConfig(tokenConfig = TokenFlow.ADAPTIVE, application = FpApplication.FPSAK)
public class FpsakRestKlient {
    protected static final String FPSAK_API = "/api";

    private final RestClient restClient;
    private final RestConfig restConfig;

    public FpsakRestKlient() {
        this.restClient = RestClient.client();
        this.restConfig = RestConfig.forClient(this.getClass());
    }

    public BrevGrunnlagDto hentBrevGrunnlag(UUID behandlingId) {
        var behandlingUri2 = UriBuilder.fromUri(restConfig.fpContextPath())
            .path(FPSAK_API)
            .path("/formidling/grunnlag")
            .queryParam("behandlingId", behandlingId.toString())
            .build();
        var request2 = RestRequest.newGET(behandlingUri2, restConfig);
        return restClient.sendReturnOptional(request2, BrevGrunnlagDto.class)
            .orElseThrow(() -> new IllegalStateException("Klarte ikke hente brevgrunnlag: " + behandlingId));
    }

    public void kvitterDokument(DokumentKvitteringDto kvittering) {
        var request = RestRequest.newPOSTJson(kvittering, toUri(restConfig.fpContextPath(), FPSAK_API, "/brev/kvittering/v3"), restConfig);
        restClient.sendReturnOptional(request, String.class);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [restClient=" + restClient + ", endpointFpsakRestBase=" + restConfig.fpContextPath() + "]";
    }

    private URI toUri(URI baseURI, String apiPath, String resourcePath) {
        try {
            return UriBuilder.fromUri(baseURI).path(apiPath).path(resourcePath).build();
        } catch (Exception e) {
            throw new IllegalArgumentException("Ugyldig uri: " + baseURI + apiPath + resourcePath, e);
        }
    }
}
