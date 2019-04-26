package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.DokumentTypeFelles.finnVerdiAv;
import static no.nav.foreldrepenger.melding.datamapper.domene.BehandlingMapper.avklarFritekst;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.stream.XMLStreamException;

import org.xml.sax.SAXException;

import no.nav.foreldrepenger.melding.aktør.NavBrukerKjønn;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingResultatType;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatFP;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.melding.brevbestiller.XmlUtil;
import no.nav.foreldrepenger.melding.datamapper.DokumentTypeMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.domene.BehandlingMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.BeregningsgrunnlagMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.BeregningsresultatMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.FellesMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.UttakMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeVerktøy;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentTypeData;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.BeregningsgrunnlagRegelListeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.BrevdataType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.FagType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.InnvilgetForeldrepengerConstants;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.LovhjemmelType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.ObjectFactory;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.PeriodeListeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.PersonstatusKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.RelasjonskodeKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.VurderingsstatusKode;
import no.nav.foreldrepenger.melding.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.melding.søknad.Søknad;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPerioder;
import no.nav.foreldrepenger.melding.ytelsefordeling.YtelseFordeling;
import no.nav.vedtak.felles.integrasjon.felles.ws.JaxbHelper;

@ApplicationScoped
@Named(DokumentMalType.INNVILGELSE_FORELDREPENGER_DOK)
public class InnvilgelseForeldrepengerMapper implements DokumentTypeMapper {

    private ObjectFactory objectFactory = new ObjectFactory();
    private BrevParametere brevParametere;
    private DomeneobjektProvider domeneobjektProvider;

    public InnvilgelseForeldrepengerMapper() {
        //CDI
    }

    @Inject
    public InnvilgelseForeldrepengerMapper(DomeneobjektProvider domeneobjektProvider,
                                           BrevParametere brevParametere) {
        this.brevParametere = brevParametere;
        this.domeneobjektProvider = domeneobjektProvider;
    }

    @Override
    public String mapTilBrevXML(FellesType fellesType,
                                DokumentFelles dokumentFelles,
                                DokumentHendelse dokumentHendelse,
                                Behandling behandling) throws JAXBException, SAXException, XMLStreamException {
        //TODO - Burde vi lage et wrapper objekt for inputobjektene når det er så mange??
        UttakResultatPerioder uttakResultatPerioder = domeneobjektProvider.hentUttaksresultat(behandling);
        BeregningsresultatFP beregningsresultatFP = domeneobjektProvider.hentBeregningsresultatFP(behandling);
        Beregningsgrunnlag beregningsgrunnlag = domeneobjektProvider.hentBeregningsgrunnlag(behandling);
        Behandling originalBehandling;
        Beregningsgrunnlag originaltBeregningsgrunnlag = null;
        if (behandling.getOriginalBehandlingId() != null) {
            originalBehandling = domeneobjektProvider.hentBehandling(behandling.getOriginalBehandlingId());
            originaltBeregningsgrunnlag = domeneobjektProvider.hentBeregningsgrunnlag(originalBehandling);
        }
        FamilieHendelse familieHendelse = domeneobjektProvider.hentFamiliehendelse(behandling);
        Søknad søknad = domeneobjektProvider.hentSøknad(behandling);
        YtelseFordeling ytelseFordeling = domeneobjektProvider.hentYtelseFordeling(behandling);
        FagType fagType = mapFagType(dokumentHendelse,
                behandling,
                beregningsresultatFP,
                familieHendelse,
                beregningsgrunnlag,
                originaltBeregningsgrunnlag,
                Collections.emptyList(),
                søknad,
                dokumentFelles,
                uttakResultatPerioder,
                ytelseFordeling);
        JAXBElement<BrevdataType> brevdataTypeJAXBElement = mapintoBrevdataType(fellesType, fagType);
        return JaxbHelper.marshalNoNamespaceXML(InnvilgetForeldrepengerConstants.JAXB_CLASS, brevdataTypeJAXBElement, InnvilgetForeldrepengerConstants.XSD_LOCATION);
    }

