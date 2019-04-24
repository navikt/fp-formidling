package no.nav.foreldrepenger.melding.datamapper.brev;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.xml.sax.SAXException;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatES;
import no.nav.foreldrepenger.melding.datamapper.DokumentTypeMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.BehandlingMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.BeregningsresultatESMapper;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.BehandlingsresultatType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.BrevdataType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.FagType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.InnvilgetConstants;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.ObjectFactory;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.PersonstatusKodeType;
import no.nav.vedtak.felles.integrasjon.felles.ws.JaxbHelper;

@ApplicationScoped
@Named(DokumentMalType.POSITIVT_VEDTAK_DOK)
public class InnvilgelseEngangstønadBrevMapper implements DokumentTypeMapper {

    private BrevParametere brevParametere;
    private BehandlingMapper behandlingMapper;
    private BeregningsresultatESMapper beregningsresultatESMapper;

    public InnvilgelseEngangstønadBrevMapper() {
    }

    @Inject
    public InnvilgelseEngangstønadBrevMapper(BrevParametere brevParametere,
                                             BehandlingMapper behandlingMapper,
                                             BeregningsresultatESMapper beregningsresultatESMapper) {
        this.brevParametere = brevParametere;
        this.behandlingMapper = behandlingMapper;
        this.beregningsresultatESMapper = beregningsresultatESMapper;
    }

    @Override
    public String mapTilBrevXML(FellesType fellesType, DokumentFelles dokumentFelles, DokumentHendelse dokumentHendelse, Behandling behandling) throws JAXBException, SAXException, XMLStreamException {
        BeregningsresultatES beregningsresultat = beregningsresultatESMapper.hentBeregningsresultatES(behandling);
        BeregningsresultatES originaltBeregningsresultat = originaltBeregningsresultat(behandling);
        FagType fagType = mapFagType(behandling, dokumentFelles, beregningsresultat, originaltBeregningsresultat);
        JAXBElement<BrevdataType> brevdataTypeJAXBElement = mapintoBrevdataType(fellesType, fagType);
        return JaxbHelper.marshalNoNamespaceXML(InnvilgetConstants.JAXB_CLASS, brevdataTypeJAXBElement, null);
    }

    private FagType mapFagType(Behandling behandling, DokumentFelles dokumentFelles, BeregningsresultatES beregningsresultat, BeregningsresultatES originaltBeregningsresultat) {
        FagType fagType = new FagType();
        fagType.setBehandlingsType(behandlingMapper.utledBehandlingsTypeInnvilgetES(behandling));
        fagType.setBehandlingsresultat(lagBehandlingResultatType(beregningsresultat, originaltBeregningsresultat));
        fagType.setKlageFristUker(brevParametere.getKlagefristUker());
        fagType.setPersonstatus(PersonstatusKodeType.fromValue(dokumentFelles.getSakspartPersonStatus()));
        fagType.setSokersNavn(dokumentFelles.getSakspartNavn());
        return fagType;
    }

    private BeregningsresultatES originaltBeregningsresultat(Behandling behandling) {
        if (behandling.getOriginalBehandlingId() != null && behandling.getOriginalBehandlingId() != behandling.getId()) {
            Behandling originalBehandling = behandlingMapper.hentBehandling(behandling.getOriginalBehandlingId());
            return beregningsresultatESMapper.hentBeregningsresultatES(originalBehandling);
        }
        return null;
    }

    private BehandlingsresultatType lagBehandlingResultatType(BeregningsresultatES beregningsresultat, BeregningsresultatES originaltBeregningsresultat) {
        BehandlingsresultatType behandlingsresultatType = new BehandlingsresultatType();
        behandlingsresultatType.setBelop(beregningsresultat.getBeløp());
        if (originaltBeregningsresultat != null) {
            behandlingsresultatType.setDifferanse((float) Math.abs(beregningsresultat.getBeløp() - originaltBeregningsresultat.getBeløp()));
        }
        return behandlingsresultatType;
    }

    private JAXBElement<BrevdataType> mapintoBrevdataType(FellesType fellesType, FagType fagType) {
        ObjectFactory of = new ObjectFactory();
        BrevdataType brevdataType = of.createBrevdataType();
        brevdataType.setFag(fagType);
        brevdataType.setFelles(fellesType);
        return of.createBrevdata(brevdataType);
    }
}
