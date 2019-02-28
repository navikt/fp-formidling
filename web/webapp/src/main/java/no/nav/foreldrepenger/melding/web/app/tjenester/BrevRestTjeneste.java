package no.nav.foreldrepenger.melding.web.app.tjenester;

import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.READ;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt.FAGSAK;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.codahale.metrics.annotation.Timed;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import no.nav.foreldrepenger.melding.web.app.tjenester.dto.DokumentHendelseDto;
import no.nav.vedtak.felles.jpa.Transaction;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;

@Api(tags = "brev")
@Path("/brev")
@ApplicationScoped
@Transaction
public class BrevRestTjeneste {

    public BrevRestTjeneste() {
        // For Rest-CDI
    }

    @POST
    @Timed
    @Path("/forhandsvis")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Returnerer en pdf som er en forhåndsvisning av brevet")
    @BeskyttetRessurs(action = READ, ressurs = FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Response hentForhåndsvisningDokument(
            @ApiParam("Inneholder type av hendelse og data som skal flettes inn i brevet") @Valid DokumentHendelseDto dokumentHendelseDto) { // NOSONAR

        byte[] dokument = new byte[0];
        Response.ResponseBuilder responseBuilder = Response.ok(dokument);
        responseBuilder.type("application/pdf");
        responseBuilder.header("Content-Disposition", "filename=dokument.pdf");
        return responseBuilder.build();
    }
}
