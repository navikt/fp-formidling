package no.nav.foreldrepenger.melding.web.app.tjenester;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.CREATE;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.Operation;
import no.nav.foreldrepenger.melding.geografisk.PoststedKodeverkRepository;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.DokgenRestKlient;
import no.nav.foreldrepenger.melding.poststed.PostnummerSynkroniseringTjeneste;
import no.nav.foreldrepenger.melding.sikkerhet.pdp.FPFormidlingBeskyttetRessursAttributt;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;


@Path("/forvaltning")
@ApplicationScoped
@Transactional
public class ForvaltningRestTjeneste {

    private PostnummerSynkroniseringTjeneste postnummerTjeneste;
    private PoststedKodeverkRepository poststedKodeverkRepository;

    public ForvaltningRestTjeneste() {
        // CDI
    }

    @Inject
    public ForvaltningRestTjeneste(DokgenRestKlient dokgenRestKlient,
                                   PostnummerSynkroniseringTjeneste postnummerTjeneste,
                                   PoststedKodeverkRepository poststedKodeverkRepository) {
        this.postnummerTjeneste = postnummerTjeneste;
        this.poststedKodeverkRepository = poststedKodeverkRepository;
    }

    @POST
    @Path("/synk-postnummer")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @Operation(description = "Hente og lagre kodeverk Postnummer", tags = "forvaltning")
    @BeskyttetRessurs(action = CREATE, resource = FPFormidlingBeskyttetRessursAttributt.DRIFT)
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
    @BeskyttetRessurs(action = CREATE, resource = FPFormidlingBeskyttetRessursAttributt.DRIFT)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Response hentPostnummer() {
        return Response.ok(poststedKodeverkRepository.finnPostnummer("SYNK")).build();
    }
}
