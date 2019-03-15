package no.nav.foreldrepenger.melding.datamapper;

import java.io.IOException;
import java.io.StringReader;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import no.nav.foreldrepenger.melding.brevbestiller.api.dto.Behandling;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentAdresse;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.geografisk.Landkoder;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.IdKodeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.MottakerAdresseType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.MottakerType;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.vedtak.feil.FeilFactory;
import no.nav.vedtak.felles.integrasjon.felles.ws.DateUtil;
import no.nav.vedtak.util.FPDateUtil;

@ApplicationScoped
public class DokumentXmlDataMapper {

    private KodeverkRepository kodeverkRepository;
    private BrevParametere brevParametere;

    public DokumentXmlDataMapper() {
        //CDI
    }

    @Inject
    public DokumentXmlDataMapper(KodeverkRepository kodeverkRepository,
                                 BrevParametere brevParametere) {
        this.kodeverkRepository = kodeverkRepository;
        this.brevParametere = brevParametere;
    }

    public Element mapTilBrevXml(DokumentMalType dokumentMalType, DokumentFelles dokumentFelles, DokumentHendelse hendelse, Behandling behandling) {
        Element brevXmlElement;
        try {
            FellesType fellesType = mapFellesType(dokumentFelles);
            String brevXml = DokumentTypeRuter.dokumentTypeMapper(dokumentMalType).mapTilBrevXML(fellesType, dokumentFelles, hendelse, behandling);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(brevXml));
            Document doc = db.parse(is);
            brevXmlElement = doc.getDocumentElement();
        } catch (SAXException | XMLStreamException | ParserConfigurationException | IOException | JAXBException e) {
            //TODO feilmelding dokumentID
            throw FeilFactory.create(DokumentBestillerFeil.class).xmlgenereringsfeil(1l, e).toException();
        } catch (InstantiationException | IllegalAccessException e) {
            //TODO feilmelding dokumentID
            throw FeilFactory.create(DokumentBestillerFeil.class).annentekniskfeil(1l, e).toException();
        }
        return brevXmlElement;
    }

    public FellesType mapFellesType(final DokumentFelles dokumentFelles) {
        final FellesType fellesType = new FellesType();
        fellesType.setSpraakkode(DokumentBestillerTjenesteUtil.mapSpråkkode(dokumentFelles.getSpråkkode()));
        fellesType.setFagsaksnummer(dokumentFelles.getSaksnummer().getVerdi());
        if (dokumentFelles.getSignerendeSaksbehandlerNavn() != null) {
            fellesType.setSignerendeSaksbehandler(DokumentBestillerTjenesteUtil.lageSignerendeSaksbehandlerType(dokumentFelles));
        }
        fellesType.setAutomatiskBehandlet(dokumentFelles.getAutomatiskBehandlet());
        fellesType.setSakspart(DokumentBestillerTjenesteUtil.lageSakspartType(dokumentFelles));
        if (dokumentFelles.getSignerendeBeslutterNavn() != null) {
            fellesType.setSignerendeBeslutter(DokumentBestillerTjenesteUtil.lageSignerendeBeslutterType(dokumentFelles));
        }
        fellesType.setMottaker(lageMottakerType(dokumentFelles));
        fellesType.setNavnAvsenderEnhet(dokumentFelles.getNavnAvsenderEnhet());
        fellesType.setNummerAvsenderEnhet(dokumentFelles.getNummerAvsenderEnhet());
        fellesType.setKontaktInformasjon(DokumentBestillerTjenesteUtil.lageKontaktInformasjonType(dokumentFelles));

        fellesType.setDokumentDato(DateUtil.convertToXMLGregorianCalendarRemoveTimezone(FPDateUtil.iDag()));

        return fellesType;
    }

    private MottakerType lageMottakerType(DokumentFelles dokumentFelles) {
        MottakerType mottakerType = new MottakerType();
        mottakerType.setMottakerId(dokumentFelles.getMottakerId());
        mottakerType.setMottakerTypeKode(IdKodeType.PERSON);
        mottakerType.setMottakerNavn(dokumentFelles.getMottakerNavn());
        MottakerAdresseType mottakerAdresseType = new MottakerAdresseType();
        final DokumentAdresse mottakerAdresse = dokumentFelles.getMottakerAdresse();
        mottakerAdresseType.setAdresselinje1(mottakerAdresse.getAdresselinje1());
        mottakerAdresseType.setAdresselinje2(mottakerAdresse.getAdresselinje2());
        mottakerAdresseType.setAdresselinje3(mottakerAdresse.getAdresselinje3());
        mottakerAdresseType.setPostNr(mottakerAdresse.getPostnummer());
        mottakerAdresseType.setPoststed(mottakerAdresse.getPoststed());
        Landkoder land = mottakerAdresse.getLand() == null ? kodeverkRepository.finn(Landkoder.class, Landkoder.NOR) : kodeverkRepository.finn(Landkoder.class, mottakerAdresse.getLand());
        mottakerAdresseType.setLand(land.getNavn());
        mottakerType.setMottakerAdresse(mottakerAdresseType);
        return mottakerType;
    }
}
