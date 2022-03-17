package no.nav.foreldrepenger.fpformidling.web.app.tjenester;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.foreldrepenger.fpformidling.sikkerhet.pdp.FPFormidlingBeskyttetRessursAttributt.DRIFT;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.READ;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import no.nav.foreldrepenger.fpformidling.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.Dokgen;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.foreldrepenger.fpformidling.migrering.MigrerJournalposterTilFpsakTjeneste;
import no.nav.foreldrepenger.konfig.Environment;
import no.nav.vedtak.mapper.json.DefaultJsonMapper;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;

@Path("/forvaltning")
@ApplicationScoped
@Transactional
public class ForvaltningRestTjeneste {
    private static final Environment ENVIRONMENT = Environment.current();

    private Dokgen dokgenRestKlient;
    private MigrerJournalposterTilFpsakTjeneste migrerTjeneste;

    public ForvaltningRestTjeneste() {
        // CDI
    }

    @Inject
    public ForvaltningRestTjeneste(/* @Jersey */ Dokgen dokgenRestKlient,
                                                 MigrerJournalposterTilFpsakTjeneste migrerTjeneste) {
        this.dokgenRestKlient = dokgenRestKlient;
        this.migrerTjeneste = migrerTjeneste;
    }

    @POST
    @Path("/dokgen-json-til-pdf")
    @Consumes(APPLICATION_JSON)
    @Produces("application/pdf")
    @Operation(description = "Tar imot en FP-Dokgen JSON og sender den til FP-Dokgen for å lage PDF. Tjenesten er ikke tilgjengelig i produksjon - bruk DEV eller lokalt miljø.", tags = "forvaltning", responses = {
            @ApiResponse(responseCode = "200", description = "Returnerer PDF", content = @Content(mediaType = "application/pdf", schema = @Schema(type = "string", format = "byte")))
    })
    @BeskyttetRessurs(action = READ, resource = DRIFT)
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

/*    @GET
    @Path("historikk/migrer")
    @Operation(description = "Sender journalposter over til fpsak", tags = "forvaltning")
    public Response migrer() {
        migrerTjeneste.migrerHistorikk();
        return Response.ok().build();
    }*/
}
