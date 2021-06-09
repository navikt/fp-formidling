package no.nav.foreldrepenger.melding.web.app.tjenester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.melding.dokumentproduksjon.v2.DokumentproduksjonConsumerProducer;
import no.nav.foreldrepenger.melding.web.app.selftest.checks.DatabaseHealthCheck;

public class NaisRestTjenesteTest {

    private NaisRestTjeneste restTjeneste;

    private ApplicationServiceStarter serviceStarterMock = mock(ApplicationServiceStarter.class);
    private DatabaseHealthCheck databaseHealthCheckMock = mock(DatabaseHealthCheck.class);
    private DokumentproduksjonConsumerProducer dokumentproduksjonConsumerProducer = mock(DokumentproduksjonConsumerProducer.class);

    @BeforeEach
    public void setup() {
        restTjeneste = new NaisRestTjeneste(serviceStarterMock, databaseHealthCheckMock, dokumentproduksjonConsumerProducer);
    }

    @Test
    public void isAlive_skal_returnere_status_200() {
        when(serviceStarterMock.isKafkaAlive()).thenReturn(true);

        Response response = restTjeneste.isAlive();

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }

    @Test
    public void isAlive_skal_returnere_server_error_når_kafka_feiler() {
        when(serviceStarterMock.isKafkaAlive()).thenReturn(false);

        Response response = restTjeneste.isAlive();

        assertThat(response.getStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }

    @Test
    public void isReady_skal_returnere_service_unavailable_når_databasetest_feiler() {
        when(serviceStarterMock.isKafkaAlive()).thenReturn(true);
        when(databaseHealthCheckMock.isReady()).thenReturn(false);

        Response response = restTjeneste.isReady();

        assertThat(response.getStatus()).isEqualTo(Response.Status.SERVICE_UNAVAILABLE.getStatusCode());
    }

    @Test
    public void isReady_skal_returnere_service_unavailable_når_kafka_feiler() {
        when(serviceStarterMock.isKafkaAlive()).thenReturn(false);
        when(databaseHealthCheckMock.isReady()).thenReturn(true);

        Response response = restTjeneste.isReady();

        assertThat(response.getStatus()).isEqualTo(Response.Status.SERVICE_UNAVAILABLE.getStatusCode());
    }

    @Test
    public void isReady_skal_returnere_status_ok_når_database_og_kafka_er_ok() {
        when(serviceStarterMock.isKafkaAlive()).thenReturn(true);
        when(databaseHealthCheckMock.isReady()).thenReturn(true);

        Response response = restTjeneste.isReady();

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }

    @Test
    public void preStop_skal_kalle_stopServices_og_returnere_status_ok() {
        Response response = restTjeneste.preStop();

        verify(serviceStarterMock).stopServices();
        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }
}
