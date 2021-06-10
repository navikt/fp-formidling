package no.nav.foreldrepenger.melding.integrasjon.dokgen;

import static java.lang.String.format;
import static javax.ws.rs.client.Entity.json;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import no.nav.foreldrepenger.konfig.KonfigVerdi;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.Dokumentdata;
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.felles.integrasjon.rest.jersey.AbstractJerseyRestClient;
import no.nav.vedtak.felles.integrasjon.rest.jersey.Jersey;

@Jersey
@Dependent
public class JerseyDokgenKlient extends AbstractJerseyRestClient implements Dokgen {
    private static final String KODE = "FPFORMIDLING-946543";
    private static final String DOKGEN_REST_BASE_URI = "dokgen_rest_base.url";
    private static final String TEMPLATE = "/template/{mal}/template_{språk}/create-pdf-variation";
    private final URI baseUri;

    @Inject
    public JerseyDokgenKlient(@KonfigVerdi(DOKGEN_REST_BASE_URI) URI baseUri) {
        this.baseUri = baseUri;
    }

    @Override
    public byte[] genererPdf(String mal, Språkkode språk, Dokumentdata dokumentdata) {
        return Optional.ofNullable(invoke(client.target(baseUri)
                .path(TEMPLATE)
                .resolveTemplates(Map.of("mal", mal, "språk", språk.getKode()))
                .request(APPLICATION_JSON_TYPE)
                .buildPost(json(dokumentdata)), byte[].class))
                .orElseThrow(() -> new TekniskException(KODE,
                        format("Fikk tomt svar ved kall til dokgen for mal %s og språkkode %s.", mal, språk.getKode())));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [baseUri=" + baseUri + "]";
    }
}
