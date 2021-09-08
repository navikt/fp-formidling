package no.nav.foreldrepenger.melding.web.app.tjenester.brev;

import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.READ;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import no.nav.foreldrepenger.kontrakter.formidling.v1.BrevmalDto;
import no.nav.foreldrepenger.melding.brevbestiller.api.DokumentBehandlingTjeneste;
import no.nav.foreldrepenger.melding.brevbestiller.impl.BrevBestillerTjeneste;
import no.nav.foreldrepenger.melding.sikkerhet.pdp.FPFormidlingBeskyttetRessursAttributt;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;

@Path("/brev")
@ApplicationScoped
@Transactional
public class BrevRestTjeneste {

    private DokumentBehandlingTjeneste dokumentBehandlingTjeneste;
    private BrevBestillerTjeneste brevBestillerApplikasjonTjeneste;

    public BrevRestTjeneste() {
        //CDI
    }

    @Inject
    public BrevRestTjeneste(DokumentBehandlingTjeneste dokumentBehandlingTjeneste,
                            BrevBestillerTjeneste brevBestillerApplikasjonTjeneste) {
        this.dokumentBehandlingTjeneste = dokumentBehandlingTjeneste;
        this.brevBestillerApplikasjonTjeneste = brevBestillerApplikasjonTjeneste;
    }

    @GET
    @Path("/maler")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Henter liste over tilgjengelige brevtyper", tags = "brev")
    @BeskyttetRessurs(action = READ, resource = FPFormidlingBeskyttetRessursAttributt.FAGSAK, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<BrevmalDto> hentMaler(@NotNull @QueryParam(AbacBehandlingUuidDto.NAME) @Parameter(description = AbacBehandlingUuidDto.DESC) @Valid AbacBehandlingUuidDto uuidDto) {
        return dokumentBehandlingTjeneste.hentBrevmalerFor(uuidDto.getBehandlingUuid()); // NOSONAR
    }

    @POST
    @Path("/forhaandsvis")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Returnerer en pdf som er en forhåndsvisning av brevet", tags = "brev")
    @BeskyttetRessurs(action = READ, resource = FPFormidlingBeskyttetRessursAttributt.FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Response forhaandsvisDokument(
            @Parameter(description = "Inneholder kode til brevmal og data som skal flettes inn i brevet") @Valid AbacDokumentbestillingDto dokumentbestillingDto) { // NOSONAR
        byte[] dokument = brevBestillerApplikasjonTjeneste.forhandsvisBrev(dokumentbestillingDto);

        if (dokument != null && dokument.length != 0) {
            Response.ResponseBuilder responseBuilder = Response.ok(dokument);
            responseBuilder.type("application/pdf");
            responseBuilder.header("Content-Disposition", "filename=dokument.pdf");
            return responseBuilder.build();
        }
        return Response.serverError().build();
    }
}
