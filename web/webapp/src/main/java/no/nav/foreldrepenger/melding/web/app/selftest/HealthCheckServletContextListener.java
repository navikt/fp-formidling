package no.nav.foreldrepenger.melding.web.app.selftest;

import javax.inject.Inject;
import javax.servlet.annotation.WebListener;

import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.servlets.HealthCheckServlet;

@WebListener
public class HealthCheckServletContextListener extends HealthCheckServlet.ContextListener {

    private HealthCheckRegistry healthCheckRegistry;

    public HealthCheckServletContextListener() {
        // for CDi
    }

    @Inject
    public HealthCheckServletContextListener(HealthCheckRegistry healthCheckRegistry) {
        this.healthCheckRegistry = healthCheckRegistry;
    }

    @Override
    protected HealthCheckRegistry getHealthCheckRegistry() {
        return healthCheckRegistry;
    }
}
