package no.nav.foreldrepenger.melding.datamapper.brev;


import java.math.BigInteger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import no.nav.foreldrepenger.fpsak.KlageRestKlient;
import no.nav.foreldrepenger.melding.brevbestiller.XmlUtil;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.brevbestiller.api.dto.klage.Klage;
import no.nav.foreldrepenger.melding.datamapper.BrevMapperUtil;
import no.nav.foreldrepenger.melding.datamapper.DokumentTypeFelles;
import no.nav.foreldrepenger.melding.datamapper.DokumentTypeMapper;
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
import no.nav.vedtak.felles.integrasjon.felles.ws.JaxbHelper;

@ApplicationScoped
@Named(DokumentMalType.KLAGE_YTELSESVEDTAK_OPPHEVET_DOK)
public class KlageYtelsesvedtakOpphevetBrevMapper implements DokumentTypeMapper {

    private BrevParametere brevParametere;
    private BehandlingMapper behandlingMapper;
    private KlageRestKlient klageRestKlient;
    private KlageMapper klageMapper;

    public KlageYtelsesvedtakOpphevetBrevMapper() {
    }

    @Inject
    public KlageYtelsesvedtakOpphevetBrevMapper(BrevParametere brevParametere,
                                                BehandlingMapper behandlingMapper,
                                                KlageRestKlient klageRestKlient,
                                                KlageMapper klageMapper) {
        this.brevParametere = brevParametere;
        this.behandlingMapper = behandlingMapper;
        this.klageRestKlient = klageRestKlient;
        this.klageMapper = klageMapper;
    }

    @Override
    public String mapTilBrevXML(FellesType fellesType, DokumentFelles dokumentFelles, DokumentHendelse dokumentHendelse, Behandling behandling) throws JAXBException, SAXException {
        Klage klage = klageMapper.hentKlagebehandling(behandling);
        FagType fagType = mapFagType(dokumentHendelse, behandling, klage);
        JAXBElement<BrevdataType> brevdataTypeJAXBElement = mapintoBrevdataType(fellesType, fagType);
        String brevXmlMedNamespace = JaxbHelper.marshalJaxb(KlageYtelsesvedtakOpphevetConstants.JAXB_CLASS, brevdataTypeJAXBElement);
        return DokumentTypeFelles.fjernNamespaceFra(brevXmlMedNamespace);
    }


    private FagType mapFagType(DokumentHendelse hendelse, Behandling behandling, Klage klage) {
        final FagType fagType = new FagType();
        fagType.setYtelseType(YtelseTypeKode.fromValue(hendelse.getYtelseType().getKode()));
        fagType.setFritekst(hendelse.getFritekst());
        //TODO denne må kanskje legges til i dto for forhåndvisning.. dessverre
        fagType.setOpphevet(klageMapper.erOpphevet(klage));
        fagType.setAntallUker(BigInteger.valueOf(behandlingMapper.finnAntallUkerBehandlingsfrist(behandling.getBehandlingType())));
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
