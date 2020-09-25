package no.nav.foreldrepenger.melding.integrasjon.dokgen;

import java.net.URISyntaxException;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.Dokumentdata;
import no.nav.vedtak.felles.integrasjon.rest.OidcRestClient;
import no.nav.vedtak.konfig.KonfigVerdi;

@ApplicationScoped
public class DokgenRestKlient {
    private static final Logger LOGGER = LoggerFactory.getLogger(DokgenRestKlient.class);
    private static final String DOKGEN_REST_BASE_URL = "dokgen_rest_base.url";
    private static final String CREATE_PDF = "/create-pdf";

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

    public Optional<byte[]> genererPdf(String maltype, Dokumentdata dokumentdata) {
        Optional<byte[]> pdf = Optional.empty();
        try {
            URIBuilder uriBuilder = new URIBuilder(endpointDokgenRestBase + String.format("/template/%s", maltype.toLowerCase()) + CREATE_PDF);
            pdf = oidcRestClient.postReturnsOptionalOfByteArray(uriBuilder.build(), dokumentdata);
        } catch (URISyntaxException e) {
            LOGGER.error("Feil ved oppretting av URI.", e);
        }
        return pdf;
    }
}