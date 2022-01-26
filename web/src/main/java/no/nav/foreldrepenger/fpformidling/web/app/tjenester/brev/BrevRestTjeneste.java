package no.nav.foreldrepenger.fpformidling.web.app.tjenester.brev;

import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.READ;

import java.util.List;
import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
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
import no.nav.foreldrepenger.fpformidling.brevproduksjon.bestiller.BrevBestillerTjeneste;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.brevmal.BrevmalTjeneste;
import no.nav.foreldrepenger.fpformidling.sikkerhet.pdp.AppAbacAttributtType;
import no.nav.foreldrepenger.fpformidling.sikkerhet.pdp.FPFormidlingBeskyttetRessursAttributt;
import no.nav.foreldrepenger.kontrakter.formidling.v1.BehandlingUuidDto;
import no.nav.foreldrepenger.kontrakter.formidling.v1.BrevmalDto;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.TilpassetAbacAttributt;

@Path("/brev")
@ApplicationScoped
public class BrevRestTjeneste {

    private BrevmalTjeneste brevmalTjeneste;
    private BrevBestillerTjeneste brevBestillerTjeneste;

    public BrevRestTjeneste() {
        //CDI
    }

    @Inject
    public BrevRestTjeneste(BrevmalTjeneste brevmalTjeneste,
                            BrevBestillerTjeneste brevBestillerApplikasjonTjeneste) {
        this.brevmalTjeneste = brevmalTjeneste;
        this.brevBestillerTjeneste = brevBestillerApplikasjonTjeneste;
    }

    @GET
    @Path("/maler")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Henter liste over tilgjengelige brevtyper", tags = "brev")
    @BeskyttetRessurs(action = READ, resource = FPFormidlingBeskyttetRessursAttributt.FAGSAK, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<BrevmalDto> hentMaler(@TilpassetAbacAttributt(supplierClass = BehandlingUuidAbacDataSupplier.class)
            @NotNull @QueryParam("uuid") @Parameter(description = "behandlingUUID") @Valid BehandlingUuidDto uuidDto) {
        return brevmalTjeneste.hentBrevmalerFor(uuidDto.getBehandlingUuid()); // NOSONAR
    }

    public static class BehandlingUuidAbacDataSupplier implements Function<Object, AbacDataAttributter> {

        @Override
        public AbacDataAttributter apply(Object obj) {
            var req = (BehandlingUuidDto) obj;
            return AbacDataAttributter.opprett().leggTil(AppAbacAttributtType.BEHANDLING_UUID, req.getBehandlingUuid());
        }
    }

    @POST
    @Path("/forhaandsvis")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Returnerer en pdf som er en forhåndsvisning av brevet", tags = "brev")
    @BeskyttetRessurs(action = READ, resource = FPFormidlingBeskyttetRessursAttributt.FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Response forhaandsvisDokument(
            @Parameter(description = "Inneholder kode til brevmal og data som skal flettes inn i brevet") @Valid AbacDokumentbestillingDto dokumentbestillingDto) { // NOSONAR
        byte[] dokument = brevBestillerTjeneste.forhandsvisBrev(dokumentbestillingDto);

        if (dokument != null && dokument.length != 0) {
            Response.ResponseBuilder responseBuilder = Response.ok(dokument);
            responseBuilder.type("application/pdf");
            responseBuilder.header("Content-Disposition", "filename=dokument.pdf");
            return responseBuilder.build();
        }
        return Response.serverError().build();
    }
}
