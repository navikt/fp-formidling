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
import no.nav.foreldrepenger.melding.brevbestiller.XmlUtil;
import no.nav.foreldrepenger.melding.datamapper.BrevMapperUtil;
import no.nav.foreldrepenger.melding.datamapper.DokumentTypeMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.domene.BehandlingMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.KlageMapper;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.klage.ytelsesvedtak.opphevet.BrevdataType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.klage.ytelsesvedtak.opphevet.FagType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.klage.ytelsesvedtak.opphevet.KlageYtelsesvedtakOpphevetConstants;
import no.nav.foreldrepenger.melding.integrasjon.dokument.klage.ytelsesvedtak.opphevet.ObjectFactory;
import no.nav.foreldrepenger.melding.integrasjon.dokument.klage.ytelsesvedtak.opphevet.YtelseTypeKode;
import no.nav.foreldrepenger.melding.klage.Klage;
import no.nav.vedtak.felles.integrasjon.felles.ws.JaxbHelper;

@ApplicationScoped
@Named(DokumentMalType.KLAGE_YTELSESVEDTAK_OPPHEVET_DOK)
public class KlageYtelsesvedtakOpphevetBrevMapper extends DokumentTypeMapper {

    private BrevParametere brevParametere;

    public KlageYtelsesvedtakOpphevetBrevMapper() {
    }

    @Inject
    public KlageYtelsesvedtakOpphevetBrevMapper(BrevParametere brevParametere,
                                                DomeneobjektProvider domeneobjektProvider) {
        this.brevParametere = brevParametere;
        this.domeneobjektProvider = domeneobjektProvider;
    }

    @Override
    public String mapTilBrevXML(FellesType fellesType, DokumentFelles dokumentFelles, DokumentHendelse dokumentHendelse,
                                Behandling behandling) throws JAXBException, SAXException, XMLStreamException {
        Klage klage = domeneobjektProvider.hentKlagebehandling(behandling);
        FagType fagType = mapFagType(dokumentHendelse, behandling, klage);
        JAXBElement<BrevdataType> brevdataTypeJAXBElement = mapintoBrevdataType(fellesType, fagType);
        return JaxbHelper.marshalNoNamespaceXML(KlageYtelsesvedtakOpphevetConstants.JAXB_CLASS, brevdataTypeJAXBElement, null);
    }

    private FagType mapFagType(DokumentHendelse hendelse, Behandling behandling, Klage klage) {
        final FagType fagType = new FagType();
        fagType.setYtelseType(YtelseTypeKode.fromValue(hendelse.getYtelseType().getKode()));
        KlageMapper.avklarFritekstKlage(hendelse, klage).ifPresent(fagType::setFritekst);
        fagType.setOpphevet(KlageMapper.erOpphevet(klage, hendelse));
        fagType.setAntallUker(BigInteger.valueOf(BehandlingMapper.finnAntallUkerBehandlingsfrist(behandling.getBehandlingType())));
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
