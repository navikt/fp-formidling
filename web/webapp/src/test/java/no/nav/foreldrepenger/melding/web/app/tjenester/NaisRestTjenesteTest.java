package no.nav.foreldrepenger.melding.web.app.tjenester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import no.nav.foreldrepenger.melding.web.app.selftest.SelftestService;

public class NaisRestTjenesteTest {

    private NaisRestTjeneste restTjeneste;

    private SelftestService selftestServiceMock = mock(SelftestService.class);
    private ApplicationServiceStarter serviceStarterMock = mock(ApplicationServiceStarter.class);

    @Before
    public void setup() {
        when(selftestServiceMock.kritiskTjenesteFeilet()).thenReturn(false);
        restTjeneste = new NaisRestTjeneste(serviceStarterMock, selftestServiceMock);
    }

    @Test
    public void test_isAlive_skal_returnere_status_200() {
        Response response = restTjeneste.isAlive();

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }

    @Test
    public void test_preStop_skal_kalle_stopServices_og_returnere_status_ok() {
        Response response = restTjeneste.preStop();

        verify(serviceStarterMock).stopServices();
        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }
}
