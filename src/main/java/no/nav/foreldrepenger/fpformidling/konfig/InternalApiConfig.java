package no.nav.foreldrepenger.fpformidling.konfig;

import java.util.Set;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import no.nav.foreldrepenger.fpformidling.server.PrometheusRestService;
import no.nav.foreldrepenger.fpformidling.server.healthcheck.HealthCheckRestService;


@ApplicationPath(InternalApiConfig.API_URI)
public class InternalApiConfig extends Application {

    public static final String API_URI = "/internal";

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(HealthCheckRestService.class, PrometheusRestService.class);
    }

}
