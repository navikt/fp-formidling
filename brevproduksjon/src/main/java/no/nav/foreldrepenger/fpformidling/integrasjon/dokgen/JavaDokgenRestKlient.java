package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.fpformidling.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.ErrorResponse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.http.JavaClient;
import no.nav.foreldrepenger.fpformidling.integrasjon.http.JavaHttpKlient;
import no.nav.foreldrepenger.konfig.KonfigVerdi;
import no.nav.vedtak.exception.IntegrasjonException;
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.log.mdc.MDCOperations;
import no.nav.vedtak.sikkerhet.context.SubjectHandler;

@ApplicationScoped
@JavaClient
public class JavaDokgenRestKlient extends JavaHttpKlient implements Dokgen {

    private static final Logger LOG = LoggerFactory.getLogger(JavaDokgenRestKlient.class);

    private final String dokgenBaseUri;

    @Inject
    public JavaDokgenRestKlient(@KonfigVerdi("dokgen.rest.base.url") String endpoint) {
        this.dokgenBaseUri = endpoint;
    }

    @Override
    public byte[] genererPdf(String maltype, Språkkode språkkode, Dokumentdata dokumentdata) {
        Optional<byte[]> pdf;
        try {
            String templatePath = String.format("/template/%s/template_%s", maltype.toLowerCase(), getSpråkkode(språkkode));
            var endpoint = URI.create(dokgenBaseUri + templatePath + "/create-pdf-variation");

            var request = getRequestBuilder()
                    .uri(endpoint)
                    .POST(HttpRequest.BodyPublishers.ofString(toJson(dokumentdata), UTF_8))
                    .build();

            LOG.info("Kaller Dokgen for generering av mal {} på språk {}", maltype, språkkode);
            var fileString = handleResponse(sendStringRequest(request), HttpResponse::body, consumeError());
            pdf = Optional.ofNullable(fileString != null ? fileString.getBytes(UTF_8) : null);
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

    private Consumer<HttpResponse<String>> consumeError() {
        return response -> {
            var statusCode = response.statusCode();
            var endpoint = response.uri();
            var error = fromJson(response.body(), ErrorResponse.class).message();
            throw new IntegrasjonException("FP-468820", String.format("[HTTP %s] Uventet respons fra %s, med melding: %s", statusCode,
                    endpoint, error));
        };
    }

    private String getSpråkkode(Språkkode språkkode) {
        return Objects.requireNonNullElse(språkkode, Språkkode.NB).name().toLowerCase();
    }

    @Override
    public Optional<String> getAuthorization() {
        // Trenges ikke authorizering
        return Optional.empty();
    }

    @Override
    public Optional<String> getConsumerId() {
        return Optional.ofNullable(SubjectHandler.getSubjectHandler().getConsumerId());
    }

    @Override
    public String getCallId() {
        return MDCOperations.getCallId();
    }
}
