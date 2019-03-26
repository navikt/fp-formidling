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
import no.nav.foreldrepenger.melding.datamapper.domene.BehandlingMapper;
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
import no.nav.vedtak.felles.integrasjon.felles.ws.JaxbHelper;

@ApplicationScoped
@Named(DokumentMalType.KLAGE_OVERSENDT_KLAGEINSTANS_DOK)
public class KlageOversendtKlageinstansBrevMapper implements DokumentTypeMapper {

    private BrevParametere brevParametere;
    private BehandlingMapper behandlingMapper;

    public KlageOversendtKlageinstansBrevMapper() {
        //CDI
    }

    @Inject
    public KlageOversendtKlageinstansBrevMapper(BehandlingMapper behandlingMapper,
                                                BrevParametere brevParametere) {
        this.behandlingMapper = behandlingMapper;
        this.brevParametere = brevParametere;
    }

    @Override
    public String mapTilBrevXML(FellesType fellesType, DokumentFelles dokumentFelles, DokumentHendelse dokumentHendelse, Behandling behandling) throws JAXBException, SAXException, XMLStreamException {
        FagType fagType = mapFagType(dokumentHendelse, behandling);
        JAXBElement<BrevdataType> brevdataTypeJAXBElement = mapintoBrevdataType(fellesType, fagType);
        return JaxbHelper.marshalNoNamespaceXML(KlageOversendtKlageinstansConstants.JAXB_CLASS, brevdataTypeJAXBElement, null);
    }


    private FagType mapFagType(DokumentHendelse hendelse, Behandling behandling) {
        final FagType fagType = new FagType();
        fagType.setYtelseType(YtelseTypeKode.fromValue(hendelse.getYtelseType().getKode()));
        //TODO må eksponeres fra fpsak
        fagType.setMottattDato(XmlUtil.finnDatoVerdiAvUtenTidSone(LocalDate.now()));
        fagType.setFritekst(hendelse.getFritekst());
        fagType.setAntallUker(BigInteger.valueOf(behandlingMapper.finnAntallUkerBehandlingsfrist(behandling)));
        fagType.setFristDato(XmlUtil.finnDatoVerdiAvUtenTidSone(BrevMapperUtil.getSvarFrist(brevParametere)));
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
