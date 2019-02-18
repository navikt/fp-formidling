package no.nav.foreldrepenger.melding.web.app.selftest;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.health.SharedHealthCheckRegistries;

@ApplicationScoped
class HealthCheckRegistryProducer {

    private static final String SELFTEST_HEALTHCHECK_REGISTRY_NAME = "selftest";

    @Produces
    @ApplicationScoped
    public HealthCheckRegistry getHealthCheckRegistry() {
        return SharedHealthCheckRegistries.getOrCreate(SELFTEST_HEALTHCHECK_REGISTRY_NAME);
    }
}
