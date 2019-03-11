package no.nav.foreldrepenger.melding.datamapper;

import java.util.Objects;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.brevbestiller.api.dto.Behandling;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.hendelsekontrakter.hendelse.DokumentHendelseDto;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avbruttbehandling.AvbruttbehandlingConstants;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avbruttbehandling.BehandlingsTypeKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avbruttbehandling.BrevdataType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avbruttbehandling.FagType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avbruttbehandling.ObjectFactory;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avbruttbehandling.OpphavTypeKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avbruttbehandling.YtelseTypeKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.vedtak.felles.integrasjon.felles.ws.JaxbHelper;

public class HenleggBehandlingBrevMapper implements DokumentTypeMapper {
    private static final String FAMPEN = "NAV Familie- og pensjonsytelser";

    private static BehandlingsTypeKode mapToXmlBehandlingsType(String vlKode) {
        if (Objects.equals(vlKode, DokumentMapperKonstanter.ENDRINGSSØKNAD)) {
            return BehandlingsTypeKode.ENDRINGSSØKNAD;
        } else if (Objects.equals(vlKode, BehandlingType.FØRSTEGANGSSØKNAD.getKode())) {
            return BehandlingsTypeKode.FØRSTEGANGSSØKNAD;
        } else if (Objects.equals(vlKode, BehandlingType.KLAGE.getKode())) {
            return BehandlingsTypeKode.KLAGE;
        } else if (Objects.equals(vlKode, BehandlingType.REVURDERING.getKode())) {
            return BehandlingsTypeKode.REVURDERING;
        } else if (Objects.equals(vlKode, BehandlingType.INNSYN.getKode())) {
            return BehandlingsTypeKode.INNSYN;
        }
        throw DokumentMapperFeil.FACTORY.HenleggBehandlingBrevKreverGyldigBehandlingstype(vlKode).toException();
    }

    @Override
    public String mapTilBrevXML(FellesType fellesType, DokumentFelles dokumentFelles, DokumentHendelseDto hendelseDto, Behandling behandling) throws JAXBException {
        FagType fagType = mapFagType(hendelseDto, behandling);
        JAXBElement<BrevdataType> brevdataTypeJAXBElement = mapintoBrevdataType(fellesType, fagType);
        String brevXmlMedNamespace = JaxbHelper.marshalJaxb(AvbruttbehandlingConstants.JAXB_CLASS, brevdataTypeJAXBElement);
        return DokumentTypeFelles.fjernNamespaceFra(brevXmlMedNamespace);
    }

    private FagType mapFagType(DokumentHendelseDto hendelseDto, Behandling behandling) {
        FagType fagType = new FagType();
        fagType.setYtelseType(YtelseTypeKode.fromValue(hendelseDto.getYtelseType()));
        fagType.setBehandlingsType(BehandlingsTypeKode.fromValue(hendelseDto.getYtelseType()));
        fagType.setOpphavType(mapToXmlOpphavType(behandling.getBehandlendeEnhetNavn()));
        return fagType;
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
