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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import no.nav.foreldrepenger.felles.integrasjon.rest.DefaultJsonMapper;
import no.nav.foreldrepenger.fpformidling.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.Dokgen;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Fagsystem;
import no.nav.foreldrepenger.fpformidling.typer.Saksnummer;
import no.nav.foreldrepenger.fpformidling.web.app.tjenester.brev.AbacBehandlingUuidDto;
import no.nav.foreldrepenger.konfig.Environment;
import no.nav.vedtak.felles.integrasjon.sak.v1.SakClient;
import no.nav.vedtak.felles.integrasjon.sak.v1.SakJson;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;

@Path("/forvaltning")
@ApplicationScoped
@Transactional
public class ForvaltningRestTjeneste {
    private static final Environment ENVIRONMENT = Environment.current();

    private Dokgen dokgenRestKlient;
    private DomeneobjektProvider domeneobjektProvider;
    private SakClient sakClient;

    public ForvaltningRestTjeneste() {
        // CDI
    }

    @Inject
    public ForvaltningRestTjeneste(/* @Jersey */ Dokgen dokgenRestKlient,
            DomeneobjektProvider domeneobjektProvider,
            SakClient sakClient) {
        this.dokgenRestKlient = dokgenRestKlient;
        this.domeneobjektProvider = domeneobjektProvider;
        this.sakClient = sakClient;
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

        byte[] resultat = dokgenRestKlient.genererPdf(dokgenJsonTilPdfDto.getMalType(), Språkkode.fraKode(dokgenJsonTilPdfDto.getSpråkKode()),
                dokumentdata);

        Response.ResponseBuilder responseBuilder = Response.ok(resultat);
        responseBuilder.type("application/pdf");
        responseBuilder.header("Content-Disposition", "attachment; filename=dokument.pdf");
        return responseBuilder.build();
    }

    @POST
    @Path("/opprett-arkivsak-hvis-mangler")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @Operation(description = "Tar imot behandlingUuid og oppretter arkivsak til bruk for dokprod dersom det mangler.", tags = "forvaltning")
    @BeskyttetRessurs(action = READ, resource = DRIFT)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Response opprettArkivsakVedBehov(@BeanParam @Valid AbacBehandlingUuidDto uuidDto) throws Exception {
        var behandling = domeneobjektProvider.hentBehandling(uuidDto.getBehandlingUuid());
        var fagsak = domeneobjektProvider.hentFagsakBackend(behandling);
        var saksnummer = fagsak.getSaksnummer();
        if (Long.parseLong(saksnummer.getVerdi()) >= 152000000L) {
            var sak = sakClient.finnForSaksnummer(saksnummer.getVerdi());
            if (sak.isEmpty()) {
                var request = SakJson.getBuilder()
                        .medAktoerId(fagsak.getAktørId().getId())
                        .medFagsakNr(saksnummer.getVerdi())
                        .medApplikasjon(Fagsystem.FPSAK.getOffisiellKode())
                        .medTema("FOR")
                        .build();
                var arkivsak = sakClient.opprettSak(request);
                return Response.ok(new Saksnummer(String.valueOf(arkivsak.getId()))).build();
            }
        }
        return Response.ok().build();
    }
}
