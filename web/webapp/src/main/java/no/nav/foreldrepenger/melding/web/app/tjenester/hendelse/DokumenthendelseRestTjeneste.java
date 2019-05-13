package no.nav.foreldrepenger.melding.web.app.tjenester.hendelse;

import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.READ;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt.FAGSAK;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import no.nav.foreldrepenger.melding.brevbestiller.api.BrevBestillerApplikasjonTjeneste;
import no.nav.foreldrepenger.melding.brevbestiller.dto.BestillBrevDto;
import no.nav.vedtak.felles.jpa.Transaction;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;

@Api(tags = "dokumenthendelse")
@Path("/dokumenthendelse")
@ApplicationScoped
@Transaction
public class DokumenthendelseRestTjeneste {
    private static final Logger LOGGER = LoggerFactory.getLogger(DokumenthendelseRestTjeneste.class);
    private BrevBestillerApplikasjonTjeneste brevBestillerApplikasjonTjeneste;

    public DokumenthendelseRestTjeneste() {
        //For Rest-CDI
    }

    @Inject
    public DokumenthendelseRestTjeneste(BrevBestillerApplikasjonTjeneste brevBestillerApplikasjonTjeneste) {
        this.brevBestillerApplikasjonTjeneste = brevBestillerApplikasjonTjeneste;
    }

    @POST
    @Timed
    @Path("/forhandsvis-dokument")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Proseserer hendelse og sender ")
    @BeskyttetRessurs(action = READ, ressurs = FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Response prossesereHendelse(@NotNull @ApiParam("DokumentHendelseDto") @Valid BestillBrevDto dokumentHendelseDto) {
        LOGGER.info("hendelse = " + dokumentHendelseDto);
        Response.ResponseBuilder responseBuilder;
        final byte[] brevPdfVersjon = brevBestillerApplikasjonTjeneste.forhandsvisBrev(dokumentHendelseDto);
        if (brevPdfVersjon != null && brevPdfVersjon.length != 0) {
            LOGGER.info("Forhåndsvist brev=" + brevPdfVersjon);
            responseBuilder = Response.ok().entity(java.util.Base64.getEncoder().encode(brevPdfVersjon));
            responseBuilder.type("application/pdf");
            responseBuilder.header("Content-Disposition", "filename=dokument.pdf");
            LOGGER.info("bytearray: " + brevPdfVersjon);
            return responseBuilder.build();
        }
        responseBuilder = Response.serverError();
        return responseBuilder.build();
    }
}
