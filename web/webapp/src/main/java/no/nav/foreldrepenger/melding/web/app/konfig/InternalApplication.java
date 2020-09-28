package no.nav.foreldrepenger.melding.web.app.konfig;

import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import no.nav.foreldrepenger.melding.web.app.metrics.PrometheusRestService;
import no.nav.foreldrepenger.melding.web.app.tjenester.NaisRestTjeneste;

@ApplicationPath(InternalApplication.API_URL)
public class InternalApplication extends Application {

    public static final String API_URL = "/internal";

    public InternalApplication() {
        // CDI
    }

    @Override
    public Set<Class<?>> getClasses() {

        return Set.of(NaisRestTjeneste.class, PrometheusRestService.class);
    }

}
