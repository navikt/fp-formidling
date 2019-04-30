package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.mal.BehandlingTypeKonstanter.REVURDERING;
import static no.nav.foreldrepenger.melding.datamapper.mal.BehandlingTypeKonstanter.SØKNAD;

import java.math.BigDecimal;
import java.math.BigInteger;
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
import no.nav.foreldrepenger.melding.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.melding.datamapper.DokumentTypeMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.domene.BehandlingMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.ÅrsakMapperOpphør;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.fagsak.Fagsak;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.opphor.AarsakListeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.opphor.OpphørConstants;
import no.nav.foreldrepenger.melding.integrasjon.dokument.opphor.BehandlingsTypeKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.opphor.BrevdataType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.opphor.FagType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.opphor.ObjectFactory;
import no.nav.foreldrepenger.melding.integrasjon.dokument.opphor.PersonstatusKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.opphor.RelasjonskodeKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPerioder;
import no.nav.vedtak.felles.integrasjon.felles.ws.JaxbHelper;
import no.nav.vedtak.util.Tuple;

@ApplicationScoped
@Named(DokumentMalType.OPPHØR_DOK)
public class OpphørbrevMapper implements DokumentTypeMapper {
    private static final Map<RelasjonsRolleType, RelasjonskodeKode> relasjonskodeTypeMap;

    static {
        relasjonskodeTypeMap = new HashMap<>();
        relasjonskodeTypeMap.put(RelasjonsRolleType.MORA, RelasjonskodeKode.MOR);
        relasjonskodeTypeMap.put(RelasjonsRolleType.FARA, RelasjonskodeKode.FAR);
        relasjonskodeTypeMap.put(RelasjonsRolleType.MEDMOR, RelasjonskodeKode.MEDMOR);
    }

    private BrevParametere brevParametere;
    private DomeneobjektProvider domeneobjektProvider;

    public OpphørbrevMapper() {
    }

    @Inject
    public OpphørbrevMapper(BrevParametere brevParametere,
                            DomeneobjektProvider domeneobjektProvider) {
        this.brevParametere = brevParametere;
        this.domeneobjektProvider = domeneobjektProvider;
    }

    @Override
    public String mapTilBrevXML(FellesType fellesType,
                                DokumentFelles dokumentFelles,
                                DokumentHendelse dokumentHendelse,
                                Behandling behandling) throws JAXBException, SAXException, XMLStreamException {
        FamilieHendelse familiehendelse = domeneobjektProvider.hentFamiliehendelse(behandling);
        Beregningsgrunnlag beregningsgrunnlag = domeneobjektProvider.hentBeregningsgrunnlag(behandling);
        UttakResultatPerioder uttakResultatPerioder = domeneobjektProvider.hentUttaksresultat(behandling);
        String behandlingstype = BehandlingMapper.utledBehandlingsTypeForAvslagVedtak(behandling);
        FagType fagType = mapFagType(behandling,
                behandlingstype,
                dokumentFelles,
                familiehendelse,
                beregningsgrunnlag,
                uttakResultatPerioder);
        JAXBElement<BrevdataType> brevdataTypeJAXBElement = mapintoBrevdataType(fellesType, fagType);
        return JaxbHelper.marshalNoNamespaceXML(OpphørConstants.JAXB_CLASS, brevdataTypeJAXBElement, null);
    }

    private FagType mapFagType(Behandling behandling,
                               String behandlingstypeKode,
                               DokumentFelles dokumentFelles,
                               FamilieHendelse familiehendelse,
                               Beregningsgrunnlag beregningsgrunnlag,
                               UttakResultatPerioder uttakResultatPerioder) {
        FagType fagType = new FagType();
        fagType.setBehandlingsType(fra(behandlingstypeKode));
        fagType.setSokersNavn(dokumentFelles.getSakspartNavn());
        fagType.setPersonstatus(PersonstatusKode.fromValue(dokumentFelles.getSakspartPersonStatus()));
        fagType.setRelasjonskode(fra(behandling.getFagsak()));
        fagType.setGjelderFoedsel(familiehendelse.isGjelderFødsel());
        fagType.setAntallBarn(familiehendelse.getAntallBarn());
        fagType.setHalvG(beregningsgrunnlag.getGrunnbeløp().getVerdi().divide(BigDecimal.valueOf(2)).longValue());
        fagType.setKlageFristUker(BigInteger.valueOf(brevParametere.getKlagefristUker()));

        mapFelterRelatertTilAvslagårsaker(behandling.getBehandlingsresultat(),
                uttakResultatPerioder,
                fagType);

        //TODO Ikke obligatoriske felter
//        finnOptionalDatoVerdiAvUtenTidSone(Flettefelt.STONADSDATO_FOM, dokumentTypeDataListe).ifPresent(fagType::setFomStonadsdato);
//        finnOptionalDatoVerdiAvUtenTidSone(Flettefelt.STONADSDATO_TOM, dokumentTypeDataListe).ifPresent(fagType::setTomStonadsdato);
//        finnOptionalDatoVerdiAvUtenTidSone(Flettefelt.OPPHORDATO, dokumentTypeDataListe).ifPresent(fagType::setOpphorDato);
//        finnOptionalDatoVerdiAvUtenTidSone(Flettefelt.DODSDATO, dokumentTypeDataListe).ifPresent(fagType::setDodsdato);
        return fagType;
    }

    private void mapFelterRelatertTilAvslagårsaker(Behandlingsresultat behandlingsresultat,
                                                   UttakResultatPerioder uttakResultatPerioder, FagType fagType) {
        Tuple<AarsakListeType, String> AarsakListeOgLovhjemmel = ÅrsakMapperOpphør.mapAarsakListeOgLovhjemmelFra(
                behandlingsresultat,
                uttakResultatPerioder);
        AarsakListeType aarsakListe = AarsakListeOgLovhjemmel.getElement1();

        fagType.setAntallAarsaker(BigInteger.valueOf(aarsakListe.getAvslagsAarsak().size()));
        fagType.setAarsakListe(aarsakListe);
        fagType.setLovhjemmelForAvslag(AarsakListeOgLovhjemmel.getElement2());
    }

    private RelasjonskodeKode fra(Fagsak fagsak) {
        if (RelasjonsRolleType.erRegistrertForeldre(fagsak.getRelasjonsRolleType())) {
            return relasjonskodeTypeMap.get(fagsak.getRelasjonsRolleType());
        }
        return RelasjonskodeKode.ANNET;
    }

    private BehandlingsTypeKode fra(String behandlingsType) {
        if (REVURDERING.equals(behandlingsType)) {
            return BehandlingsTypeKode.REVURDERING;
        }
        if (SØKNAD.equals(behandlingsType)) {
            return BehandlingsTypeKode.SØKNAD;
        }
        return BehandlingsTypeKode.FOERSTEGANGSBEHANDLING;
    }

    private JAXBElement<BrevdataType> mapintoBrevdataType(FellesType fellesType, FagType fagType) {
        ObjectFactory of = new ObjectFactory();
        BrevdataType brevdataType = of.createBrevdataType();
        brevdataType.setFag(fagType);
        brevdataType.setFelles(fellesType);
        return of.createBrevdata(brevdataType);
    }
}
