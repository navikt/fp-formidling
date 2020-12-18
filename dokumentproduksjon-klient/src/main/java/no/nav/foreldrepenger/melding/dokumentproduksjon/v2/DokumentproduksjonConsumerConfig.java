package no.nav.foreldrepenger.melding.dokumentproduksjon.v2;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;

import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.binding.DokumentproduksjonV2;
import no.nav.vedtak.felles.integrasjon.felles.ws.CallIdOutInterceptor;
import no.nav.vedtak.konfig.KonfigVerdi;

@Dependent
@SuppressWarnings("java:S1874") //Dokprod-koden blir etterhvert fjernet helt
public class DokumentproduksjonConsumerConfig {
    private static final String WSDL = "wsdl/no/nav/tjeneste/virksomhet/dokumentproduksjon/v2/Binding.wsdl";
    private static final String NAMESPACE = "http://nav.no/tjeneste/virksomhet/dokumentproduksjon/v2/Binding";
    private static final QName SERVICE = new QName(NAMESPACE, "Dokumentproduksjon_v2");
    private static final QName PORT = new QName(NAMESPACE, "Dokumentproduksjon_v2Port");

    @Inject
    @KonfigVerdi("Dokumentproduksjon_v2.url")
    private String endpointUrl;  // NOSONAR

    DokumentproduksjonV2 getPort() {
        JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
        factoryBean.setWsdlURL(WSDL);
        factoryBean.setServiceName(SERVICE);
        factoryBean.setEndpointName(PORT);
        factoryBean.setServiceClass(DokumentproduksjonV2.class);
        factoryBean.setAddress(endpointUrl);
        factoryBean.getFeatures().add(new WSAddressingFeature());
        factoryBean.getFeatures().add(new LoggingFeature());
        factoryBean.getOutInterceptors().add(new CallIdOutInterceptor());
        return factoryBean.create(DokumentproduksjonV2.class);
    }

    public String getEndpointUrl() {
        return endpointUrl;
    }
}
