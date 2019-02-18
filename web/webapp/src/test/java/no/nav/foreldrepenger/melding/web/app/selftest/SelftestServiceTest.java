package no.nav.foreldrepenger.melding.web.app.selftest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_HTML;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codahale.metrics.health.HealthCheck;

import no.nav.modig.core.test.LogSniffer;

public class SelftestServiceTest {

    private static final String MSG_KRITISK_FEIL = "kritisk feil";
    private static final String MSG_IKKEKRITISK_FEIL = "ikke-kritisk feil";
    @Rule
    public final LogSniffer logSniffer = new LogSniffer();
    private SelftestService service; // objektet vi tester
    private Selftests mockSelftests;

    @Before
    public void setup() {
        mockSelftests = mock(Selftests.class);
        service = new SelftestService(mockSelftests);
    }

    @Test
    public void test_doGet_alleDeltesterOk() {
        SelftestResultat resultat = lagSelftestResultat(true, true);
        when(mockSelftests.run()).thenReturn(resultat);

        Response response = service.doSelftest(APPLICATION_JSON, false);

        assertThat(response).isNotNull();
        logSniffer.assertNoErrors();
        logSniffer.assertNoWarnings();
    }

    @Test
    public void test_doGet_kritiskeDeltesterOkIkkeKritiskeDeltesterFeil() {
        SelftestResultat resultat = lagSelftestResultat(true, false);
        when(mockSelftests.run()).thenReturn(resultat);

        Response response = service.doSelftest(APPLICATION_JSON, false);

        assertThat(response).isNotNull();
        logSniffer.assertNoErrors();
        logSniffer.assertHasWarnMessage(MSG_IKKEKRITISK_FEIL);
    }

    @Test
    public void test_doGet_kritiskeDeltesterFeilIkkeKritiskeDeltesterOk() {
        SelftestResultat resultat = lagSelftestResultat(false, true);
        when(mockSelftests.run()).thenReturn(resultat);

        Response response = service.doSelftest(APPLICATION_JSON, false);

        assertThat(response).isNotNull();
        logSniffer.assertHasErrorMessage(MSG_KRITISK_FEIL);
        logSniffer.assertNoWarnings();
    }

    @Test
    public void test_doGet_kritiskeDeltesterFeilIkkeKritiskeDeltesterFeil() {
        SelftestResultat resultat = lagSelftestResultat(false, false);
        when(mockSelftests.run()).thenReturn(resultat);

        Response response = service.doSelftest(APPLICATION_JSON, false);

        assertThat(response).isNotNull();
        logSniffer.assertHasErrorMessage(MSG_KRITISK_FEIL);
        logSniffer.assertHasWarnMessage(MSG_IKKEKRITISK_FEIL);
    }

    @Test
    public void test_doGet_html() {
        SelftestResultat resultat = lagSelftestResultat(true, true);
        when(mockSelftests.run()).thenReturn(resultat);

        Response response = service.doSelftest(TEXT_HTML, false);

        assertThat(response).isNotNull();
        assertThat(response.getHeaders()).containsKeys("Content-Type");
        assertThat(response.getHeaders().get("Content-Type")).contains(TEXT_HTML);
        assertThat(response.getEntity()).isNotNull();
    }

    @Test
    public void test_doGet_jsonAsHtml() {
        SelftestResultat resultat = lagSelftestResultat(true, true);
        when(mockSelftests.run()).thenReturn(resultat);

        Response response = service.doSelftest(TEXT_HTML, true);

        assertThat(response).isNotNull();
        assertThat(response.getHeaders()).containsKeys("Content-Type");
        assertThat(response.getHeaders().get("Content-Type")).contains(TEXT_HTML);
        assertThat(response.getEntity()).isNotNull();
    }

    //-------

    private SelftestResultat lagSelftestResultat(boolean kritiskeOk, boolean ikkeKritiskeOk) {
        SelftestResultat resultat = lagSelftestResultat();

        HealthCheck.Result delRes1 = kritiskeOk ? HealthCheck.Result.healthy() : HealthCheck.Result.unhealthy(MSG_KRITISK_FEIL);
        resultat.leggTilResultatForKritiskTjeneste(delRes1);

        HealthCheck.Result delRes2 = ikkeKritiskeOk ? HealthCheck.Result.healthy() : HealthCheck.Result.unhealthy(MSG_IKKEKRITISK_FEIL);
        resultat.leggTilResultatForIkkeKritiskTjeneste(delRes2);

        return resultat;
    }

    private SelftestResultat lagSelftestResultat() {
        SelftestResultat resultat = new SelftestResultat();
        resultat.setApplication("test-appl");
        resultat.setRevision("1");
        resultat.setVersion("2");
        resultat.setBuildTime("nu");
        resultat.setTimestamp(LocalDateTime.now());
        return resultat;
    }
}
