package no.nav.foreldrepenger.melding.web.app.tjenester.brev;

import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.READ;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.UPDATE;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt.APPLIKASJON;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt.FAGSAK;

import java.util.Collections;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.codahale.metrics.annotation.Timed;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingIdDto;
import no.nav.foreldrepenger.kontrakter.formidling.v1.DokumentProdusertDto;
import no.nav.foreldrepenger.kontrakter.formidling.v1.ForhaandsvisDokumentDto;
import no.nav.foreldrepenger.kontrakter.formidling.v1.HentBrevmalerDto;
import no.nav.foreldrepenger.melding.brevbestiller.api.BrevBestillerApplikasjonTjeneste;
import no.nav.foreldrepenger.melding.brevbestiller.api.DokumentBehandlingTjeneste;
import no.nav.foreldrepenger.melding.brevbestiller.dto.DokumentbestillingDtoMapper;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.hendelse.HendelseHandler;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.vedtak.felles.jpa.Transaction;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;

@Api(tags = "brev")
@Path("/brev")
@ApplicationScoped
@Transaction
public class BrevRestTjeneste {
    private DokumentBehandlingTjeneste dokumentBehandlingTjeneste;
    private BrevBestillerApplikasjonTjeneste brevBestillerApplikasjonTjeneste;
    private HendelseHandler hendelseHandler;
    private DokumentbestillingDtoMapper dokumentbestillingDtoMapper;

    public BrevRestTjeneste() {
        //CDI
    }

    @Inject
    public BrevRestTjeneste(DokumentBehandlingTjeneste dokumentBehandlingTjeneste,
                            BrevBestillerApplikasjonTjeneste brevBestillerApplikasjonTjeneste,
                            HendelseHandler hendelseHandler,
                            DokumentbestillingDtoMapper dokumentbestillingDtoMapper) {
        this.dokumentBehandlingTjeneste = dokumentBehandlingTjeneste;
        this.brevBestillerApplikasjonTjeneste = brevBestillerApplikasjonTjeneste;
        this.hendelseHandler = hendelseHandler;
        this.dokumentbestillingDtoMapper = dokumentbestillingDtoMapper;
    }

    @POST
    @Timed
    @Path("/maler")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @ApiOperation(value = "Henter liste over tilgjengelige brevtyper")
    @BeskyttetRessurs(action = READ, ressurs = FAGSAK, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public HentBrevmalerDto hentMaler(@Valid AbacBehandlingUuidDto dto) {
        return new HentBrevmalerDto(dokumentBehandlingTjeneste.hentBrevmalerFor(dto.getBehandlingUuid())); // NOSONAR
    }


    @POST
    @Timed
    @Path("/maler-dummy")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @ApiOperation(value = "Henter tom liste")
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    @BeskyttetRessurs(action = READ, ressurs = APPLIKASJON, sporingslogg = false)
    public HentBrevmalerDto hentMalerDummy(@Valid AbacBehandlingUuidDummyDto dto) {
        return new HentBrevmalerDto(Collections.emptyList());
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
    @Path("/dokument-sendt")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @ApiOperation(value = "Sjekker om dokument for mal er sendt")
    @BeskyttetRessurs(action = READ, ressurs = FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Boolean harProdusertDokument(@Valid DokumentProdusertDto dto) {
        return dokumentBehandlingTjeneste.erDokumentProdusert(dto.getBehandlingUuid(), dto.getDokumentMal()); // NOSONAR
    }

    @POST
    @Timed
    @Path("/forhandsvis")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Returnerer en pdf som er en forhåndsvisning av brevet")
    @BeskyttetRessurs(action = READ, ressurs = FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public ForhaandsvisDokumentDto forhaandsvisDokument(
            @ApiParam("Inneholder kode til brevmal og data som skal flettes inn i brevet") @Valid AbacDokumentbestillingDto dokumentbestillingDto) { // NOSONAR
        Response.ResponseBuilder responseBuilder;
        byte[] dokument = brevBestillerApplikasjonTjeneste.forhandsvisBrev(dokumentbestillingDto);
        //TODO: Dette trenges når fpsak-frontend kaller fpformidling direkt, ikke via fpsak
/*
        if (dokument != null && dokument.length != 0) {
            responseBuilder = Response.ok().entity(java.util.Base64.getEncoder().encode(dokument));
            responseBuilder.type("application/pdf");
            responseBuilder.header("Content-Disposition", "filename=dokument.pdf");
            return responseBuilder.build();
        }
        responseBuilder = Response.serverError();
        return responseBuilder.build();
*/
        return new ForhaandsvisDokumentDto(dokument);
    }

    @POST
    @Timed
    @Path("/bestill")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Bestiller generering og sending av brevet")
    @BeskyttetRessurs(action = UPDATE, ressurs = FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public void bestillDokument(
            @ApiParam("Inneholder kode til brevmal og data som skal flettes inn i brevet") @Valid AbacDokumentbestillingDto dokumentbestillingDto) { // NOSONAR
        DokumentHendelse hendelse = dokumentbestillingDtoMapper.mapDokumentbestillingFraDtoForEndepunkt(dokumentbestillingDto);
        hendelseHandler.prosesser(hendelse);
    }
}
