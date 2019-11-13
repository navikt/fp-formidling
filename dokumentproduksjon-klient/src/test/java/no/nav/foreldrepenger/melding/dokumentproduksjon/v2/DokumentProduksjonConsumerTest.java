package no.nav.foreldrepenger.melding.dokumentproduksjon.v2;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.soap.SOAPFaultException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.binding.DokumentproduksjonV2;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.meldinger.FerdigstillForsendelseRequest;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.meldinger.KnyttVedleggTilForsendelseRequest;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.meldinger.ProduserDokumentutkastRequest;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.meldinger.ProduserIkkeredigerbartDokumentRequest;
import no.nav.vedtak.exception.IntegrasjonException;

public class DokumentProduksjonConsumerTest {

    private DokumentproduksjonConsumer consumer;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private DokumentproduksjonV2 mockWebservice = mock(DokumentproduksjonV2.class);

    @Before
    public void setUp() {
        consumer = new DokumentproduksjonConsumerImpl(mockWebservice);
    }

    @Test
    public void skalKasteTekniskfeilNårWebserviceSenderSoapFault_ikkeRedigerbart() throws Exception {
        when(mockWebservice.produserIkkeredigerbartDokument(any(ProduserIkkeredigerbartDokumentRequest.class))).thenThrow(opprettSOAPFaultException("feil"));

        expectedException.expect(IntegrasjonException.class);
        expectedException.expectMessage("FP-942048");

        consumer.produserIkkeredigerbartDokument(mock(ProduserIkkeredigerbartDokumentRequest.class));
    }

    @Test
    public void skalKasteTekniskfeilNårWebserviceSenderSoapFault_dokumentUtkast() throws Exception {
        when(mockWebservice.produserDokumentutkast(any(ProduserDokumentutkastRequest.class))).thenThrow(opprettSOAPFaultException("feil"));

        expectedException.expect(IntegrasjonException.class);
        expectedException.expectMessage("FP-942048");

        consumer.produserDokumentutkast(mock(ProduserDokumentutkastRequest.class));
    }

    @Test
    public void skalKasteTekniskfeilVedSoapFault_ferdigstillForsendelse() throws Exception {
        doThrow(opprettSOAPFaultException("feil")).when(mockWebservice).ferdigstillForsendelse(any(FerdigstillForsendelseRequest.class));

        expectedException.expect(IntegrasjonException.class);
        expectedException.expectMessage("FP-942048");

        consumer.ferdigstillForsendelse(mock(FerdigstillForsendelseRequest.class));
    }

    @Test
    public void skalKasteTekniskfeilVedSoapFault_knyttVedleggTilForsendelse() throws Exception {
        doThrow(opprettSOAPFaultException("feil")).when(mockWebservice).knyttVedleggTilForsendelse(any(KnyttVedleggTilForsendelseRequest.class));

        expectedException.expect(IntegrasjonException.class);
        expectedException.expectMessage("FP-942048");

        consumer.knyttVedleggTilForsendelse(mock(KnyttVedleggTilForsendelseRequest.class));
    }

    private SOAPFaultException opprettSOAPFaultException(String faultString) throws SOAPException {
        SOAPFault fault = SOAPFactory.newInstance().createFault();
        fault.setFaultString(faultString);
        fault.setFaultCode(new QName("local"));
        return new SOAPFaultException(fault);
    }

}
