package no.nav.foreldrepenger.fpformidling.integrasjon.dokdist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Fagsystem;
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
    private static MockedStatic<TokenProvider> TOKEN_PROVIDER_MOCKED_STATIC;
    private MockWebServer mockWebServer;
    private Dokdist klient;

    @BeforeAll
    static void initStaticMocks() {
        MDCOperations.putCallId();

        var idToken = Mockito.mock(OpenIDToken.class);
        when(idToken.token()).thenReturn("token_string");
        TOKEN_PROVIDER_MOCKED_STATIC = Mockito.mockStatic(TokenProvider.class);
        TOKEN_PROVIDER_MOCKED_STATIC.when(TokenProvider::getStsSystemToken).thenReturn(idToken);
    }

    @AfterAll
    static void deregisterStaticMocks() {
        TOKEN_PROVIDER_MOCKED_STATIC.close();
    }

    @BeforeEach
    void setup() {
        this.mockWebServer = new MockWebServer();
        this.klient = new JavaDokdistRestKlient(mockWebServer.url("/fpformidling").toString());
    }

    @Test
    void distribuerJournalpost_401() {
        var statusCode = 401;
        var message = "Mangler tilgang.";
        var body = DefaultJsonMapper.toJson(new ErrorResponse(statusCode, "Unauthorized", message, "/"));

        mockWebServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody(body)
                .setResponseCode(statusCode));

        var journalpostId = new JournalpostId("12334233");
        var request = lagRequest(journalpostId);
        Exception exception = assertThrows(ManglerTilgangException.class, () -> {
            klient.distribuerJournalpost(request);
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

        var journalpostId = new JournalpostId("12334233");
        var request = lagRequest(journalpostId);
        Exception exception = assertThrows(ManglerTilgangException.class, () -> {
            klient.distribuerJournalpost(request);
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

        var journalpostId = new JournalpostId("12334233");
        var request = lagRequest(journalpostId);
        Exception exception = assertThrows(IntegrasjonException.class, () -> {
            klient.distribuerJournalpost(request);
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

        var journalpostId = new JournalpostId("12334233");
        var request = lagRequest(journalpostId);
        var resultat = klient.distribuerJournalpost(request);

        assertThat(resultat).isEqualTo(Dokdist.Resultat.MANGLER_ADRESSE);
    }

    @Test
    @DisplayName("""
            409 Conflict
            Journalposten er allerede distribuert så man kan hoppe over distribueringen.
            Kan håndteres som at en distribusjon har gått OK.
            Man skal også få samme svar i respons payload som en OK distribusjon (idempotent receiver).""")
    void distribuerJournalpost_409_accept_conflict() {
        var statusCode = 409;
        var journalpostId = new JournalpostId("12334233");
        var bestillingsId = "123456645654";
        var body = DefaultJsonMapper.toJson(new DistribuerJournalpostResponse(bestillingsId));

        mockWebServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody(body)
                .setResponseCode(statusCode));

        var request = lagRequest(journalpostId);
        var resultat = klient.distribuerJournalpost(request);

        assertThat(resultat).isEqualTo(Dokdist.Resultat.OK);
    }

    @Test
    void distribuerJournalpost_404() {
        var statusCode = 404;
        var journalpostId = new JournalpostId("12334233");
        var message = "[HTTP 404] Feilet";
        var body = DefaultJsonMapper.toJson(new ErrorResponse(statusCode, "not_found", message, "/"));

        mockWebServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody(body)
                .setResponseCode(statusCode));

        var request = lagRequest(journalpostId);
        Exception exception = assertThrows(TekniskException.class, () -> {
            klient.distribuerJournalpost(request);
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

        var journalpostId = new JournalpostId("1231");
        var request = lagRequest(journalpostId);
        var resultat = klient.distribuerJournalpost(request);

        assertThat(resultat).isEqualTo(Dokdist.Resultat.OK);
    }

    private DistribuerJournalpostRequest lagRequest(JournalpostId journalpostId) {
        return new DistribuerJournalpostRequest(journalpostId.getVerdi(),
                "test_bestillingId",
                Fagsystem.FPSAK.getOffisiellKode(),
                Fagsystem.FPSAK.getKode(),
                Distribusjonstype.VEDTAK,
                Distribusjonstidspunkt.KJERNETID);
    }
}
