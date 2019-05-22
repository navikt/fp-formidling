package no.nav.foreldrepenger.melding.datamapper.brev;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.stream.XMLStreamException;

import org.xml.sax.SAXException;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.brevbestiller.XmlUtil;
import no.nav.foreldrepenger.melding.datamapper.BrevMapperUtil;
import no.nav.foreldrepenger.melding.datamapper.DokumentMapperFeil;
import no.nav.foreldrepenger.melding.datamapper.DokumentTypeMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.domene.BehandlingMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.InnhentingMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.MottattdokumentMapper;
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
import no.nav.foreldrepenger.melding.klage.KlageDokument;
import no.nav.foreldrepenger.melding.mottattdokument.MottattDokument;
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
    private DomeneobjektProvider domeneobjektProvider;

    public InnhentOpplysningerBrevMapper() {
        //CDI
    }

    @Inject
    public InnhentOpplysningerBrevMapper(BrevParametere brevParametere,
                                         DomeneobjektProvider domeneobjektProvider) {
        this.brevParametere = brevParametere;
        this.domeneobjektProvider = domeneobjektProvider;
    }

    @Override
    public String mapTilBrevXML(FellesType fellesType, DokumentFelles dokumentFelles, DokumentHendelse dokumentHendelse, Behandling behandling) throws JAXBException, SAXException, XMLStreamException {
        List<MottattDokument> mottattDokumenter = domeneobjektProvider.hentMottatteDokumenter(behandling);
        Optional<KlageDokument> klageDokument = Optional.empty();
        if (BehandlingType.KLAGE.equals(behandling.getBehandlingType())) {
            klageDokument = Optional.of(domeneobjektProvider.hentKlageDokument(behandling));
        }
        FagType fagType = mapFagType(dokumentFelles, dokumentHendelse, behandling, mottattDokumenter, klageDokument);
        JAXBElement<BrevdataType> brevdataTypeJAXBElement = mapintoBrevdataType(fellesType, fagType);
        return JaxbHelper.marshalNoNamespaceXML(InnhentopplysningerConstants.JAXB_CLASS, brevdataTypeJAXBElement, null);
    }

    private FagType mapFagType(DokumentFelles dokumentFelles, DokumentHendelse dokumentHendelse, Behandling behandling, List<MottattDokument> mottatteDokumenter, Optional<KlageDokument> klageDokument) {
        FagType fagType = new FagType();
        fagType.setBehandlingsType(mapBehandlingType(behandling));
        fagType.setYtelseType(YtelseTypeKode.fromValue(dokumentHendelse.getYtelseType().getKode()));
        fagType.setSoknadDato(klageDokument.map(this::hentMottattDatoFraKlage).orElse(MottattdokumentMapper.finnSøknadsDatoFraMottatteDokumenter(behandling, mottatteDokumenter)));
        fagType.setFristDato(XmlUtil.finnDatoVerdiAvUtenTidSone(BrevMapperUtil.getSvarFrist(brevParametere)));
        fagType.setPersonstatus(PersonstatusKode.fromValue(dokumentFelles.getSakspartPersonStatus()));
        fagType.setSokersNavn(dokumentFelles.getSakspartNavn());
        fagType.setFritekst(dokumentHendelse.getFritekst());
        return fagType;
    }

    private XMLGregorianCalendar hentMottattDatoFraKlage(KlageDokument klageDokument) {
        return XmlUtil.finnDatoVerdiAvUtenTidSone(klageDokument.getMottattDato());
    }

    private BehandlingsTypeKode mapBehandlingType(Behandling behandling) {
        String behandlingTypeKode = BehandlingMapper.finnBehandlingTypeForDokument(behandling);
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
