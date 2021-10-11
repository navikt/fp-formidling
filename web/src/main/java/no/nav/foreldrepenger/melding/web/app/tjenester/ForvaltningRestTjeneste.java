package no.nav.foreldrepenger.melding.web.app.tjenester;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.foreldrepenger.melding.sikkerhet.pdp.FPFormidlingBeskyttetRessursAttributt.DRIFT;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.CREATE;
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
import no.nav.foreldrepenger.felles.integrasjon.rest.DefaultJsonMapper;
import no.nav.foreldrepenger.konfig.Environment;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.geografisk.PoststedKodeverkRepository;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.Dokgen;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.Fagsystem;
import no.nav.foreldrepenger.melding.poststed.PostnummerSynkroniseringTjeneste;
import no.nav.foreldrepenger.melding.typer.Saksnummer;
import no.nav.foreldrepenger.melding.web.app.tjenester.brev.AbacBehandlingUuidDto;
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
    private PostnummerSynkroniseringTjeneste postnummerTjeneste;
    private PoststedKodeverkRepository poststedKodeverkRepository;

    public ForvaltningRestTjeneste() {
        // CDI
    }

    @Inject
    public ForvaltningRestTjeneste(/* @Jersey */ Dokgen dokgenRestKlient,
            DomeneobjektProvider domeneobjektProvider,
            SakClient sakClient,
            PostnummerSynkroniseringTjeneste postnummerTjeneste,
            PoststedKodeverkRepository poststedKodeverkRepository) {
        this.dokgenRestKlient = dokgenRestKlient;
        this.domeneobjektProvider = domeneobjektProvider;
        this.sakClient = sakClient;
        this.postnummerTjeneste = postnummerTjeneste;
        this.poststedKodeverkRepository = poststedKodeverkRepository;
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

    @POST
    @Path("/synk-postnummer")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @Operation(description = "Hente og lagre kodeverk Postnummer", tags = "forvaltning")
    @BeskyttetRessurs(action = CREATE, resource = DRIFT)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Response synkPostnummer() {
        postnummerTjeneste.synkroniserPostnummer();
        return Response.ok().build();
    }

    @GET
    @Path("/hent-postnummer")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @Operation(description = "Hente lokale Postnummer", tags = "forvaltning")
    @BeskyttetRessurs(action = CREATE, resource = DRIFT)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Response hentPostnummer() {
        return Response.ok(poststedKodeverkRepository.finnPostnummer("SYNK")).build();
    }
}
