package no.nav.foreldrepenger.melding.web.app.metrics;

import java.lang.management.ManagementFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.management.MBeanServer;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jvm.BufferPoolMetricSet;
import com.codahale.metrics.jvm.ClassLoadingGaugeSet;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;
import io.prometheus.client.exporter.MetricsServlet;

@ApplicationScoped
@WebServlet("internal/prometheus")
public class PrometheusServlet extends MetricsServlet {

    private transient MetricRegistry registry; // NOSONAR

    private static void configureJvmMetrics(MetricRegistry registry) {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        registry.register("jvm_buffer-pool", new BufferPoolMetricSet(mBeanServer));
        registry.register("jvm_class-loading", new ClassLoadingGaugeSet());
        registry.register("jvm_gc", new GarbageCollectorMetricSet());
        registry.register("jvm_memory", new MemoryUsageGaugeSet());
        registry.register("jvm_threads", new ThreadStatesGaugeSet());
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        configureJvmMetrics(registry);
        // Hook the Dropwizard registry into the Prometheus registry
        // via the DropwizardExports collector.
        CollectorRegistry.defaultRegistry.register(new DropwizardExports(registry));
    }

    @Inject
    public void setRegistry(MetricRegistry registry) {
        this.registry = registry; // NOSONAR
    }

}
