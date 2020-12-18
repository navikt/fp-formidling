package no.nav.foreldrepenger.melding.web.app.tjenester;

import static java.lang.String.format;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.v3.oas.annotations.Operation;
import no.nav.foreldrepenger.melding.dokumentproduksjon.v2.DokumentproduksjonConsumerProducer;
import no.nav.foreldrepenger.melding.web.app.selftest.checks.DatabaseHealthCheck;

@Path("/health")
@ApplicationScoped
public class NaisRestTjeneste {

    private static final Logger LOGGER = LoggerFactory.getLogger(NaisRestTjeneste.class);

    private static final String RESPONSE_CACHE_KEY = "Cache-Control";
    private static final String RESPONSE_CACHE_VAL = "must-revalidate,no-cache,no-store";
    private static final String RESPONSE_OK = "OK";

    private ApplicationServiceStarter starterService;
    private DatabaseHealthCheck databaseHealthCheck;
    private DokumentproduksjonConsumerProducer dokumentproduksjonConsumerProducer;

    private boolean harInitDokprod = false;

    public NaisRestTjeneste() {
        // CDI
    }

    @Inject
    public NaisRestTjeneste(ApplicationServiceStarter starterService, DatabaseHealthCheck databaseHealthCheck,
                            DokumentproduksjonConsumerProducer dokumentproduksjonConsumerProducer) {
        this.starterService = starterService;
        this.databaseHealthCheck = databaseHealthCheck;
        this.dokumentproduksjonConsumerProducer = dokumentproduksjonConsumerProducer;
    }

    @GET
    @Path("isReady")
    @Operation(description = "sjekker om poden er klar", tags = "nais", hidden = true)
    public Response isReady() {
        if (!harInitDokprod) {
            try {
                dokumentproduksjonConsumerProducer.dokumentproduksjonSelftestConsumer().ping();
            } catch (Exception e) {
                LOGGER.info(format("Kall til dokprod-selftest feilet: %s", e.getMessage()), e);
            }
            harInitDokprod = true;
        }
        if (starterService.isKafkaAlive() && databaseHealthCheck.isReady()) {
            return Response
                    .ok(RESPONSE_OK)
                    .header(RESPONSE_CACHE_KEY, RESPONSE_CACHE_VAL)
                    .build();
        } else {
            return Response
                    .status(Response.Status.SERVICE_UNAVAILABLE)
                    .header(RESPONSE_CACHE_KEY, RESPONSE_CACHE_VAL)
                    .build();
        }
    }

    @GET
    @Path("isAlive")
    @Operation(description = "sjekker om poden lever", tags = "nais", hidden = true)
    public Response isAlive() {
        if (starterService.isKafkaAlive()) {
            return Response
                    .ok(RESPONSE_OK)
                    .header(RESPONSE_CACHE_KEY, RESPONSE_CACHE_VAL)
                    .build();
        } else {
            return Response
                    .serverError()
                    .header(RESPONSE_CACHE_KEY, RESPONSE_CACHE_VAL)
                    .build();
        }
    }

    @GET
    @Path("preStop")
    @Operation(description = "kalles på før stopp", tags = "nais", hidden = true)
    public Response preStop() {
        starterService.stopServices();
        return Response.ok(RESPONSE_OK).build();
    }

}
