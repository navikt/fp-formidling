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
import no.nav.foreldrepenger.melding.behandling.Innsyn;
import no.nav.foreldrepenger.melding.datamapper.DokumentTypeFelles;
import no.nav.foreldrepenger.melding.datamapper.DokumentTypeMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.InnsynMapper;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innsyn.BrevdataType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innsyn.FagType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innsyn.InnsynConstants;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innsyn.ObjectFactory;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innsyn.YtelseTypeKode;
import no.nav.vedtak.felles.integrasjon.felles.ws.JaxbHelper;

@ApplicationScoped
@Named(DokumentMalType.INNSYNSKRAV_SVAR)
public class InnsynskravSvarBrevMapper implements DokumentTypeMapper {

    private BrevParametere brevParametere;
    private InnsynMapper innsynMapper;

    @Inject
    public InnsynskravSvarBrevMapper(BrevParametere brevParametere,
                                     InnsynMapper innsynMapper) {
        this.brevParametere = brevParametere;
        this.innsynMapper = innsynMapper;
    }

    @Override
    public String mapTilBrevXML(FellesType fellesType, DokumentFelles dokumentFelles, DokumentHendelse dokumentHendelse, Behandling behandling) throws JAXBException, SAXException, XMLStreamException {
        Innsyn innsyn = innsynMapper.hentInnsyn(behandling.getId());
        FagType fagType = mapFagType(dokumentHendelse, behandling, innsyn);
        JAXBElement<BrevdataType> brevdataTypeJAXBElement = mapintoBrevdataType(fellesType, fagType);
        return JaxbHelper.marshalNoNamespaceXML(InnsynConstants.JAXB_CLASS, brevdataTypeJAXBElement, null);
    }

    private FagType mapFagType(DokumentHendelse dokumentHendelse, Behandling behandling, Innsyn innsyn) {
        FagType fagType = new FagType();
        fagType.setFritekst(dokumentHendelse.getFritekst());
        fagType.setInnsynResultatType(InnsynMapper.mapInnsynResultatKode(innsyn.getInnsynResultatType()));
        fagType.setYtelseType(YtelseTypeKode.fromValue(dokumentHendelse.getYtelseType().getKode()));
        fagType.setKlageFristUker(BigInteger.valueOf(brevParametere.getKlagefristUkerInnsyn()));
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
