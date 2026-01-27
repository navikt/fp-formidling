package no.nav.foreldrepenger.fpformidling.konfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;
import no.nav.vedtak.mapper.json.DefaultJsonMapper;


@Provider
public class JacksonJsonConfig implements ContextResolver<ObjectMapper> {

    private static final JsonMapper JM = DefaultJsonMapper.getJsonMapper();

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return JM;
    }

}
