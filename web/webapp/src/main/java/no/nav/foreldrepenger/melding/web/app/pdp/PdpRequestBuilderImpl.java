package no.nav.foreldrepenger.melding.web.app.pdp;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import no.nav.abac.xacml.NavAttributter;
import no.nav.abac.xacml.StandardAttributter;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.vedtak.sikkerhet.abac.AbacAttributtSamling;
import no.nav.vedtak.sikkerhet.abac.PdpKlient;
import no.nav.vedtak.sikkerhet.abac.PdpRequest;
import no.nav.vedtak.sikkerhet.abac.PdpRequestBuilder;
import no.nav.vedtak.sikkerhet.abac.StandardAbacAttributtType;

/**
 * Implementasjon av PDP request for denne applikasjonen.
 */
@Dependent
@Alternative
@Priority(2)
public class PdpRequestBuilderImpl implements PdpRequestBuilder {

    public static final String ABAC_DOMAIN = "foreldrepenger";

    private DomeneobjektProvider domeneobjektProvider;

    @Inject
    public PdpRequestBuilderImpl(DomeneobjektProvider domeneobjektProvider) {
        this.domeneobjektProvider = domeneobjektProvider;
    }

    @Override
    public PdpRequest lagPdpRequest(AbacAttributtSamling attributter) {
        PdpRequest pdpRequest = new PdpRequest();
        List<String> aktørIder = new ArrayList<>();
        attributter.getVerdier(StandardAbacAttributtType.BEHANDLING_UUID)
                .forEach(uuid -> {
                    aktørIder.add(domeneobjektProvider.hentBehandling(UUID.fromString(uuid)).getFagsak().getPersoninfo().getAktørId().getId());
                });
        pdpRequest.put(PdpKlient.ENVIRONMENT_AUTH_TOKEN, attributter.getIdToken());
        pdpRequest.put(StandardAttributter.ACTION_ID, attributter.getActionType().getEksternKode());
        pdpRequest.put(NavAttributter.RESOURCE_FELLES_DOMENE, ABAC_DOMAIN);
        pdpRequest.put(NavAttributter.RESOURCE_FELLES_RESOURCE_TYPE, attributter.getResource().getEksternKode());
        pdpRequest.put(NavAttributter.RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE, aktørIder);
        return pdpRequest;
    }
}
