package no.nav.foreldrepenger.melding.web.app.metrics;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import io.swagger.v3.oas.annotations.Operation;
import no.nav.vedtak.log.metrics.MetricsUtil;

@Path("/metrics")
@ApplicationScoped
public class PrometheusRestService {

    @GET
    @Operation(tags = "metrics", hidden = true)
    @Path("/prometheus")
    public String prometheus() {
        return MetricsUtil.scrape();
    }
}
