package no.nav.foreldrepenger.melding.web.app.tjenester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

public class NaisRestTjenesteTest {

    private NaisRestTjeneste restTjeneste;

    private ApplicationServiceStarter serviceStarterMock = mock(ApplicationServiceStarter.class);

    @Before
    public void setup() {
        restTjeneste = new NaisRestTjeneste(serviceStarterMock);
    }

    @Test
    public void test_isAlive_skal_ikke_returnere_status_200_når_kafka_feiler() {
        doReturn(false).when(serviceStarterMock).isKafkaAlive();
        Response response = restTjeneste.isAlive();

        assertThat(response.getStatus()).isNotEqualTo(Response.Status.OK.getStatusCode());
    }

    @Test
    public void test_isAlive_skal_returnere_status_200() {
        doReturn(true).when(serviceStarterMock).isKafkaAlive();
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
