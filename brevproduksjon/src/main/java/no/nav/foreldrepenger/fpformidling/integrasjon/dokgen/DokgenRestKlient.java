package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen;

import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.fpformidling.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.foreldrepenger.konfig.KonfigVerdi;
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.felles.integrasjon.rest.OidcRestClient;

@ApplicationScoped
public class DokgenRestKlient implements Dokgen {
    private static final Logger LOGGER = LoggerFactory.getLogger(DokgenRestKlient.class);

    private OidcRestClient oidcRestClient;
    private String endpointDokgenRestBase;

    public DokgenRestKlient() {
        // CDI
    }

    @Inject
    public DokgenRestKlient(OidcRestClient oidcRestClient,
                            @KonfigVerdi("dokgen.rest.base.url") String endpointDokgenRestBase) {
        this.oidcRestClient = oidcRestClient;
        this.endpointDokgenRestBase = endpointDokgenRestBase;
    }

    @Override
    public byte[] genererPdf(String maltype, Språkkode språkkode, Dokumentdata dokumentdata) {
        Optional<byte[]> pdf;
        try {
            String templatePath = String.format("/template/%s/template_%s", maltype.toLowerCase(), getSpråkkode(språkkode));
            URIBuilder uriBuilder = new URIBuilder(endpointDokgenRestBase + templatePath + "/create-pdf-variation");
            LOGGER.info("Kaller Dokgen for generering av mal {} på språk {}", maltype, språkkode);
            pdf = oidcRestClient.postReturnsOptionalOfByteArray(uriBuilder.build(), dokumentdata);
        } catch (Exception e) {
            throw new TekniskException("FPFORMIDLING-946544",
                    String.format("Fikk feil ved kall til dokgen for mal %s og språkkode %s", maltype, språkkode), e);
        }
        if (pdf.isEmpty()) {
            throw new TekniskException("FPFORMIDLING-946543",
                    String.format("Fikk tomt svar ved kall til dokgen for mal %s og språkkode %s.", maltype, språkkode));
        }
        return pdf.get();
    }

    private String getSpråkkode(Språkkode språkkode) {
        return Objects.requireNonNullElse(språkkode, Språkkode.NB).name().toLowerCase();
    }
}