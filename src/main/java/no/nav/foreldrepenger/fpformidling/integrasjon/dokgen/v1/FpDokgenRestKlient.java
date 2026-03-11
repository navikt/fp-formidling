package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.v1;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.UriBuilder;
import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.Dokgen;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.felles.integrasjon.rest.RestClient;
import no.nav.vedtak.felles.integrasjon.rest.RestClientConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestConfig;
import no.nav.vedtak.felles.integrasjon.rest.RestRequest;
import no.nav.vedtak.felles.integrasjon.rest.TokenFlow;
import no.nav.vedtak.mapper.json.DefaultJsonMapper;

@ApplicationScoped
@RestClientConfig(tokenConfig = TokenFlow.AZUREAD_CC, endpointProperty = "fpdokgen.base.url", endpointDefault = "https://fp-dokgen.intern.nav.no/fpdokgen", scopesProperty = "fpdokgen.scopes", scopesDefault = "api://prod-gcp.teamforeldrepenger.fp-dokgen/.default")
public class FpDokgenRestKlient implements Dokgen {

    protected static final String API_PATH = "/api";
    private static final String V1_GENERER_PATH = "/v1/dokument/generer";

    private final RestClient restClient;
    private final RestConfig restConfig;

    public FpDokgenRestKlient() {
        this(RestClient.client());
    }

    public FpDokgenRestKlient(RestClient restClient) {
        this.restClient = restClient;
        this.restConfig = RestConfig.forClient(FpDokgenRestKlient.class);
    }

    @Override
    public byte[] genererPdf(String maltype, Språkkode språkkode, Dokumentdata dokumentdata) {
        var endpoint = UriBuilder.fromUri(restConfig.endpoint()).path(API_PATH).path(V1_GENERER_PATH).path("/pdf").build();
        var requestDto = new FpDokgenRequest(maltype, mapSpråk(språkkode), FpDokgenRequest.CssStyling.PDF, DefaultJsonMapper.toJson(dokumentdata));

        var request = RestRequest.newPOSTJson(requestDto, endpoint, restConfig)
            .header(HttpHeaders.ACCEPT, "application/pdf");
        var pdf = restClient.sendReturnByteArray(request);

        if (pdf == null || pdf.length == 0) {
            throw new TekniskException("FPFORMIDLING-946543",
                String.format("Fikk tomt svar ved kall til dokgen for mal %s og språkkode %s.", maltype, språkkode));
        }
        return pdf;
    }

    @Override
    public String genererHtml(String maltype, Språkkode språkkode, Dokumentdata dokumentdata) {
        var endpoint = UriBuilder.fromUri(restConfig.endpoint()).path(API_PATH).path(V1_GENERER_PATH).path("/html").build();
        // PDF styling brukes for å sikre at html-en som genereres er optimalisert for å kunne konverteres til PDF senere. HTML styling er aldri brukt og kunne vurderes fjernet.
        var requestDto = new FpDokgenRequest(maltype, mapSpråk(språkkode), FpDokgenRequest.CssStyling.PDF, DefaultJsonMapper.toJson(dokumentdata));

        var request = RestRequest.newPOSTJson(requestDto, endpoint, restConfig)
            .header(HttpHeaders.ACCEPT, "text/html");

        var html = restClient.sendReturnResponseString(request).body();

        if (html == null || html.isEmpty()) {
            throw new TekniskException("FPFORMIDLING-946543",
                String.format("Fikk tomt svar (html) ved kall til dokgen for mal %s og språkkode %s.", maltype, språkkode));
        }
        return html;
    }

    private FpDokgenRequest.Språk mapSpråk(Språkkode språkkode) {
        return switch (språkkode) {
            case NB -> FpDokgenRequest.Språk.BOKMÅL;
            case NN -> FpDokgenRequest.Språk.NYNORSK;
            case EN -> FpDokgenRequest.Språk.ENGELSK;
            case null -> FpDokgenRequest.Språk.BOKMÅL;
        };
    }
}
