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
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.domene.KlageMapper;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.vedtak.mehold.BrevdataType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.vedtak.mehold.FagType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.vedtak.mehold.ObjectFactory;
import no.nav.foreldrepenger.melding.integrasjon.dokument.vedtak.mehold.OpphavTypeKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.vedtak.mehold.VedtakMedholdConstants;
import no.nav.foreldrepenger.melding.integrasjon.dokument.vedtak.mehold.YtelseTypeKode;
import no.nav.foreldrepenger.melding.klage.Klage;
import no.nav.vedtak.felles.integrasjon.felles.ws.JaxbHelper;

@ApplicationScoped
@Named(DokumentMalType.VEDTAK_MEDHOLD)
public class VedtakMedholdBrevMapper implements DokumentTypeMapper {

    private BrevParametere brevParametere;
    private DomeneobjektProvider domeneobjektProvider;

    public VedtakMedholdBrevMapper() {
        //CDI
    }

    @Inject
    public VedtakMedholdBrevMapper(DomeneobjektProvider domeneobjektProvider,
                                   BrevParametere brevParametere) {
        this.domeneobjektProvider = domeneobjektProvider;
        this.brevParametere = brevParametere;
    }

    @Override
    public String mapTilBrevXML(FellesType fellesType, DokumentFelles dokumentFelles, DokumentHendelse dokumentHendelse, Behandling behandling) throws JAXBException, SAXException, XMLStreamException {
        Klage klage = domeneobjektProvider.hentKlagebehandling(behandling);
        FagType fagType = mapFagType(dokumentHendelse, klage);
        JAXBElement<BrevdataType> brevdataTypeJAXBElement = mapTilBrevDataType(fellesType, fagType);
        return JaxbHelper.marshalNoNamespaceXML(VedtakMedholdConstants.JAXB_CLASS, brevdataTypeJAXBElement, null);
    }

    private FagType mapFagType(DokumentHendelse dokumentHendelse, Klage klage) {
        FagType fagType = new FagType();
        fagType.setYtelseType(YtelseTypeKode.fromValue(dokumentHendelse.getYtelseType().getKode()));
        fagType.setOpphavType(utledOpphaveTypeKode(klage));
        fagType.setKlageFristUker(BigInteger.valueOf(brevParametere.getKlagefristUker()));
        KlageMapper.avklarFritekstKlage(dokumentHendelse, klage).ifPresent(fagType::setFritekst);
        return fagType;
    }

    private OpphavTypeKode utledOpphaveTypeKode(Klage klage) {
        if (klage.getFormkravKA() != null) {
            return OpphavTypeKode.KLAGE;
        }
        return OpphavTypeKode.FAMPEN;
    }

    private JAXBElement<BrevdataType> mapTilBrevDataType(FellesType fellesType, FagType fagType) {
        ObjectFactory of = new ObjectFactory();
        BrevdataType brevdataType = of.createBrevdataType();
        brevdataType.setFag(fagType);
        brevdataType.setFelles(fellesType);
        return of.createBrevdata(brevdataType);
    }

}
