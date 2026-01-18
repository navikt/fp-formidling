package no.nav.foreldrepenger.fpformidling.konfig;

import java.util.Set;

import no.nav.foreldrepenger.fpformidling.server.exceptions.ConstraintViolationMapper;
import no.nav.foreldrepenger.fpformidling.server.exceptions.GeneralRestExceptionMapper;
import no.nav.foreldrepenger.fpformidling.server.exceptions.JsonMappingExceptionMapper;
import no.nav.foreldrepenger.fpformidling.server.exceptions.JsonParseExceptionMapper;

public class FellesConfigClasses {

    private FellesConfigClasses() {
    }

    // Jakarta RS container
    public static Set<Class<?>> getFellesContainerFilterClasses() {
        return Set.of(AuthenticationFilter.class);
    }

    // Jakarta RS ext
    public static Set<Class<?>> getFellesRsExtConfigClasses() {
        // knytter inn Jackson med en ContextResolver, resten er ulike ExceptionMappers
        return Set.of(JacksonJsonConfig.class,
            ConstraintViolationMapper.class,
            JsonMappingExceptionMapper.class,
            JsonParseExceptionMapper.class,
            GeneralRestExceptionMapper.class);
    }
}
