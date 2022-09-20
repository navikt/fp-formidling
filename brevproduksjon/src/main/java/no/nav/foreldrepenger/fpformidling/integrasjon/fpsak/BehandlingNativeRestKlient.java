package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.fpformidling.behandling.BehandlingRelLinkPayload;
import no.nav.foreldrepenger.fpformidling.behandling.BehandlingResourceLink;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.kontrakter.formidling.v1.DokumentProdusertDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagDto;
import no.nav.vedtak.felles.integrasjon.rest.NativeClient;
import no.nav.vedtak.felles.integrasjon.rest.RestClient;
import no.nav.vedtak.felles.integrasjon.rest.RestClientConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestRequest;
import no.nav.vedtak.felles.integrasjon.rest.TokenFlow;

// TODO: lande konvensjon for fp-interne scopes. Legge inn i FpApplication i felles.
@Dependent
@NativeClient
@RestClientConfig(tokenConfig = TokenFlow.CONTEXT, endpointProperty = "fpsak.rest.base.url", endpointDefault = "http://fpsak")
public class BehandlingNativeRestKlient implements Behandlinger {
    private static final Logger LOGGER = LoggerFactory.getLogger(BehandlingNativeRestKlient.class);
    protected static final String FPSAK_API = "/fpsak/api";

    private final RestClient restClient;
    private final URI endpointFpsakRestBase;

    @Inject
    public BehandlingNativeRestKlient(RestClient restClient) {
        this.restClient = restClient;
        this.endpointFpsakRestBase = RestConfig.endpointFromAnnotation(BehandlingNativeRestKlient.class);
    }

    @Override
    public void kvitterDokument(DokumentProdusertDto kvittering) {
        var request = RestRequest.newPOSTJson(kvittering, toUri(endpointFpsakRestBase, FPSAK_API, "/brev/kvittering"), BehandlingNativeRestKlient.class);
        restClient.sendReturnOptional(request, String.class);
    }

    @Override
    public BehandlingDto hentBehandling(UUID behandlingId) {
        Optional<BehandlingDto> behandling = Optional.empty();
        try {
            var behandlingUri = UriBuilder.fromUri(endpointFpsakRestBase)
                    .path(FPSAK_API)
                    .path("/formidling/ressurser")
                    .queryParam("behandlingId", behandlingId.toString())
                    .build();
            var request = RestRequest.newGET(behandlingUri, BehandlingNativeRestKlient.class);
            behandling = restClient.sendReturnOptional(request, BehandlingDto.class);
        } catch (IllegalArgumentException|UriBuilderException e) {
            LOGGER.error("Feil ved oppretting av URI.", e);
        }
        return behandling.orElseThrow(() -> new IllegalStateException("Klarte ikke hente behandling: " + behandlingId));
    }

    @Override
    public <T> Optional<T> hentDtoFraLink(BehandlingResourceLink link, Class<T> clazz) {

        if ("POST".equals(link.getType())) {
            var request = RestRequest.newPOSTJson(link.getRequestPayload(), UriBuilder.fromUri(endpointFpsakRestBase + link.getHref()).build(), BehandlingNativeRestKlient.class);
            return restClient.sendReturnOptional(request, clazz);
        }
        return restClient.sendReturnOptional(saksnummerRequest(endpointFpsakRestBase + link.getHref(), link.getRequestPayload()), clazz);
    }

    @Override
    public Optional<BeregningsgrunnlagDto> hentBeregningsgrunnlagV2HvisFinnes(UUID behandlingUuid) {
        try {
            var uriBuilder = UriBuilder.fromUri(endpointFpsakRestBase)
                    .path(FPSAK_API).path("/formidling/beregningsgrunnlag/v2")
                    .queryParam("uuid", behandlingUuid.toString());
            return restClient.sendReturnOptional(RestRequest.newGET(uriBuilder.build(), BehandlingNativeRestKlient.class), BeregningsgrunnlagDto.class);
        } catch (IllegalArgumentException|UriBuilderException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static RestRequest saksnummerRequest(String endpoint, BehandlingRelLinkPayload payload) {
        try {
            var uriBuilder = UriBuilder.fromUri(endpoint);
            if (payload != null) {
                // Hvis payloaden er null, er GET parameterne antagelivis allerede satt i urlen
                if (payload.saksnummer() != null) {
                    uriBuilder.queryParam("saksnummer", String.valueOf(payload.saksnummer()));
                }
                if (payload.behandlingUuid() != null) {
                    uriBuilder.queryParam("behandlingId", String.valueOf(payload.behandlingUuid()));
                }
            }
            return RestRequest.newGET(uriBuilder.build(), BehandlingNativeRestKlient.class);
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
