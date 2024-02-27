package no.nav.foreldrepenger.fpformidling.brevproduksjon.bestiller;

import java.util.function.Function;

import no.nav.foreldrepenger.fpformidling.domene.RevurderingVarselÅrsak;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.fpformidling.tjenester.DokumentHendelseDtoMapper;

import no.nav.foreldrepenger.kontrakter.formidling.kodeverk.DokumentMal;

import no.nav.foreldrepenger.kontrakter.formidling.kodeverk.RevurderingÅrsak;

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
import no.nav.foreldrepenger.fpformidling.brevproduksjon.task.BrevTaskProperties;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.task.ProduserBrevTask;
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
public class DokumentRestTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(DokumentRestTjeneste.class);

    private DokumentBestillerTjeneste dokumentBestillerTjeneste;
    private DokumentHendelseTjeneste dokumentHendelseTjeneste;
    private ProsessTaskTjeneste taskTjeneste;

    public DokumentRestTjeneste() {
        // CDI
    }

    @Inject
    public DokumentRestTjeneste(DokumentBestillerTjeneste brevBestillerApplikasjonTjeneste,
                                DokumentHendelseTjeneste dokumentHendelseTjeneste,
                                ProsessTaskTjeneste taskTjeneste) {
        this.dokumentBestillerTjeneste = brevBestillerApplikasjonTjeneste;
        this.dokumentHendelseTjeneste = dokumentHendelseTjeneste;
        this.taskTjeneste = taskTjeneste;
    }

    @POST
    @Path("/forhaandsvis/v3")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Returnerer en pdf som er en forhåndsvisning av brevet", tags = "brev")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Response forhåndsvisDokument(@Parameter(description = "Inneholder kode til brevmal og bestillingsdetaljer.") @TilpassetAbacAttributt(supplierClass = ForhåndsvisV3Supplier.class) @Valid DokumentForhåndsvisDto dokumentbestillingDto) {
        var dokumentHendelse = DokumentHendelseDtoMapper.mapFra(dokumentbestillingDto);

        LOG.info("Forhåndsvis V3 hendelse: {}", dokumentHendelse);
        var dokument = dokumentBestillerTjeneste.forhandsvisBrev(dokumentHendelse);
        if (dokument != null && dokument.length != 0) {
            return Response.ok(dokument).build();
        }
        return Response.serverError().build();
    }

    @POST
    @Path("/bestill/v3")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Bestiller, produserer og journalfører brevet", tags = "brev")
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Response bestillDokument(@Parameter(description = "Inneholder kode til brevmal og bestillingsdetaljer.") @TilpassetAbacAttributt(supplierClass = BestillingV3Supplier.class) @Valid DokumentBestillingDto dokumentbestillingDto) { // NOSONAR
        //var hendelse = DokumentHendelseDtoMapper.mapFra(dokumentbestillingDto);
        var hendelse = mapTilBestilling(dokumentbestillingDto);
        LOG.info("Bestill V3 hendelse: {}", hendelse);
        dokumentHendelseTjeneste.validerUnikOgLagre(hendelse).ifPresent(this::opprettBestillBrevTask);
        return Response.ok().build();
    }

    private DokumentHendelseTjeneste.DokumentBestilling mapTilBestilling(DokumentBestillingDto dto) {
        return new DokumentHendelseTjeneste.DokumentBestilling(dto.behandlingUuid(), dto.dokumentbestillingUuid(), mapMal(dto.dokumentMal()),
            mapMal(dto.journalførSom()), mapÅrsak(dto.revurderingÅrsak()), dto.fritekst(), null);
    }

    private RevurderingVarselÅrsak mapÅrsak(RevurderingÅrsak revurderingÅrsak) {
        return switch (revurderingÅrsak) {
            case ANNET -> RevurderingVarselÅrsak.ANNET;
            case BRUKER_REGISTRERT_UTVANDRET -> RevurderingVarselÅrsak.BRUKER_REGISTRERT_UTVANDRET;
            case BARN_IKKE_REGISTRERT_FOLKEREGISTER -> RevurderingVarselÅrsak.BARN_IKKE_REGISTRERT_FOLKEREGISTER;
            case ARBEIDS_I_STØNADSPERIODEN -> RevurderingVarselÅrsak.ARBEIDS_I_STØNADSPERIODEN;
            case ARBEID_I_UTLANDET -> RevurderingVarselÅrsak.ARBEID_I_UTLANDET;
            case OPPTJENING_IKKE_OPPFYLT -> RevurderingVarselÅrsak.OPPTJENING_IKKE_OPPFYLT;
            case IKKE_LOVLIG_OPPHOLD -> RevurderingVarselÅrsak.IKKE_LOVLIG_OPPHOLD;
            case BEREGNINGSGRUNNLAG_UNDER_HALV_G -> RevurderingVarselÅrsak.BEREGNINGSGRUNNLAG_UNDER_HALV_G;
            case MOR_AKTIVITET_IKKE_OPPFYLT -> RevurderingVarselÅrsak.MOR_AKTIVITET_IKKE_OPPFYLT;
            case null -> null;
        };
    }

    private DokumentMalType mapMal(DokumentMal dokumentMal) {
        return switch (dokumentMal) {
            case SVANGERSKAPSPENGER_INNVILGELSE -> DokumentMalType.SVANGERSKAPSPENGER_INNVILGELSE;
            case SVANGERSKAPSPENGER_OPPHØR -> DokumentMalType.SVANGERSKAPSPENGER_OPPHØR;
            case SVANGERSKAPSPENGER_AVSLAG -> DokumentMalType.SVANGERSKAPSPENGER_AVSLAG;
            case FORELDREPENGER_ANNULLERT -> DokumentMalType.FORELDREPENGER_ANNULLERT;
            case FORELDREPENGER_INNVILGELSE -> DokumentMalType.FORELDREPENGER_INNVILGELSE;
            case FORELDREPENGER_AVSLAG -> DokumentMalType.FORELDREPENGER_AVSLAG;
            case FORELDREPENGER_OPPHØR -> DokumentMalType.FORELDREPENGER_OPPHØR;
            case ENGANGSSTØNAD_INNVILGELSE -> DokumentMalType.ENGANGSSTØNAD_INNVILGELSE;
            case ENGANGSSTØNAD_AVSLAG -> DokumentMalType.ENGANGSSTØNAD_AVSLAG;
            case ENDRING_UTBETALING -> DokumentMalType.ENDRING_UTBETALING;
            case ETTERLYS_INNTEKTSMELDING -> DokumentMalType.ETTERLYS_INNTEKTSMELDING;
            case INNHENTE_OPPLYSNINGER -> DokumentMalType.INNHENTE_OPPLYSNINGER;
            case VARSEL_OM_REVURDERING -> DokumentMalType.VARSEL_OM_REVURDERING;
            case INFO_OM_HENLEGGELSE -> DokumentMalType.INFO_OM_HENLEGGELSE;
            case INGEN_ENDRING -> DokumentMalType.INGEN_ENDRING;
            case INNSYN_SVAR -> DokumentMalType.INNSYN_SVAR;
            case IKKE_SØKT -> DokumentMalType.IKKE_SØKT;
            case FORELDREPENGER_INFO_TIL_ANNEN_FORELDER -> DokumentMalType.FORELDREPENGER_INFOBREV_TIL_ANNEN_FORELDER;
            case FRITEKSTBREV -> DokumentMalType.FRITEKSTBREV;
            case KLAGE_OVERSENDT -> DokumentMalType.KLAGE_OVERSENDT;
            case KLAGE_OMGJORT -> DokumentMalType.KLAGE_OMGJORT;
            case KLAGE_AVVIST -> DokumentMalType.KLAGE_AVVIST;
            case FORLENGET_SAKSBEHANDLINGSTID_TIDLIG -> DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID_TIDLIG;
            case FORLENGET_SAKSBEHANDLINGSTID_MEDL -> DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID_MEDL;
            case FORLENGET_SAKSBEHANDLINGSTID -> DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID;
            case null -> null;
        };
    }

    private void opprettBestillBrevTask(DokumentHendelseEntitet dokumentHendelseEntitet) {
        var prosessTaskData = ProsessTaskData.forProsessTask(ProduserBrevTask.class);
        prosessTaskData.setProperty(BrevTaskProperties.HENDELSE_ID, String.valueOf(dokumentHendelseEntitet.getId()));
        prosessTaskData.setProperty(BrevTaskProperties.BEHANDLING_UUID, String.valueOf(dokumentHendelseEntitet.getBehandlingUuid()));
        taskTjeneste.lagre(prosessTaskData);
    }

    public static class ForhåndsvisV3Supplier implements Function<Object, AbacDataAttributter> {
        @Override
        public AbacDataAttributter apply(Object obj) {
            var req = (DokumentForhåndsvisDto) obj;
            return AbacDataAttributter.opprett().leggTil(StandardAbacAttributtType.BEHANDLING_UUID, req.behandlingUuid());
        }
    }

    public static class BestillingV3Supplier implements Function<Object, AbacDataAttributter> {
        @Override
        public AbacDataAttributter apply(Object obj) {
            var req = (DokumentBestillingDto) obj;
            return AbacDataAttributter.opprett().leggTil(StandardAbacAttributtType.BEHANDLING_UUID, req.behandlingUuid());
        }
    }
}
