package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;

import no.nav.foreldrepenger.fpformidling.behandling.BehandlingResourceLink;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.konfig.KonfigVerdi;
import no.nav.foreldrepenger.kontrakter.formidling.v1.DokumentProdusertDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagDto;
import no.nav.vedtak.felles.integrasjon.rest.jersey.AbstractJerseyRestClient;
import no.nav.vedtak.felles.integrasjon.rest.jersey.Jersey;

@Dependent
@Jersey
public class JerseyBehandlinger extends AbstractJerseyRestClient implements Behandlinger {

    protected static final String FPSAK_API = "/fpsak/api";
    private final URI baseUri;

    @Inject
    public JerseyBehandlinger(@KonfigVerdi("fpsak.rest.base.url") URI baseUri) {
        this.baseUri = baseUri;
    }

    @Override
    public BehandlingDto hentBehandling(UUID behandlingId) {
        return Optional.ofNullable(client.target(baseUri).path(FPSAK_API)
                .path("/formidling/ressurser")
                .queryParam("behandlingId", behandlingId)
                .request(APPLICATION_JSON_TYPE)
                .get(BehandlingDto.class))
                .orElseThrow(() -> new IllegalStateException("Klarte ikke hente behandling: " + behandlingId));
    }

    @Override
    public Optional<BeregningsgrunnlagDto> hentBeregningsgrunnlagV2HvisFinnes(UUID behandlingUuid) {
        return Optional.ofNullable(
                invoke(client.target(baseUri).path(FPSAK_API)
                        .path("/formidling/beregningsgrunnlag/v2")
                        .queryParam("uuid", behandlingUuid)
                        .request(APPLICATION_JSON_TYPE)
                        .buildGet(), BeregningsgrunnlagDto.class
                )
        );
    }

    @Override
    public <T> Optional<T> hentDtoFraLink(BehandlingResourceLink link, Class<T> clazz) {
        if ("POST".equals(link.getType())) {
            return Optional.ofNullable(invoke(client.target(baseUri)
                    .path(link.getHref())
                    .request(APPLICATION_JSON_TYPE)
                    .buildPost(Entity.json(link.getRequestPayload())), clazz));
        }
        return Optional.ofNullable(invoke(invocation(link), clazz));
    }

    @Override
    public void kvitterDokument(DokumentProdusertDto kvittering) {
        invoke(client.target(baseUri).path(FPSAK_API)
                .path("/brev/kvittering")
                .request(APPLICATION_JSON_TYPE)
                .buildPost(Entity.json(kvittering)));
    }

    private Invocation invocation(BehandlingResourceLink link) {
        var target = client.target(baseUri).path(link.getHref());
        var payload = link.getRequestPayload();
        if (payload != null) {
            if (payload.saksnummer() != null) {
                target.queryParam("saksnummer", payload.saksnummer());
            }
            if (payload.behandlingUuid() != null) {
                target.queryParam("behandlingId", payload.behandlingUuid());
            }
        }
        return target
                .request(APPLICATION_JSON_TYPE)
                .buildGet();
    }
}