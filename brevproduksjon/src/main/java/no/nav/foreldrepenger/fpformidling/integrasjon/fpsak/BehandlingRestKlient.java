package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;

import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.fpformidling.behandling.BehandlingRelLinkPayload;
import no.nav.foreldrepenger.fpformidling.behandling.BehandlingResourceLink;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.konfig.KonfigVerdi;
import no.nav.foreldrepenger.kontrakter.formidling.v1.DokumentProdusertDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagDto;
import no.nav.vedtak.felles.integrasjon.rest.OidcRestClient;

@Dependent
@Deprecated
/*
 *
 * @see JerseyBehandlinger
 * @deprecated
 */
public class BehandlingRestKlient implements Behandlinger {
    private static final Logger LOGGER = LoggerFactory.getLogger(BehandlingRestKlient.class);
    protected static final String FPSAK_API = "/fpsak/api";

    private final OidcRestClient oidcRestClient;
    private final URI endpointFpsakRestBase;

    @Inject
    public BehandlingRestKlient(OidcRestClient oidcRestClient,
                                @KonfigVerdi("fpsak.rest.base.url") URI endpointFpsakRestBase) {
        this.oidcRestClient = oidcRestClient;
        this.endpointFpsakRestBase = endpointFpsakRestBase;
    }

    @Override
    public void kvitterDokument(DokumentProdusertDto kvittering) {
        oidcRestClient.post(toUri(endpointFpsakRestBase, FPSAK_API, "/brev/kvittering"), kvittering);
    }

    @Override
    public BehandlingDto hentBehandling(UUID behandlingId) {
        Optional<BehandlingDto> behandling = Optional.empty();
        try {
            var behandlingUriBuilder = new URIBuilder(endpointFpsakRestBase + FPSAK_API + "/formidling/ressurser");
            behandlingUriBuilder.setParameter("behandlingId", behandlingId.toString());
            behandling = oidcRestClient.getReturnsOptional(behandlingUriBuilder.build(), BehandlingDto.class);
        } catch (URISyntaxException e) {
            LOGGER.error("Feil ved oppretting av URI.", e);
        }
        return behandling.orElseThrow(() -> new IllegalStateException("Klarte ikke hente behandling: " + behandlingId));
    }

    @Override
    public <T> Optional<T> hentDtoFraLink(BehandlingResourceLink link, Class<T> clazz) {

        if ("POST".equals(link.getType())) {
            return oidcRestClient.postReturnsOptional(URI.create(endpointFpsakRestBase + link.getHref()), link.getRequestPayload(), clazz);
        }
        return oidcRestClient.getReturnsOptional(saksnummerRequest(endpointFpsakRestBase + link.getHref(), link.getRequestPayload()), clazz);
    }

    @Override
    public Optional<BeregningsgrunnlagDto> hentBeregningsgrunnlagV2HvisFinnes(UUID behandlingUuid) {
        try {
            var uriBuilder = new URIBuilder(endpointFpsakRestBase + FPSAK_API + "/formidling/beregningsgrunnlag/v2");
            return oidcRestClient.getReturnsOptional(
                    uriBuilder.addParameter("uuid", behandlingUuid.toString()).build(),
                    BeregningsgrunnlagDto.class);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static URI saksnummerRequest(String endpoint, BehandlingRelLinkPayload payload) {
        try {
            var uriBuilder = new URIBuilder(endpoint);
            if (payload != null) {
                // Hvis payloaden er null, er GET parameterne antagelivis allerede satt i urlen
                if (payload.saksnummer() != null) {
                    uriBuilder.addParameter("saksnummer", String.valueOf(payload.saksnummer()));
                }
                if (payload.behandlingUuid() != null) {
                    uriBuilder.addParameter("behandlingId", String.valueOf(payload.behandlingUuid()));
                }
            }
            return uriBuilder.build();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [oidcRestClient=" + oidcRestClient + ", endpointFpsakRestBase=" + endpointFpsakRestBase + "]";
    }

    private URI toUri(URI baseURI, String apiPath, String resourcePath) {
        try {
            return UriBuilder.fromUri(baseURI).path(apiPath).path(resourcePath).build();
        } catch (Exception e) {
            throw new IllegalArgumentException("Ugyldig uri: " + baseURI + apiPath + resourcePath, e);
        }
    }
}
