package no.nav.foreldrepenger.melding.dokumentproduksjon.v2;

import no.nav.vedtak.exception.IntegrasjonException;
import no.nav.vedtak.exception.TekniskException;

import javax.security.auth.login.LoginException;
import javax.xml.ws.WebServiceException;

public class SoapWebServiceFeil {

    public static String DOKPROD_FEIL_INNHOLD = "F-942048";

    private SoapWebServiceFeil() {

    }

    static IntegrasjonException soapFaultIwebserviceKall(String webservice, WebServiceException e) {
        return new IntegrasjonException(DOKPROD_FEIL_INNHOLD, String.format("SOAP tjenesten [ %s ] returnerte en SOAP Fault:", webservice), e);
    }

    static TekniskException feiletUtlogging(LoginException e) {
        return new TekniskException("F-668217", "Feilet utlogging.", e);
    }

    static IntegrasjonException midlertidigFeil(String webservice, WebServiceException e) {
        return new IntegrasjonException("F-134134", String.format("SOAP tjenesten [ %s ] returnerte en feil som trolig er midlertidig", webservice),
                e);
    }
}
