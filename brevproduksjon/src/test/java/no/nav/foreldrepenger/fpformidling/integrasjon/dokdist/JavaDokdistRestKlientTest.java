package no.nav.foreldrepenger.fpformidling.integrasjon.dokdist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.dto.DistribuerJournalpostResponse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.dto.Distribusjonstype;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.dto.ErrorResponse;
import no.nav.foreldrepenger.fpformidling.integrasjon.oppgave.VirkedagUtil;
import no.nav.foreldrepenger.fpformidling.integrasjon.oppgave.v2.OpprettOppgaveRequest;
import no.nav.foreldrepenger.fpformidling.integrasjon.oppgave.v2.Prioritet;
import no.nav.foreldrepenger.fpformidling.typer.AktørId;
import no.nav.foreldrepenger.fpformidling.typer.JournalpostId;
import no.nav.vedtak.exception.IntegrasjonException;
import no.nav.vedtak.exception.ManglerTilgangException;
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.log.mdc.MDCOperations;
import no.nav.vedtak.mapper.json.DefaultJsonMapper;
import no.nav.vedtak.sikkerhet.oidc.token.OpenIDToken;
import no.nav.vedtak.sikkerhet.oidc.token.TokenProvider;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

class JavaDokdistRestKlientTest {

    private MockWebServer mockWebServer;
    private Dokdist klient;

    @BeforeAll
    static void initStaticMocks() {
        MockedStatic<MDCOperations> mdcMock = Mockito.mockStatic(MDCOperations.class);
        mdcMock.when(MDCOperations::getCallId).thenReturn("test");

        var idToken = Mockito.mock(OpenIDToken.class);
        when(idToken.token()).thenReturn("token_string");

        MockedStatic<TokenProvider> tokenProviderMockedStatic = Mockito.mockStatic(TokenProvider.class);
        tokenProviderMockedStatic.when(TokenProvider::getStsSystemToken).thenReturn(idToken);
    }

    @BeforeEach
    void setup() {
        this.mockWebServer = new MockWebServer();
        this.klient = new JavaDokdistRestKlient(mockWebServer.url("/fpformidling").toString());
    }

    @Test
    void distribuerJournalpost_401() {
        var statusCode = 401;
        var message = "Bruker mangler tilgang.";
        var body = DefaultJsonMapper.toJson(new ErrorResponse(statusCode, "Unauthorized", message, "/"));

        mockWebServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody(body)
                .setResponseCode(statusCode));

        Exception exception = assertThrows(ManglerTilgangException.class, () -> {
            klient.distribuerJournalpost(new JournalpostId("12334233"), "test_bestillingId", Distribusjonstype.VEDTAK);
        });
        assertThat(exception.getMessage()).contains("401");
        assertThat(exception.getMessage()).contains(message);
    }

    @Test
    void distribuerJournalpost_403() {
        var statusCode = 403;

        mockWebServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setResponseCode(statusCode));

        Exception exception = assertThrows(ManglerTilgangException.class, () -> {
            klient.distribuerJournalpost(new JournalpostId("12334233"), "test_bestillingId", Distribusjonstype.VEDTAK);
        });
        assertThat(exception.getMessage()).contains("403");
    }

    @Test
    void distribuerJournalpost_400() {
        var statusCode = 400;
        var message = "Noe har feilet!";
        var body = DefaultJsonMapper.toJson(new ErrorResponse(statusCode, "Unauthorized", message, "/"));

        mockWebServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody(body)
                .setResponseCode(statusCode));

        Exception exception = assertThrows(IntegrasjonException.class, () -> {
            klient.distribuerJournalpost(new JournalpostId("12334233"), "test_bestillingId", Distribusjonstype.VEDTAK);
        });
        assertThat(exception.getMessage()).contains("400");
        assertThat(exception.getMessage()).contains(message);
    }

    @Test
    void distribuerJournalpost_400_bruker_mangler_adresse() {
        var statusCode = 400;
        var message = "Mottaker har ukjent adresse.";
        var body = DefaultJsonMapper.toJson(new ErrorResponse(statusCode, "Unauthorized", message, "/"));

        mockWebServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody(body)
                .setResponseCode(statusCode));

        var resultat = klient.distribuerJournalpost(new JournalpostId("12334233"), "test_bestillingId",
                Distribusjonstype.VEDTAK);

        assertThat(resultat).isEqualTo(Dokdist.Resultat.MANGLER_ADRESSE);
    }

    @Test
    void distribuerJournalpost_404() {
        var statusCode = 404;
        var journalpostId = "12334233";
        var message = "Journalpost<" + journalpostId + "> finnes ikke.";
        var body = DefaultJsonMapper.toJson(new ErrorResponse(statusCode, "Unauthorized", message, "/"));

        mockWebServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody(body)
                .setResponseCode(statusCode));

        Exception exception = assertThrows(TekniskException.class, () -> {
            klient.distribuerJournalpost(new JournalpostId(journalpostId), "test_bestillingId", Distribusjonstype.VEDTAK);
        });
        assertThat(exception.getMessage()).contains("404");
        assertThat(exception.getMessage()).contains(message);
    }

    @Test
    void distribuerJournalpost_200() {
        var statusCode = 200;
        var bestillingsId = "123456645654";
        var body = DefaultJsonMapper.toJson(new DistribuerJournalpostResponse(bestillingsId));

        mockWebServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody(body)
                .setResponseCode(statusCode));

        var resultat = klient.distribuerJournalpost(new JournalpostId("1231"), "test_bestillingId",
                Distribusjonstype.VEDTAK);

        assertThat(resultat).isEqualTo(Dokdist.Resultat.OK);
    }
}
