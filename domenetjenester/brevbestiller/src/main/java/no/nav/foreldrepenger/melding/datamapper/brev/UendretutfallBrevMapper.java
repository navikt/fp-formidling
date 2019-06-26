package no.nav.foreldrepenger.melding.datamapper.brev;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.xml.sax.SAXException;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.datamapper.DokumentTypeMapper;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.uendretutfall.BrevdataType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.uendretutfall.FagType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.uendretutfall.ObjectFactory;
import no.nav.foreldrepenger.melding.integrasjon.dokument.uendretutfall.UendretutfallConstants;
import no.nav.foreldrepenger.melding.integrasjon.dokument.uendretutfall.YtelseTypeKode;
import no.nav.vedtak.felles.integrasjon.felles.ws.JaxbHelper;

@ApplicationScoped
@Named(DokumentMalType.UENDRETUTFALL_DOK)
public class UendretutfallBrevMapper extends DokumentTypeMapper {

    public UendretutfallBrevMapper() {
        //CDI
    }

    @Override
    public String mapTilBrevXML(FellesType fellesType, DokumentFelles dokumentFelles, DokumentHendelse hendelse, Behandling behandling) throws JAXBException, XMLStreamException, SAXException {
        FagType fagType = mapFagType(hendelse);
        JAXBElement<BrevdataType> brevdataTypeJAXBElement = mapintoBrevdataType(fellesType, fagType);
        return JaxbHelper.marshalNoNamespaceXML(UendretutfallConstants.JAXB_CLASS, brevdataTypeJAXBElement, null);
    }

    FagType mapFagType(DokumentHendelse hendelse) {
        FagType fagType = new FagType();
        fagType.setYtelseType(YtelseTypeKode.fromValue(hendelse.getYtelseType().getKode()));
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
