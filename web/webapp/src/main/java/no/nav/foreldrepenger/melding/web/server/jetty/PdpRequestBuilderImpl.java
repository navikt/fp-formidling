package no.nav.foreldrepenger.melding.web.server.jetty;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

import no.nav.abac.xacml.NavAttributter;
import no.nav.abac.xacml.StandardAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacAttributtSamling;
import no.nav.vedtak.sikkerhet.abac.PdpKlient;
import no.nav.vedtak.sikkerhet.abac.PdpRequest;
import no.nav.vedtak.sikkerhet.abac.PdpRequestBuilder;
import no.nav.vedtak.sikkerhet.abac.StandardAbacAttributtType;

/**
 * Implementasjon av PDP request for denne applikasjonen.
 */
@ApplicationScoped
@Alternative
@Priority(2)
// HACK - FORDI DENNE KOLLIDERER MED DUMMYREQUESTBUILDER FRA FELLES-SIKKERHET-TESTUTILITIES NÅR VI KJØRER JETTY
public class PdpRequestBuilderImpl implements PdpRequestBuilder {

    public static final String ABAC_DOMAIN = "foreldrepenger";

    @Override
    public PdpRequest lagPdpRequest(AbacAttributtSamling attributter) {
        PdpRequest pdpRequest = new PdpRequest();
        pdpRequest.put(PdpKlient.ENVIRONMENT_AUTH_TOKEN, attributter.getIdToken());
        pdpRequest.put(StandardAttributter.ACTION_ID, attributter.getActionType());
        pdpRequest.put(NavAttributter.RESOURCE_FELLES_DOMENE, ABAC_DOMAIN);
        pdpRequest.put(NavAttributter.RESOURCE_FELLES_RESOURCE_TYPE, attributter.getResource());
        pdpRequest.put(NavAttributter.RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE, attributter.getVerdier(StandardAbacAttributtType.AKTØR_ID));
        pdpRequest.put(NavAttributter.RESOURCE_FELLES_PERSON_FNR, attributter.getVerdier(StandardAbacAttributtType.FNR));
        return pdpRequest;
    }
}
