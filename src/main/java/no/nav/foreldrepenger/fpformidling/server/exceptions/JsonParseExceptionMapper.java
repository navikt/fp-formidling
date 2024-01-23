package no.nav.foreldrepenger.fpformidling.server.exceptions;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;

public class JsonParseExceptionMapper implements ExceptionMapper<JsonParseException> {

    private static final Logger log = LoggerFactory.getLogger(JsonParseExceptionMapper.class);

    @Override
    public Response toResponse(JsonParseException exception) {
        var feil = String.format("FPFORMIDLING-299955: JSON-parsing feil: %s", exception.getMessage());
        log.warn(feil);
        return Response.status(Response.Status.BAD_REQUEST).entity(new FeilDto(feil)).type(MediaType.APPLICATION_JSON).build();
    }
}