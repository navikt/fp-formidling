package no.nav.foreldrepenger.melding.integrasjon.dokgen;

import java.util.Optional;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.konfig.KonfigVerdi;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.felles.integrasjon.rest.OidcRestClient;

@ApplicationScoped
public class DokgenRestKlient implements Dokgen {
    private static final Logger LOGGER = LoggerFactory.getLogger(DokgenRestKlient.class);
    private static final String DOKGEN_REST_BASE_URL = "dokgen_rest_base.url";
    private static final String CREATE_PDF = "/create-pdf-variation";
    private static final Set<Språkkode> STØTTEDE_SPRÅK = Set.of(Språkkode.NB, Språkkode.NN, Språkkode.EN);

    private OidcRestClient oidcRestClient;
    private String endpointDokgenRestBase;

    public DokgenRestKlient() {
        // CDI
    }

    @Inject
    public DokgenRestKlient(OidcRestClient oidcRestClient,
            @KonfigVerdi(DOKGEN_REST_BASE_URL) String endpointDokgenRestBase) {
        this.oidcRestClient = oidcRestClient;
        this.endpointDokgenRestBase = endpointDokgenRestBase;
    }

    @Override
    public byte[] genererPdf(String maltype, Språkkode språkkode, Dokumentdata dokumentdata) {
        Optional<byte[]> pdf;
        try {
            String templatePath = String.format("/template/%s/template_%s", maltype.toLowerCase(), getSpråkkode(språkkode));
            URIBuilder uriBuilder = new URIBuilder(endpointDokgenRestBase + templatePath + CREATE_PDF);
            LOGGER.info("Kaller Dokgen for generering av mal {} på språk {}", maltype, språkkode.getKode());
            pdf = oidcRestClient.postReturnsOptionalOfByteArray(uriBuilder.build(), dokumentdata);
        } catch (Exception e) {
            throw new TekniskException("FPFORMIDLING-946544",
                    String.format("Fikk feil ved kall til dokgen for mal %s og språkkode %s", maltype, språkkode.getKode()), e);
        }
        if (pdf.isEmpty()) {
            throw new TekniskException("FPFORMIDLING-946543",
                    String.format("Fikk tomt svar ved kall til dokgen for mal %s og språkkode %s.", maltype, språkkode.getKode()));
        }
        return pdf.get();
    }

    private String getSpråkkode(Språkkode språkkode) {
        return STØTTEDE_SPRÅK.contains(språkkode) ? språkkode.getKode().toLowerCase() : Språkkode.NB.getKode().toLowerCase();
    }
}