package no.nav.foreldrepenger.melding.integrasjon.dokgen;

import java.net.URISyntaxException;
import java.util.Optional;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.Dokumentdata;
import no.nav.vedtak.felles.integrasjon.rest.OidcRestClient;
import no.nav.vedtak.konfig.KonfigVerdi;

@ApplicationScoped
public class DokgenRestKlient {
    private static final Logger LOGGER = LoggerFactory.getLogger(DokgenRestKlient.class);
    private static final String DOKGEN_REST_BASE_URL = "dokgen_rest_base.url";
    private static final String CREATE_PDF = "/create-pdf-variation";
    private static final Set<Språkkode> STØTTEDE_SPRÅK = Set.of(Språkkode.nb, Språkkode.nn);

    private OidcRestClient oidcRestClient;
    private String endpointDokgenRestBase;

    public DokgenRestKlient() {
        //CDI
    }

    @Inject
    public DokgenRestKlient(OidcRestClient oidcRestClient,
                            @KonfigVerdi(DOKGEN_REST_BASE_URL) String endpointDokgenRestBase) {
        this.oidcRestClient = oidcRestClient;
        this.endpointDokgenRestBase = endpointDokgenRestBase;
    }

    public Optional<byte[]> genererPdf(String maltype, Språkkode språkKode, Dokumentdata dokumentdata) {
        Optional<byte[]> pdf = Optional.empty();
        try {
            String templatePath = String.format("/template/%s/template_%s", maltype.toLowerCase(), getSpråkkode(språkKode));
            URIBuilder uriBuilder = new URIBuilder(endpointDokgenRestBase + templatePath + CREATE_PDF);
            pdf = oidcRestClient.postReturnsOptionalOfByteArray(uriBuilder.build(), dokumentdata);
        } catch (URISyntaxException e) {
            LOGGER.error("Feil ved oppretting av URI.", e);
        }
        return pdf;
    }

    private String getSpråkkode(Språkkode språkkode) {
        return STØTTEDE_SPRÅK.contains(språkkode) ? språkkode.getKode().toLowerCase() : Språkkode.nb.getKode().toLowerCase();
    }
}