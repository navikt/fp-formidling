package no.nav.vedtak.felles.prosesstask.rest;

import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.CREATE;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.READ;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt.DRIFT;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import no.nav.vedtak.felles.jpa.Transaction;
import no.nav.vedtak.felles.prosesstask.rest.app.ProsessTaskApplikasjonTjeneste;
import no.nav.vedtak.felles.prosesstask.rest.dto.FeiletProsessTaskDataDto;
import no.nav.vedtak.felles.prosesstask.rest.dto.ProsessTaskDataDto;
import no.nav.vedtak.felles.prosesstask.rest.dto.ProsessTaskDataInfo;
import no.nav.vedtak.felles.prosesstask.rest.dto.ProsessTaskDataPayloadDto;
import no.nav.vedtak.felles.prosesstask.rest.dto.ProsessTaskIdDto;
import no.nav.vedtak.felles.prosesstask.rest.dto.ProsessTaskRestartInputDto;
import no.nav.vedtak.felles.prosesstask.rest.dto.ProsessTaskRestartResultatDto;
import no.nav.vedtak.felles.prosesstask.rest.dto.ProsessTaskRetryAllResultatDto;
import no.nav.vedtak.felles.prosesstask.rest.dto.SokeFilterDto;
import no.nav.vedtak.log.sporingslogg.Sporingsdata;
import no.nav.vedtak.log.sporingslogg.SporingsloggHelper;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;

@Api(tags = "prosesstask")
@Path("/prosesstask")
@RequestScoped
@Transaction
public class ProsessTaskRestTjeneste {

    private static final Logger logger = LoggerFactory.getLogger(ProsessTaskRestTjeneste.class);

    private ProsessTaskApplikasjonTjeneste prosessTaskApplikasjonTjeneste;

    public ProsessTaskRestTjeneste() {
        // REST CDI
    }

    @Inject
    public ProsessTaskRestTjeneste(ProsessTaskApplikasjonTjeneste prosessTaskApplikasjonTjeneste) {
        this.prosessTaskApplikasjonTjeneste = prosessTaskApplikasjonTjeneste;
    }

