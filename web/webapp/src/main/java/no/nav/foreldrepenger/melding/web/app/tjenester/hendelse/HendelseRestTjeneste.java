package no.nav.foreldrepenger.melding.web.app.tjenester.hendelse;

import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.READ;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt.FAGSAK;

import java.util.Optional;

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
import no.nav.foreldrepenger.fpsak.BehandlingRestKlient;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingIdDto;
import no.nav.foreldrepenger.melding.web.app.tjenester.dto.HendelseDto;
import no.nav.vedtak.felles.jpa.Transaction;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;

@Api(tags = "hendelse")
@Path("/hendelse")
@ApplicationScoped
@Transaction
public class HendelseRestTjeneste {
    private static final Logger LOGGER = LoggerFactory.getLogger(HendelseRestTjeneste.class);

    private BehandlingRestKlient behandlingRestKlient;

    public HendelseRestTjeneste() {
        //For Rest-CDI
    }

    @Inject
    public HendelseRestTjeneste(BehandlingRestKlient behandlingRestKlient) {
        this.behandlingRestKlient = behandlingRestKlient;
    }

    @POST
    @Timed
    @Path("/prossesere-hendelse")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Proseserer hendelse og sender ")
    @BeskyttetRessurs(action = READ, ressurs = FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Response prossesereHendelse(@NotNull @ApiParam("hendelseDto") @Valid HendelseDto hendelseDto) {
        LOGGER.info("hendelse = " + hendelseDto);
        final Optional<BehandlingDto> behandlingInfo = behandlingRestKlient.hentBehandling(new BehandlingIdDto(hendelseDto.getBehandlingId()));
        Response.ResponseBuilder responseBuilder;
        if (behandlingInfo.isPresent()) {
            responseBuilder = Response.ok().entity(behandlingInfo.get());
        } else {
            responseBuilder = Response.noContent();
        }
        return responseBuilder.build();
    }
}
