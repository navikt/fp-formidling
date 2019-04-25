package no.nav.foreldrepenger.melding.datamapper.brev;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.xml.sax.SAXException;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.datamapper.DokumentTypeMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.domene.KlageMapper;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.klage.avvist.AvvistGrunnListeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.klage.avvist.AvvistGrunnType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.klage.avvist.BrevdataType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.klage.avvist.FagType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.klage.avvist.KlageAvvistConstants;
import no.nav.foreldrepenger.melding.integrasjon.dokument.klage.avvist.ObjectFactory;
import no.nav.foreldrepenger.melding.integrasjon.dokument.klage.avvist.YtelseTypeKode;
import no.nav.foreldrepenger.melding.klage.Klage;
import no.nav.foreldrepenger.melding.klage.KlageAvvistÅrsak;
import no.nav.vedtak.felles.integrasjon.felles.ws.JaxbHelper;

@ApplicationScoped
@Named(DokumentMalType.KLAGE_AVVIST_DOK)
public class KlageAvvistDokumentBrevMapper implements DokumentTypeMapper {

    private BrevParametere brevParametere;
    private DomeneobjektProvider domeneobjektProvider;

    public KlageAvvistDokumentBrevMapper() {
    }

    @Inject
    public KlageAvvistDokumentBrevMapper(BrevParametere brevParametere,
                                         DomeneobjektProvider domeneobjektProvider) {
        this.brevParametere = brevParametere;
        this.domeneobjektProvider = domeneobjektProvider;
    }

    @Override
    public String mapTilBrevXML(FellesType fellesType, DokumentFelles dokumentFelles, DokumentHendelse dokumentHendelse, Behandling behandling) throws JAXBException, SAXException, XMLStreamException {
        Klage klage = domeneobjektProvider.hentKlagebehandling(behandling);
        FagType fagType = mapFagType(dokumentHendelse, klage);
        JAXBElement<BrevdataType> brevdataTypeJAXBElement = mapintoBrevdataType(fellesType, fagType);
        return JaxbHelper.marshalNoNamespaceXML(KlageAvvistConstants.JAXB_CLASS, brevdataTypeJAXBElement, null);
    }

    private FagType mapFagType(DokumentHendelse dokumentHendelse, Klage klage) {
        FagType fagType = new FagType();
        fagType.setYtelseType(YtelseTypeKode.fromValue(dokumentHendelse.getYtelseType().getKode()));
        fagType.setKlageFristUker(BigInteger.valueOf(brevParametere.getKlagefristUker()));
        List<KlageAvvistÅrsak> avvistÅrsaker = KlageMapper.listeAvAvvisteÅrsaker(klage);
        fagType.setAntallAvvistGrunn(BigInteger.valueOf(avvistÅrsaker.size()));
        //Valgfrie felter
        if (!avvistÅrsaker.isEmpty()) {
            fagType.setAvvistGrunnListe(avvistGrunnListeFra(
                    avvistÅrsaker.stream().map(KlageAvvistÅrsak::getKode).collect(Collectors.toList())));
            KlageMapper.hentOgFormaterLovhjemlerForAvvistKlage(klage).ifPresent(fagType::setLovhjemler);
        }
        return fagType;
    }

    static AvvistGrunnListeType avvistGrunnListeFra(List<String> avvistGrunn) {
        AvvistGrunnListeType liste = new ObjectFactory().createAvvistGrunnListeType();
        avvistGrunn.forEach(avvistString -> {
            AvvistGrunnType avvistGrunnType = new ObjectFactory().createAvvistGrunnType();
            avvistGrunnType.setAvvistGrunnKode(KlageMapper.avvistGrunnMap.get(avvistString));
            liste.getAvvistGrunn().add(avvistGrunnType);
        });
        return liste;
    }

    private JAXBElement<BrevdataType> mapintoBrevdataType(FellesType fellesType, FagType fagType) {
        ObjectFactory of = new ObjectFactory();
        BrevdataType brevdataType = of.createBrevdataType();
        brevdataType.setFag(fagType);
        brevdataType.setFelles(fellesType);
        return of.createBrevdata(brevdataType);
    }

}
