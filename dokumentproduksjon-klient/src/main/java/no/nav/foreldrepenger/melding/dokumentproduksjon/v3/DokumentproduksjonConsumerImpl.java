package no.nav.foreldrepenger.melding.dokumentproduksjon.v3;

import javax.xml.ws.WebServiceException;

import no.nav.tjeneste.virksomhet.dokumentproduksjon.v3.DokumentproduksjonV3;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v3.FerdigstillForsendelseDokumentUnderRedigering;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v3.FerdigstillForsendelseInputValideringFeilet;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v3.FerdigstillForsendelseJournalpostIkkeFunnet;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v3.FerdigstillForsendelseJournalpostIkkeUnderArbeid;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v3.FerdigstillForsendelseSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v3.KnyttVedleggTilForsendelseDokumentIkkeFunnet;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v3.KnyttVedleggTilForsendelseDokumentTillatesIkkeGjenbrukt;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v3.KnyttVedleggTilForsendelseEksterntVedleggIkkeTillatt;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v3.KnyttVedleggTilForsendelseInputValideringFeilet;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v3.KnyttVedleggTilForsendelseJournalpostIkkeFerdigstilt;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v3.KnyttVedleggTilForsendelseJournalpostIkkeFunnet;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v3.KnyttVedleggTilForsendelseJournalpostIkkeUnderArbeid;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v3.KnyttVedleggTilForsendelseSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v3.KnyttVedleggTilForsendelseUlikeFagomraader;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v3.ProduserDokumentutkastBrevdataValideringFeilet;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v3.ProduserDokumentutkastInputValideringFeilet;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v3.ProduserIkkeRedigerbartDokumentInputValideringFeilet;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v3.ProduserIkkeRedigerbartDokumentJoarkForretningsmessigUnntak;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v3.ProduserIkkeredigerbartDokumentBrevdataValideringFeilet;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v3.ProduserIkkeredigerbartDokumentDokumentErRedigerbart;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v3.ProduserIkkeredigerbartDokumentDokumentErVedlegg;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v3.ProduserIkkeredigerbartDokumentSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v3.meldinger.FerdigstillForsendelseRequest;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v3.meldinger.KnyttVedleggTilForsendelseRequest;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v3.meldinger.ProduserDokumentutkastRequest;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v3.meldinger.ProduserDokumentutkastResponse;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v3.meldinger.ProduserIkkeredigerbartDokumentRequest;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v3.meldinger.ProduserIkkeredigerbartDokumentResponse;
import no.nav.vedtak.felles.integrasjon.felles.ws.SoapWebServiceFeil;

public class DokumentproduksjonConsumerImpl implements DokumentproduksjonConsumer {
    private static final String SERVICE_IDENTIFIER = "DokumentproduksjonV3";
    private DokumentproduksjonV3 port;

    public DokumentproduksjonConsumerImpl(DokumentproduksjonV3 port) {
        this.port = port;
    }

    @Override
    public ProduserIkkeredigerbartDokumentResponse produserIkkeredigerbartDokument(ProduserIkkeredigerbartDokumentRequest request)
        throws ProduserIkkeRedigerbartDokumentInputValideringFeilet, ProduserIkkeredigerbartDokumentDokumentErRedigerbart,
        ProduserIkkeredigerbartDokumentSikkerhetsbegrensning, ProduserIkkeRedigerbartDokumentJoarkForretningsmessigUnntak,
        ProduserIkkeredigerbartDokumentBrevdataValideringFeilet, ProduserIkkeredigerbartDokumentDokumentErVedlegg {
        try {
            return port.produserIkkeredigerbartDokument(request);
        } catch (WebServiceException e) { //NOSONAR
            throw SoapWebServiceFeil.FACTORY.soapFaultIwebserviceKall(SERVICE_IDENTIFIER, e).toException();
        }
    }

    @Override
    public ProduserDokumentutkastResponse produserDokumentutkast(ProduserDokumentutkastRequest request)
        throws ProduserDokumentutkastInputValideringFeilet, ProduserDokumentutkastBrevdataValideringFeilet {
        try {
            return port.produserDokumentutkast(request);
        } catch (WebServiceException e) { //NOSONAR
            throw SoapWebServiceFeil.FACTORY.soapFaultIwebserviceKall(SERVICE_IDENTIFIER, e).toException();
        }
    }

    @Override
    public void ferdigstillForsendelse(FerdigstillForsendelseRequest request)
        throws FerdigstillForsendelseDokumentUnderRedigering, FerdigstillForsendelseJournalpostIkkeFunnet,
        FerdigstillForsendelseJournalpostIkkeUnderArbeid, FerdigstillForsendelseInputValideringFeilet,
        FerdigstillForsendelseSikkerhetsbegrensning {
        try {
            port.ferdigstillForsendelse(request);
        } catch (WebServiceException e) { //NOSONAR
            throw SoapWebServiceFeil.FACTORY.soapFaultIwebserviceKall(SERVICE_IDENTIFIER, e).toException();
        }
    }

    @Override
    public void knyttVedleggTilForsendelse(KnyttVedleggTilForsendelseRequest request)
        throws KnyttVedleggTilForsendelseJournalpostIkkeFerdigstilt, KnyttVedleggTilForsendelseUlikeFagomraader,
        KnyttVedleggTilForsendelseJournalpostIkkeFunnet, KnyttVedleggTilForsendelseJournalpostIkkeUnderArbeid,
        KnyttVedleggTilForsendelseInputValideringFeilet, KnyttVedleggTilForsendelseDokumentIkkeFunnet,
        KnyttVedleggTilForsendelseDokumentTillatesIkkeGjenbrukt, KnyttVedleggTilForsendelseEksterntVedleggIkkeTillatt,
        KnyttVedleggTilForsendelseSikkerhetsbegrensning {
        try {
            port.knyttVedleggTilForsendelse(request);
        } catch (WebServiceException e) { //NOSONAR
            throw SoapWebServiceFeil.FACTORY.soapFaultIwebserviceKall(SERVICE_IDENTIFIER, e).toException();
        }
    }
}
