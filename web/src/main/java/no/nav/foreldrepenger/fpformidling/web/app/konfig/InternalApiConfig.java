package no.nav.foreldrepenger.fpformidling.web.app.konfig;

import java.util.Set;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import no.nav.foreldrepenger.fpformidling.web.app.healthcheck.HealthCheckRestService;
import no.nav.foreldrepenger.fpformidling.web.app.metrics.PrometheusRestService;

@ApplicationPath(InternalApiConfig.API_URL)
public class InternalApiConfig extends Application {

    public static final String API_URL = "/internal";

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(HealthCheckRestService.class, PrometheusRestService.class);
    }

}
