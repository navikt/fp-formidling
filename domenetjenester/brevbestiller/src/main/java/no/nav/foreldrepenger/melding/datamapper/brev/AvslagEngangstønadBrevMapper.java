package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.domene.BehandlingMapper.avklarFritekst;
import static no.nav.foreldrepenger.melding.datamapper.mal.BehandlingTypeKonstanter.REVURDERING;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.xml.sax.SAXException;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.datamapper.DokumentMapperFeil;
import no.nav.foreldrepenger.melding.datamapper.DokumentTypeMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.domene.BehandlingMapper;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.fagsak.Fagsak;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avslag.AvslagConstants;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avslag.BehandlingstypeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avslag.BrevdataType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avslag.FagType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avslag.ObjectFactory;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avslag.RelasjonskodeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avslag.VilkaartypeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.personopplysning.NavBrukerKjønn;
import no.nav.foreldrepenger.melding.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.melding.vilkår.Vilkår;
import no.nav.foreldrepenger.melding.vilkår.VilkårType;
import no.nav.foreldrepenger.melding.vilkår.repository.VilkårKodeverkRepository;
import no.nav.vedtak.felles.integrasjon.felles.ws.JaxbHelper;

@ApplicationScoped
@Named(DokumentMalType.AVSLAGSVEDTAK_DOK)
public class AvslagEngangstønadBrevMapper extends DokumentTypeMapper {
    private static final Map<RelasjonsRolleType, RelasjonskodeType> relasjonskodeTypeMap;
    private static final Map<String, VilkaartypeType> vilkaartypeMap;

    static {
        relasjonskodeTypeMap = new HashMap<>();
        relasjonskodeTypeMap.put(RelasjonsRolleType.MORA, RelasjonskodeType.MOR);
        relasjonskodeTypeMap.put(RelasjonsRolleType.FARA, RelasjonskodeType.FAR);
        relasjonskodeTypeMap.put(RelasjonsRolleType.MEDMOR, RelasjonskodeType.MEDMOR);

        vilkaartypeMap = new HashMap<>();
        vilkaartypeMap.put(VilkårType.FP_VK_1, VilkaartypeType.FP_VK_1);
        vilkaartypeMap.put(VilkårType.FP_VK_2, VilkaartypeType.FP_VK_2);
        vilkaartypeMap.put(VilkårType.FP_VK_3, VilkaartypeType.FP_VK_3);
        vilkaartypeMap.put(VilkårType.FP_VK_4, VilkaartypeType.FP_VK_4);
        vilkaartypeMap.put(VilkårType.FP_VK_5, VilkaartypeType.FP_VK_5);
        vilkaartypeMap.put(VilkårType.FP_VK_8, VilkaartypeType.FP_VK_8);
        vilkaartypeMap.put(VilkårType.FP_VK_33, VilkaartypeType.FP_VK_33);
        vilkaartypeMap.put(VilkårType.FP_VK_34, VilkaartypeType.FP_VK_34);
    }

    private BrevParametere brevParametere;
    private VilkårKodeverkRepository vilkårKodeverkRepository;

    public AvslagEngangstønadBrevMapper() {
    }

    @Inject
    public AvslagEngangstønadBrevMapper(BrevParametere brevParametere,
                                        DomeneobjektProvider domeneobjektProvider,
                                        VilkårKodeverkRepository vilkårKodeverkRepository) {
        this.brevParametere = brevParametere;
        this.domeneobjektProvider = domeneobjektProvider;
        this.vilkårKodeverkRepository = vilkårKodeverkRepository;
    }

