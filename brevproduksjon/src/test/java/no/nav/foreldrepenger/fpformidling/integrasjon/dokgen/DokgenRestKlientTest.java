package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.ErrorResponse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.vedtak.exception.IntegrasjonException;
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.felles.integrasjon.rest.RestClient;
import no.nav.vedtak.mapper.json.DefaultJsonMapper;

@ExtendWith(MockitoExtension.class)
class DokgenRestKlientTest {

    @Mock
    private RestClient restClient;
    private Dokgen klient;

    @BeforeEach
    void setup() {
        this.klient = new DokgenRestKlient(restClient);
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

        when(restClient.sendReturnByteArray(any())).thenThrow(new IntegrasjonException("F-468817", String.format("Uventet respons %s fra %s", statusCode, "/endpoint")));

        var språkkode = Språkkode.defaultNorsk("nb");
        var dokumentdata = new TestDokumentdata();
        Exception exception = assertThrows(IntegrasjonException.class, () -> {
            klient.genererPdf(template, språkkode, dokumentdata);
        });
        assertThat(exception.getMessage()).contains("F-468817");
    }

    @Test
    @DisplayName("No body returned. Body fra serveren er aldri null, den er en tomtUttak array. Dvs at man må teste på lenght isteden.")
    void generatePdf_no_body_returned() {
        var template = "abc";

        when(restClient.sendReturnByteArray(any())).thenReturn(new byte[]{});
        var språkkode = Språkkode.defaultNorsk("nb");
        var dokumentdata = new TestDokumentdata();
        Exception exception = assertThrows(TekniskException.class, () -> {
            klient.genererPdf(template, språkkode, dokumentdata);
        });
        assertThat(exception.getMessage()).contains(String.format("Fikk tomt svar ved kall til dokgen for mal %s og språkkode %s.", template, språkkode));
    }

    static class TestDokumentdata extends Dokumentdata {}
}