    private FagType mapFagType(DokumentHendelse dokumentHendelse,
                               Behandling behandling,
                               BeregningsresultatFP beregningsresultatFP,
                               FamilieHendelse familieHendelse,
                               Beregningsgrunnlag beregningsgrunnlag,
                               Beregningsgrunnlag originaltBeregningsgrunnlag,
                               List<DokumentTypeData> dokumentTypeDataListe,
                               Søknad søknad,
                               DokumentFelles dokumentFelles,
                               UttakResultatPerioder uttakResultatPerioder,
                               YtelseFordeling ytelseFordeling) {
        final FagType fagType = objectFactory.createFagType();

        fagType.setSokersNavn(dokumentFelles.getSakspartNavn());
        fagType.setRelasjonskode(tilRelasjonskode(behandling.getRelasjonsRolleType(), behandling.getPersonopplysning().getNavBrukerKjonn()));
        fagType.setPersonstatus(PersonstatusKode.fromValue(dokumentFelles.getSakspartPersonStatus()));
        fagType.setKlageFristUker(BigInteger.valueOf(brevParametere.getKlagefristUker()));
        fagType.setBehandlingsResultat(BehandlingMapper.tilBehandlingsResultatKode(behandling.getBehandlingsresultat().getBehandlingResultatType()));
        String konsekvensForYtelsen = BehandlingMapper.kodeFra(behandling.getBehandlingsresultat().getKonsekvenserForYtelsen());
        fagType.setKonsekvensForYtelse(BehandlingMapper.finnKonsekvensForYtelseKode(konsekvensForYtelsen));
        avklarFritekst(dokumentHendelse, behandling).ifPresent(fagType::setFritekst);
        fagType.setDekningsgrad(BigInteger.valueOf(ytelseFordeling.getDekningsgrad().getVerdi()));

        mapFelterRelatertTilBehandling(behandling, fagType);
        mapFelterRelatertTilBeregningsgrunnlag(beregningsgrunnlag, originaltBeregningsgrunnlag, fagType);
        mapFelterRelatertTilPerioder(beregningsresultatFP, beregningsgrunnlag, uttakResultatPerioder, fagType, behandling);
        mapFelterRelatertTilStønadskontoer(fagType);
        mapFelterRelatertTilFamiliehendelse(familieHendelse, fagType);
        mapFelterRelatertTilSøknad(søknad, fagType);
        mapLovhjemmel(dokumentTypeDataListe, fagType, beregningsgrunnlag, konsekvensForYtelsen, behandling);
        return fagType;
    }

    private void mapLovhjemmel(List<DokumentTypeData> dokumentTypeDataListe,
                               FagType fagType,
                               Beregningsgrunnlag beregningsgrunnlag, String konsekvensForYtelsen,
                               Behandling behandling) {
        LovhjemmelType lovhjemmelType = objectFactory.createLovhjemmelType();
        boolean revurdering = BehandlingType.REVURDERING.equals(behandling.getBehandlingType());
        boolean innvilget = BehandlingResultatType.INNVILGET.equals(behandling.getBehandlingsresultat().getBehandlingResultatType());
        boolean innvilgetRevurdering = revurdering && innvilget;
        lovhjemmelType.setBeregning(FellesMapper.formaterLovhjemlerForBeregning(beregningsgrunnlag.getHjemmel().getNavn(), konsekvensForYtelsen, innvilgetRevurdering));
        lovhjemmelType.setVurdering(finnVerdiAv("PLACEHOLDER", dokumentTypeDataListe)); /* TODO Må Mappes */
        fagType.setLovhjemmel(lovhjemmelType);
    }

    private void mapFelterRelatertTilSøknad(Søknad søknad, FagType fagType) {
        fagType.setMottattDato(XmlUtil.finnDatoVerdiAvUtenTidSone(søknad.getMottattDato()));
        //Disse ser også på perioder
        fagType.setAnnenForelderHarRett(Boolean.parseBoolean(finnVerdiAv("PLACEHOLDER", Collections.emptyList())));
        fagType.setAleneomsorg(VurderingsstatusKode.fromValue("IKKE_VURDERT"));
    }

    private void mapFelterRelatertTilStønadskontoer(FagType fagType) {
        fagType.setDagerTaptFørTermin(BigInteger.valueOf(0)); /*TODO Mangler Stønadskonto - Finnes et endepunkt for dette*/
        fagType.setDisponibleDager(BigInteger.valueOf(0));/*TODO Bruker Stønadskonto*/
        fagType.setDisponibleFellesDager(BigInteger.valueOf(0))/*TODO Bruker Stønadskonto*/;
    }

