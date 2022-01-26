package no.nav.foreldrepenger.fpformidling.web.app.pdp;

import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.RESOURCE_FELLES_DOMENE;
import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE;
import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.RESOURCE_FELLES_RESOURCE_TYPE;
import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.XACML10_ACTION_ACTION_ID;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.sikkerhet.pdp.AppAbacAttributtType;
import no.nav.foreldrepenger.fpformidling.typer.AktørId;
import no.nav.foreldrepenger.fpformidling.web.app.pdp.dto.PipDto;
import no.nav.vedtak.sikkerhet.abac.AbacAttributtSamling;
import no.nav.vedtak.sikkerhet.abac.PdpKlient;
import no.nav.vedtak.sikkerhet.abac.PdpRequest;
import no.nav.vedtak.sikkerhet.abac.PdpRequestBuilder;
import no.nav.vedtak.sikkerhet.abac.StandardAbacAttributtType;


/**
 * Implementasjon av PDP request for denne applikasjonen.
 */
@Dependent
public class AppPdpRequestBuilderImpl implements PdpRequestBuilder {

    public static final String ABAC_DOMAIN = "foreldrepenger";

    private final DomeneobjektProvider domeneobjektProvider;
    private final PipRestKlient pipRestKlient;

    @Inject
    public AppPdpRequestBuilderImpl(DomeneobjektProvider domeneobjektProvider,
                                 PipRestKlient pipRestKlient) {
        this.domeneobjektProvider = domeneobjektProvider;
        this.pipRestKlient = pipRestKlient;
    }

    @Override
    public PdpRequest lagPdpRequest(AbacAttributtSamling attributter) {
        PdpRequest pdpRequest = new PdpRequest();
        List<String> aktørIder = new ArrayList<>();
        Set<UUID> uuids = attributter.getVerdier(StandardAbacAttributtType.BEHANDLING_UUID);
        Optional<Behandling> behandling = uuids.stream().findFirst().map(domeneobjektProvider::hentBehandling);

        behandling.ifPresent(b -> {
                    PipDto dto = pipRestKlient.hentPipdataForBehandling(b.getUuid().toString());
                    aktørIder.addAll(dto.getAktørIder().stream().map(AktørId::getId).toList());
                    pdpRequest.put(AppAbacAttributtType.RESOURCE_FORELDREPENGER_SAK_SAKSSTATUS, dto.getFagsakStatus());
                    pdpRequest.put(AppAbacAttributtType.RESOURCE_FORELDREPENGER_SAK_BEHANDLINGSSTATUS, dto.getBehandlingStatus());
                }
        );

        pdpRequest.put(PdpKlient.ENVIRONMENT_AUTH_TOKEN, attributter.getIdToken());
        pdpRequest.put(XACML10_ACTION_ACTION_ID, attributter.getActionType().getEksternKode());
        pdpRequest.put(RESOURCE_FELLES_DOMENE, ABAC_DOMAIN);
        pdpRequest.put(RESOURCE_FELLES_RESOURCE_TYPE, attributter.getResource());
        pdpRequest.put(RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE, aktørIder);
        return pdpRequest;
    }

}

