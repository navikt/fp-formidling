package no.nav.foreldrepenger.melding.web.app.exceptions;

import java.net.URI;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.ApplicationException;
import org.owasp.encoder.Encode;

import no.nav.vedtak.sikkerhet.ContextPathHolder;
import no.nav.vedtak.util.env.Environment;

@Provider
public class RedirectExceptionMapper implements ExceptionMapper<ApplicationException> {

    private static final Environment ENV = Environment.current();
    private final String loadBalancerUrl;
    private final GeneralRestExceptionMapper generalRestExceptionMapper;

    public RedirectExceptionMapper() {
        this(ENV.getProperty("loadbalancer.url"));
    }

    RedirectExceptionMapper(String url) {
        this(url, new GeneralRestExceptionMapper());
    }

    RedirectExceptionMapper(String url, GeneralRestExceptionMapper mapper) {
        this.generalRestExceptionMapper = mapper;
        this.loadBalancerUrl = url;
    }

    @Override
    public Response toResponse(ApplicationException exception) {
        Response response = generalRestExceptionMapper.toResponse(exception);
        String feilmelding = ((FeilDto) response.getEntity()).feilmelding();
        String enkodetFeilmelding = Encode.forUriComponent(feilmelding);

        String formattertFeilmelding = String.format("%s/#?errorcode=%s", getBaseUrl(), enkodetFeilmelding);
        Response.ResponseBuilder responser = Response.temporaryRedirect(URI.create(formattertFeilmelding));
        responser.encoding("UTF-8");
        return responser.build();
    }

    protected String getBaseUrl() {
        return loadBalancerUrl + ContextPathHolder.instance().getContextPath();
    }

}
