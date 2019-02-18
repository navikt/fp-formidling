package no.nav.foreldrepenger.melding.web.app.exceptions;

import java.net.URI;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.ApplicationException;
import org.owasp.encoder.Encode;

import no.nav.vedtak.sikkerhet.ContextPathHolder;

@Provider
public class RedirectExceptionMapper implements ExceptionMapper<ApplicationException> {

    private String loadBalancerUrl = System.getProperty("loadbalancer.url");

    private GeneralRestExceptionMapper generalRestExceptionMapper = new GeneralRestExceptionMapper();

    @Override
    public Response toResponse(ApplicationException exception) {
        Response response = generalRestExceptionMapper.toResponse(exception);
        String feilmelding = ((FeilDto) response.getEntity()).getFeilmelding();
        String enkodetFeilmelding = Encode.forUriComponent(feilmelding);

        String formattertFeilmelding = String.format("%s/#?errorcode=%s", getBaseUrl(), enkodetFeilmelding);//$NON-NLS-1$
        Response.ResponseBuilder responser = Response.temporaryRedirect(URI.create(formattertFeilmelding));
        responser.encoding("UTF-8");
        return responser.build();
    }

    protected String getBaseUrl() {
        return loadBalancerUrl + ContextPathHolder.instance().getContextPath();
    }

}
