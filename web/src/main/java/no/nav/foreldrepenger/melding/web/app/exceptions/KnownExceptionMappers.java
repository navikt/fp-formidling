package no.nav.foreldrepenger.melding.web.app.exceptions;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.ext.ExceptionMapper;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import no.nav.vedtak.exception.VLException;


public class KnownExceptionMappers {

    @SuppressWarnings("rawtypes")
    private final Map<Class<? extends Throwable>, ExceptionMapper> exceptionMappers = new LinkedHashMap<>();

    public KnownExceptionMappers() {
        // NB pass på rekkefølge dersom exceptions arver (håndter minst spesifikk til slutt)
        register(ConstraintViolationException.class, new ConstraintViolationMapper());
        register(JsonMappingException.class, new JsonMappingExceptionMapper());
        register(JsonParseException.class, new JsonParseExceptionMapper());
        register(VLException.class, new VLExceptionMapper());
        register(Throwable.class, new ThrowableExceptionMapper());
    }

    @SuppressWarnings("rawtypes")
    private void register(Class<? extends Throwable> exception, ExceptionMapper mapper) {
        exceptionMappers.put(exception, mapper);
    }

    @SuppressWarnings("rawtypes")
    public Collection<ExceptionMapper> getExceptionMappers() {
        return exceptionMappers.values();
    }
}
