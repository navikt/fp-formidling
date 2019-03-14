package no.nav.foreldrepenger.melding.web.app.metrics;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.annotation.WebListener;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.servlets.MetricsServlet;

@ApplicationScoped
@WebListener
public class MetricsServletContextListener extends MetricsServlet.ContextListener {

    @Inject
    private MetricRegistry metricRegistry;  // NOSONAR

    @Override
    protected MetricRegistry getMetricRegistry() {
        return metricRegistry;
    }
}
