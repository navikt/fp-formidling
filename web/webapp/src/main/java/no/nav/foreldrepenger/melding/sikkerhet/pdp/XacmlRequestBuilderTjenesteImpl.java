package no.nav.foreldrepenger.melding.sikkerhet.pdp;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;

import no.nav.abac.xacml.NavAttributter;
import no.nav.abac.xacml.StandardAttributter;
import no.nav.vedtak.sikkerhet.abac.PdpRequest;
import no.nav.vedtak.sikkerhet.pdp.XacmlRequestBuilderTjeneste;
import no.nav.vedtak.sikkerhet.pdp.xacml.XacmlAttributeSet;
import no.nav.vedtak.sikkerhet.pdp.xacml.XacmlRequestBuilder;
import no.nav.vedtak.util.Tuple;

@Dependent
@Alternative
@Priority(2)
public class XacmlRequestBuilderTjenesteImpl implements XacmlRequestBuilderTjeneste {

    public XacmlRequestBuilderTjenesteImpl() {
    }

    @Override
    public XacmlRequestBuilder lagXacmlRequestBuilder(PdpRequest pdpRequest) {
        XacmlRequestBuilder xacmlBuilder = new XacmlRequestBuilder();

        XacmlAttributeSet actionAttributeSet = new XacmlAttributeSet();
        actionAttributeSet.addAttribute(StandardAttributter.ACTION_ID, pdpRequest.getString(StandardAttributter.ACTION_ID));
        xacmlBuilder.addActionAttributeSet(actionAttributeSet);

        List<Tuple<String, String>> identer = hentIdenter(pdpRequest,
                NavAttributter.RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE,
                NavAttributter.RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE);

        if (identer.isEmpty()) {
            populerResources(xacmlBuilder, pdpRequest, null);
        } else {
            for (Tuple<String, String> ident : identer) {
                populerResources(xacmlBuilder, pdpRequest, ident);
            }
        }

        return xacmlBuilder;
    }

    private void populerResources(XacmlRequestBuilder xacmlBuilder, PdpRequest pdpRequest, Tuple<String, String> ident) {
        xacmlBuilder.addResourceAttributeSet(byggRessursAttributter(pdpRequest, ident));
    }

    private XacmlAttributeSet byggRessursAttributter(PdpRequest pdpRequest, Tuple<String, String> ident) {
        XacmlAttributeSet resourceAttributeSet = new XacmlAttributeSet();
        resourceAttributeSet.addAttribute(NavAttributter.RESOURCE_FELLES_DOMENE, pdpRequest.getString(NavAttributter.RESOURCE_FELLES_DOMENE));
        resourceAttributeSet.addAttribute(NavAttributter.RESOURCE_FELLES_RESOURCE_TYPE, pdpRequest.getString(NavAttributter.RESOURCE_FELLES_RESOURCE_TYPE));
        if (ident != null) {
            resourceAttributeSet.addAttribute(ident.getElement1(), ident.getElement2());
        }

        return resourceAttributeSet;
    }

    private List<Tuple<String, String>> hentIdenter(PdpRequest pdpRequest, String... identNøkler) {
        List<Tuple<String, String>> identer = new ArrayList<>();
        for (String key : identNøkler) {
            identer.addAll(pdpRequest.getListOfString(key).stream().map(it -> new Tuple<>(key, it)).collect(Collectors.toList()));
        }
        return identer;
    }
}
