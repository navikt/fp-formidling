package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen;

import static javax.ws.rs.client.Entity.json;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpformidling.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.foreldrepenger.konfig.KonfigVerdi;
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.felles.integrasjon.rest.jersey.AbstractJerseyOidcRestClient;
import no.nav.vedtak.felles.integrasjon.rest.jersey.Jersey;

@Jersey
@Dependent
public class JerseyDokgenKlient extends AbstractJerseyOidcRestClient implements Dokgen {
    private final URI baseUri;

    @Inject
    public JerseyDokgenKlient(@KonfigVerdi("dokgen.rest.base.url") URI baseUri) {
        this.baseUri = baseUri;
    }

    @Override
    public byte[] genererPdf(String mal, Språkkode språk, Dokumentdata data) {
        return Optional.ofNullable(invoke(client.target(baseUri)
                .path("/template/{mal}/template_{språk}/create-pdf-variation")
                .resolveTemplates(Map.of("mal", mal, "språk", språkkode(språk)))
                .request(APPLICATION_JSON_TYPE)
                .buildPost(json(data)), byte[].class))
                .orElseThrow(() -> new TekniskException("FPFORMIDLING-946543",
                        String.format("Fikk tomt svar ved kall til dokgen for mal %s og språkkode %s.", mal, språk)));
    }

    private String språkkode(Språkkode språkkode) {
        return Objects.requireNonNullElse(språkkode, Språkkode.NB).name().toLowerCase();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [baseUri=" + baseUri + "]";
    }
}
