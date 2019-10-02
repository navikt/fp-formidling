package no.nav.foreldrepenger.melding.sikkerhet.pdp;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;

import no.nav.abac.common.xacml.CommonAttributter;
import no.nav.abac.foreldrepenger.xacml.ForeldrepengerAttributter;
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
        actionAttributeSet.addAttribute(CommonAttributter.XACML_1_0_ACTION_ACTION_ID, pdpRequest.getString(CommonAttributter.XACML_1_0_ACTION_ACTION_ID));
        xacmlBuilder.addActionAttributeSet(actionAttributeSet);

        List<Tuple<String, String>> identer = hentIdenter(pdpRequest,
                CommonAttributter.RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE);

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
        resourceAttributeSet.addAttribute(CommonAttributter.RESOURCE_FELLES_DOMENE, pdpRequest.getString(CommonAttributter.RESOURCE_FELLES_DOMENE));
        resourceAttributeSet.addAttribute(CommonAttributter.RESOURCE_FELLES_RESOURCE_TYPE, pdpRequest.getString(CommonAttributter.RESOURCE_FELLES_RESOURCE_TYPE));
        setOptionalValueinAttributeSet(resourceAttributeSet, pdpRequest, ForeldrepengerAttributter.RESOURCE_FORELDREPENGER_SAK_SAKSSTATUS);
        setOptionalValueinAttributeSet(resourceAttributeSet, pdpRequest, ForeldrepengerAttributter.RESOURCE_FORELDREPENGER_SAK_BEHANDLINGSSTATUS);
        if (ident != null) {
            resourceAttributeSet.addAttribute(ident.getElement1(), ident.getElement2());
        }
        return resourceAttributeSet;
    }

    private void setOptionalValueinAttributeSet(XacmlAttributeSet resourceAttributeSet, PdpRequest pdpRequest, String key) {
        pdpRequest.getOptional(key).ifPresent(s -> resourceAttributeSet.addAttribute(key, s));
    }

    private List<Tuple<String, String>> hentIdenter(PdpRequest pdpRequest, String... identNøkler) {
        List<Tuple<String, String>> identer = new ArrayList<>();
        for (String key : identNøkler) {
            identer.addAll(pdpRequest.getListOfString(key).stream().map(it -> new Tuple<>(key, it)).collect(Collectors.toList()));
        }
        return identer;
    }
}
