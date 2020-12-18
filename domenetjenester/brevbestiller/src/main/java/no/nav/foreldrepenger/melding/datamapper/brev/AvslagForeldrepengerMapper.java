package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.domene.BehandlingMapper.avklarFritekst;

import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.xml.sax.SAXException;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatFP;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.melding.datamapper.DokumentTypeMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.domene.BeregningsgrunnlagMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.MottattdokumentMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.ÅrsakMapperAvslag;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.fagsak.FagsakBackend;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avslag.foreldrepenger.AarsakListeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avslag.foreldrepenger.AvslagForeldrepengerConstants;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avslag.foreldrepenger.BrevdataType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avslag.foreldrepenger.FagType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avslag.foreldrepenger.ObjectFactory;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avslag.foreldrepenger.RelasjonskodeKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalTypeKode;
import no.nav.foreldrepenger.melding.mottattdokument.MottattDokument;
import no.nav.foreldrepenger.melding.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPerioder;
import no.nav.vedtak.felles.integrasjon.felles.ws.JaxbHelper;
import no.nav.vedtak.util.Tuple;

@ApplicationScoped
@Named(DokumentMalTypeKode.AVSLAG_FORELDREPENGER_DOK)
public class AvslagForeldrepengerMapper extends DokumentTypeMapper {
    private static final Map<RelasjonsRolleType, RelasjonskodeKode> relasjonskodeTypeMap;

    static {
        relasjonskodeTypeMap = new HashMap<>();
        relasjonskodeTypeMap.put(RelasjonsRolleType.MORA, RelasjonskodeKode.MOR);
        relasjonskodeTypeMap.put(RelasjonsRolleType.FARA, RelasjonskodeKode.FAR);
        relasjonskodeTypeMap.put(RelasjonsRolleType.MEDMOR, RelasjonskodeKode.MEDMOR);
    }

    private BrevParametere brevParametere;

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
        FagType fagType = mapFagType(behandling, dokumentHendelse);
        JAXBElement<BrevdataType> brevdataTypeJAXBElement = mapintoBrevdataType(fellesType, fagType);
        return JaxbHelper.marshalNoNamespaceXML(AvslagForeldrepengerConstants.JAXB_CLASS, brevdataTypeJAXBElement, null);
    }

    private FagType mapFagType(Behandling behandling, DokumentHendelse dokumentHendelse) {
        FamilieHendelse familiehendelse = domeneobjektProvider.hentFamiliehendelse(behandling);
        Optional<Beregningsgrunnlag> beregningsgrunnlagOpt = domeneobjektProvider.hentBeregningsgrunnlagHvisFinnes(behandling);
        Optional<BeregningsresultatFP> beregningsresultatFP = domeneobjektProvider.hentBeregningsresultatFPHvisFinnes(behandling);
        Optional<UttakResultatPerioder> uttakResultatPerioder = domeneobjektProvider.hentUttaksresultatHvisFinnes(behandling);
        long halvG = BeregningsgrunnlagMapper.getHalvGOrElseZero(beregningsgrunnlagOpt);
        FagsakBackend fagsak = domeneobjektProvider.hentFagsakBackend(behandling);

        List<MottattDokument> mottatteDokumenter = domeneobjektProvider.hentMottatteDokumenter(behandling);

        FagType fagType = new FagType();
        fagType.setRelasjonskode(fra(fagsak));
        fagType.setMottattDato(MottattdokumentMapper.finnSøknadsdatoFraMottatteDokumenterXml(behandling, mottatteDokumenter));
        fagType.setGjelderFoedsel(familiehendelse.isGjelderFødsel());
        fagType.setAntallBarn(familiehendelse.getAntallBarn());
        fagType.setBarnErFødt(familiehendelse.isBarnErFødt());
        fagType.setHalvG(halvG);
        fagType.setKlageFristUker(BigInteger.valueOf(brevParametere.getKlagefristUker()));

        mapFelterRelatertTilAvslagårsaker(behandling.getBehandlingsresultat(),
                beregningsresultatFP,
                uttakResultatPerioder,
                fagType);

        avklarFritekst(dokumentHendelse, behandling)
                .ifPresent(fagType::setFritekst);
        return fagType;
    }

    private void mapFelterRelatertTilAvslagårsaker(Behandlingsresultat behandlingsresultat,
                                                   Optional<BeregningsresultatFP> beregningsresultatFP,
                                                   Optional<UttakResultatPerioder> uttakResultatPerioder, FagType fagType) {
        Tuple<AarsakListeType, String> aarsakListeOgLovhjemmel = ÅrsakMapperAvslag.mapAarsakListeOgLovhjemmelFra(
                behandlingsresultat,
                beregningsresultatFP.map(BeregningsresultatFP::getBeregningsresultatPerioder).orElse(Collections.emptyList()),
                uttakResultatPerioder);
        AarsakListeType aarsakListe = aarsakListeOgLovhjemmel.getElement1();

        fagType.setAntallAarsaker(BigInteger.valueOf(aarsakListe.getAvslagsAarsak().size()));
        fagType.setAarsakListe(aarsakListe);
        fagType.setLovhjemmelForAvslag(aarsakListeOgLovhjemmel.getElement2());
    }

    private RelasjonskodeKode fra(FagsakBackend fagsak) {
        if (RelasjonsRolleType.erRegistrertForeldre(fagsak.getRelasjonsRolleType())) {
            return relasjonskodeTypeMap.get(fagsak.getRelasjonsRolleType());
        }
        return RelasjonskodeKode.ANNET;
    }

    private JAXBElement<BrevdataType> mapintoBrevdataType(FellesType fellesType, FagType fagType) {
        ObjectFactory of = new ObjectFactory();
        BrevdataType brevdataType = of.createBrevdataType();
        brevdataType.setFag(fagType);
        brevdataType.setFelles(fellesType);
        return of.createBrevdata(brevdataType);
    }
}
