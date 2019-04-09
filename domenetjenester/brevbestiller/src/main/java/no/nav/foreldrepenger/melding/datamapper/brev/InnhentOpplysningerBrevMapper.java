package no.nav.foreldrepenger.melding.datamapper.brev;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.xml.sax.SAXException;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.brevbestiller.XmlUtil;
import no.nav.foreldrepenger.melding.datamapper.BrevMapperUtil;
import no.nav.foreldrepenger.melding.datamapper.DokumentMapperFeil;
import no.nav.foreldrepenger.melding.datamapper.DokumentTypeMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.BehandlingMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.InnhentingMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.SøknadMapper;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.datamapper.mal.BehandlingTypeKonstanter;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innhentopplysninger.BehandlingsTypeKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innhentopplysninger.BrevdataType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innhentopplysninger.FagType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innhentopplysninger.InnhentopplysningerConstants;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innhentopplysninger.ObjectFactory;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innhentopplysninger.PersonstatusKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innhentopplysninger.YtelseTypeKode;
import no.nav.foreldrepenger.melding.søknad.Søknad;
import no.nav.vedtak.felles.integrasjon.felles.ws.JaxbHelper;

@ApplicationScoped
@Named(DokumentMalType.INNHENT_DOK)
public class InnhentOpplysningerBrevMapper implements DokumentTypeMapper {

    private static final Set<String> gyldigeKoder = new HashSet<>(Arrays.asList(
            BehandlingType.REVURDERING.getKode(),
            BehandlingType.KLAGE.getKode(),
            BehandlingType.FØRSTEGANGSSØKNAD.getKode(),
            BehandlingTypeKonstanter.ENDRINGSSØKNAD
    ));

    private BrevParametere brevParametere;
    private BehandlingMapper behandlingMapper;
    private SøknadMapper søknadMapper;

    public InnhentOpplysningerBrevMapper() {
        //CDI
    }

    @Inject
    public InnhentOpplysningerBrevMapper(BrevParametere brevParametere,
                                         BehandlingMapper behandlingMapper,
                                         SøknadMapper søknadMapper) {
        this.brevParametere = brevParametere;
        this.behandlingMapper = behandlingMapper;
        this.søknadMapper = søknadMapper;
    }

    @Override
    public String mapTilBrevXML(FellesType fellesType, DokumentFelles dokumentFelles, DokumentHendelse dokumentHendelse, Behandling behandling) throws JAXBException, SAXException, XMLStreamException {
        Søknad søknad = søknadMapper.hentSøknad(behandling);
        FagType fagType = mapFagType(dokumentFelles, dokumentHendelse, behandling, søknad);
        JAXBElement<BrevdataType> brevdataTypeJAXBElement = mapintoBrevdataType(fellesType, fagType);
        return JaxbHelper.marshalNoNamespaceXML(InnhentopplysningerConstants.JAXB_CLASS, brevdataTypeJAXBElement, null);
    }

    private FagType mapFagType(DokumentFelles dokumentFelles, DokumentHendelse dokumentHendelse, Behandling behandling, Søknad søknad) {
        FagType fagType = new FagType();
        fagType.setBehandlingsType(mapBehandlingType(behandling));
        fagType.setYtelseType(YtelseTypeKode.fromValue(dokumentHendelse.getYtelseType().getKode()));
        fagType.setSoknadDato(XmlUtil.finnDatoVerdiAvUtenTidSone(søknad.getSøknadsdato()));
        fagType.setFristDato(XmlUtil.finnDatoVerdiAvUtenTidSone(BrevMapperUtil.getSvarFrist(brevParametere)));
        fagType.setPersonstatus(PersonstatusKode.fromValue(dokumentFelles.getSakspartPersonStatus()));
        fagType.setSokersNavn(dokumentFelles.getSakspartNavn());
        fagType.setFritekst(dokumentHendelse.getFritekst());
        return fagType;
    }

    private BehandlingsTypeKode mapBehandlingType(Behandling behandling) {
        String behandlingTypeKode = behandlingMapper.finnBehandlingTypeForDokument(behandling);
        if (!gyldigeKoder.contains(behandlingTypeKode)) {
            throw DokumentMapperFeil.FACTORY.innhentDokumentasjonKreverGyldigBehandlingstype(behandlingTypeKode).toException();
        }
        return InnhentingMapper.mapToXmlBehandlingsType(behandlingTypeKode);
    }

    private JAXBElement<BrevdataType> mapintoBrevdataType(FellesType fellesType, FagType fagType) {
        ObjectFactory of = new ObjectFactory();
        BrevdataType brevdataType = of.createBrevdataType();
        brevdataType.setFag(fagType);
        brevdataType.setFelles(fellesType);
        return of.createBrevdata(brevdataType);
    }
}
