package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen;

import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.felles.integrasjon.rest.*;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.UriBuilder;

import java.util.Objects;

@ApplicationScoped
@RestClientConfig(tokenConfig = TokenFlow.NO_AUTH_NEEDED, endpointProperty = "dokgen.rest.base.url", endpointDefault = "http://fpdokgen.teamforeldrepenger")
public class DokgenRestKlient implements Dokgen {

    private final RestClient restClient;
    private final RestConfig restConfig;


    public DokgenRestKlient() {
        this(RestClient.client());
    }

    public DokgenRestKlient(RestClient restClient) {
        this.restClient = restClient;
        this.restConfig = RestConfig.forClient(DokgenRestKlient.class);
    }

    @Override
    public byte[] genererPdf(String maltype, Språkkode språkkode, Dokumentdata dokumentdata) {
        var templatePath = String.format("/template/%s/template_%s", maltype.toLowerCase(), getSpråkkode(språkkode));
        var endpoint = UriBuilder.fromUri(restConfig.endpoint()).path(templatePath).path("/create-pdf-variation").build();
        var request = RestRequest.newPOSTJson(dokumentdata, endpoint, restConfig);
        var pdf = restClient.sendReturnByteArray(request);

        if (pdf == null || pdf.length == 0) {
            throw new TekniskException("FPFORMIDLING-946543",
                String.format("Fikk tomt svar ved kall til dokgen for mal %s og språkkode %s.", maltype, språkkode));
        }
        return pdf;
    }

    private String getSpråkkode(Språkkode språkkode) {
        return Objects.requireNonNullElse(språkkode, Språkkode.NB).name().toLowerCase();
    }
}
