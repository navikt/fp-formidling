package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelse.typeFødsel;
import static no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelse.typeTermin;

import java.util.HashMap;
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
import no.nav.foreldrepenger.melding.datamapper.domene.BehandlingMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.FamiliehendelseMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.VilkårMapper;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avslag.AvslagConstants;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avslag.BehandlingstypeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avslag.RelasjonskodeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avslag.VilkaartypeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avslag.BrevdataType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avslag.FagType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avslag.ObjectFactory;
import no.nav.foreldrepenger.melding.vilkår.Vilkår;
import no.nav.foreldrepenger.melding.vilkår.VilkårType;
import no.nav.vedtak.felles.integrasjon.felles.ws.JaxbHelper;

@ApplicationScoped
@Named(DokumentMalType.AVSLAGSVEDTAK_DOK)
public class AvslagEngangstønadBrevMapper implements DokumentTypeMapper {
    private static final Map<String, RelasjonskodeType> relasjonskodeTypeMap = new HashMap<>();
    private static final Map<String, VilkaartypeType> vilkaartypeMap = new HashMap<>();
    static {
        relasjonskodeTypeMap.put("MORA", RelasjonskodeType.MOR);
        relasjonskodeTypeMap.put("FARA", RelasjonskodeType.FAR);
        relasjonskodeTypeMap.put("MMOR", RelasjonskodeType.MEDMOR);

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
    private BehandlingMapper behandlingMapper;
    private FamiliehendelseMapper familiehendelseMapper;
    private VilkårMapper vilkårMapper;

    public AvslagEngangstønadBrevMapper() {
    }

    @Inject
    public AvslagEngangstønadBrevMapper(BrevParametere brevParametere,
                                        BehandlingMapper behandlingMapper,
                                        FamiliehendelseMapper familiehendelseMapper,
                                        VilkårMapper vilkårMapper) {
        this.brevParametere = brevParametere;
        this.behandlingMapper = behandlingMapper;
        this.familiehendelseMapper = familiehendelseMapper;
        this.vilkårMapper = vilkårMapper;
    }

    @Override
    public String mapTilBrevXML(FellesType fellesType,
                                DokumentFelles dokumentFelles,
                                DokumentHendelse dokumentHendelse,
                                Behandling behandling) throws JAXBException, SAXException, XMLStreamException {
        FamilieHendelse familiehendelse = familiehendelseMapper.hentFamiliehendelse(behandling);
        Vilkår vilkår = vilkårMapper.hentVilkår(behandling);
        BehandlingstypeType value = behandlingMapper.utledBehandlingsTypeAvslagES(behandling);
        FagType fagType = mapFagType(behandling, familiehendelse, vilkår, value);
        JAXBElement<BrevdataType> brevdataTypeJAXBElement = mapintoBrevdataType(fellesType, fagType);
        return JaxbHelper.marshalNoNamespaceXML(AvslagConstants.JAXB_CLASS, brevdataTypeJAXBElement, null);
    }

    private FagType mapFagType(Behandling behandling, FamilieHendelse familiehendelse, Vilkår vilkår, BehandlingstypeType value) {
        FagType fagType = new FagType();
        fagType.setBehandlingsType(value);
        fagType.setRelasjonsKode(relasjonskodeTypeMap.get(behandling.getFagsak().getRelasjonsRolleType()));
        fagType.setGjelderFoedsel(fra(familiehendelse));
        fagType.setAntallBarn(familiehendelse.getAntallBarn().intValue());
        fagType.setAvslagsAarsak(behandling.getBehandlingsresultat().getAvslagsårsak());
        fagType.setFritekst(behandling.getBehandlingsresultat().getAvslagarsakFritekst());
        fagType.setKlageFristUker(brevParametere.getKlagefristUker());
        fagType.setVilkaarType(fra(vilkår.getVilkårType()));
        return fagType;
    }

    private boolean fra(FamilieHendelse familiehendelse) {
        String familieHendelseType = familiehendelse.getFamilieHendelseType();
        return typeFødsel.equals(familieHendelseType) || typeTermin.equals(familieHendelseType);
    }

    private VilkaartypeType fra(String internVilkårType) {
        if (vilkaartypeMap.containsKey(internVilkårType)) {
            return vilkaartypeMap.get(internVilkårType);
        } else if ("FP_VK_6".equals(internVilkårType)) {
            return VilkaartypeType.FP_VK_6;
        } else if ("FP_VK_7".equals(internVilkårType)) {
            return VilkaartypeType.FP_VK_7;
        }
        throw DokumentMapperFeil.FACTORY.behandlingHarUkjentVilkårType(internVilkårType).toException();
    }

    private JAXBElement<BrevdataType> mapintoBrevdataType(FellesType fellesType, FagType fagType) {
        ObjectFactory of = new ObjectFactory();
        BrevdataType brevdataType = of.createBrevdataType();
        brevdataType.setFag(fagType);
        brevdataType.setFelles(fellesType);
        return of.createBrevdata(brevdataType);
    }
}
