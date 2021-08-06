package no.nav.foreldrepenger.melding.web.app.exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import no.nav.vedtak.log.mdc.MDCOperations;
import no.nav.vedtak.log.util.LoggerUtils;


public class ThrowableExceptionMapper implements ExceptionMapper<Throwable> {

    private final Logger LOG = LoggerFactory.getLogger(ThrowableExceptionMapper.class);

    @Override
    public Response toResponse(Throwable exception) {
        var message = exception.getMessage() != null ? LoggerUtils.removeLineBreaks(exception.getMessage()) : "";
        var callId = MDCOperations.getCallId();
        String generellFeilmelding = "Det oppstod en serverfeil: " + message + ". Meld til support med referanse-id: " + callId; //$NON-NLS-1$ //$NON-NLS-2$
        LOG.warn("Fikk uventet feil: " + message, exception); // NOSONAR //$NON-NLS-1$
        MDC.remove("prosess"); //$NON-NLS-1$

        return Response.serverError()
                    .entity(new FeilDto(generellFeilmelding, FeilType.GENERELL_FEIL))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
    }
}
