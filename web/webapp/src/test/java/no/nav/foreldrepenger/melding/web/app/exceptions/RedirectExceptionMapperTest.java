package no.nav.foreldrepenger.melding.web.app.exceptions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.spi.ApplicationException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import no.nav.vedtak.felles.testutilities.Whitebox;
import no.nav.vedtak.sikkerhet.ContextPathHolder;

@Ignore
@SuppressWarnings("deprecation")
public class RedirectExceptionMapperTest {

    @Mock
    private GeneralRestExceptionMapper generalRestExceptionMapper;

    private RedirectExceptionMapper exceptionMapper;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        exceptionMapper = new RedirectExceptionMapper();
        Whitebox.setInternalState(exceptionMapper, "loadBalancerUrl", "https://erstatter.nav.no");
        Whitebox.setInternalState(exceptionMapper, "generalRestExceptionMapper", generalRestExceptionMapper);

        ContextPathHolder.instance("/fpformidling");
    }

    @Test
    public void skalMappeValideringsfeil() {
        // Arrange
        String feilmelding = "feilmelding";
        FeilType feilType = FeilType.MANGLER_TILGANG_FEIL;
        Response generalResponse = Response.status(Response.Status.FORBIDDEN)
                .entity(new FeilDto(feilType, feilmelding))
                .type(MediaType.APPLICATION_JSON)
                .build();

        ApplicationException exception = new ApplicationException(null);
        when(generalRestExceptionMapper.toResponse(exception)).thenReturn(generalResponse);

        // Act
        Response response = exceptionMapper.toResponse(exception);

        // Assert
        assertThat(response.getStatus()).isEqualTo(Response.Status.TEMPORARY_REDIRECT.getStatusCode());
        assertThat(response.getMediaType()).isEqualTo(null);
        assertThat(response.getMetadata().get("Content-Encoding").get(0))
                .isEqualTo("UTF-8");
        assertThat(response.getMetadata().get("Location").get(0).toString())
                .isEqualTo("https://erstatter.nav.no/fpsak/#?errorcode=feilmelding");
    }

}
