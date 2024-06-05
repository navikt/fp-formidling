package no.nav.foreldrepenger.fpformidling.server;

import static io.micrometer.core.instrument.Metrics.timer;

import java.io.IOException;
import java.time.Duration;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import no.nav.vedtak.log.metrics.MetricsUtil;

@Provider
@Priority(Priorities.USER)
public class TimingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final String STATUS = "status";
    private static final String PATH = "path";
    private static final String METRIC_NAME = "rest";
    private static final ThreadLocalTimer TIMER = new ThreadLocalTimer();

    static {
        MetricsUtil.timerUtenHistogram(METRIC_NAME);
    }

    @Override
    public void filter(ContainerRequestContext req, ContainerResponseContext res) throws IOException {
        timer(METRIC_NAME, PATH, req.getUriInfo().getPath(), STATUS, String.valueOf(res.getStatus())).record(Duration.ofMillis(TIMER.stop()));
    }

    @Override
    public void filter(ContainerRequestContext req) throws IOException {
        TIMER.start();
    }

    private static class ThreadLocalTimer extends ThreadLocal<Long> {
        public void start() {
            this.set(System.currentTimeMillis());
        }

        public long stop() {
            return System.currentTimeMillis() - get();
        }

        @Override
        protected Long initialValue() {
            return System.currentTimeMillis();
        }
    }
}
