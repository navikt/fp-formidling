package no.nav.foreldrepenger.fpformidling.web.app.tjenester;

import static javax.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.vedtak.log.metrics.ReadinessAware;

@ExtendWith(MockitoExtension.class)
public class NaisRestTjenesteTest {

    private NaisRestTjeneste restTjeneste;

    @Mock
    private ApplicationServiceStarter starter;

    @Mock
    private ReadinessAware db;

    @BeforeEach
    void setup() {
        restTjeneste = new NaisRestTjeneste(starter, List.of(), List.of(db));
    }

    @Test
    void isAlive_skal_returnere_status_200() {
        assertThat(restTjeneste.isAlive().getStatus()).isEqualTo(OK.getStatusCode());
    }

    @Test
    void isReady_skal_returnere_service_unavailable_når_databasetest_feiler() {
        when(db.isReady()).thenReturn(false);
        assertThat(restTjeneste.isReady().getStatus()).isEqualTo(Response.Status.SERVICE_UNAVAILABLE.getStatusCode());
    }

    @Test
    void preStop_skal_kalle_stopServices_og_returnere_status_ok() {
        var response = restTjeneste.preStop();
        verify(starter).stopServices();
        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }
}
