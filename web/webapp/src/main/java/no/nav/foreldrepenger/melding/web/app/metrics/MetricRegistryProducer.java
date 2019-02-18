package no.nav.foreldrepenger.melding.web.app.metrics;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import com.codahale.metrics.MetricRegistry;

/**
 * Producer siden MetricRegistry ikke er annotert.
 */
@ApplicationScoped
public class MetricRegistryProducer {

    @Produces
    @ApplicationScoped
    public MetricRegistry createMetricRegistry() {
        return new MetricRegistry();
    }
}
