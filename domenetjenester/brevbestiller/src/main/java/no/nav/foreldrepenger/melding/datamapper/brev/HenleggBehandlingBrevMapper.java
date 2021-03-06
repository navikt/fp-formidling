package no.nav.foreldrepenger.melding.datamapper.brev;

import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.xml.sax.SAXException;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.datamapper.DokumentTypeMapper;
import no.nav.foreldrepenger.melding.datamapper.mal.BehandlingTypeKonstanter;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avbruttbehandling.AvbruttbehandlingConstants;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avbruttbehandling.BehandlingsTypeKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avbruttbehandling.BrevdataType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avbruttbehandling.FagType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avbruttbehandling.ObjectFactory;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avbruttbehandling.OpphavTypeKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avbruttbehandling.YtelseTypeKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalTypeKode;
import no.nav.foreldrepenger.xmlutils.JaxbHelper;
import no.nav.vedtak.exception.TekniskException;

@ApplicationScoped
@Named(DokumentMalTypeKode.HENLEGG_BEHANDLING_DOK)
public class HenleggBehandlingBrevMapper extends DokumentTypeMapper {

    static final String FAMPEN = "NAV Familie- og pensjonsytelser";

    public HenleggBehandlingBrevMapper() {
        // CDI
    }

    @Override
    public String mapTilBrevXML(FellesType fellesType, DokumentFelles dokumentFelles, DokumentHendelse hendelse,
            Behandling behandling) throws JAXBException, XMLStreamException, SAXException {
        FagType fagType = mapFagType(hendelse, behandling);
        JAXBElement<BrevdataType> brevdataTypeJAXBElement = mapintoBrevdataType(fellesType, fagType);
        return JaxbHelper.marshalNoNamespaceXML(AvbruttbehandlingConstants.JAXB_CLASS, brevdataTypeJAXBElement, null);
    }

    private FagType mapFagType(DokumentHendelse hendelse, Behandling behandling) {
        final FagType fagType = new FagType();
        fagType.setYtelseType(YtelseTypeKode.fromValue(hendelse.getYtelseType().getKode()));
        fagType.setBehandlingsType(mapToXmlBehandlingsType(behandling.getBehandlingType().getKode()));
        fagType.setOpphavType(utledOpphavType(hendelse, behandling));
        return fagType;
    }

    private static BehandlingsTypeKode mapToXmlBehandlingsType(String vlKode) {
        if (Objects.equals(vlKode, BehandlingTypeKonstanter.ENDRINGSSØKNAD)) {
            return BehandlingsTypeKode.ENDRINGSSØKNAD;
        }
        if (Objects.equals(vlKode, BehandlingType.FØRSTEGANGSSØKNAD.getKode())) {
            return BehandlingsTypeKode.FØRSTEGANGSSØKNAD;
        }
        if (Objects.equals(vlKode, BehandlingType.KLAGE.getKode())) {
            return BehandlingsTypeKode.KLAGE;
        }
        if (Objects.equals(vlKode, BehandlingType.REVURDERING.getKode())) {
            return BehandlingsTypeKode.REVURDERING;
        }
        if (Objects.equals(vlKode, BehandlingType.INNSYN.getKode())) {
            return BehandlingsTypeKode.INNSYN;
        }
        throw new TekniskException("FPFORMIDLING-875835", String.format("Ugyldig behandlingstype %s for brev med malkode HENLEG", vlKode));
    }

    private OpphavTypeKode utledOpphavType(DokumentHendelse hendelse, Behandling behandling) {
        if (hendelse.getBehandlendeEnhetNavn() != null && !hendelse.getBehandlendeEnhetNavn().isEmpty()) {
            return mapToXmlOpphavType(hendelse.getBehandlendeEnhetNavn());
        }
        return mapToXmlOpphavType(behandling.getBehandlendeEnhetNavn());
    }

    private OpphavTypeKode mapToXmlOpphavType(String behandlendeEnhetNavn) {
        if (behandlendeEnhetNavn.contains(FAMPEN)) {
            return OpphavTypeKode.FAMPEN;
        }
        return OpphavTypeKode.KLAGE;
    }

    private JAXBElement<BrevdataType> mapintoBrevdataType(FellesType fellesType, FagType fagType) {
        ObjectFactory of = new ObjectFactory();
        BrevdataType brevdataType = of.createBrevdataType();
        brevdataType.setFag(fagType);
        brevdataType.setFelles(fellesType);
        return of.createBrevdata(brevdataType);
    }

}
