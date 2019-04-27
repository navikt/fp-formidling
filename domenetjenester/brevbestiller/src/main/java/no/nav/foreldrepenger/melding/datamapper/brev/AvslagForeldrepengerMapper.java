package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.domene.BehandlingMapper.avklarFritekst;
import static no.nav.foreldrepenger.melding.datamapper.mal.BehandlingTypeKonstanter.REVURDERING;
import static no.nav.foreldrepenger.melding.datamapper.mal.BehandlingTypeKonstanter.SØKNAD;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
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
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatFP;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.melding.brevbestiller.XmlUtil;
import no.nav.foreldrepenger.melding.datamapper.DokumentTypeMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.domene.BehandlingMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.UttakMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.AvslagsårsakMapper;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.fagsak.Fagsak;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avslag.foreldrepenger.AarsakListeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avslag.foreldrepenger.AvslagForeldrepengerConstants;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avslag.foreldrepenger.BehandlingsTypeKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avslag.foreldrepenger.BrevdataType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avslag.foreldrepenger.FagType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avslag.foreldrepenger.ObjectFactory;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avslag.foreldrepenger.PersonstatusKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avslag.foreldrepenger.RelasjonskodeKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPerioder;
import no.nav.vedtak.felles.integrasjon.felles.ws.JaxbHelper;

@ApplicationScoped
@Named(DokumentMalType.AVSLAG_FORELDREPENGER_DOK)
public class AvslagForeldrepengerMapper implements DokumentTypeMapper {
    private static final Map<RelasjonsRolleType, RelasjonskodeKode> relasjonskodeTypeMap;

    static {
        relasjonskodeTypeMap = new HashMap<>();
        relasjonskodeTypeMap.put(RelasjonsRolleType.MORA, RelasjonskodeKode.MOR);
        relasjonskodeTypeMap.put(RelasjonsRolleType.FARA, RelasjonskodeKode.FAR);
        relasjonskodeTypeMap.put(RelasjonsRolleType.MEDMOR, RelasjonskodeKode.MEDMOR);
    }

    private BrevParametere brevParametere;
    private DomeneobjektProvider domeneobjektProvider;

    public AvslagForeldrepengerMapper() {
    }

    @Inject
    public AvslagForeldrepengerMapper(BrevParametere brevParametere,
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
        BeregningsresultatFP beregningsresultatFP = domeneobjektProvider.hentBeregningsresultatFP(behandling);
        UttakResultatPerioder uttakResultatPerioder = domeneobjektProvider.hentUttaksresultat(behandling);
        String behandlingstype = BehandlingMapper.utledBehandlingsTypeForAvslagVedtak(behandling);
        FagType fagType = mapFagType(behandling,
                behandlingstype,
                dokumentFelles,
                dokumentHendelse,
                familiehendelse,
                beregningsgrunnlag,
                beregningsresultatFP,
                uttakResultatPerioder);
        JAXBElement<BrevdataType> brevdataTypeJAXBElement = mapintoBrevdataType(fellesType, fagType);
        return JaxbHelper.marshalNoNamespaceXML(AvslagForeldrepengerConstants.JAXB_CLASS, brevdataTypeJAXBElement, null);
    }

    private FagType mapFagType(Behandling behandling,
                               String behandlingstypeKode,
                               DokumentFelles dokumentFelles,
                               DokumentHendelse dokumentHendelse,
                               FamilieHendelse familiehendelse,
                               Beregningsgrunnlag beregningsgrunnlag,
                               BeregningsresultatFP beregningsresultatFP,
                               UttakResultatPerioder uttakResultatPerioder) {

        FagType fagType = new FagType();
        fagType.setBehandlingsType(fra(behandlingstypeKode));
        fagType.setSokersNavn(dokumentFelles.getSakspartNavn());
        fagType.setPersonstatus(PersonstatusKode.fromValue(dokumentFelles.getSakspartPersonStatus()));
        fagType.setRelasjonskode(fra(behandling.getFagsak()));
        // TODO mangler denne verdien..
        fagType.setMottattDato(XmlUtil.finnDatoVerdiAvUtenTidSone(LocalDate.now()));
        fagType.setGjelderFoedsel(familiehendelse.isGjelderFødsel());
        fagType.setAntallBarn(familiehendelse.getAntallBarn());
        fagType.setBarnErFødt(familiehendelse.isBarnErFødt());
        fagType.setHalvG(beregningsgrunnlag.getGrunnbeløp().getVerdi().divide(BigDecimal.valueOf(2)).longValue());
        UttakMapper.finnSisteDagIFelleseriodeHvisFinnes(uttakResultatPerioder).ifPresent(fagType::setSisteDagIFellesPeriode);
        AarsakListeType aarsakListe = AvslagsårsakMapper.mapAarsakListeFra(behandling.getBehandlingsresultat(),
                beregningsresultatFP.getBeregningsresultatPerioder(),
                uttakResultatPerioder);
        fagType.setAntallAarsaker(BigInteger.valueOf(aarsakListe.getAvslagsAarsak().size()));
        fagType.setAarsakListe(aarsakListe);
        fagType.setKlageFristUker(BigInteger.valueOf(brevParametere.getKlagefristUker()));
        // TODO ..
        //fagType.setLovhjemmelForAvslag();
        avklarFritekst(dokumentHendelse, behandling).ifPresent(fagType::setFritekst);
        return fagType;
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
