package no.nav.foreldrepenger.fpformidling.integrasjon.oppgave.v2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import no.nav.foreldrepenger.fpformidling.integrasjon.oppgave.VirkedagUtil;
import no.nav.foreldrepenger.fpformidling.typer.AktørId;
import no.nav.foreldrepenger.fpformidling.typer.JournalpostId;
import no.nav.vedtak.exception.IntegrasjonException;
import no.nav.vedtak.exception.ManglerTilgangException;
import no.nav.vedtak.log.mdc.MDCOperations;
import no.nav.vedtak.mapper.json.DefaultJsonMapper;
import no.nav.vedtak.sikkerhet.oidc.token.OpenIDToken;
import no.nav.vedtak.sikkerhet.oidc.token.TokenProvider;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

class JavaOppgaveRestKlientTest {
    private static MockedStatic<TokenProvider> TOKEN_PROVIDER_MOCKED_STATIC;
    private MockWebServer mockWebServer;
    private Oppgaver klient;

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
        this.klient = new JavaOppgaveRestKlient(mockWebServer.url("/fpformidling").toString());
    }

    @Test
    void opprettOppgave_401() {
        var statusCode = 401;
        var message = "Bruker mangler tilgang.";
        var body = DefaultJsonMapper.toJson(new ErrorResponse(UUID.randomUUID(), message));

        mockWebServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody(body)
                .setResponseCode(statusCode));

        var request = createRequest(new JournalpostId("1234"), new AktørId("123456"));
        Exception exception = assertThrows(ManglerTilgangException.class, () -> {
            klient.opprettetOppgave(request);
        });

        assertThat(exception.getMessage()).contains("401");
        assertThat(exception.getMessage()).contains(message);
    }

    @Test
    void opprettOppgave_403() {
        var statusCode = 403;

        mockWebServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setResponseCode(statusCode));

        var request = createRequest(new JournalpostId("1234"), new AktørId("123456"));
        Exception exception = assertThrows(ManglerTilgangException.class, () -> {
            klient.opprettetOppgave(request);
        });
        assertThat(exception.getMessage()).contains("403");
    }

    @Test
    void opprettOppgave_400() {
        var statusCode = 400;
        var message = "Noe har feilet!";
        var body = DefaultJsonMapper.toJson(new ErrorResponse(UUID.randomUUID(), message));

        mockWebServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody(body)
                .setResponseCode(statusCode));

        var request = createRequest(new JournalpostId("1234"), new AktørId("123456"));
        Exception exception = assertThrows(IntegrasjonException.class, () -> {
            klient.opprettetOppgave(request);
        });
        assertThat(exception.getMessage()).contains("400");
        assertThat(exception.getMessage()).contains(message);
    }

    @Test
    void opprettOppgave_200() {
        var statusCode = 200;
        var oppgavetype = "VURD_KONS";
        var body = DefaultJsonMapper.toJson(new Oppgave(1234, oppgavetype, "FOR", Oppgavestatus.OPPRETTET, Prioritet.NORM));

        mockWebServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody(body)
                .setResponseCode(statusCode));

        var resultat = klient.opprettetOppgave(createRequest(new JournalpostId("1234"), new AktørId("123456")));

        assertThat(resultat).isNotNull();
        assertThat(resultat.oppgavetype()).isEqualTo(oppgavetype);
        assertThat(resultat.status()).isEqualTo(Oppgavestatus.OPPRETTET);
        assertThat(resultat.prioritet()).isEqualTo(Prioritet.NORM);

    }

    private OpprettOppgaveRequest createRequest(JournalpostId journalpostId,
                                                           AktørId aktørId) {
        return new OpprettOppgaveRequest("1234", "1234",
                journalpostId.getVerdi(),
                null, "98877655",
                aktørId.getId(), "la la la",
                "FMLI",
                "FOR",
                null,
                "VUR_KONS_YTE",
                null,
                LocalDate.now(),
                Prioritet.NORM,
                VirkedagUtil.fomVirkedag(LocalDate.now().plusDays(1)));
    }
}
