package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import no.nav.foreldrepenger.fpformidling.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.ErrorResponse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.log.mdc.MDCOperations;
import no.nav.vedtak.mapper.json.DefaultJsonMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

class JavaDokgenRestKlientTest {

    private MockWebServer mockWebServer;
    private Dokgen klient;

    @BeforeAll
    static void setupStatics() {
        MDCOperations.putCallId();
    }

    @BeforeEach
    void setup() {
        this.mockWebServer = new MockWebServer();
        this.klient = new JavaDokgenRestKlient(mockWebServer.url("/fpformidling").toString());
    }

    @ParameterizedTest
    @CsvSource({
            "401, Unauthorized",
            "403, Forbidden",
            "404, Kan ikke finne template med navn abc",
            "500, Feil",
    })
    void generatePdf(int statusCode, String message) {
        var template = "abc";
        var variation = "template_nb";
        var body = DefaultJsonMapper.toJson(new ErrorResponse(statusCode, message, message, String.format("/template/%s/%s/create-pdf-variation", template, variation)));

        mockWebServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody(body)
                .setResponseCode(statusCode));

        var språkkode = Språkkode.defaultNorsk("nb");
        var dokumentdata = new TestDokumentdata();
        Exception exception = assertThrows(TekniskException.class, () -> {
            klient.genererPdf(template, språkkode, dokumentdata);
        });
        assertThat(exception.getMessage()).contains(String.format("Fikk feil ved kall til dokgen for mal %s og språkkode %s", template, språkkode));
    }

    @Test
    @DisplayName("No body returned. Body fra serveren er aldri null, den er en tom array. Dvs at man må teste på lenght isteden.")
    void generatePdf_no_body_returned() {
        var statusCode = 200;
        var template = "abc";

        mockWebServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setResponseCode(statusCode));

        var språkkode = Språkkode.defaultNorsk("nb");
        var dokumentdata = new TestDokumentdata();
        Exception exception = assertThrows(TekniskException.class, () -> {
            klient.genererPdf(template, språkkode, dokumentdata);
        });
        assertThat(exception.getMessage()).contains(String.format("Fikk tomt svar ved kall til dokgen for mal %s og språkkode %s.", template, språkkode));
    }

    static class TestDokumentdata extends Dokumentdata {}
}
