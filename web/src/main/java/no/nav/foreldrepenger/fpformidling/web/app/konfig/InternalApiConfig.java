package no.nav.foreldrepenger.fpformidling.web.app.konfig;

import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import no.nav.foreldrepenger.fpformidling.web.app.metrics.PrometheusRestService;
import no.nav.foreldrepenger.fpformidling.web.app.tjenester.NaisRestTjeneste;

@ApplicationPath(InternalApiConfig.API_URL)
public class InternalApiConfig extends Application {

    public static final String API_URL = "/internal";

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(NaisRestTjeneste.class, PrometheusRestService.class);
    }

}