    private void mapFelterRelatertTilPerioder(BeregningsresultatFP beregningsresultatFP, Beregningsgrunnlag beregningsgrunnlag, UttakResultatPerioder uttakResultatPerioder, FagType fagType, Behandling behandling) {
        //Match, Map, merge - Blæh
        fagType.setAntallArbeidsgivere(BeregningsresultatMapper.antallArbeidsgivere(beregningsresultatFP));
        PeriodeListeType periodeListe = BeregningsresultatMapper.mapPeriodeListe(beregningsresultatFP.getBeregningsresultatPerioder(), uttakResultatPerioder, beregningsgrunnlag.getBeregningsgrunnlagPerioder());
        fagType.setPeriodeListe(periodeListe);
        fagType.setAntallPerioder(BigInteger.valueOf(periodeListe.getPeriode().size()));
        fagType.setTotalArbeidsgiverAndel(BeregningsresultatMapper.finnTotalArbeidsgiverAndel(beregningsresultatFP));
        fagType.setTotalBrukerAndel(BeregningsresultatMapper.finnTotalBrukerAndel(beregningsresultatFP));
        fagType.setAntallAvslag(BeregningsresultatMapper.tellAntallAvslag(periodeListe));
        fagType.setAntallInnvilget(BeregningsresultatMapper.tellAntallInnvilget(periodeListe));
        fagType.setGraderingFinnes(PeriodeVerktøy.graderingFinnes(periodeListe));
        fagType.setStønadsperiodeFom(BeregningsresultatMapper.finnStønadsperiodeFom(periodeListe));
        XMLGregorianCalendar sisteInnvilgedeDag = BeregningsresultatMapper.finnStønadsperiodeTom(periodeListe);
        fagType.setStønadsperiodeTom(sisteInnvilgedeDag);
        fagType.setSisteDagAvSistePeriode(sisteInnvilgedeDag);
        if (sisteInnvilgedeDag != null) {
            fagType.setSisteUtbetalingsdag(sisteInnvilgedeDag);
        }
        fagType.setIkkeOmsorg(UttakMapper.finnesPeriodeMedIkkeOmsorg(periodeListe.getPeriode()));
        fagType.setForMyeUtbetalt(UttakMapper.forMyeUtbetaltKode(periodeListe, behandling));
        UttakMapper.finnSisteDagIFelleseriodeHvisFinnes(uttakResultatPerioder).ifPresent(fagType::setSisteDagIFellesPeriode);
        UttakMapper.finnSisteDagMedUtsettelseHvisFinnes(uttakResultatPerioder).ifPresent(fagType::setSisteDagMedUtsettelse);
        //Optional.empty().ifPresent(fagType::setForeldrepengeperiodenUtvidetUker); // TODO aleksander Mangler Tjeneste som regner ut dette
    }

    private void mapFelterRelatertTilBehandling(Behandling behandling, FagType fagType) {
        fagType.setBehandlingsType(BehandlingMapper.utledBehandlingsTypeInnvilgetFP(behandling));
        fagType.setFødselsHendelse(BehandlingMapper.erRevurderingPgaFødselshendelse(behandling));
        fagType.setInntektMottattArbgiver(BehandlingMapper.erEndringMedEndretInntektsmelding(behandling));
    }

    private void mapFelterRelatertTilBeregningsgrunnlag(Beregningsgrunnlag beregningsgrunnlag, Beregningsgrunnlag originaltBeregningsgrunnlag, FagType fagType) {
        fagType.setOverbetaling(BeregningsgrunnlagMapper.erOverbetalt(beregningsgrunnlag, originaltBeregningsgrunnlag));
        fagType.setBruttoBeregningsgrunnlag(BeregningsgrunnlagMapper.finnBrutto(beregningsgrunnlag));
        fagType.setDagsats(BeregningsgrunnlagMapper.finnDagsats(beregningsgrunnlag));
        BeregningsgrunnlagRegelListeType beregningsgrunnlagRegelListe = BeregningsgrunnlagMapper.mapRegelListe(beregningsgrunnlag);
        fagType.setBeregningsgrunnlagRegelListe(beregningsgrunnlagRegelListe);
        fagType.setAntallBeregningsgrunnlagRegeler(BigInteger.valueOf(beregningsgrunnlagRegelListe.getBeregningsgrunnlagRegel().size()));
        fagType.setMånedsbeløp(BeregningsgrunnlagMapper.finnMånedsbeløp(beregningsgrunnlag));
        fagType.setSeksG(BeregningsgrunnlagMapper.finnSeksG(beregningsgrunnlag).longValue());
        fagType.setInntektOverSeksG(BeregningsgrunnlagMapper.inntektOverSeksG(beregningsgrunnlag));
    }

    private void mapFelterRelatertTilFamiliehendelse(FamilieHendelse familieHendelse, FagType fagType) {
        fagType.setAntallBarn(familieHendelse.getAntallBarn());
        fagType.setBarnErFødt(familieHendelse.isBarnErFødt());
        fagType.setGjelderFoedsel(familieHendelse.isGjelderFødsel());
    }

    private JAXBElement<BrevdataType> mapintoBrevdataType(FellesType fellesType, FagType fagType) {
        BrevdataType brevdataType = objectFactory.createBrevdataType();
        brevdataType.setFag(fagType);
        brevdataType.setFelles(fellesType);
        return objectFactory.createBrevdata(brevdataType);
    }


    private RelasjonskodeKode tilRelasjonskode(RelasjonsRolleType brukerRolle, NavBrukerKjønn navBrukerKjønn) {
        if (RelasjonsRolleType.MORA.equals(brukerRolle)) {
            return RelasjonskodeKode.MOR;
        } else if (NavBrukerKjønn.MANN.equals(navBrukerKjønn)) {
            return RelasjonskodeKode.FAR;
        } else {
            return RelasjonskodeKode.MEDMOR;
        }
    }
}
