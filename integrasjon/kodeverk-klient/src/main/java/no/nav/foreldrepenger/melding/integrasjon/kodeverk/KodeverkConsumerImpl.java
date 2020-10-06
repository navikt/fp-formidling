package no.nav.foreldrepenger.melding.integrasjon.kodeverk;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import no.nav.tjeneste.virksomhet.kodeverk.v2.HentKodeverkHentKodeverkKodeverkIkkeFunnet;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.FinnKodeverkListeRequest;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.FinnKodeverkListeResponse;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.HentKodeverkRequest;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.HentKodeverkResponse;
import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.feil.FeilFactory;
import no.nav.vedtak.feil.LogLevel;
import no.nav.vedtak.feil.deklarasjon.DeklarerteFeil;
import no.nav.vedtak.feil.deklarasjon.IntegrasjonFeil;


public class KodeverkConsumerImpl implements KodeverkConsumer {
    public static final String SERVICE_IDENTIFIER = "KodeverkV2";

    private KodeverkPortType port;

    public KodeverkConsumerImpl(KodeverkPortType port) {
        this.port = port;
    }

    @Override
    public FinnKodeverkListeResponse finnKodeverkListe(FinnKodeverkListeRequest finnKodeverkListeRequest) {
        try {
            return port.finnKodeverkListe(finnKodeverkListeRequest);
        } catch (SOAPFaultException e) { // NOSONAR
            throw KodeverkWebServiceFeil.FACTORY.soapFaultIwebserviceKall(SERVICE_IDENTIFIER, e).toException();
        }
    }

    @Override
    public HentKodeverkResponse hentKodeverk(HentKodeverkRequest hentKodeverkRequest) throws HentKodeverkHentKodeverkKodeverkIkkeFunnet {
        try {
            return port.hentKodeverk(hentKodeverkRequest);
        } catch (SOAPFaultException e) { // NOSONAR
            throw KodeverkWebServiceFeil.FACTORY.soapFaultIwebserviceKall(SERVICE_IDENTIFIER, e).toException();
        }
    }

    interface KodeverkWebServiceFeil extends DeklarerteFeil {
        KodeverkWebServiceFeil FACTORY = FeilFactory.create(KodeverkWebServiceFeil.class);

        @IntegrasjonFeil(feilkode = "FP-942048", feilmelding = "SOAP tjenesten [ %s ] returnerte en SOAP Fault: %s", logLevel = LogLevel.WARN)
        Feil soapFaultIwebserviceKall(String webservice, WebServiceException soapException);
    }
}
