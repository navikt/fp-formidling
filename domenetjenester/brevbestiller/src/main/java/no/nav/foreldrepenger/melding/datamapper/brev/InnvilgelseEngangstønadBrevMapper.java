package no.nav.foreldrepenger.melding.datamapper.brev;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.xml.sax.SAXException;

import no.nav.foreldrepenger.fpsak.BehandlingRestKlient;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingIdDto;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.datamapper.DokumentTypeMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.BehandlingMapper;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.BehandlingsTypeType;
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
    private BehandlingRestKlient behandlingRestKlient;

    public InnvilgelseEngangstønadBrevMapper() {
    }

    @Inject
    public InnvilgelseEngangstønadBrevMapper(BrevParametere brevParametere,
                                             BehandlingMapper behandlingMapper,
                                             BehandlingRestKlient behandlingRestKlient) {
        this.brevParametere = brevParametere;
        this.behandlingMapper = behandlingMapper;
        this.behandlingRestKlient = behandlingRestKlient;
    }

    @Override
    public String mapTilBrevXML(FellesType fellesType, DokumentFelles dokumentFelles, DokumentHendelse dokumentHendelse, Behandling behandling) throws JAXBException, SAXException, XMLStreamException {
        FagType fagType = mapFagType(behandling, dokumentFelles);
        JAXBElement<BrevdataType> brevdataTypeJAXBElement = mapintoBrevdataType(fellesType, fagType);
        return JaxbHelper.marshalNoNamespaceXML(InnvilgetConstants.JAXB_CLASS, brevdataTypeJAXBElement, null);
    }

    private FagType mapFagType(Behandling behandling, DokumentFelles dokumentFelles) {
        FagType fagType = new FagType();
        fagType.setBehandlingsType(fra(behandlingMapper.utledBehandlingsTypeForPositivtVedtak(behandling)));
        fagType.setBehandlingsresultat(lagBehandlingResultatType(behandling));
        fagType.setKlageFristUker(brevParametere.getKlagefristUker());
        fagType.setPersonstatus(PersonstatusKodeType.fromValue(dokumentFelles.getSakspartPersonStatus()));
        fagType.setSokersNavn(dokumentFelles.getSakspartNavn());
        return fagType;
    }

    private BehandlingsresultatType lagBehandlingResultatType(Behandling behandling) {
        BehandlingsresultatType behandlingsresultatType = new BehandlingsresultatType();
        Long beregnetTilkjentYtelse = behandlingRestKlient
                .hentBeregningsresultatEngangsstønad(behandling.getResourceLinkDtos()).getBeregnetTilkjentYtelse();
        behandlingsresultatType.setBelop(beregnetTilkjentYtelse);
        if (behandling.getOriginalBehandlingId() != null && behandling.getOriginalBehandlingId() != behandling.getId()) {
            Behandling originalBehandling = new Behandling(behandlingRestKlient.hentBehandling(new BehandlingIdDto(behandling.getOriginalBehandlingId())));
            Long originalTilkjentYtelse = behandlingRestKlient
                    .hentBeregningsresultatEngangsstønad(originalBehandling.getResourceLinkDtos()).getBeregnetTilkjentYtelse();
            //Float :(
            behandlingsresultatType.setDifferanse((float) Math.abs(beregnetTilkjentYtelse - originalTilkjentYtelse));
        }
        return behandlingsresultatType;
    }

    private BehandlingsTypeType fra(String behandlingsType) {
        if ("REVURDERING".equals(behandlingsType)) {
            return BehandlingsTypeType.REVURDERING;
        }
        if ("MEDHOLD".equals(behandlingsType)) {
            return BehandlingsTypeType.MEDHOLD;
        }
        return BehandlingsTypeType.FOERSTEGANGSBEHANDLING;
    }


    private JAXBElement<BrevdataType> mapintoBrevdataType(FellesType fellesType, FagType fagType) {
        ObjectFactory of = new ObjectFactory();
        BrevdataType brevdataType = of.createBrevdataType();
        brevdataType.setFag(fagType);
        brevdataType.setFelles(fellesType);
        return of.createBrevdata(brevdataType);
    }

}
