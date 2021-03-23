package no.nav.foreldrepenger.melding.dokumentproduksjon.v2;

import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.binding.DokumentproduksjonV2;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.binding.FerdigstillForsendelseDokumentUnderRedigering;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.binding.FerdigstillForsendelseJournalpostIkkeFunnet;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.binding.FerdigstillForsendelseJournalpostIkkeUnderArbeid;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.binding.KnyttVedleggTilForsendelseDokumentIkkeFunnet;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.binding.KnyttVedleggTilForsendelseDokumentTillatesIkkeGjenbrukt;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.binding.KnyttVedleggTilForsendelseEksterntVedleggIkkeTillatt;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.binding.KnyttVedleggTilForsendelseJournalpostIkkeFerdigstilt;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.binding.KnyttVedleggTilForsendelseJournalpostIkkeFunnet;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.binding.KnyttVedleggTilForsendelseJournalpostIkkeUnderArbeid;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.binding.KnyttVedleggTilForsendelseUlikeFagomraader;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.binding.ProduserIkkeredigerbartDokumentDokumentErRedigerbart;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.binding.ProduserIkkeredigerbartDokumentDokumentErVedlegg;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.meldinger.FerdigstillForsendelseRequest;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.meldinger.KnyttVedleggTilForsendelseRequest;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.meldinger.ProduserDokumentutkastRequest;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.meldinger.ProduserDokumentutkastResponse;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.meldinger.ProduserIkkeredigerbartDokumentRequest;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.meldinger.ProduserIkkeredigerbartDokumentResponse;

import javax.xml.ws.WebServiceException;

public class DokumentproduksjonConsumerImpl implements DokumentproduksjonConsumer {
    private static final String SERVICE_IDENTIFIER = "DokumentproduksjonV2";
    private DokumentproduksjonV2 port;

    public DokumentproduksjonConsumerImpl(DokumentproduksjonV2 port) {
        this.port = port;
    }

    @Override
    public ProduserIkkeredigerbartDokumentResponse produserIkkeredigerbartDokument(ProduserIkkeredigerbartDokumentRequest request)
            throws ProduserIkkeredigerbartDokumentDokumentErRedigerbart, ProduserIkkeredigerbartDokumentDokumentErVedlegg {
        try {
            return port.produserIkkeredigerbartDokument(request);
        } catch (WebServiceException e) { //NOSONAR
            throw SoapWebServiceFeil.soapFaultIwebserviceKall(SERVICE_IDENTIFIER, e);
        }
    }

    @Override
    public ProduserDokumentutkastResponse produserDokumentutkast(ProduserDokumentutkastRequest request) {
        try {
            return port.produserDokumentutkast(request);
        } catch (WebServiceException e) { //NOSONAR
            throw SoapWebServiceFeil.soapFaultIwebserviceKall(SERVICE_IDENTIFIER, e);
        }
    }

    @Override
    public void ferdigstillForsendelse(FerdigstillForsendelseRequest request)
            throws FerdigstillForsendelseDokumentUnderRedigering,
            FerdigstillForsendelseJournalpostIkkeFunnet,
            FerdigstillForsendelseJournalpostIkkeUnderArbeid {
        try {
            port.ferdigstillForsendelse(request);
        } catch (WebServiceException e) { //NOSONAR
            throw SoapWebServiceFeil.soapFaultIwebserviceKall(SERVICE_IDENTIFIER, e);
        }
    }

    @Override
    public void knyttVedleggTilForsendelse(KnyttVedleggTilForsendelseRequest request)
            throws KnyttVedleggTilForsendelseDokumentIkkeFunnet,
            KnyttVedleggTilForsendelseEksterntVedleggIkkeTillatt,
            KnyttVedleggTilForsendelseJournalpostIkkeFunnet,
            KnyttVedleggTilForsendelseJournalpostIkkeUnderArbeid,
            KnyttVedleggTilForsendelseDokumentTillatesIkkeGjenbrukt,
            KnyttVedleggTilForsendelseUlikeFagomraader,
            KnyttVedleggTilForsendelseJournalpostIkkeFerdigstilt {
        try {
            port.knyttVedleggTilForsendelse(request);
        } catch (WebServiceException e) { //NOSONAR
            throw SoapWebServiceFeil.soapFaultIwebserviceKall(SERVICE_IDENTIFIER, e);
        }
    }
}
