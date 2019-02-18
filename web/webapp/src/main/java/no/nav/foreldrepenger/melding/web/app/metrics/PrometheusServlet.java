package no.nav.foreldrepenger.melding.web.app.metrics;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.codahale.metrics.MetricRegistry;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;
import io.prometheus.client.exporter.MetricsServlet;

@ApplicationScoped
public class PrometheusServlet extends MetricsServlet {

    private transient MetricRegistry registry; // NOSONAR
    private transient AppMetricsServlet dropWizardServlet;

    @Override
    public void init(ServletConfig config) throws ServletException {
        dropWizardServlet.doConfig();
        // Hook the Dropwizard registry into the Prometheus registry
        // via the DropwizardExports collector.
        CollectorRegistry.defaultRegistry.register(new DropwizardExports(registry));
    }

    @Inject
    public void setRegistry(MetricRegistry registry) {
        this.registry = registry; // NOSONAR
    }

    @Inject
    public void setRegistry(AppMetricsServlet dropWizardServlet) {
        this.dropWizardServlet = dropWizardServlet;
    }
}
