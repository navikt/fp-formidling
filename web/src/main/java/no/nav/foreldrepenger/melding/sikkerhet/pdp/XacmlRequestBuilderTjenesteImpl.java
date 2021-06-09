package no.nav.foreldrepenger.melding.sikkerhet.pdp;

import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.RESOURCE_FELLES_DOMENE;
import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE;
import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.RESOURCE_FELLES_RESOURCE_TYPE;
import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.XACML10_ACTION_ACTION_ID;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;

import no.nav.vedtak.sikkerhet.abac.PdpRequest;
import no.nav.vedtak.sikkerhet.pdp.XacmlRequestBuilderTjeneste;
import no.nav.vedtak.sikkerhet.pdp.xacml.XacmlAttributeSet;
import no.nav.vedtak.sikkerhet.pdp.xacml.XacmlRequestBuilder;

@Dependent
public class XacmlRequestBuilderTjenesteImpl implements XacmlRequestBuilderTjeneste {

    public XacmlRequestBuilderTjenesteImpl() {
    }

    @Override
    public XacmlRequestBuilder lagXacmlRequestBuilder(PdpRequest pdpRequest) {
        XacmlRequestBuilder xacmlBuilder = new XacmlRequestBuilder();

        XacmlAttributeSet actionAttributeSet = new XacmlAttributeSet();
        actionAttributeSet.addAttribute(XACML10_ACTION_ACTION_ID, pdpRequest.getString(XACML10_ACTION_ACTION_ID));
        xacmlBuilder.addActionAttributeSet(actionAttributeSet);

        List<IdentKey> identer = hentIdenter(pdpRequest, RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE);

        if (identer.isEmpty()) {
            populerResources(xacmlBuilder, pdpRequest, null);
        } else {
            for (var ident : identer) {
                populerResources(xacmlBuilder, pdpRequest, ident);
            }
        }

        return xacmlBuilder;
    }

    private void populerResources(XacmlRequestBuilder xacmlBuilder, PdpRequest pdpRequest, IdentKey ident) {
        xacmlBuilder.addResourceAttributeSet(byggRessursAttributter(pdpRequest, ident));
    }

    private XacmlAttributeSet byggRessursAttributter(PdpRequest pdpRequest, IdentKey ident) {
        XacmlAttributeSet resourceAttributeSet = new XacmlAttributeSet();
        resourceAttributeSet.addAttribute(RESOURCE_FELLES_DOMENE, pdpRequest.getString(RESOURCE_FELLES_DOMENE));
        resourceAttributeSet.addAttribute(RESOURCE_FELLES_RESOURCE_TYPE, pdpRequest.getString(RESOURCE_FELLES_RESOURCE_TYPE));
        setOptionalValueinAttributeSet(resourceAttributeSet, pdpRequest, AppAbacAttributtType.RESOURCE_FORELDREPENGER_SAK_SAKSSTATUS);
        setOptionalValueinAttributeSet(resourceAttributeSet, pdpRequest, AppAbacAttributtType.RESOURCE_FORELDREPENGER_SAK_BEHANDLINGSSTATUS);
        if (ident != null) {
            resourceAttributeSet.addAttribute(ident.key(), ident.ident());
        }
        return resourceAttributeSet;
    }

    private void setOptionalValueinAttributeSet(XacmlAttributeSet resourceAttributeSet, PdpRequest pdpRequest, String key) {
        pdpRequest.getOptional(key).ifPresent(s -> resourceAttributeSet.addAttribute(key, s));
    }

    private List<IdentKey> hentIdenter(PdpRequest pdpRequest, String... identNøkler) {
        List<IdentKey> identer = new ArrayList<>();
        for (String key : identNøkler) {
            identer.addAll(pdpRequest.getListOfString(key).stream().map(it -> new IdentKey(key, it)).collect(Collectors.toList()));
        }
        return identer;
    }

    private static record IdentKey(String key, String ident) {}
}
