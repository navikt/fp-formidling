package no.nav.foreldrepenger.melding.sikkerhet.pdp;

import static no.nav.abac.xacml.NavAttributter.RESOURCE_ARKIV_GSAK_SAKSID;
import static no.nav.abac.xacml.NavAttributter.RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE;
import static no.nav.abac.xacml.NavAttributter.RESOURCE_FELLES_PERSON_FNR;
import static no.nav.abac.xacml.NavAttributter.RESOURCE_FORELDREPENGER_SAK_AKSJONSPUNKT_TYPE;

import java.util.List;
import java.util.Optional;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;

import no.nav.abac.foreldrepenger.xacml.ForeldrepengerAttributter;
import no.nav.abac.xacml.NavAttributter;
import no.nav.abac.xacml.StandardAttributter;
import no.nav.vedtak.sikkerhet.abac.PdpRequest;
import no.nav.vedtak.sikkerhet.pdp.XacmlRequestBuilderTjeneste;
import no.nav.vedtak.sikkerhet.pdp.xacml.XacmlAttributeSet;
import no.nav.vedtak.sikkerhet.pdp.xacml.XacmlRequestBuilder;

@Dependent
@Alternative
@Priority(2)
public class XamlRequestBuilderTjenesteImpl implements XacmlRequestBuilderTjeneste {

    public XamlRequestBuilderTjenesteImpl() {
    }

    @Override
    public XacmlRequestBuilder lagXacmlRequestBuilder(PdpRequest pdpRequest) {
        XacmlRequestBuilder xacmlBuilder = new XacmlRequestBuilder();

        XacmlAttributeSet actionAttributeSet = new XacmlAttributeSet();
        actionAttributeSet.addAttribute(StandardAttributter.ACTION_ID, pdpRequest.getString(StandardAttributter.ACTION_ID));
        xacmlBuilder.addActionAttributeSet(actionAttributeSet);

        int antall = antallResources(pdpRequest);
        for (int i = 0; i < antall; i++) {
            XacmlAttributeSet resourceAttributeSet = byggXacmlResourceAttrSet(pdpRequest, i);
            xacmlBuilder.addResourceAttributeSet(resourceAttributeSet);
        }

        return xacmlBuilder;
    }

    private int antallResources(PdpRequest pdpRequest) {
        return Math.max(1, antallIdenter(pdpRequest)) * Math.max(1, antallAksjonspunktTyper(pdpRequest));
    }

    private int antallIdenter(PdpRequest pdpRequest) {
        return pdpRequest.getAntall(RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE) + pdpRequest.getAntall(RESOURCE_FELLES_PERSON_FNR);
    }

    private int antallAksjonspunktTyper(PdpRequest pdpRequest) {
        return pdpRequest.getAntall(RESOURCE_FORELDREPENGER_SAK_AKSJONSPUNKT_TYPE);
    }

    private XacmlAttributeSet byggXacmlResourceAttrSet(PdpRequest pdpRequest, int index) {

        XacmlAttributeSet resourceAttributeSet = new XacmlAttributeSet();
        resourceAttributeSet.addAttribute(NavAttributter.RESOURCE_FELLES_DOMENE, pdpRequest.getString(NavAttributter.RESOURCE_FELLES_DOMENE));
        resourceAttributeSet.addAttribute(NavAttributter.RESOURCE_FELLES_RESOURCE_TYPE, pdpRequest.getString(NavAttributter.RESOURCE_FELLES_RESOURCE_TYPE));

        int antallFnrPåRequest = pdpRequest.getAntall(RESOURCE_FELLES_PERSON_FNR);
        if (index < antallFnrPåRequest) {
            setOptionalListValueinAttributeSet(resourceAttributeSet, pdpRequest, RESOURCE_FELLES_PERSON_FNR, index % antallFnrPåRequest);
        } else {
            int kalkulertIndex = (index - antallFnrPåRequest) % Math.max(pdpRequest.getAntall(RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE), 1);
            setOptionalListValueinAttributeSet(resourceAttributeSet, pdpRequest, RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE, kalkulertIndex);
        }
        setOptionalListValueinAttributeSet(resourceAttributeSet, pdpRequest, RESOURCE_FORELDREPENGER_SAK_AKSJONSPUNKT_TYPE, (index / Math.max(antallIdenter(pdpRequest), 1)));
        setOptionalValueinAttributeSet(resourceAttributeSet, pdpRequest, NavAttributter.RESOURCE_FORELDREPENGER_SAK_SAKSSTATUS);
        setOptionalValueinAttributeSet(resourceAttributeSet, pdpRequest, NavAttributter.RESOURCE_FORELDREPENGER_SAK_BEHANDLINGSSTATUS);
        setOptionalValueinAttributeSet(resourceAttributeSet, pdpRequest, NavAttributter.RESOURCE_FORELDREPENGER_SAK_ANSVARLIG_SAKSBEHANDLER);
        setOptionalListValueinAttributeSet(resourceAttributeSet, pdpRequest, RESOURCE_ARKIV_GSAK_SAKSID, index);
        int kalkulertOppgaveIndex = index % Math.max(pdpRequest.getAntall(ForeldrepengerAttributter.FORELDREPENGER_OPPGAVESTYRING_AVDELINGSENHET), 1);
        setOptionalListValueinAttributeSet(resourceAttributeSet, pdpRequest, ForeldrepengerAttributter.FORELDREPENGER_OPPGAVESTYRING_AVDELINGSENHET, kalkulertOppgaveIndex);

        return resourceAttributeSet;
    }

    private void setOptionalValueinAttributeSet(XacmlAttributeSet resourceAttributeSet, PdpRequest pdpRequest, String key) {
        pdpRequest.getOptional(key).ifPresent(s -> resourceAttributeSet.addAttribute(key, s));
    }

    private void setOptionalListValueinAttributeSet(XacmlAttributeSet resourceAttributeSet, PdpRequest pdpRequest, String key, int index) {
        List<String> list = pdpRequest.getListOfString(key);
        if (list.size() >= index + 1) {
            Optional.ofNullable(list.get(index)).ifPresent(s -> resourceAttributeSet.addAttribute(key, s));
        }
    }
}
