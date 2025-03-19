package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriBuilderException;
import no.nav.foreldrepenger.fpformidling.domene.behandling.BehandlingRelLinkPayload;
import no.nav.foreldrepenger.fpformidling.domene.behandling.BehandlingResourceLink;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.kontrakter.formidling.v3.DokumentKvitteringDto;
import no.nav.vedtak.felles.integrasjon.rest.FpApplication;
import no.nav.vedtak.felles.integrasjon.rest.RestClient;
import no.nav.vedtak.felles.integrasjon.rest.RestClientConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestRequest;
import no.nav.vedtak.felles.integrasjon.rest.TokenFlow;

@ApplicationScoped
@RestClientConfig(tokenConfig = TokenFlow.ADAPTIVE, application = FpApplication.FPSAK)
public class BehandlingRestKlient implements Behandlinger {
    protected static final String FPSAK_API = "/api";

    private final RestClient restClient;
    private final RestConfig restConfig;

    public BehandlingRestKlient() {
        this.restClient = RestClient.client();
        this.restConfig = RestConfig.forClient(this.getClass());
    }

    @Override
    public void kvitterDokument(DokumentKvitteringDto kvittering) {
        var request = RestRequest.newPOSTJson(kvittering, toUri(restConfig.fpContextPath(), FPSAK_API, "/brev/kvittering/v3"), restConfig);
        restClient.sendReturnOptional(request, String.class);
    }

    @Override
    public BehandlingDto hentBehandling(UUID behandlingUuid) {

        var behandlingUri = UriBuilder.fromUri(restConfig.fpContextPath())
            .path(FPSAK_API)
            .path("/formidling/v2/behandling")
            .queryParam("uuid", behandlingUuid.toString())
            .build();
        var request = RestRequest.newGET(behandlingUri, restConfig);
        return restClient.sendReturnOptional(request, BehandlingDto.class)
            .orElseThrow(() -> new IllegalStateException("Klarte ikke hente behandling: " + behandlingUuid));
    }

    @Override
    public <T> Optional<T> hentDtoFraLink(BehandlingResourceLink link, Class<T> clazz) {
        var linkpath = link.getHref();
        var path = linkpath.startsWith("/fpsak") ? linkpath.replaceFirst("/fpsak", "") : linkpath;
        var uri = URI.create(restConfig.fpContextPath() + path);
        if ("POST".equals(link.getType())) {
            var request = RestRequest.newPOSTJson(link.getRequestPayload(), uri, restConfig);
            return restClient.sendReturnOptional(request, clazz);
        }
        return restClient.sendReturnOptional(saksnummerRequest(uri, link.getRequestPayload()), clazz);
    }

    private RestRequest saksnummerRequest(URI uri, BehandlingRelLinkPayload payload) {
        try {
            var brukUri = uri;
            if (payload != null) {
                var uriBuilder = UriBuilder.fromUri(uri);
                // Hvis payloaden er null, er GET parameterne antagelivis allerede satt i urlen
                if (payload.saksnummer() != null) {
                    uriBuilder.queryParam("saksnummer", String.valueOf(payload.saksnummer()));
                }
                if (payload.behandlingUuid() != null) {
                    uriBuilder.queryParam("behandlingId", String.valueOf(payload.behandlingUuid()));
                }
                brukUri = uriBuilder.build();
            }
            return RestRequest.newGET(brukUri, restConfig);
        } catch (IllegalArgumentException | UriBuilderException e) {
            throw new IllegalArgumentException(e);
        }
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
