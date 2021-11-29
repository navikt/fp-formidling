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
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.fritekstbrev.BrevdataType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.fritekstbrev.FagType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.fritekstbrev.FritekstbrevConstants;
import no.nav.foreldrepenger.melding.integrasjon.dokument.fritekstbrev.ObjectFactory;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalTypeKode;
import no.nav.foreldrepenger.xmlutils.JaxbHelper;

@ApplicationScoped
@Named(DokumentMalTypeKode.FRITEKSTBREV_DOK)
public class FritekstBrevMapper extends DokumentTypeMapper {

    @Override
    public String mapTilBrevXML(FellesType fellesType, DokumentFelles dokumentFelles, DokumentHendelse hendelse, Behandling behandling) throws JAXBException, XMLStreamException, SAXException {
        FagType fagType = mapFagType(hendelse, behandling);
        JAXBElement<BrevdataType> brevdataTypeJAXBElement = mapintoBrevdataType(fellesType, fagType);
        return JaxbHelper.marshalNoNamespaceXML(FritekstbrevConstants.JAXB_CLASS, brevdataTypeJAXBElement, null);
    }

    protected FagType mapFagType(DokumentHendelse hendelse, Behandling behandling) {
        FagType fagType = new FagType();
        String faktiskBrødtekst = finnFaktiskBrødtekst(hendelse, behandling);
        String faktiskTittel = finnFaktiskTittel(hendelse, behandling);
        fagType.setBrødtekst(faktiskBrødtekst);
        fagType.setHovedoverskrift(faktiskTittel);
        return fagType;
    }

    private String finnFaktiskTittel(DokumentHendelse hendelse, Behandling behandling) {
        return hendelse.getTittel() != null && !hendelse.getTittel().isEmpty() ? hendelse.getTittel() : behandling.getBehandlingsresultat().getOverskrift();
    }

    private String finnFaktiskBrødtekst(DokumentHendelse hendelse, Behandling behandling) {
        return hendelse.getFritekst() != null && !hendelse.getFritekst().isEmpty()  ? hendelse.getFritekst() : behandling.getBehandlingsresultat().getFritekstbrev();
    }


    private JAXBElement<BrevdataType> mapintoBrevdataType(FellesType fellesType, FagType fagType) {
        ObjectFactory of = new ObjectFactory();
        BrevdataType brevdataType = of.createBrevdataType();
        brevdataType.setFag(fagType);
        brevdataType.setFelles(fellesType);
        return of.createBrevdata(brevdataType);
    }

}
