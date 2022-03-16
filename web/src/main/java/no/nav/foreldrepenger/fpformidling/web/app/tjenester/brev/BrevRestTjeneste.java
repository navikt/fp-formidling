package no.nav.foreldrepenger.fpformidling.web.app.tjenester.brev;

import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.CREATE;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.READ;

import java.util.List;
import java.util.function.Function;

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
import no.nav.foreldrepenger.fpformidling.brevproduksjon.bestiller.BrevBestillerTjeneste;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.bestiller.DokumentHendelseMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.task.BrevTaskProperties;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.task.ProduserBrevTask;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.brevmal.BrevmalTjeneste;
import no.nav.foreldrepenger.fpformidling.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.web.app.tjenester.DokumentHendelseTjeneste;
import no.nav.foreldrepenger.fpformidling.sikkerhet.pdp.FPFormidlingBeskyttetRessursAttributt;
import no.nav.foreldrepenger.kontrakter.formidling.v1.BehandlingUuidDto;
import no.nav.foreldrepenger.kontrakter.formidling.v1.BrevmalDto;
import no.nav.foreldrepenger.kontrakter.formidling.v1.DokumentbestillingDto;
import no.nav.foreldrepenger.kontrakter.formidling.v1.DokumentbestillingV2Dto;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskTjeneste;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.StandardAbacAttributtType;
import no.nav.vedtak.sikkerhet.abac.TilpassetAbacAttributt;

@Path("/brev")
@Transactional
@ApplicationScoped
public class BrevRestTjeneste {

    private BrevmalTjeneste brevmalTjeneste;
    private BrevBestillerTjeneste brevBestillerTjeneste;
    private DokumentHendelseTjeneste dokumentHendelseTjeneste;
    private ProsessTaskTjeneste taskTjeneste;

    public BrevRestTjeneste() {
        //CDI
    }

    @Inject
    public BrevRestTjeneste(BrevmalTjeneste brevmalTjeneste,
                            BrevBestillerTjeneste brevBestillerApplikasjonTjeneste,
                            DokumentHendelseTjeneste dokumentHendelseTjeneste,
                            ProsessTaskTjeneste taskTjeneste) {
        this.brevmalTjeneste = brevmalTjeneste;
        this.brevBestillerTjeneste = brevBestillerApplikasjonTjeneste;
        this.dokumentHendelseTjeneste = dokumentHendelseTjeneste;
        this.taskTjeneste = taskTjeneste;
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

    @POST
    @Path("/forhaandsvis")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Returnerer en pdf som er en forhåndsvisning av brevet", tags = "brev")
    @BeskyttetRessurs(action = READ, resource = FPFormidlingBeskyttetRessursAttributt.FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Response forhaandsvisDokument(
            @Parameter(description = "Inneholder kode til brevmal og bestillingsdetaljer.")
            @TilpassetAbacAttributt(supplierClass = ForhåndsvisSupplier.class)
            @Valid DokumentbestillingDto dokumentbestillingDto) { // NOSONAR

        var dokumentHendelse = DokumentHendelseMapper.mapFra(dokumentbestillingDto);


        byte[] dokument = brevBestillerTjeneste.forhandsvisBrev(dokumentHendelse);

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
    @Operation(description = "Bestiller, produserer og journalfører brevet", tags = "brev")
    @BeskyttetRessurs(action = CREATE, resource = FPFormidlingBeskyttetRessursAttributt.FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Response bestillDokument(
            @Parameter(description = "Inneholder kode til brevmal og bestillingsdetaljer.")
            @TilpassetAbacAttributt(supplierClass = BestillingSupplier.class)
            @Valid DokumentbestillingV2Dto dokumentbestillingDto) { // NOSONAR

        var hendelse = DokumentHendelseDtoMapper.mapDokumentHendelseFraDto(dokumentbestillingDto);
        dokumentHendelseTjeneste.validerUnikOgLagre(hendelse).ifPresent(this::opprettBestillBrevTask);

        return Response.ok().build();
    }

    private void opprettBestillBrevTask(DokumentHendelse dokumentHendelse) {
        ProsessTaskData prosessTaskData = ProsessTaskData.forProsessTask(ProduserBrevTask.class);
        prosessTaskData.setProperty(BrevTaskProperties.HENDELSE_ID, String.valueOf(dokumentHendelse.getId()));
        prosessTaskData.setProperty(BrevTaskProperties.BEHANDLING_UUID, String.valueOf(dokumentHendelse.getBehandlingUuid()));
        taskTjeneste.lagre(prosessTaskData);
    }

    public static class BehandlingUuidAbacDataSupplier implements Function<Object, AbacDataAttributter> {
        @Override
        public AbacDataAttributter apply(Object obj) {
            var req = (BehandlingUuidDto) obj;
            return AbacDataAttributter.opprett().leggTil(StandardAbacAttributtType.BEHANDLING_UUID, req.behandlingUuid());
        }
    }

    public static class BestillingSupplier implements Function<Object, AbacDataAttributter> {
        @Override
        public AbacDataAttributter apply(Object obj) {
            var req = (DokumentbestillingV2Dto) obj;
            return AbacDataAttributter.opprett().leggTil(StandardAbacAttributtType.BEHANDLING_UUID, req.behandlingUuid());
        }
    }

    public static class ForhåndsvisSupplier implements Function<Object, AbacDataAttributter> {
        @Override
        public AbacDataAttributter apply(Object obj) {
            var req = (DokumentbestillingDto) obj;
            return AbacDataAttributter.opprett().leggTil(StandardAbacAttributtType.BEHANDLING_UUID, req.getBehandlingUuid());
        }
    }
}
