package no.nav.foreldrepenger.melding.integrasjon.kodeverk;

import no.nav.tjeneste.virksomhet.kodeverk.v2.HentKodeverkHentKodeverkKodeverkIkkeFunnet;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.FinnKodeverkListeRequest;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.FinnKodeverkListeResponse;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.HentKodeverkRequest;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.HentKodeverkResponse;
import no.nav.vedtak.exception.IntegrasjonException;
import no.nav.vedtak.konfig.KonfigVerdi;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.xml.namespace.QName;
import javax.xml.ws.soap.SOAPFaultException;

@ApplicationScoped
public class KodeverkConsumer {

    public static final String SERVICE_IDENTIFIER = "KodeverkV2";

    private static final String WSDL = "wsdl/no/nav/tjeneste/virksomhet/kodeverk/v2/Kodeverk.wsdl";
    private static final String NAMESPACE = "http://nav.no/tjeneste/virksomhet/kodeverk/v2/";
    private static final QName SERVICE = new QName(NAMESPACE, "Kodeverk_v2");
    private static final QName PORT = new QName(NAMESPACE, "Kodeverk_v2");

    private KodeverkPortType kodeverkPortType;

    KodeverkConsumer() {
        // CDI
    }

    @Inject
    public KodeverkConsumer(@KonfigVerdi("Kodeverk.v2.url") String endpointUrl) {
        this.kodeverkPortType = getPort(endpointUrl);
    }

    public FinnKodeverkListeResponse finnKodeverkListe(FinnKodeverkListeRequest finnKodeverkListeRequest) {
        try {
            return kodeverkPortType.finnKodeverkListe(finnKodeverkListeRequest);
        } catch (SOAPFaultException e) { // NOSONAR
            throw new IntegrasjonException("FP-942048", String.format("SOAP tjenesten [ %s ] returnerte en SOAP Fault: %s", SERVICE_IDENTIFIER), e);
        }
    }

    public HentKodeverkResponse hentKodeverk(HentKodeverkRequest hentKodeverkRequest) throws HentKodeverkHentKodeverkKodeverkIkkeFunnet {
        try {
            return kodeverkPortType.hentKodeverk(hentKodeverkRequest);
        } catch (SOAPFaultException e) { // NOSONAR
            throw new IntegrasjonException("FP-942048", String.format("SOAP tjenesten [ %s ] returnerte en SOAP Fault: %s", SERVICE_IDENTIFIER), e);
        }
    }

    private static KodeverkPortType getPort(String endpointUrl) {
        JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
        factoryBean.setWsdlURL(WSDL);
        factoryBean.setServiceName(SERVICE);
        factoryBean.setEndpointName(PORT);
        factoryBean.setServiceClass(KodeverkPortType.class);
        factoryBean.setAddress(endpointUrl);
        factoryBean.getFeatures().add(new WSAddressingFeature());
        return factoryBean.create(KodeverkPortType.class);
    }
}
