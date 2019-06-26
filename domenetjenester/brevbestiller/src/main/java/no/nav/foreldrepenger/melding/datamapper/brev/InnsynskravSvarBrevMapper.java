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
import no.nav.foreldrepenger.melding.behandling.innsyn.Innsyn;
import no.nav.foreldrepenger.melding.datamapper.DokumentTypeMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
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
public class InnsynskravSvarBrevMapper extends DokumentTypeMapper {

    private BrevParametere brevParametere;

    @Inject
    public InnsynskravSvarBrevMapper(BrevParametere brevParametere,
                                     DomeneobjektProvider domeneobjektProvider) {
        this.brevParametere = brevParametere;
        this.domeneobjektProvider = domeneobjektProvider;
    }

    @Override
    public String mapTilBrevXML(FellesType fellesType, DokumentFelles dokumentFelles, DokumentHendelse dokumentHendelse, Behandling behandling) throws JAXBException, SAXException, XMLStreamException {
        Innsyn innsyn = domeneobjektProvider.hentInnsyn(behandling);
        FagType fagType = mapFagType(dokumentHendelse, innsyn);
        JAXBElement<BrevdataType> brevdataTypeJAXBElement = mapintoBrevdataType(fellesType, fagType);
        return JaxbHelper.marshalNoNamespaceXML(InnsynConstants.JAXB_CLASS, brevdataTypeJAXBElement, null);
    }

    FagType mapFagType(DokumentHendelse dokumentHendelse, Innsyn innsyn) {
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
