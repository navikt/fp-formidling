package no.nav.foreldrepenger.fpformidling.tjenester;

import java.util.Optional;
import java.util.function.Function;

import no.nav.foreldrepenger.kontrakter.formidling.v3.DokumentBestillingHtmlDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.bestiller.BrevBestillerTjeneste;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.task.BrevTaskProperties;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.task.ProduserBrevTask;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelseTjeneste;
import no.nav.foreldrepenger.kontrakter.formidling.v3.DokumentBestillingDto;
import no.nav.foreldrepenger.kontrakter.formidling.v3.DokumentForhåndsvisDto;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskTjeneste;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.StandardAbacAttributtType;
import no.nav.vedtak.sikkerhet.abac.TilpassetAbacAttributt;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ActionType;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ResourceType;

@Path("/brev")
@Transactional
@ApplicationScoped
public class BrevRestTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(BrevRestTjeneste.class);

    private BrevBestillerTjeneste brevBestillerTjeneste;
    private DokumentHendelseTjeneste dokumentHendelseTjeneste;
    private ProsessTaskTjeneste taskTjeneste;

    public BrevRestTjeneste() {
        // CDI
    }

    @Inject
    public BrevRestTjeneste(BrevBestillerTjeneste brevBestillerApplikasjonTjeneste,
                            DokumentHendelseTjeneste dokumentHendelseTjeneste,
                            ProsessTaskTjeneste taskTjeneste) {
        this.brevBestillerTjeneste = brevBestillerApplikasjonTjeneste;
        this.dokumentHendelseTjeneste = dokumentHendelseTjeneste;
        this.taskTjeneste = taskTjeneste;
    }

    @POST
    @Path("/forhaandsvis/v3")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Returnerer en pdf som er en forhåndsvisning av brevet", tags = "brev")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.FAGSAK, sporingslogg = true)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Response forhåndsvisDokument(@Parameter(description = "Inneholder kode til brevmal og bestillingsdetaljer.") @TilpassetAbacAttributt(supplierClass = ForhåndsvisV3Supplier.class) @Valid DokumentForhåndsvisDto dokumentbestillingDto) {
        var dokumentHendelse = DokumentHendelseDtoMapper.mapFra(dokumentbestillingDto);

        LOG.info("Forhåndsvis V3 hendelse: {}", dokumentHendelse);
        var dokument = brevBestillerTjeneste.forhandsvisBrev(dokumentHendelse);
        if (dokument != null && dokument.length != 0) {
            return Response.ok(dokument).build();
        }
        return Response.serverError().build();
    }

    @POST
    @Path("/generer/html/v3")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Returnerer en html som er en forhåndsvisning av brevet", tags = "brev")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.FAGSAK, sporingslogg = true)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Response genererBrevHtml(@Parameter(description = "Inneholder kode til brevmal og bestillingsdetaljer.") @TilpassetAbacAttributt(supplierClass = DokumentBestillingHtmlDtoSupplier.class) @Valid DokumentBestillingHtmlDto dokumentbestillingDto) {
        var dokumentHendelse = DokumentHendelseDtoMapper.mapFra(dokumentbestillingDto);

        LOG.info("Genererer brev html for behandling {} med mal {}", dokumentHendelse.getBehandlingUuid(), dokumentHendelse.getDokumentMal());
        var dokument = brevBestillerTjeneste.genererBrevHtml(dokumentHendelse);
        if (dokument != null && !dokument.isEmpty()) {
            return Response.ok(dokument)
                    .type(MediaType.TEXT_HTML)
                    .build();
        }
        return Response.serverError().build();
    }

    @POST
    @Path("/bestill/v3")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Bestiller, produserer og journalfører brevet", tags = "brev")
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.FAGSAK, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Response bestillDokument(@Parameter(description = "Inneholder kode til brevmal og bestillingsdetaljer.") @TilpassetAbacAttributt(supplierClass = BestillingV3Supplier.class) @Valid DokumentBestillingDto dokumentbestillingDto) { // NOSONAR
        var hendelse = DokumentHendelseDtoMapper.mapFra(dokumentbestillingDto);
        LOG.info("Bestill V3 hendelse: {}", hendelse);
        dokumentHendelseTjeneste.validerUnikOgLagre(hendelse).ifPresent(h -> opprettBestillBrevTask(h, dokumentbestillingDto.saksnummer().saksnummer()));
        return Response.ok().build();
    }

    private void opprettBestillBrevTask(DokumentHendelse dokumentHendelse, String saksnummer) {
        var prosessTaskData = ProsessTaskData.forProsessTask(ProduserBrevTask.class);
        prosessTaskData.setProperty(BrevTaskProperties.HENDELSE_ID, String.valueOf(dokumentHendelse.getId()));
        prosessTaskData.setBehandlingUUid(dokumentHendelse.getBehandlingUuid());
        Optional.ofNullable(saksnummer).ifPresent(prosessTaskData::setSaksnummer);
        taskTjeneste.lagre(prosessTaskData);
    }

    public static class ForhåndsvisV3Supplier implements Function<Object, AbacDataAttributter> {
        @Override
        public AbacDataAttributter apply(Object obj) {
            var req = (DokumentForhåndsvisDto) obj;
            return AbacDataAttributter.opprett()
                .leggTil(StandardAbacAttributtType.BEHANDLING_UUID, req.behandlingUuid())
                .leggTil(StandardAbacAttributtType.SAKSNUMMER, req.saksnummer().saksnummer());
        }
    }

    public static class DokumentBestillingHtmlDtoSupplier implements Function<Object, AbacDataAttributter> {
        @Override
        public AbacDataAttributter apply(Object obj) {
            var req = (DokumentBestillingHtmlDto) obj;
            return AbacDataAttributter.opprett()
                .leggTil(StandardAbacAttributtType.BEHANDLING_UUID, req.behandlingUuid())
                .leggTil(StandardAbacAttributtType.SAKSNUMMER, req.saksnummer().saksnummer());
        }
    }

    public static class BestillingV3Supplier implements Function<Object, AbacDataAttributter> {
        @Override
        public AbacDataAttributter apply(Object obj) {
            var req = (DokumentBestillingDto) obj;
            return AbacDataAttributter.opprett()
                .leggTil(StandardAbacAttributtType.BEHANDLING_UUID, req.behandlingUuid())
                .leggTil(StandardAbacAttributtType.SAKSNUMMER, req.saksnummer().saksnummer());
        }
    }
}
