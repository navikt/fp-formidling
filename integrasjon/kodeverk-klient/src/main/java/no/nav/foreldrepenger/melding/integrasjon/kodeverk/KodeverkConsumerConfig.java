package no.nav.foreldrepenger.melding.integrasjon.kodeverk;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;

import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import no.nav.vedtak.konfig.KonfigVerdi;

@Dependent
public class KodeverkConsumerConfig {
    private static final String WSDL = "wsdl/no/nav/tjeneste/virksomhet/kodeverk/v2/Kodeverk.wsdl";
    private static final String NAMESPACE = "http://nav.no/tjeneste/virksomhet/kodeverk/v2/";
    private static final QName SERVICE = new QName(NAMESPACE, "Kodeverk_v2");
    private static final QName PORT = new QName(NAMESPACE, "Kodeverk_v2");

    private final String endpointUrl;

    @Inject
    public KodeverkConsumerConfig(@KonfigVerdi("Kodeverk.v2.url") String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    KodeverkPortType getPort() {
        Map<String, Object> properties = new HashMap<>();

        JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
        factoryBean.setWsdlURL(WSDL);
        factoryBean.setProperties(properties);
        factoryBean.setServiceName(SERVICE);
        factoryBean.setEndpointName(PORT);
        factoryBean.setServiceClass(KodeverkPortType.class);
        factoryBean.setAddress(endpointUrl);
        factoryBean.getFeatures().add(new WSAddressingFeature());
        return factoryBean.create(KodeverkPortType.class);
    }

    public String getEndpointUrl() {
        return endpointUrl;
    }
}
