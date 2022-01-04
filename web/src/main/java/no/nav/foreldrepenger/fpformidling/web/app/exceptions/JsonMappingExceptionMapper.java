package no.nav.foreldrepenger.fpformidling.web.app.exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonMappingException;

public class JsonMappingExceptionMapper implements ExceptionMapper<JsonMappingException> {

    private static final Logger log = LoggerFactory.getLogger(JsonMappingExceptionMapper.class);

    @Override
    public Response toResponse(JsonMappingException exception) {
        var feil = "FPFORMIDLING-252294: JSON-mapping feil";
        log.warn(feil);
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(new FeilDto(feil))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
