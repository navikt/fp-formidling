package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;

import no.nav.foreldrepenger.fpformidling.behandling.BehandlingRelLinkPayload;
import no.nav.foreldrepenger.fpformidling.behandling.BehandlingResourceLink;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.kontrakter.formidling.v1.DokumentProdusertDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagDto;
import no.nav.vedtak.felles.integrasjon.rest.FpApplication;
import no.nav.vedtak.felles.integrasjon.rest.NativeClient;
import no.nav.vedtak.felles.integrasjon.rest.RestClient;
import no.nav.vedtak.felles.integrasjon.rest.RestClientConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestRequest;
import no.nav.vedtak.felles.integrasjon.rest.TokenFlow;

@ApplicationScoped
@NativeClient
@RestClientConfig(tokenConfig = TokenFlow.ADAPTIVE, application = FpApplication.FPSAK)
public class BehandlingNativeRestKlient implements Behandlinger {
    protected static final String FPSAK_API = "/api";

    private RestClient restClient;
    private URI endpointFpsakRestBase;

    BehandlingNativeRestKlient() {
        // CDI
    }

    @Inject
    public BehandlingNativeRestKlient(RestClient restClient) {
        this.restClient = restClient;
        this.endpointFpsakRestBase = RestConfig.contextPathFromAnnotation(BehandlingNativeRestKlient.class);
    }

    @Override
    public void kvitterDokument(DokumentProdusertDto kvittering) {
        var request = RestRequest.newPOSTJson(kvittering, toUri(endpointFpsakRestBase, FPSAK_API, "/brev/kvittering"), BehandlingNativeRestKlient.class);
        restClient.sendReturnOptional(request, String.class);
    }

    @Override
    public BehandlingDto hentBehandling(UUID behandlingId) {

        var behandlingUri = UriBuilder.fromUri(endpointFpsakRestBase)
                .path(FPSAK_API)
                .path("/formidling/ressurser")
                .queryParam("behandlingId", behandlingId.toString())
                .build();
        var request = RestRequest.newGET(behandlingUri, BehandlingNativeRestKlient.class);
        return restClient.sendReturnOptional(request, BehandlingDto.class)
                .orElseThrow(() -> new IllegalStateException("Klarte ikke hente behandling: " + behandlingId));
    }

    @Override
    public <T> Optional<T> hentDtoFraLink(BehandlingResourceLink link, Class<T> clazz) {
        var linkpath = link.getHref();
        var path = linkpath.startsWith("/fpsak") ?  linkpath.replaceFirst("/fpsak", "") : linkpath;
        var uri = URI.create(endpointFpsakRestBase + path);
        if ("POST".equals(link.getType())) {
            var request = RestRequest.newPOSTJson(link.getRequestPayload(), uri, BehandlingNativeRestKlient.class);
            return restClient.sendReturnOptional(request, clazz);
        }
        return restClient.sendReturnOptional(saksnummerRequest(uri, link.getRequestPayload()), clazz);
    }

    @Override
    public Optional<BeregningsgrunnlagDto> hentBeregningsgrunnlagV2HvisFinnes(UUID behandlingUuid) {
        var uriBuilder = UriBuilder.fromUri(endpointFpsakRestBase)
                .path(FPSAK_API).path("/formidling/beregningsgrunnlag/v2")
                .queryParam("uuid", behandlingUuid.toString());
        return restClient.sendReturnOptional(RestRequest.newGET(uriBuilder.build(), BehandlingNativeRestKlient.class), BeregningsgrunnlagDto.class);
    }

    private static RestRequest saksnummerRequest(URI uri, BehandlingRelLinkPayload payload) {
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
            return RestRequest.newGET(brukUri, BehandlingNativeRestKlient.class);
        } catch (IllegalArgumentException|UriBuilderException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [restClient=" + restClient + ", endpointFpsakRestBase=" + endpointFpsakRestBase + "]";
    }

    private URI toUri(URI baseURI, String apiPath, String resourcePath) {
        try {
            return UriBuilder.fromUri(baseURI).path(apiPath).path(resourcePath).build();
        } catch (Exception e) {
            throw new IllegalArgumentException("Ugyldig uri: " + baseURI + apiPath + resourcePath, e);
        }
    }
}
