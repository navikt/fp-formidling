package no.nav.foreldrepenger.melding.web.app.tjenester;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import io.swagger.annotations.Api;
import no.nav.foreldrepenger.melding.web.app.selftest.SelftestService;

@Api(tags = {"nais"})
@Path("/")
@Produces(TEXT_PLAIN)
@RequestScoped
public class NaisRestTjeneste {

    private static final String RESPONSE_CACHE_KEY = "Cache-Control";
    private static final String RESPONSE_CACHE_VAL = "must-revalidate,no-cache,no-store";
    private static final String RESPONSE_OK = "OK";

    private ApplicationServiceStarter starterService;
    private SelftestService selftestService;

    public NaisRestTjeneste() {
        // CDI
    }

    @Inject
    public NaisRestTjeneste(ApplicationServiceStarter starterService,
                            SelftestService selftestService) {
        this.starterService = starterService;
        this.selftestService = selftestService;
    }

    @GET
    @Path("isAlive")
    public Response isAlive() {
        return selftestService.kritiskTjenesteFeilet() ?
                Response.serverError()
                        .header(RESPONSE_CACHE_KEY, RESPONSE_CACHE_VAL)
                        .build()
                :
                Response
                        .ok(RESPONSE_OK)
                        .header(RESPONSE_CACHE_KEY, RESPONSE_CACHE_VAL)
                        .build();
    }

    @GET
    @Path("isReady")
    public Response isReady() {
        return Response.ok(RESPONSE_OK).header(RESPONSE_CACHE_KEY, RESPONSE_CACHE_VAL).build();

    }

    @GET
    @Path("preStop")
    public Response preStop() {
        starterService.stopServices();
        return Response.ok(RESPONSE_OK).build();
    }

}
