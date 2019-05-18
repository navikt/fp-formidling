package no.nav.foreldrepenger.melding.web.app.tjenester.brev;

import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.READ;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.UPDATE;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt.FAGSAK;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
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
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingIdDto;
import no.nav.foreldrepenger.melding.brevbestiller.api.BrevBestillerApplikasjonTjeneste;
import no.nav.foreldrepenger.melding.brevbestiller.api.DokumentBehandlingTjeneste;
import no.nav.foreldrepenger.melding.brevbestiller.dto.BestillBrevDto;
import no.nav.foreldrepenger.melding.brevbestiller.dto.BestillBrevDtoMapper;
import no.nav.foreldrepenger.melding.brevbestiller.dto.BrevmalDto;
import no.nav.foreldrepenger.melding.brevbestiller.task.ProduserBrevTaskProperties;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.hendelser.HendelseRepository;
import no.nav.vedtak.felles.jpa.Transaction;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskRepository;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;

@Api(tags = "brev")
@Path("/brev")
@ApplicationScoped
@Transaction
public class BrevRestTjeneste {
    private static final Logger LOG = LoggerFactory.getLogger(BrevRestTjeneste.class);
    private DokumentBehandlingTjeneste dokumentBehandlingTjeneste;
    private BrevBestillerApplikasjonTjeneste brevBestillerApplikasjonTjeneste;
    private ProsessTaskRepository prosessTaskRepository;
    private HendelseRepository hendelseRepository;
    private BestillBrevDtoMapper bestillBrevDtoMapper;

    public BrevRestTjeneste() {
        //CDI
    }

    @Inject
    public BrevRestTjeneste(DokumentBehandlingTjeneste dokumentBehandlingTjeneste,
                            BrevBestillerApplikasjonTjeneste brevBestillerApplikasjonTjeneste,
                            ProsessTaskRepository prosessTaskRepository,
                            HendelseRepository hendelseRepository,
                            BestillBrevDtoMapper bestillBrevDtoMapper) {
        this.dokumentBehandlingTjeneste = dokumentBehandlingTjeneste;
        this.brevBestillerApplikasjonTjeneste = brevBestillerApplikasjonTjeneste;
        this.prosessTaskRepository = prosessTaskRepository;
        this.hendelseRepository = hendelseRepository;
        this.bestillBrevDtoMapper = bestillBrevDtoMapper;
    }

    @POST
    @Timed
    @Path("/maler")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @ApiOperation(value = "Henter liste over tilgjengelige brevtyper")
    @BeskyttetRessurs(action = READ, ressurs = FAGSAK, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<BrevmalDto> hentMaler(@Valid BehandlingIdDto dto) {
        return dokumentBehandlingTjeneste.hentBrevmalerFor(dto.getBehandlingUuid()); // NOSONAR
    }

    @POST
    @Timed
    @Path("/varsel/revurdering")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @ApiOperation(value = "Sjekk har varsel sendt om revurdering")
    @BeskyttetRessurs(action = READ, ressurs = FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Boolean harSendtVarselOmRevurdering(@Valid BehandlingIdDto dto) {
        return dokumentBehandlingTjeneste.erDokumentProdusert(dto.getBehandlingUuid(), DokumentMalType.REVURDERING_DOK); // NOSONAR
    }

    @POST
    @Timed
    @Path("/forhandsvis")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Returnerer en pdf som er en forhåndsvisning av brevet")
    @BeskyttetRessurs(action = READ, ressurs = FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Response forhaandsvisDokument(
            @ApiParam("Inneholder kode til brevmal og data som skal flettes inn i brevet") @Valid BestillBrevDto bestillBrevDto) { // NOSONAR
        Response.ResponseBuilder responseBuilder;
        byte[] dokument = brevBestillerApplikasjonTjeneste.forhandsvisBrev(bestillBrevDto);
        if (dokument != null && dokument.length != 0) {
            responseBuilder = Response.ok().entity(java.util.Base64.getEncoder().encode(dokument));
            responseBuilder.type("application/pdf");
            responseBuilder.header("Content-Disposition", "filename=dokument.pdf");
            return responseBuilder.build();
        }
        responseBuilder = Response.serverError();
        return responseBuilder.build();
    }

    @POST
    @Timed
    @Path("/bestill")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Bestiller generering og sending av brevet")
    @BeskyttetRessurs(action = UPDATE, ressurs = FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public void bestillDokument(
            @ApiParam("Inneholder kode til brevmal og data som skal flettes inn i brevet") @Valid BestillBrevDto bestillBrevDto) { // NOSONAR
        DokumentHendelse hendelse = bestillBrevDtoMapper.mapDokumentbestillingFraDtoForEndepunkt(bestillBrevDto);
        hendelseRepository.lagre(hendelse);
        opprettBestillBrevTask(hendelse);
        LOG.info("lagret hendelse:{} for behandling: {} OK", hendelse.getId(), hendelse.getBehandlingUuid());
    }

    private void opprettBestillBrevTask(DokumentHendelse dokumentHendelse) {
        ProsessTaskData prosessTaskData = new ProsessTaskData(ProduserBrevTaskProperties.TASKTYPE);
        prosessTaskData.setProperty(ProduserBrevTaskProperties.HENDELSE_ID, String.valueOf(dokumentHendelse.getId()));
        prosessTaskData.setGruppe("FORMIDLING");
        prosessTaskRepository.lagre(prosessTaskData);
    }
}
