package no.nav.foreldrepenger.melding.web.app.tjenester.hendelse;

import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.READ;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt.FAGSAK;

import javax.enterprise.context.RequestScoped;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import no.nav.foreldrepenger.melding.web.app.tjenester.dto.HendelseDto;
import no.nav.vedtak.felles.jpa.Transaction;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;

@Api(tags = "hendelse")
@Path("/hendelse")
@RequestScoped
@Transaction
public class HendelseRestTjeneste {
    private static final Logger log = LoggerFactory.getLogger(HendelseRestTjeneste.class);

    public HendelseRestTjeneste() {
        //For Rest-CDI
    }

    @POST
    @Timed
    @Path("/prossesere-hendelse")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Proseserer hendelse og sender ")
    @BeskyttetRessurs(action = READ, ressurs = FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public void prossesereHendelse(@ApiParam("hendelseDto") @Valid HendelseDto hendelseDto) {
        log.info("hendelse = " + hendelseDto);
    }
}
