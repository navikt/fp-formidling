package no.nav.foreldrepenger.melding.datamapper.brev;

import java.math.BigInteger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.xml.sax.SAXException;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.datamapper.DokumentTypeMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.KlageMapper;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.klage.ytelsesvedtak.stadfestet.BrevdataType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.klage.ytelsesvedtak.stadfestet.FagType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.klage.ytelsesvedtak.stadfestet.KlageYtelsesvedtakStadfestetConstants;
import no.nav.foreldrepenger.melding.integrasjon.dokument.klage.ytelsesvedtak.stadfestet.ObjectFactory;
import no.nav.foreldrepenger.melding.integrasjon.dokument.klage.ytelsesvedtak.stadfestet.YtelseTypeKode;
import no.nav.vedtak.felles.integrasjon.felles.ws.JaxbHelper;

@ApplicationScoped
@Named(DokumentMalType.KLAGE_YTELSESVEDTAK_STADFESTET_DOK)
public class KlageYtelsesvedtakStadfestetBrevMapper implements DokumentTypeMapper {

    private BrevParametere brevParametere;
    private KlageMapper klageMapper;

    public KlageYtelsesvedtakStadfestetBrevMapper() {
        //CDI
    }

    @Inject
    public KlageYtelsesvedtakStadfestetBrevMapper(BrevParametere brevParametere,
                                                  KlageMapper klageMapper) {
        this.brevParametere = brevParametere;
        this.klageMapper = klageMapper;
    }

    @Override
    public String mapTilBrevXML(FellesType fellesType, DokumentFelles dokumentFelles, DokumentHendelse dokumentHendelse, Behandling behandling) throws JAXBException, SAXException, XMLStreamException {
        FagType fagType = mapFagType(dokumentHendelse);
        JAXBElement<BrevdataType> brevdataTypeJAXBElement = mapintoBrevdataType(fellesType, fagType);
        return JaxbHelper.marshalNoNamespaceXML(KlageYtelsesvedtakStadfestetConstants.JAXB_CLASS, brevdataTypeJAXBElement, null);
    }

    private FagType mapFagType(DokumentHendelse dokumentHendelse) {
        FagType fagType = new FagType();
        fagType.setFritekst(dokumentHendelse.getFritekst());
        fagType.setKlageFristUker(BigInteger.valueOf(brevParametere.getKlagefristUker()));
        fagType.setYtelseType(YtelseTypeKode.fromValue(dokumentHendelse.getYtelseType().getKode()));
        return fagType;
    }


    private JAXBElement<BrevdataType> mapintoBrevdataType(FellesType fellesType, FagType fagType) {
        ObjectFactory of = new ObjectFactory();
        BrevdataType brevdataType = of.createBrevdataType();
        brevdataType.setFag(fagType);
        brevdataType.setFelles(fellesType);
        return of.createBrevdata(brevdataType);
    }
}