    @POST
    @Path("/launch")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Restarter en eksisterende prosesstask.",
            notes = "En allerede FERDIG prosesstask kan ikke restartes. En prosesstask har normalt et gitt antall forsøk den kan kjøres automatisk. " +
                    "Dette endepunktet vil tvinge tasken til å trigge uavhengig av maks antall forsøk"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "Prosesstaskens oppdatert informasjon",
                    response = ProsessTaskRestartResultatDto.class
            ),
            @ApiResponse(code = 500, message = "Feilet pga ukjent feil eller tekniske/funksjonelle feil")
    })
    @BeskyttetRessurs(action = CREATE, ressurs = DRIFT)
    public ProsessTaskRestartResultatDto restartProsessTask(@ApiParam("Informasjon for restart en eksisterende prosesstask") @Valid ProsessTaskRestartInputDto restartInputDto) {
        //kjøres manuelt for å avhjelpe feilsituasjon, da er det veldig greit at det blir logget!
        logger.info("Restarter prossess task {}", restartInputDto.getProsessTaskId());

        return prosessTaskApplikasjonTjeneste.flaggProsessTaskForRestart(restartInputDto);
    }

    @POST
    @Path("/launch-batch")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Planlegger neste kjøring for alle batch tasktyper som ikke har status fra før av",
            notes = "Gir status på alle batch-tasks"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "Prosesstaskens oppdatert informasjon",
                    response = ProsessTaskRestartResultatDto.class
            ),
            @ApiResponse(code = 500, message = "Feilet pga ukjent feil eller tekniske/funksjonelle feil")
    })
    @BeskyttetRessurs(action = CREATE, ressurs = DRIFT)
    public List<ProsessTaskDataDto> initBatch() {

        return prosessTaskApplikasjonTjeneste.finnStatusPåBatchTasks();
    }

    @POST
    @Path("/retryall")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Restarter alle prosesstask med status FEILET.",
            notes = "Dette endepunktet vil tvinge feilede tasks til å trigge ett forsøk uavhengig av maks antall forsøk"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "Response med liste av prosesstasks som restartes",
                    response = ProsessTaskRetryAllResultatDto.class
            ),
            @ApiResponse(code = 500, message = "Feilet pga ukjent feil eller tekniske/funksjonelle feil")
    })
    @BeskyttetRessurs(action = CREATE, ressurs = DRIFT)
    public ProsessTaskRetryAllResultatDto retryAllProsessTask() {
        //kjøres manuelt for å avhjelpe feilsituasjon, da er det veldig greit at det blir logget!
        logger.info("Restarter alle prossess task i status FEILET");

        return prosessTaskApplikasjonTjeneste.flaggAlleFeileteProsessTasksForRestart();
    }

    @POST
    @Path("/list")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Søker etter prosesstask med mulighet for filtrert søk.",
            notes = ("Default søkes det etter alle tasks med status KLAR, eller VENTER_SVAR fra siste 24 timer. Dette kan endres med søkefilter"))
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "Liste over prosesstasker, eller tom liste når angitt/default søkefilter ikke finner noen prosesstasker",
                    response = ProsessTaskDataDto.class, responseContainer = "List"
            ),
    })
    @BeskyttetRessurs(action = READ, ressurs = DRIFT)
    public List<ProsessTaskDataDto> finnProsessTasks(@ApiParam("Søkefilter for å begrense resultatet av returnerte prosesstask.") @Valid SokeFilterDto sokeFilterDto) {
        List<ProsessTaskDataDto> resultat = prosessTaskApplikasjonTjeneste.finnAlle(sokeFilterDto);

        //må logge tilgang til personopplysninger, det blir ikke logget nok via @BeskyttetRessurs siden det er rolle-tilgang her
        for (ProsessTaskDataDto dto : resultat) {
            loggLesingAvPersondataFraProsessTask(dto, "/list");
        }

        return resultat;
    }

    @POST
    @Path("/feil")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Henter informasjon om feilet prosesstask med angitt prosesstask-id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Angit prosesstask-id finnes", response = FeiletProsessTaskDataDto.class),
            @ApiResponse(code = 404, message = "Tom respons når angitt prosesstask-id ikke finnes"),
            @ApiResponse(code = 400, message = "Feil input")
    })
    @BeskyttetRessurs(action = READ, ressurs = DRIFT)
    public Response finnFeiletProsessTask(@NotNull @ApiParam("Prosesstask-id for feilet prosesstask") @Valid ProsessTaskIdDto prosessTaskIdDto) {
        Optional<FeiletProsessTaskDataDto> resultat = prosessTaskApplikasjonTjeneste.finnFeiletProsessTask(prosessTaskIdDto.getProsessTaskId());
        if (resultat.isPresent()) {

            //må logge tilgang til personopplysninger, det blir ikke logget nok via @BeskyttetRessurs siden det er rolle-tilgang her
            loggLesingAvPersondataFraProsessTask(resultat.get(), "/no/nav/vedtak/felles/behandlingsprosess/prosesstask/rest/feil");

            return Response.ok(resultat.get()).build();
        }
        return Response.status(HttpStatus.SC_NOT_FOUND).build();
    }

    @POST
    @Path("/payload")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Henter informasjon om prosesstask, inkludert payload for angitt prosesstask-id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Angit prosesstask-id finnes", response = ProsessTaskDataPayloadDto.class),
            @ApiResponse(code = 404, message = "Tom respons når angitt prosesstask-id ikke finnes"),
            @ApiResponse(code = 400, message = "Feil input")
    })
    @BeskyttetRessurs(action = READ, ressurs = DRIFT)
    public Response finnProsessTaskInkludertPayload(@NotNull @ApiParam("Prosesstask-id for en eksisterende prosesstask") @Valid ProsessTaskIdDto prosessTaskIdDto) {
        Optional<ProsessTaskDataPayloadDto> resultat = prosessTaskApplikasjonTjeneste.finnProsessTaskMedPayload(prosessTaskIdDto.getProsessTaskId());
        if (resultat.isPresent()) {

            //må logge tilgang til personopplysninger, det blir ikke logget nok via @BeskyttetRessurs siden det er rolle-tilgang her
            loggLesingAvPersondataFraProsessTask(resultat.get(), "/payload");

            return Response.ok(resultat.get()).build();
        }
        return Response.status(HttpStatus.SC_NOT_FOUND).build();
    }

    private void loggLesingAvPersondataFraProsessTask(ProsessTaskDataInfo prosessTaskInfo, String metode) {
        String actionType = "read";
        String endepunkt = ProsessTaskRestTjeneste.class.getAnnotation(Path.class).value() + metode;
        Optional<Sporingsdata> sporingsdata = prosessTaskInfo.lagSporingsloggData();
        sporingsdata.ifPresent(sd -> SporingsloggHelper.logSporing(ProsessTaskRestTjeneste.class, sd, actionType, endepunkt));
    }
}
