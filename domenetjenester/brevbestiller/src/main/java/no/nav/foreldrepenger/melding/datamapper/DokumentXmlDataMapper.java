package no.nav.foreldrepenger.melding.datamapper;

import java.io.StringReader;
import java.time.LocalDate;

import javax.enterprise.inject.spi.CDI;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jboss.weld.literal.NamedLiteral;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentAdresse;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.geografisk.Landkoder;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.IdKodeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.MottakerAdresseType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.MottakerType;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.melding.typer.Saksnummer;
import no.nav.foreldrepenger.xmlutils.DateUtil;

public class DokumentXmlDataMapper {

    public static Element mapTilBrevXml(DokumentMalType dokumentMalType, DokumentFelles dokumentFelles, DokumentHendelse hendelse, Behandling behandling, Saksnummer saksnummer) {
        Element brevXmlElement;
        FellesType fellesType = mapFellesType(dokumentFelles, saksnummer);
        try (DokumentTypeMapper dokumentTypeMapper = velgDokumentMapper(dokumentMalType)) {
            String brevXml = dokumentTypeMapper.mapTilBrevXML(fellesType, dokumentFelles, hendelse, behandling);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(brevXml));
            Document doc = db.parse(is);
            brevXmlElement = doc.getDocumentElement();
        } catch (Exception e) { // SAXException | XMLStreamException | ParserConfigurationException | IOException | JAXBException
            throw DokumentBestillerFeil.xmlgenereringsfeil(behandling.getUuid().toString(), e);
        }
        return brevXmlElement;
    }

    public static DokumentTypeMapper velgDokumentMapper(DokumentMalType dokumentMalType) {
        String faktiskDokumentmal = dokumentMalType.getKode();
        if (DokumentMalType.FORLENGET_BREVMALER_DOKPROD.contains(dokumentMalType)) {
            faktiskDokumentmal = DokumentMalType.FORLENGET_DOK.getKode();
        }
        return CDI.current().select(DokumentTypeMapper.class, new NamedLiteral(faktiskDokumentmal)).get();
    }

    private static FellesType mapFellesType(final DokumentFelles dokumentFelles, Saksnummer saksnummer) {
        final FellesType fellesType = new FellesType();
        fellesType.setSpraakkode(DokumentBestillerTjenesteUtil.mapSpråkkode(dokumentFelles.getSpråkkode()));
        fellesType.setFagsaksnummer(saksnummer.getVerdi());
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
        fellesType.setKontaktInformasjon(DokumentBestillerTjenesteUtil.lageKontaktInformasjonType(dokumentFelles));

        fellesType.setDokumentDato(DateUtil.convertToXMLGregorianCalendarRemoveTimezone(LocalDate.now()));

        return fellesType;
    }

    private static MottakerType lageMottakerType(DokumentFelles dokumentFelles) {
        MottakerType mottakerType = new MottakerType();
        mottakerType.setMottakerId(dokumentFelles.getMottakerId());
        if(dokumentFelles.getMottakerType()==DokumentFelles.MottakerType.PERSON) {
            mottakerType.setMottakerTypeKode(IdKodeType.PERSON);
        }
        else {
            mottakerType.setMottakerTypeKode(IdKodeType.ORGANISASJON);
        }

        mottakerType.setMottakerNavn(dokumentFelles.getMottakerNavn());
        MottakerAdresseType mottakerAdresseType = new MottakerAdresseType();
        final DokumentAdresse mottakerAdresse = dokumentFelles.getMottakerAdresse();
        mottakerAdresseType.setAdresselinje1(mottakerAdresse.getAdresselinje1());
        mottakerAdresseType.setAdresselinje2(mottakerAdresse.getAdresselinje2());
        mottakerAdresseType.setAdresselinje3(mottakerAdresse.getAdresselinje3());
        mottakerAdresseType.setPostNr(mottakerAdresse.getPostnummer());
        mottakerAdresseType.setPoststed(mottakerAdresse.getPoststed());
        Landkoder land = mottakerAdresse.getLand() == null ? Landkoder.NOR :
                Landkoder.fraKodeDefaultNorge(mottakerAdresse.getLand());
        mottakerAdresseType.setLand(land.getNavn());
        mottakerType.setMottakerAdresse(mottakerAdresseType);
        return mottakerType;
    }
}
