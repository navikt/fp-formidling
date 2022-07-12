package no.nav.foreldrepenger.fpformidling.web.app.tjenester;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.foreldrepenger.fpformidling.sikkerhet.pdp.FPFormidlingBeskyttetRessursAttributt.DRIFT;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.CREATE;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.READ;

import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.bestiller.DokgenBrevproduksjonTjeneste;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.task.BrevTaskProperties;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.task.ProduserBrevTask;
import no.nav.foreldrepenger.fpformidling.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.Dokgen;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.http.JavaClient;
import no.nav.foreldrepenger.fpformidling.sikkerhet.pdp.FPFormidlingBeskyttetRessursAttributt;
import no.nav.foreldrepenger.fpformidling.web.app.tjenester.brev.BrevRestTjeneste;
import no.nav.foreldrepenger.fpformidling.web.app.tjenester.brev.DokumentHendelseDtoMapper;
import no.nav.foreldrepenger.konfig.Environment;
import no.nav.foreldrepenger.kontrakter.formidling.v1.DokumentbestillingV2Dto;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskTjeneste;
import no.nav.vedtak.mapper.json.DefaultJsonMapper;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.TilpassetAbacAttributt;

@Path("/forvaltning")
@ApplicationScoped
@Transactional
public class ForvaltningRestTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(ForvaltningRestTjeneste.class);
    private static final Environment ENVIRONMENT = Environment.current();

    private Dokgen dokgenRestKlient;

    private DokumentHendelseTjeneste dokumentHendelseTjeneste;

    private ProsessTaskTjeneste taskTjeneste;

    public ForvaltningRestTjeneste() {
        // CDI
    }

    @Inject
    public ForvaltningRestTjeneste(@JavaClient Dokgen dokgenRestKlient,
                                   DokumentHendelseTjeneste dokumentHendelseTjeneste,
                                   ProsessTaskTjeneste taskTjeneste) {
        this.dokgenRestKlient = dokgenRestKlient;
        this.dokumentHendelseTjeneste = dokumentHendelseTjeneste;
        this.taskTjeneste = taskTjeneste;
    }

    @POST
    @Path("/dokgen-json-til-pdf")
    @Consumes(APPLICATION_JSON)
    @Produces("application/pdf")
    @Operation(description = "Tar imot en FP-Dokgen JSON og sender den til FP-Dokgen for å lage PDF. Tjenesten er ikke tilgjengelig i produksjon - bruk DEV eller lokalt miljø.", tags = "forvaltning", responses = {
            @ApiResponse(responseCode = "200", description = "Returnerer PDF", content = @Content(mediaType = "application/pdf", schema = @Schema(type = "string", format = "byte")))
    })
    @BeskyttetRessurs(action = READ, resource = DRIFT, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Response dokgenJsonTilPdf(@BeanParam @Valid DokgenJsonTilPdfDto dokgenJsonTilPdfDto) throws Exception {
        if (ENVIRONMENT.isProd()) {
            // Kjøring i prod vil potensielt gi unødvendig loggstøy, feks ved syntaksfeil
            return Response.status(Response.Status.METHOD_NOT_ALLOWED).build();
        }

        Dokumentdata dokumentdata = (Dokumentdata) DefaultJsonMapper.getObjectMapper().readValue(dokgenJsonTilPdfDto.getDokumentdataJson(),
                Class.forName(dokgenJsonTilPdfDto.getDokumentdataKlasse()));

        byte[] resultat = dokgenRestKlient.genererPdf(dokgenJsonTilPdfDto.getMalType(), Språkkode.defaultNorsk(dokgenJsonTilPdfDto.getSpråkKode()),
                dokumentdata);

        Response.ResponseBuilder responseBuilder = Response.ok(resultat);
        responseBuilder.type("application/pdf");
        responseBuilder.header("Content-Disposition", "attachment; filename=dokument.pdf");
        return responseBuilder.build();
    }

    @POST
    @Path("/rebestill")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Sender brev på nytt med oppdatert bestilling id. Brukes kun i situasjoner hvor brevet ", tags = "brev")
    @BeskyttetRessurs(action = CREATE, resource = FPFormidlingBeskyttetRessursAttributt.DRIFT, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Response rebestillDokument(
            @Parameter(description = "Hendelse ID til den opprinelige bestillingen.")
            @Valid @NotNull Long hendelseId) {

        var originalHendelse = dokumentHendelseTjeneste.hentHendelse(hendelseId);

        var dokumentHendelse = originalHendelse.map(this::oppdaterBestillingId).orElseThrow();
        dokumentHendelseTjeneste.validerUnikOgLagre(dokumentHendelse).ifPresent(this::opprettBestillBrevTask);

        return Response.ok().build();
    }

    private DokumentHendelse oppdaterBestillingId(DokumentHendelse originalHendelse) {
        var nyBestillingUuid = UUID.randomUUID();
        var originalBestillingUuid = originalHendelse.getBestillingUuid();
        LOG.info("Produserer ny brev for bestilling {} med ny bestillingsid {}", originalBestillingUuid, nyBestillingUuid);
        return DokumentHendelse.builder()
                .medBehandlingUuid(originalHendelse.getBehandlingUuid())
                .medBestillingUuid(nyBestillingUuid)
                .medBehandlendeEnhetNavn(originalHendelse.getBehandlendeEnhetNavn())
                .medDokumentMalType(originalHendelse.getDokumentMalType())
                .medFritekst(originalHendelse.getFritekst())
                .medErOpphevetKlage(originalHendelse.getErOpphevetKlage())
                .medGjelderVedtak(originalHendelse.isGjelderVedtak())
                .medTittel(originalHendelse.getTittel())
                .medVedtaksbrev(originalHendelse.getVedtaksbrev())
                .medRevurderingVarslingÅrsak(originalHendelse.getRevurderingVarslingÅrsak())
                .medYtelseType(originalHendelse.getYtelseType())
                .build();
    }

    private void opprettBestillBrevTask(DokumentHendelse dokumentHendelse) {
        ProsessTaskData prosessTaskData = ProsessTaskData.forProsessTask(ProduserBrevTask.class);
        prosessTaskData.setProperty(BrevTaskProperties.HENDELSE_ID, String.valueOf(dokumentHendelse.getId()));
        prosessTaskData.setProperty(BrevTaskProperties.BEHANDLING_UUID, String.valueOf(dokumentHendelse.getBehandlingUuid()));
        taskTjeneste.lagre(prosessTaskData);
    }
}
