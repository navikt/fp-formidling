package no.nav.foreldrepenger.melding.web.app.tjenester;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.CREATE;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt.DRIFT;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import no.nav.vedtak.felles.jpa.Transaction;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskRepository;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskStatus;
import no.nav.vedtak.felles.prosesstask.rest.dto.ProsessTaskIdDto;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;

@Api(tags = {"Forvaltning"})
@Path("/forvaltning")
@RequestScoped
@Transaction
public class ForvaltningRestTjeneste {

    private ProsessTaskRepository prosessTaskRepository;

    public ForvaltningRestTjeneste() {
        // CDI
    }

    @Inject
    public ForvaltningRestTjeneste(ProsessTaskRepository prosessTaskRepository) {
        this.prosessTaskRepository = prosessTaskRepository;
    }

    @POST
    @Path("/sett-task-ferdig")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @ApiOperation(value = "Setter prosesstask til status FERDIG")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Task satt til ferdig."),
            @ApiResponse(code = 500, message = "Feilet pga ukjent feil.")
    })
    @BeskyttetRessurs(action = CREATE, ressurs = DRIFT)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Response setTaskFerdig(@ApiParam("Task som skal settes ferdig") @NotNull @Valid ProsessTaskIdDto taskId) {
        ProsessTaskData data = prosessTaskRepository.finn(taskId.getProsessTaskId());
        if (data != null) {
            data.setStatus(ProsessTaskStatus.FERDIG);
            data.setSisteFeil(null);
            data.setSisteFeilKode(null);
            prosessTaskRepository.lagre(data);
        }
        return Response.ok().build();
    }
}
