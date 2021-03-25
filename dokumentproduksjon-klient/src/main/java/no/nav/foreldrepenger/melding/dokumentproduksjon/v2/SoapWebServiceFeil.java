package no.nav.foreldrepenger.melding.dokumentproduksjon.v2;

import no.nav.vedtak.exception.IntegrasjonException;

import javax.xml.ws.WebServiceException;

public class SoapWebServiceFeil {
    private SoapWebServiceFeil() {

    }

    static IntegrasjonException soapFaultIwebserviceKall(String webservice, WebServiceException e) {
        return new IntegrasjonException("F-942048", String.format("SOAP tjenesten [ %s ] returnerte en SOAP Fault:", webservice), e);
    }
}
