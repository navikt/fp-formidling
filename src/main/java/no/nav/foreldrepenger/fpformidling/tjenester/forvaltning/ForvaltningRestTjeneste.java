package no.nav.foreldrepenger.fpformidling.tjenester.forvaltning;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.UUID;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.bestiller.BrevBestillerTjeneste;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.task.BrevTaskProperties;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.task.ProduserBrevTask;
import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelseTjeneste;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.Dokgen;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.foreldrepenger.fpformidling.tjenester.DokumentHendelseDtoMapper;
import no.nav.foreldrepenger.konfig.Environment;
import no.nav.foreldrepenger.kontrakter.formidling.kodeverk.DokumentMal;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskTjeneste;
import no.nav.vedtak.mapper.json.DefaultJsonMapper;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.TilpassetAbacAttributt;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ActionType;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ResourceType;

@Path("/forvaltning")
@ApplicationScoped
@Transactional
public class ForvaltningRestTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(ForvaltningRestTjeneste.class);
    private static final Environment ENVIRONMENT = Environment.current();
    private Dokgen dokgenRestKlient;
    private DokumentHendelseTjeneste dokumentHendelseTjeneste;
    private ProsessTaskTjeneste taskTjeneste;
    private BrevBestillerTjeneste brevBestillerTjeneste;

    public ForvaltningRestTjeneste() {
        // CDI
    }

    @Inject
    public ForvaltningRestTjeneste(Dokgen dokgenRestKlient, DokumentHendelseTjeneste dokumentHendelseTjeneste, ProsessTaskTjeneste taskTjeneste, BrevBestillerTjeneste brevBestillerTjeneste) {
        this.dokgenRestKlient = dokgenRestKlient;
        this.dokumentHendelseTjeneste = dokumentHendelseTjeneste;
        this.taskTjeneste = taskTjeneste;
        this.brevBestillerTjeneste = brevBestillerTjeneste;
    }

    @POST
    @Path("/dokgen-json-til-pdf")
    @Consumes(APPLICATION_JSON)
    @Produces("application/pdf")
    @Operation(description = "Tar imot en FP-Dokgen JSON og sender den til FP-Dokgen for å lage PDF. Tjenesten er ikke tilgjengelig i produksjon - bruk DEV eller lokalt miljø.", tags = "forvaltning", responses = {@ApiResponse(responseCode = "200", description = "Returnerer PDF", content = @Content(mediaType = "application/pdf", schema = @Schema(type = "string", format = "byte")))})
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.DRIFT, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Response dokgenJsonTilPdf(@BeanParam @Valid DokgenJsonTilPdfDto dokgenJsonTilPdfDto) throws ClassNotFoundException, JsonProcessingException {
        if (ENVIRONMENT.isProd()) {
            // Kjøring i prod vil potensielt gi unødvendig loggstøy, feks ved syntaksfeil
            return Response.status(Response.Status.METHOD_NOT_ALLOWED).build();
        }

        var dokumentdata = (Dokumentdata) DefaultJsonMapper.getObjectMapper()
            .readValue(dokgenJsonTilPdfDto.getDokumentdataJson(), Class.forName(dokgenJsonTilPdfDto.getDokumentdataKlasse()));

        var resultat = dokgenRestKlient.genererPdf(dokgenJsonTilPdfDto.getMalType(), Språkkode.defaultNorsk(dokgenJsonTilPdfDto.getSpråkKode()),
            dokumentdata);

        var responseBuilder = Response.ok(resultat);
        responseBuilder.type("application/pdf");
        responseBuilder.header("Content-Disposition", "attachment; filename=dokument.pdf");
        return responseBuilder.build();
    }

    @POST
    @Path("/rebestill")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @Operation(description = "Sender brev på nytt med oppdatert bestilling id. Brukes kun i situasjoner hvor brevet ble helt feil.", tags = "forvaltning")
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.DRIFT, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Response rebestillDokument(@Parameter(description = "Hendelse ID til den opprinelige bestillingen.") @TilpassetAbacAttributt(supplierClass = ForvaltningRestTjeneste.AbacSupplier.class) @QueryParam("hendelseId") @Valid @NotNull Long hendelseId) {

        if (ENVIRONMENT.isProd()) {
            return Response.status(Response.Status.METHOD_NOT_ALLOWED).build();
        }

        var originalHendelse = dokumentHendelseTjeneste.hentHendelse(hendelseId);

        var dokumentHendelse = originalHendelse.map(this::oppdaterBestillingId).orElseThrow();
        dokumentHendelseTjeneste.validerUnikOgLagre(dokumentHendelse).ifPresent(this::opprettBestillBrevTask);

        return Response.ok().build();
    }

    @POST
    @Path("/generer-brevdata-json")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @Operation(description = "Generer brevdata-json for siste vedtak i en behandling", tags = "forvaltning")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.DRIFT, sporingslogg = false)
    public Response genererBrevdata(@Parameter(description = "Behandling uuid det skal genereres json for.") @TilpassetAbacAttributt(supplierClass = ForvaltningRestTjeneste.AbacSupplier.class) @QueryParam("behandlingUuid") @Valid @NotNull UUID behandlingUuid, @TilpassetAbacAttributt(supplierClass = ForvaltningRestTjeneste.AbacSupplier.class) @QueryParam("dokumentMal") @Valid @NotNull DokumentMal dokumentMal) {

        var resultat = brevBestillerTjeneste.genererJson(behandlingUuid, DokumentHendelseDtoMapper.mapDokumentMal(dokumentMal));

        var responseBuilder = Response.ok(resultat);

        return responseBuilder.build();
    }

    private DokumentHendelse oppdaterBestillingId(DokumentHendelse originalHendelse) {
        var nyBestillingUuid = UUID.randomUUID();
        var originalBestillingUuid = originalHendelse.getBestillingUuid();
        LOG.info("Produserer ny brev for bestilling {} med ny bestillingsid {}", originalBestillingUuid, nyBestillingUuid);
        return DokumentHendelse.builder()
            .medBehandlingUuid(originalHendelse.getBehandlingUuid())
            .medBestillingUuid(nyBestillingUuid)
            .medDokumentMal(originalHendelse.getDokumentMal())
            .medFritekst(originalHendelse.getFritekst())
            .medTittel(originalHendelse.getTittel())
            .medRevurderingÅrsak(originalHendelse.getRevurderingÅrsak())
            .build();
    }

    private void opprettBestillBrevTask(DokumentHendelse dokumentHendelse) {
        var prosessTaskData = ProsessTaskData.forProsessTask(ProduserBrevTask.class);
        prosessTaskData.setProperty(BrevTaskProperties.HENDELSE_ID, String.valueOf(dokumentHendelse.getId()));
        prosessTaskData.setBehandlingUUid(dokumentHendelse.getBehandlingUuid());
        taskTjeneste.lagre(prosessTaskData);
    }

    public static class AbacSupplier implements Function<Object, AbacDataAttributter> {
        @Override
        public AbacDataAttributter apply(Object obj) {
            return AbacDataAttributter.opprett();
        }
    }
}
