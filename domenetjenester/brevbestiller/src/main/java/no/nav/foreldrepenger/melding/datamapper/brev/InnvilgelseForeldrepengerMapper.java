package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.domene.BehandlingMapper.avklarFritekst;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.xml.sax.SAXException;

import no.nav.foreldrepenger.melding.aksjonspunkt.Aksjonspunkt;
import no.nav.foreldrepenger.melding.aksjonspunkt.AksjonspunktDefinisjon;
import no.nav.foreldrepenger.melding.aksjonspunkt.AksjonspunktStatus;
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
import no.nav.foreldrepenger.melding.datamapper.domene.StønadskontoMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.UttakMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeVerktøy;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.fagsak.Fagsak;
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
import no.nav.foreldrepenger.melding.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.melding.søknad.Søknad;
import no.nav.foreldrepenger.melding.uttak.Saldoer;
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
        UttakResultatPerioder uttakResultatPerioder = domeneobjektProvider.hentUttaksresultat(behandling);
        BeregningsresultatFP beregningsresultatFP = domeneobjektProvider.hentBeregningsresultatFP(behandling);
        Beregningsgrunnlag beregningsgrunnlag = domeneobjektProvider.hentBeregningsgrunnlag(behandling);
        FamilieHendelse familieHendelse = domeneobjektProvider.hentFamiliehendelse(behandling);
        Søknad søknad = hentSøknadUansett(behandling);
        Optional<FamilieHendelse> originalFamiliehendelse = domeneobjektProvider.hentOriginalBehandlingHvisFinnes(behandling).map(domeneobjektProvider::hentFamiliehendelse);
        YtelseFordeling ytelseFordeling = domeneobjektProvider.hentYtelseFordeling(behandling);
        Saldoer saldoer = domeneobjektProvider.hentSaldoer(behandling);
        List<Aksjonspunkt> aksjonspunkter = domeneobjektProvider.hentAksjonspunkter(behandling);
        Fagsak fagsak = domeneobjektProvider.hentFagsak(behandling);
        FagType fagType = mapFagType(dokumentHendelse,
                behandling,
                beregningsresultatFP,
                familieHendelse,
                originalFamiliehendelse,
                beregningsgrunnlag,
                søknad,
                dokumentFelles,
                uttakResultatPerioder,
                ytelseFordeling,
                saldoer,
                aksjonspunkter,
                fagsak);
        JAXBElement<BrevdataType> brevdataTypeJAXBElement = mapintoBrevdataType(fellesType, fagType);
        return JaxbHelper.marshalNoNamespaceXML(InnvilgetForeldrepengerConstants.JAXB_CLASS, brevdataTypeJAXBElement, null);
    }

    Søknad hentSøknadUansett(Behandling behandling) {
        int maxForsøk = 20;
        int nåværendeForsøk = 0;
        Optional<Søknad> søknad = Optional.empty();
        Behandling nåværendeBehandling = behandling;
        while (søknad.isEmpty() && nåværendeForsøk < maxForsøk) {
            søknad = domeneobjektProvider.hentSøknad(nåværendeBehandling);
            if (søknad.isEmpty()) {
                Behandling nesteBehandling = domeneobjektProvider.hentOriginalBehandlingHvisFinnes(nåværendeBehandling).orElseThrow(IllegalStateException::new);
                if (nåværendeBehandling.getId() == nesteBehandling.getId()) {
                    throw new IllegalStateException();
                }
                nåværendeBehandling = nesteBehandling;
            }
            nåværendeForsøk++;
        }
        return søknad.orElseThrow(IllegalStateException::new);
    }

    private FagType mapFagType(DokumentHendelse dokumentHendelse,
                               Behandling behandling,
                               BeregningsresultatFP beregningsresultatFP,
                               FamilieHendelse familieHendelse,
                               Optional<FamilieHendelse> originalFamiliehendelse,
                               Beregningsgrunnlag beregningsgrunnlag,
                               Søknad søknad,
                               DokumentFelles dokumentFelles,
                               UttakResultatPerioder uttakResultatPerioder,
                               YtelseFordeling ytelseFordeling,
                               Saldoer saldoer,
                               List<Aksjonspunkt> aksjonspunkter,
                               Fagsak fagsak) {
        final FagType fagType = objectFactory.createFagType();

        fagType.setSokersNavn(dokumentFelles.getSakspartNavn());
        fagType.setRelasjonskode(tilRelasjonskode(fagsak.getRelasjonsRolleType(), fagsak.getPersoninfo().getKjønn()));
        fagType.setPersonstatus(PersonstatusKode.fromValue(dokumentFelles.getSakspartPersonStatus()));
        fagType.setKlageFristUker(BigInteger.valueOf(brevParametere.getKlagefristUker()));
        fagType.setBehandlingsResultat(BehandlingMapper.tilBehandlingsResultatKode(behandling.getBehandlingsresultat().getBehandlingResultatType()));
        String konsekvensForYtelsen = BehandlingMapper.kodeFra(behandling.getBehandlingsresultat().getKonsekvenserForYtelsen());
        fagType.setKonsekvensForYtelse(BehandlingMapper.finnKonsekvensForYtelseKode(konsekvensForYtelsen));
        if (!fritekstGjelderIkkeLenger(aksjonspunkter)) {
            avklarFritekst(dokumentHendelse, behandling).ifPresent(fagType::setFritekst);
        }
        fagType.setDekningsgrad(BigInteger.valueOf(ytelseFordeling.getDekningsgrad().getVerdi()));

        mapFelterRelatertTilBehandling(behandling, fagType);
        mapFelterRelatertTilBeregningsgrunnlag(beregningsgrunnlag, fagType);
        mapFelterRelatertTilPerioder(beregningsresultatFP, beregningsgrunnlag, uttakResultatPerioder, fagType, behandling);
        mapFelterRelatertTilSøknadOgRettighet(søknad, uttakResultatPerioder, fagType);
        mapFelterRelatertTilStønadskontoer(fagType, uttakResultatPerioder, saldoer, familieHendelse, behandling, søknad, ytelseFordeling, fagsak);
        mapFelterRelatertTilFamiliehendelse(behandling, familieHendelse, originalFamiliehendelse, fagType);
        mapLovhjemmel(fagType, beregningsgrunnlag, konsekvensForYtelsen, behandling, uttakResultatPerioder);
        mapFelterRelatertTilDagsats(fagType, beregningsresultatFP);
        return fagType;
    }

    private boolean fritekstGjelderIkkeLenger(List<Aksjonspunkt> aksjonspunkter) {
        return aksjonspunkter.stream()
                .filter(ap -> Objects.equals(ap.getAksjonspunktDefinisjon(), AksjonspunktDefinisjon.FASTSETT_BEREGNINGSGRUNNLAG_ARBEIDSTAKER_FRILANS))
                .anyMatch(ap -> Objects.equals(ap.getAksjonspunktStatus(), AksjonspunktStatus.AVBRUTT));
    }

    private void mapFelterRelatertTilDagsats(FagType fagType, BeregningsresultatFP beregningsresultat) {
        fagType.setDagsats(BeregningsresultatMapper.finnDagsats(beregningsresultat));
        fagType.setMånedsbeløp(BeregningsresultatMapper.finnMånedsbeløp(beregningsresultat));

    }

    private void mapLovhjemmel(FagType fagType,
                               Beregningsgrunnlag beregningsgrunnlag, String konsekvensForYtelsen,
                               Behandling behandling, UttakResultatPerioder uttakResultatPerioder) {
        LovhjemmelType lovhjemmelType = objectFactory.createLovhjemmelType();
        boolean revurdering = BehandlingType.REVURDERING.equals(behandling.getBehandlingType());
        boolean innvilget = BehandlingResultatType.INNVILGET.equals(behandling.getBehandlingsresultat().getBehandlingResultatType());
        boolean innvilgetRevurdering = revurdering && innvilget;
        lovhjemmelType.setBeregning(FellesMapper.formaterLovhjemlerForBeregning(beregningsgrunnlag.getHjemmel().getNavn(), konsekvensForYtelsen, innvilgetRevurdering));
        lovhjemmelType.setVurdering(UttakMapper.mapLovhjemlerForUttak(uttakResultatPerioder, konsekvensForYtelsen, innvilgetRevurdering));
        fagType.setLovhjemmel(lovhjemmelType);
    }

    private void mapFelterRelatertTilSøknadOgRettighet(Søknad søknad, UttakResultatPerioder uttakResultatPerioder, FagType fagType) {
        fagType.setMottattDato(XmlUtil.finnDatoVerdiAvUtenTidSone(søknad.getMottattDato()));
        fagType.setAnnenForelderHarRett(uttakResultatPerioder.isAnnenForelderHarRett());
        fagType.setAleneomsorg(UttakMapper.harSøkerAleneomsorg(søknad, uttakResultatPerioder));
    }

    private void mapFelterRelatertTilStønadskontoer(FagType fagType, UttakResultatPerioder uttakResultatPerioder, Saldoer saldoer, FamilieHendelse familieHendelse, Behandling behandling, Søknad søknad, YtelseFordeling ytelseFordeling, Fagsak fagsak) {
        fagType.setDagerTaptFørTermin(StønadskontoMapper.finnTapteDagerFørTermin(uttakResultatPerioder, saldoer, familieHendelse));
        fagType.setDisponibleDager(StønadskontoMapper.finnDisponibleDager(fagsak, UttakMapper.harSøkerAleneomsorgBoolean(søknad, uttakResultatPerioder), ytelseFordeling.isAnnenForelderHarRett(), saldoer));
        fagType.setDisponibleFellesDager(StønadskontoMapper.finnDisponibleFellesDager(saldoer));
        StønadskontoMapper.finnForeldrepengeperiodenUtvidetUkerHvisFinnes(saldoer).ifPresent(fagType::setForeldrepengeperiodenUtvidetUker);
    }

    private void mapFelterRelatertTilPerioder(BeregningsresultatFP beregningsresultatFP, Beregningsgrunnlag beregningsgrunnlag, UttakResultatPerioder uttakResultatPerioder, FagType fagType, Behandling behandling) {
        fagType.setAntallArbeidsgivere(BeregningsresultatMapper.antallArbeidsgivere(beregningsresultatFP));
        PeriodeListeType periodeListe = BeregningsresultatMapper.mapPeriodeListe(beregningsresultatFP.getBeregningsresultatPerioder(), uttakResultatPerioder, beregningsgrunnlag.getBeregningsgrunnlagPerioder());
        fagType.setPeriodeListe(periodeListe);
        fagType.setAntallPerioder(BigInteger.valueOf(periodeListe.getPeriode().size()));
        fagType.setTotalArbeidsgiverAndel(BeregningsresultatMapper.finnTotalArbeidsgiverAndel(beregningsresultatFP));
        fagType.setTotalBrukerAndel(BeregningsresultatMapper.finnTotalBrukerAndel(beregningsresultatFP));
        fagType.setAntallAvslag(BeregningsresultatMapper.tellAntallAvslag(periodeListe));
        fagType.setAntallInnvilget(BeregningsresultatMapper.tellAntallInnvilget(periodeListe));
        fagType.setGraderingFinnes(PeriodeVerktøy.graderingFinnes(periodeListe));
        fagType.setSisteDagAvSistePeriode(UttakMapper.finnSisteDagAvSistePeriode(uttakResultatPerioder));
        fagType.setIkkeOmsorg(UttakMapper.finnesPeriodeMedIkkeOmsorg(periodeListe.getPeriode()));
        fagType.setForMyeUtbetalt(UttakMapper.forMyeUtbetaltKode(periodeListe, behandling));

        BeregningsresultatMapper.finnStønadsperiodeFomHvisFinnes(periodeListe).ifPresent(fagType::setStønadsperiodeFom);
        BeregningsresultatMapper.finnStønadsperiodeTomHvisFinnes(periodeListe).ifPresent(sisteInnvilgedeDag -> {
            fagType.setStønadsperiodeTom(sisteInnvilgedeDag);
            fagType.setSisteUtbetalingsdag(sisteInnvilgedeDag);
        });
        UttakMapper.finnSisteDagIFelleseriodeHvisFinnes(uttakResultatPerioder).ifPresent(fagType::setSisteDagIFellesPeriode);
        UttakMapper.finnSisteDagMedUtsettelseHvisFinnes(uttakResultatPerioder).ifPresent(fagType::setSisteDagMedUtsettelse);
    }

    private void mapFelterRelatertTilBehandling(Behandling behandling, FagType fagType) {
        fagType.setBehandlingsType(BehandlingMapper.utledBehandlingsTypeInnvilgetFP(behandling));
        fagType.setInntektMottattArbgiver(BehandlingMapper.erEndringMedEndretInntektsmelding(behandling));
    }

    private void mapFelterRelatertTilBeregningsgrunnlag(Beregningsgrunnlag beregningsgrunnlag, FagType fagType) {
        fagType.setBruttoBeregningsgrunnlag(BeregningsgrunnlagMapper.finnBrutto(beregningsgrunnlag));
        BeregningsgrunnlagRegelListeType beregningsgrunnlagRegelListe = BeregningsgrunnlagMapper.mapRegelListe(beregningsgrunnlag);
        fagType.setBeregningsgrunnlagRegelListe(beregningsgrunnlagRegelListe);
        fagType.setAntallBeregningsgrunnlagRegeler(BigInteger.valueOf(beregningsgrunnlagRegelListe.getBeregningsgrunnlagRegel().size()));
        fagType.setSeksG(BeregningsgrunnlagMapper.finnSeksG(beregningsgrunnlag).longValue());
        fagType.setInntektOverSeksG(BeregningsgrunnlagMapper.inntektOverSeksG(beregningsgrunnlag));
    }

    private void mapFelterRelatertTilFamiliehendelse(Behandling behandling, FamilieHendelse familieHendelse, Optional<FamilieHendelse> originalFamiliehendelse, FagType fagType) {
        fagType.setAntallBarn(familieHendelse.getAntallBarn());
        fagType.setBarnErFødt(familieHendelse.isBarnErFødt());
        fagType.setGjelderFoedsel(familieHendelse.isGjelderFødsel());
        fagType.setFødselsHendelse(BehandlingMapper.erRevurderingPgaFødselshendelse(behandling, familieHendelse, originalFamiliehendelse));
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