    @Override
    public String mapTilBrevXML(FellesType fellesType,
                                DokumentFelles dokumentFelles,
                                DokumentHendelse dokumentHendelse,
                                Behandling behandling) throws JAXBException, SAXException, XMLStreamException {
        FamilieHendelse familiehendelse = domeneobjektProvider.hentFamiliehendelse(behandling);
        Fagsak fagsak = domeneobjektProvider.hentFagsak(behandling);
        List<Vilkår> vilkår = domeneobjektProvider.hentVilkår(behandling);
        String behandlingstype = BehandlingMapper.utledBehandlingsTypeForAvslagVedtak(behandling, dokumentHendelse);
        FagType fagType = mapFagType(behandling, behandlingstype, dokumentHendelse, familiehendelse, vilkår, fagsak);
        JAXBElement<BrevdataType> brevdataTypeJAXBElement = mapintoBrevdataType(fellesType, fagType);
        return JaxbHelper.marshalNoNamespaceXML(AvslagConstants.JAXB_CLASS, brevdataTypeJAXBElement, null);
    }

    private FagType mapFagType(Behandling behandling,
                               String behandlingstype,
                               DokumentHendelse dokumentHendelse,
                               FamilieHendelse familiehendelse,
                               List<Vilkår> vilkårene,
                               Fagsak fagsak) {
        FagType fagType = new FagType();
        fagType.setBehandlingsType(REVURDERING.equals(behandlingstype) ? BehandlingstypeType.REVURDERING : BehandlingstypeType.SØKNAD);
        fagType.setRelasjonsKode(utledRelasjonsrolle(fagsak));
        fagType.setGjelderFoedsel(familiehendelse.isGjelderFødsel());
        fagType.setAntallBarn(familiehendelse.getAntallBarn().intValue());
        fagType.setAvslagsAarsak(behandling.getBehandlingsresultat().getAvslagsårsak().getKode());
        avklarFritekst(dokumentHendelse, behandling).ifPresent(fagType::setFritekst);
        fagType.setKlageFristUker(brevParametere.getKlagefristUker());
        fagType.setVilkaarType(utledVilkårTypeFra(vilkårene, fagType.getAvslagsAarsak()));
        return fagType;
    }

    static RelasjonskodeType utledRelasjonsrolle(Fagsak fagsak) {
        return tilRelasjonskodeType(fagsak.getRelasjonsRolleType(), fagsak.getPersoninfo().getKjønn());
    }

    static RelasjonskodeType tilRelasjonskodeType(RelasjonsRolleType brukerRolle, NavBrukerKjønn navBrukerKjønn) {
        if (RelasjonsRolleType.MORA.equals(brukerRolle)) {
            return RelasjonskodeType.MOR;
        } else if (NavBrukerKjønn.MANN.equals(navBrukerKjønn)) {
            return RelasjonskodeType.FAR;
        } else {
            return RelasjonskodeType.MEDMOR;
        }
    }

    private VilkaartypeType utledVilkårTypeFra(List<Vilkår> vilkårene, String avslagsÅrsakKode) {
        List<VilkårType> vilkårTyper = vilkårKodeverkRepository.finnVilkårTypeListe(avslagsÅrsakKode);
        return vilkårene.stream()
                .filter(v -> vilkårTyper.contains(v.getVilkårType()))
                .map(Vilkår::getVilkårType)
                .map(this::tilVilkaartypeType)
                .findFirst().orElseThrow(() -> new IllegalStateException("Fant ingen vilkår"));
    }

    private VilkaartypeType tilVilkaartypeType(VilkårType internVilkårType) {
        if (vilkaartypeMap.containsKey(internVilkårType.getKode())) {
            return vilkaartypeMap.get(internVilkårType.getKode());
        } else if ("FP_VK_6".equals(internVilkårType.getKode())) {
            return VilkaartypeType.FP_VK_6;
        } else if ("FP_VK_7".equals(internVilkårType.getKode())) {
            return VilkaartypeType.FP_VK_7;
        }
        throw DokumentMapperFeil.FACTORY.behandlingHarUkjentVilkårType(internVilkårType.getKode()).toException();
    }

    private JAXBElement<BrevdataType> mapintoBrevdataType(FellesType fellesType, FagType fagType) {
        ObjectFactory of = new ObjectFactory();
        BrevdataType brevdataType = of.createBrevdataType();
        brevdataType.setFag(fagType);
        brevdataType.setFelles(fellesType);
        return of.createBrevdata(brevdataType);
    }
}
