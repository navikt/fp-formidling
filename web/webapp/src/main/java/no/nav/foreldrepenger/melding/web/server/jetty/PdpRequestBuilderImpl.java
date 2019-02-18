package no.nav.foreldrepenger.melding.web.server.jetty;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

import no.nav.vedtak.sikkerhet.abac.AbacAttributtSamling;
import no.nav.vedtak.sikkerhet.abac.PdpRequest;
import no.nav.vedtak.sikkerhet.abac.PdpRequestBuilder;

/**
 * Implementasjon av PDP request for denne applikasjonen.
 */
@ApplicationScoped
@Alternative
@Priority(2)
// HACK - FORDI DENNE KOLLIDERER MED DUMMYREQUESTBUILDER FRA FELLES-SIKKERHET-TESTUTILITIES NÅR VI KJØRER JETTY
public class PdpRequestBuilderImpl implements PdpRequestBuilder {

    @Override
    public PdpRequest lagPdpRequest(AbacAttributtSamling attributter) {
        PdpRequest pdpRequest = new PdpRequest();
        pdpRequest.setToken(attributter.getIdToken());
        pdpRequest.setAction(attributter.getActionType());
        pdpRequest.setResource(attributter.getResource());
        return pdpRequest;
    }
}
