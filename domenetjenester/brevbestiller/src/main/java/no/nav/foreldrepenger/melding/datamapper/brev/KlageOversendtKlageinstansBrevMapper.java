package no.nav.foreldrepenger.melding.datamapper.brev;

import java.math.BigInteger;
import java.time.LocalDate;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.xml.sax.SAXException;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.brevbestiller.XmlUtil;
import no.nav.foreldrepenger.melding.datamapper.BrevMapperUtil;
import no.nav.foreldrepenger.melding.datamapper.DokumentTypeMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.klage.oversendt.klageinstans.BrevdataType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.klage.oversendt.klageinstans.FagType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.klage.oversendt.klageinstans.KlageOversendtKlageinstansConstants;
import no.nav.foreldrepenger.melding.integrasjon.dokument.klage.oversendt.klageinstans.ObjectFactory;
import no.nav.foreldrepenger.melding.integrasjon.dokument.klage.oversendt.klageinstans.YtelseTypeKode;
import no.nav.foreldrepenger.melding.klage.Klage;
import no.nav.foreldrepenger.melding.klage.KlageDokument;
import no.nav.vedtak.felles.integrasjon.felles.ws.JaxbHelper;
import no.nav.vedtak.util.FPDateUtil;

@ApplicationScoped
@Named(DokumentMalType.KLAGE_OVERSENDT_KLAGEINSTANS_DOK)
public class KlageOversendtKlageinstansBrevMapper extends DokumentTypeMapper {
    private static final int BEHANDLINGSFRIST_UKER_KA = 14;

    private BrevParametere brevParametere;

    public KlageOversendtKlageinstansBrevMapper() {
        //CDI
    }

    @Inject
    public KlageOversendtKlageinstansBrevMapper(BrevParametere brevParametere,
                                                DomeneobjektProvider domeneobjektProvider) {
        this.brevParametere = brevParametere;
        this.domeneobjektProvider = domeneobjektProvider;
    }

    @Override
    public String mapTilBrevXML(FellesType fellesType, DokumentFelles dokumentFelles, DokumentHendelse dokumentHendelse, Behandling behandling) throws JAXBException, SAXException, XMLStreamException {
        KlageDokument klageDokument = domeneobjektProvider.hentKlageDokument(behandling);
        Klage klage = domeneobjektProvider.hentKlagebehandling(behandling);
        FagType fagType = mapFagType(dokumentHendelse, klage, klageDokument);
        JAXBElement<BrevdataType> brevdataTypeJAXBElement = mapintoBrevdataType(fellesType, fagType);
        return JaxbHelper.marshalNoNamespaceXML(KlageOversendtKlageinstansConstants.JAXB_CLASS, brevdataTypeJAXBElement, null);
    }


    private FagType mapFagType(DokumentHendelse hendelse, Klage klage, KlageDokument klageDokument) {
        final FagType fagType = new FagType();
        fagType.setYtelseType(YtelseTypeKode.fromValue(hendelse.getYtelseType().getKode()));
        LocalDate mottattDato = klageDokument.getMottattDato() != null ? klageDokument.getMottattDato() : FPDateUtil.iDag();
        fagType.setMottattDato(XmlUtil.finnDatoVerdiAvUtenTidSone(mottattDato));
        fagType.setFritekst(avklarFritekst(hendelse, klage));
        fagType.setAntallUker(BigInteger.valueOf(BEHANDLINGSFRIST_UKER_KA));
        fagType.setFristDato(XmlUtil.finnDatoVerdiAvUtenTidSone(BrevMapperUtil.getSvarFrist(brevParametere)));
        return fagType;
    }

    private String avklarFritekst(DokumentHendelse hendelse, Klage klage) {
        return hendelse.getFritekst() != null ? hendelse.getFritekst() :
                klage.getGjeldendeKlageVurderingsresultat().getFritekstTilBrev();
    }

    private JAXBElement<BrevdataType> mapintoBrevdataType(FellesType fellesType, FagType fagType) {
        ObjectFactory of = new ObjectFactory();
        BrevdataType brevdataType = of.createBrevdataType();
        brevdataType.setFag(fagType);
        brevdataType.setFelles(fellesType);
        return of.createBrevdata(brevdataType);
    }

}
