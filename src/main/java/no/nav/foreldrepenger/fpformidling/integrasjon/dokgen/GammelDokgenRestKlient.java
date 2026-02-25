package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen;

import java.util.Objects;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.UriBuilder;
import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.felles.integrasjon.rest.RestClient;
import no.nav.vedtak.felles.integrasjon.rest.RestClientConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestRequest;
import no.nav.vedtak.felles.integrasjon.rest.TokenFlow;

@ApplicationScoped
@GammelDokgen
@RestClientConfig(tokenConfig = TokenFlow.NO_AUTH_NEEDED, endpointProperty = "dokgen.rest.base.url", endpointDefault = "http://fpdokgen.teamforeldrepenger")
public class GammelDokgenRestKlient implements Dokgen {

    private final RestClient restClient;
    private final RestConfig restConfig;

    public GammelDokgenRestKlient() {
        this(RestClient.client());
    }

    public GammelDokgenRestKlient(RestClient restClient) {
        this.restClient = restClient;
        this.restConfig = RestConfig.forClient(GammelDokgenRestKlient.class);
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

    public String genererHtml(String maltype, Språkkode språkkode, Dokumentdata dokumentdata) {
        var templatePath = String.format("/template/%s/template_%s", maltype.toLowerCase(), getSpråkkode(språkkode));
        var endpoint = UriBuilder.fromUri(restConfig.endpoint()).path(templatePath).path("/create-html-variation-of-pdf").build();
        var request = RestRequest.newPOSTJson(dokumentdata, endpoint, restConfig);
        var html = restClient.sendReturnResponseString(request).body();

        if (html == null || html.isEmpty()) {
            throw new TekniskException("FPFORMIDLING-946543",
                String.format("Fikk tomt svar (html) ved kall til dokgen for mal %s og språkkode %s.", maltype, språkkode));
        }
        return html;
    }

    private String getSpråkkode(Språkkode språkkode) {
        return Objects.requireNonNullElse(språkkode, Språkkode.NB).name().toLowerCase();
    }
}
