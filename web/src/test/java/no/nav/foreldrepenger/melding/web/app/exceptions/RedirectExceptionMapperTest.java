package no.nav.foreldrepenger.melding.web.app.exceptions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.spi.ApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.vedtak.sikkerhet.ContextPathHolder;

@ExtendWith(MockitoExtension.class)
public class RedirectExceptionMapperTest {

    @Mock
    private GeneralRestExceptionMapper generalRestExceptionMapper;

    private RedirectExceptionMapper exceptionMapper;

    @BeforeEach
    public void setUp() {
        exceptionMapper = new RedirectExceptionMapper("https://erstatter.nav.no", generalRestExceptionMapper);
        ContextPathHolder.instance("/fpformidling");
    }

    @Test
    public void skalMappeValideringsfeil() {
        // Arrange
        String feilmelding = "feilmelding";
        FeilType feilType = FeilType.MANGLER_TILGANG_FEIL;
        Response generalResponse = Response.status(Response.Status.FORBIDDEN)
                .entity(new FeilDto(feilmelding, feilType))
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
                .isEqualTo("https://erstatter.nav.no/fpformidling/#?errorcode=feilmelding");
    }
}
