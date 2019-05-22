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
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.domene.BehandlingMapper;
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
    private DomeneobjektProvider domeneobjektProvider;

    public InnvilgelseEngangstønadBrevMapper() {
    }

    @Inject
    public InnvilgelseEngangstønadBrevMapper(BrevParametere brevParametere,
                                             DomeneobjektProvider domeneobjektProvider) {
        this.brevParametere = brevParametere;
        this.domeneobjektProvider = domeneobjektProvider;
    }

    @Override
    public String mapTilBrevXML(FellesType fellesType, DokumentFelles dokumentFelles, DokumentHendelse dokumentHendelse, Behandling behandling) throws JAXBException, SAXException, XMLStreamException {
        BeregningsresultatES beregningsresultat = domeneobjektProvider.hentBeregningsresultatES(behandling);
        BeregningsresultatES originaltBeregningsresultat = originaltBeregningsresultat(behandling);
        FagType fagType = mapFagType(behandling, dokumentFelles, beregningsresultat, originaltBeregningsresultat);
        JAXBElement<BrevdataType> brevdataTypeJAXBElement = mapintoBrevdataType(fellesType, fagType);
        return JaxbHelper.marshalNoNamespaceXML(InnvilgetConstants.JAXB_CLASS, brevdataTypeJAXBElement, null);
    }

    private FagType mapFagType(Behandling behandling, DokumentFelles dokumentFelles, BeregningsresultatES beregningsresultat, BeregningsresultatES originaltBeregningsresultat) {
        FagType fagType = new FagType();
        fagType.setBehandlingsType(BehandlingMapper.utledBehandlingsTypeInnvilgetES(behandling));
        fagType.setBehandlingsresultat(lagBehandlingResultatType(beregningsresultat, originaltBeregningsresultat));
        fagType.setKlageFristUker(brevParametere.getKlagefristUker());
        fagType.setPersonstatus(PersonstatusKodeType.fromValue(dokumentFelles.getSakspartPersonStatus()));
        fagType.setSokersNavn(dokumentFelles.getSakspartNavn());
        return fagType;
    }

    private BeregningsresultatES originaltBeregningsresultat(Behandling behandling) {
        if (behandling.getOriginalBehandling() != null && behandling.getOriginalBehandling().getId() != behandling.getId()) {
            return domeneobjektProvider.hentBeregningsresultatESHvisFinnes(behandling.getOriginalBehandling()).orElse(null);
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
