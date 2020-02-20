package no.nav.foreldrepenger.melding.web.app.tjenester.brev;

import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.READ;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.UPDATE;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt.APPLIKASJON;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt.FAGSAK;

import java.util.Collections;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingIdDto;
import no.nav.foreldrepenger.kontrakter.formidling.v1.BrevmalDto;
import no.nav.foreldrepenger.kontrakter.formidling.v1.HentBrevmalerDto;
import no.nav.foreldrepenger.melding.brevbestiller.api.BrevBestillerApplikasjonTjeneste;
import no.nav.foreldrepenger.melding.brevbestiller.api.DokumentBehandlingTjeneste;
import no.nav.foreldrepenger.melding.brevbestiller.dto.DokumentbestillingDtoMapper;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.hendelse.HendelseHandler;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;

@Path("/brev")
@ApplicationScoped
@Transactional
public class BrevRestTjeneste {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrevRestTjeneste.class);

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

    @GET
    @Path("/maler")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Henter liste over tilgjengelige brevtyper", tags = "brev")
    @BeskyttetRessurs(action = READ, ressurs = FAGSAK, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<BrevmalDto> hentMaler(@NotNull @QueryParam(AbacBehandlingUuidDto.NAME) @Parameter(description = AbacBehandlingUuidDto.DESC) @Valid AbacBehandlingUuidDto uuidDto) {
        return dokumentBehandlingTjeneste.hentBrevmalerFor(uuidDto.getBehandlingUuid()); // NOSONAR
    }

    @POST
    @Path("/maler")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Henter liste over tilgjengelige brevtyper", tags = "brev")
    @BeskyttetRessurs(action = READ, ressurs = FAGSAK, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public HentBrevmalerDto hentMalerPost(@Valid AbacBehandlingUuidDto dto) {
        LOGGER.info("Utvikler-feil: Gammel POST-tjeneste for hent maler ble kalt. Skal ikke skje etter TFP-1404 på fpsak-frontend (Jan Erik følger opp)");
        return new HentBrevmalerDto(dokumentBehandlingTjeneste.hentBrevmalerFor(dto.getBehandlingUuid())); // NOSONAR
    }

    @POST
    @Path("/maler-dummy")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Henter tom liste over maler", tags = "brev")
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    @BeskyttetRessurs(action = READ, ressurs = APPLIKASJON, sporingslogg = false)
    public HentBrevmalerDto hentMalerDummy(@Valid AbacBehandlingUuidDummyDto dto) {
        LOGGER.info("Utvikler-feil: Dummy-tjeneste for hent maler ble kalt. Skal ikke skje etter TFP-1404 på fpsak-frontend (Jan Erik følger opp)");
        return new HentBrevmalerDto(Collections.emptyList());
    }

    @POST
    @Path("/varsel/revurdering")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Sjekk har varsel sendt om revurdering", tags = "brev")
    @BeskyttetRessurs(action = READ, ressurs = FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Boolean harSendtVarselOmRevurdering(@Valid BehandlingIdDto dto) {
        LOGGER.info("Utvikler-feil: Gammel tjeneste for har har sendt varsel om revurdering ble kalt. Skal ikke skje etter TFP-1404 på fpsak (Jan Erik følger opp)");
        return dokumentBehandlingTjeneste.erDokumentProdusert(dto.getBehandlingUuid(), DokumentMalType.REVURDERING_DOK); // NOSONAR
    }


    @POST
    @Path("/dokument-sendt")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Sjekker om dokument for mal er sendt", tags = "brev")
    @BeskyttetRessurs(action = READ, ressurs = FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Boolean harProdusertDokument(@Valid AbacDokumentProdusertDto dto) {
        LOGGER.info("Utvikler-feil: Gammel tjeneste for har produsert dokument ble kalt. Skal ikke skje etter TFP-1404 på fpsak (Jan Erik følger opp)");
        return dokumentBehandlingTjeneste.erDokumentProdusert(dto.getBehandlingUuid(), dto.getDokumentMal()); // NOSONAR
    }

    @POST
    @Path("/dokument-sendt-dummy")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Returnerer alltid false", tags = "brev")
    @BeskyttetRessurs(action = READ, ressurs = APPLIKASJON, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Boolean harProdusertDokumentDummy(@Valid AbacDokumentProdusertDummyDto dto) {
        LOGGER.info("Utvikler-feil: Dummy-tjeneste for har produsert dokument ble kalt. Skal ikke skje etter TFP-1404 på fpsak (Jan Erik følger opp)");
        return false; // NOSONAR
    }

    @POST
    @Path("/forhaandsvis")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Returnerer en pdf som er en forhåndsvisning av brevet", tags = "brev")
    @BeskyttetRessurs(action = READ, ressurs = FAGSAK)
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

    @POST
    @Path("/bestill")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Bestiller generering og sending av brevet", tags = "brev")
    @BeskyttetRessurs(action = UPDATE, ressurs = FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public void bestillDokument(
            @Parameter(description = "Inneholder kode til brevmal og data som skal flettes inn i brevet") @Valid AbacDokumentbestillingDto dokumentbestillingDto) { // NOSONAR
        LOGGER.info("Utvikler-feil: Gammel tjeneste for manuell bestilling av dokumenter ble kalt. Skal ikke skje etter TFP-1404 på fpsak (Jan Erik følger opp)");
        DokumentHendelse hendelse = dokumentbestillingDtoMapper.mapDokumentbestillingFraDtoForEndepunkt(dokumentbestillingDto);
        hendelseHandler.prosesser(hendelse);
    }
}
