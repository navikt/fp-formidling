package no.nav.foreldrepenger.fpformidling.web.app.metrics;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Operation;
import no.nav.vedtak.log.metrics.MetricsUtil;

@Path("/metrics")
@Produces(MediaType.TEXT_PLAIN)
@ApplicationScoped
public class PrometheusRestService {

    @GET
    @Operation(tags = "metrics", hidden = true)
    @Path("/prometheus")
    public String prometheus() {
        return MetricsUtil.scrape();
    }
}
