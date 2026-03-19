package no.nav.foreldrepenger.fpformidling.konfig;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import no.nav.vedtak.server.rest.ForvaltningAuthorizationFilter;

import org.glassfish.jersey.server.ServerProperties;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import no.nav.foreldrepenger.fpformidling.tjenester.forvaltning.ForvaltningRestTjeneste;
import no.nav.foreldrepenger.konfig.Environment;
import no.nav.vedtak.felles.prosesstask.rest.ProsessTaskRestTjeneste;
import no.nav.vedtak.openapi.OpenApiUtils;
import no.nav.vedtak.server.rest.FpRestJackson2Feature;
import no.nav.vedtak.server.rest.GeneralRestExceptionMapper;

@ApplicationPath(ForvaltningApiConfig.API_URL)
public class ForvaltningApiConfig extends Application {

    private static final Environment ENV = Environment.current();

    public static final String API_URL = "/forvaltning/api";

    public ForvaltningApiConfig() {
        GeneralRestExceptionMapper.setBrukerRettetApplikasjon(false);
        var contextPath = ENV.getProperty("context.path", "/fpformidling");
        OpenApiUtils.setupOpenApi("FPFORMIDLING Forvaltning - Foreldrepenger, engangsstønad og svangerskapspenger",
            contextPath, getAllClasses(), this);
    }

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>(getAllClasses());
        classes.add(FpRestJackson2Feature.class);
        classes.add(ForvaltningAuthorizationFilter.class);
        classes.add(OpenApiResource.class);
        return Collections.unmodifiableSet(classes);
    }

    private static Collection<Class<?>> getAllClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(ProsessTaskRestTjeneste.class);
        classes.add(ForvaltningRestTjeneste.class);
        return classes;
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
