package no.nav.foreldrepenger.fpformidling.konfig;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.glassfish.jersey.server.ServerProperties;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import no.nav.foreldrepenger.fpformidling.tjenester.BrevRestTjeneste;

@ApplicationPath(ApiConfig.API_URI)
public class ApiConfig extends Application {

    public static final String API_URI = "/api";

    @Override
    public Set<Class<?>> getClasses() {
        // eksponert grensesnitt

        Set<Class<?>> classes = new HashSet<>(getAllClasses());

        // Autentisering
        classes.addAll(FellesConfigClasses.getFellesContainerFilterClasses());

        classes.addAll(FellesConfigClasses.getFellesRsExtConfigClasses());

        return Collections.unmodifiableSet(classes);
    }

    private static Collection<Class<?>> getAllClasses() {
        return Set.of(BrevRestTjeneste.class);
    }

    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> properties = new HashMap<>();
        // Ref Jersey doc
        properties.put(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
        properties.put(ServerProperties.PROCESSING_RESPONSE_ERRORS_ENABLED, true);
        return properties;
    }
}
