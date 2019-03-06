package no.nav.foreldrepenger.melding.datamapper;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.hendelsekontrakter.hendelse.DokumentHendelseDto;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.uendretutfall.BrevdataType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.uendretutfall.FagType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.uendretutfall.ObjectFactory;
import no.nav.foreldrepenger.melding.integrasjon.dokument.uendretutfall.UendretutfallConstants;
import no.nav.foreldrepenger.melding.integrasjon.dokument.uendretutfall.YtelseTypeKode;
import no.nav.vedtak.felles.integrasjon.felles.ws.JaxbHelper;

public class UendretutfallBrevMapper implements DokumentTypeMapper {

    @Override
    public String mapTilBrevXML(FellesType fellesType, DokumentFelles dokumentFelles, DokumentHendelseDto hendelseDto) throws JAXBException {
        FagType fagType = mapFagType(hendelseDto);
        JAXBElement<BrevdataType> brevdataTypeJAXBElement = mapintoBrevdataType(fellesType, fagType);
        String brevXmlMedNamespace = JaxbHelper.marshalJaxb(UendretutfallConstants.JAXB_CLASS, brevdataTypeJAXBElement);
        return DokumentTypeFelles.fjernNamespaceFra(brevXmlMedNamespace);
    }

    private FagType mapFagType(DokumentHendelseDto hendelseDto) {
        FagType fagType = new FagType();
        fagType.setYtelseType(YtelseTypeKode.fromValue(hendelseDto.getYtelseType()));
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
