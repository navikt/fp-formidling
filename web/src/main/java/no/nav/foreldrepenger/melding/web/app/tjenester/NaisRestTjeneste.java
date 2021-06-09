package no.nav.foreldrepenger.melding.web.app.tjenester;

import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.Operation;
import no.nav.vedtak.log.metrics.LivenessAware;
import no.nav.vedtak.log.metrics.ReadinessAware;

@Path("/health")
@ApplicationScoped
public class NaisRestTjeneste {

    private static final String RESPONSE_OK = "OK";
    private static final CacheControl CC = cacheControl();

    private ApplicationServiceStarter starterService;
    private List<LivenessAware> live;
    private List<ReadinessAware> ready;

    public NaisRestTjeneste() {
    }

    @Inject
    public NaisRestTjeneste(ApplicationServiceStarter starterService, @Any Instance<LivenessAware> livenessAware,
            @Any Instance<ReadinessAware> readinessAware) {
        this(starterService, livenessAware.stream().collect(toList()), readinessAware.stream().collect(toList()));

    }

    NaisRestTjeneste(ApplicationServiceStarter starterService, List<LivenessAware> live, List<ReadinessAware> ready) {
        this.live = live;
        this.ready = ready;
        this.starterService = starterService;
    }

    @GET
    @Path("isReady")
    @Operation(description = "sjekker om poden er klar", tags = "nais", hidden = true)
    public Response isReady() {
        if (ready.stream().allMatch(ReadinessAware::isReady)) {
            return Response
                    .ok(RESPONSE_OK)
                    .cacheControl(CC)
                    .build();
        }
        return Response
                .status(SERVICE_UNAVAILABLE)
                .cacheControl(CC)
                .build();
    }

    @GET
    @Path("isAlive")
    @Operation(description = "sjekker om poden lever", tags = "nais", hidden = true)
    public Response isAlive() {
        if (live.stream().allMatch(LivenessAware::isAlive)) {
            return Response
                    .ok(RESPONSE_OK)
                    .cacheControl(CC)
                    .build();
        }
        return Response
                .serverError()
                .build();
    }

    @GET
    @Path("preStop")
    @Operation(description = "kalles på før stopp", tags = "nais", hidden = true)
    public Response preStop() {
        starterService.stopServices();
        return Response.ok(RESPONSE_OK).build();
    }

    private static CacheControl cacheControl() {
        CacheControl cc = new CacheControl();
        cc.setNoCache(true);
        cc.setNoStore(true);
        cc.setMustRevalidate(true);
        return cc;
    }
}
