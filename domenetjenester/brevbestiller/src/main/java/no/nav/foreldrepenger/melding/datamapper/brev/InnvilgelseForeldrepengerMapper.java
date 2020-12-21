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

import no.nav.foreldrepenger.PersonAdapter;
import no.nav.foreldrepenger.melding.aksjonspunkt.Aksjonspunkt;
import no.nav.foreldrepenger.melding.aksjonspunkt.AksjonspunktDefinisjon;
import no.nav.foreldrepenger.melding.aksjonspunkt.AksjonspunktStatus;
import no.nav.foreldrepenger.melding.aktør.Personinfo;
import no.nav.foreldrepenger.melding.behandling.Behandling;
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
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.fagsak.FagsakBackend;
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
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.RelasjonskodeKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.VurderingsstatusKode;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalTypeKode;
import no.nav.foreldrepenger.melding.personopplysning.NavBrukerKjønn;
import no.nav.foreldrepenger.melding.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.melding.søknad.Søknad;
import no.nav.foreldrepenger.melding.uttak.Saldoer;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPerioder;
import no.nav.foreldrepenger.melding.ytelsefordeling.YtelseFordeling;
import no.nav.vedtak.felles.integrasjon.felles.ws.JaxbHelper;

@ApplicationScoped
@Named(DokumentMalTypeKode.INNVILGELSE_FORELDREPENGER_DOK)
public class InnvilgelseForeldrepengerMapper extends DokumentTypeMapper {

    private ObjectFactory objectFactory = new ObjectFactory();
    private BrevParametere brevParametere;
    private PersonAdapter tpsTjeneste;

    public InnvilgelseForeldrepengerMapper() {
        //CDI
    }

    @Inject
    public InnvilgelseForeldrepengerMapper(DomeneobjektProvider domeneobjektProvider,
                                           BrevParametere brevParametere,
                                           PersonAdapter tpsTjeneste) {
        this.brevParametere = brevParametere;
        this.domeneobjektProvider = domeneobjektProvider;
        this.tpsTjeneste = tpsTjeneste;
    }

    @Override
    public String mapTilBrevXML(FellesType fellesType,
                                DokumentFelles dokumentFelles,
                                DokumentHendelse dokumentHendelse,
                                Behandling behandling) throws JAXBException, SAXException, XMLStreamException {

        FagType fagType = mapFagType(dokumentHendelse, behandling);
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
                if (nåværendeBehandling.getUuid() == nesteBehandling.getUuid()) {
                    throw new IllegalStateException();
                }
                nåværendeBehandling = nesteBehandling;
            }
            nåværendeForsøk++;
        }
        return søknad.orElseThrow(IllegalStateException::new);
    }

    private FagType mapFagType(DokumentHendelse dokumentHendelse, Behandling behandling) {
        final FagType fagType = objectFactory.createFagType();

        UttakResultatPerioder uttakResultatPerioder = domeneobjektProvider.hentUttaksresultat(behandling);
        BeregningsresultatFP beregningsresultatFP = domeneobjektProvider.hentBeregningsresultatFP(behandling);
        Beregningsgrunnlag beregningsgrunnlag = domeneobjektProvider.hentBeregningsgrunnlag(behandling);
        FamilieHendelse familieHendelse = domeneobjektProvider.hentFamiliehendelse(behandling);
        Søknad søknad = hentSøknadUansett(behandling);
        Optional<FamilieHendelse> originalFamiliehendelse = domeneobjektProvider.hentOriginalBehandlingHvisFinnes(behandling).map(domeneobjektProvider::hentFamiliehendelse);
        YtelseFordeling ytelseFordeling = domeneobjektProvider.hentYtelseFordeling(behandling);
        Saldoer saldoer = domeneobjektProvider.hentSaldoer(behandling);
        List<Aksjonspunkt> aksjonspunkter = domeneobjektProvider.hentAksjonspunkter(behandling);
        FagsakBackend fagsak = domeneobjektProvider.hentFagsakBackend(behandling);

        fagType.setRelasjonskode(utledRelasjonsrolle(fagsak));
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
        mapFelterRelatertTilSøknadOgRettighet(søknad, uttakResultatPerioder, aksjonspunkter, fagType);
        mapFelterRelatertTilStønadskontoer(fagType, saldoer, fagsak);
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

    private void mapFelterRelatertTilSøknadOgRettighet(Søknad søknad, UttakResultatPerioder uttakResultatPerioder, List<Aksjonspunkt> aksjonspunkter, FagType fagType) {
        fagType.setMottattDato(XmlUtil.finnDatoVerdiAvUtenTidSone(søknad.getMottattDato()));

        //Dokprod tolker FagType.aleneomsorg som om "det har vært søkt om aleneomsorg og verdien er resultatet. Hvis det ikke er søkt aleneomsorg så skal det ikke stå noe i brevet om dette (derav IKKE_VURDERT)
        VurderingsstatusKode aleneomsorg;
        if (søknad.getOppgittRettighet().harAleneomsorgForBarnet()) {
            aleneomsorg = uttakResultatPerioder.isAleneomsorg() ? VurderingsstatusKode.JA : VurderingsstatusKode.NEI;
        } else {
            aleneomsorg = VurderingsstatusKode.IKKE_VURDERT;
        }
        fagType.setAleneomsorg(aleneomsorg);

        VurderingsstatusKode annenForelderHarRettVurdert;
        if (aksjonspunkter.stream().
                filter(ap -> Objects.equals(ap.getAksjonspunktDefinisjon(), AksjonspunktDefinisjon.AVKLAR_FAKTA_ANNEN_FORELDER_HAR_IKKE_RETT)).
                anyMatch(ap -> Objects.equals(ap.getAksjonspunktStatus(), AksjonspunktStatus.UTFØRT))) {
            annenForelderHarRettVurdert = uttakResultatPerioder.isAnnenForelderHarRett() ? VurderingsstatusKode.JA : VurderingsstatusKode.NEI;
        }
        else {
            annenForelderHarRettVurdert = VurderingsstatusKode.IKKE_VURDERT;
        }
        //Begge felt brukes for å skille på overstyrt rettighet og ikke
        fagType.setAnnenForelderHarRettVurdert(annenForelderHarRettVurdert);
        fagType.setAnnenForelderHarRett(uttakResultatPerioder.isAnnenForelderHarRett());
    }

    private void mapFelterRelatertTilStønadskontoer(FagType fagType, Saldoer saldoer, FagsakBackend fagsak) {
        fagType.setDagerTaptFørTermin(BigInteger.valueOf(saldoer.getTapteDagerFpff()));
        fagType.setDisponibleDager(StønadskontoMapper.finnDisponibleDager(saldoer, fagsak.getRelasjonsRolleType()));
        fagType.setDisponibleFellesDager(StønadskontoMapper.finnDisponibleFellesDager(saldoer));
        StønadskontoMapper.finnForeldrepengeperiodenUtvidetUkerHvisFinnes(saldoer).ifPresent(fagType::setForeldrepengeperiodenUtvidetUker);
        StønadskontoMapper.finnPrematurDagerHvisFinnes(saldoer).ifPresent(fagType::setPrematurDager);
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
        fagType.setSisteDagAvSistePeriode(UttakMapper.finnSisteDagAvSistePeriode(uttakResultatPerioder));
        fagType.setIkkeOmsorg(UttakMapper.finnesPeriodeMedIkkeOmsorg(periodeListe.getPeriode()));
        fagType.setForMyeUtbetalt(UttakMapper.forMyeUtbetaltKode(periodeListe, behandling));

        BeregningsresultatMapper.finnStønadsperiodeFomHvisFinnes(periodeListe).ifPresent(fagType::setStønadsperiodeFom);
        BeregningsresultatMapper.finnStønadsperiodeTomHvisFinnes(periodeListe).ifPresent(fagType::setStønadsperiodeTom);
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
        fagType.setBesteBeregning(BeregningsgrunnlagMapper.harNoenAvAndeleneBesteberegning(BeregningsgrunnlagMapper.finnBgpsaListe(beregningsgrunnlag)));
    }

    private void mapFelterRelatertTilFamiliehendelse(Behandling behandling, FamilieHendelse familieHendelse, Optional<FamilieHendelse> originalFamiliehendelse, FagType fagType) {
        fagType.setAntallBarn(familieHendelse.getAntallBarn());
        fagType.setBarnErFødt(familieHendelse.isBarnErFødt());
        fagType.setGjelderFoedsel(familieHendelse.isGjelderFødsel());
        fagType.setFødselsHendelse(BehandlingMapper.erRevurderingPgaFødselshendelse(behandling, familieHendelse, originalFamiliehendelse));
        familieHendelse.getDødsdato().map(XmlUtil::finnDatoVerdiAvUtenTidSone).ifPresent(fagType::setDodsdato);
    }

    private JAXBElement<BrevdataType> mapintoBrevdataType(FellesType fellesType, FagType fagType) {
        BrevdataType brevdataType = objectFactory.createBrevdataType();
        brevdataType.setFag(fagType);
        brevdataType.setFelles(fellesType);
        return objectFactory.createBrevdata(brevdataType);
    }

    private RelasjonskodeKode utledRelasjonsrolle(FagsakBackend fagsak) {
        var kjønn = tpsTjeneste.hentBrukerForAktør(fagsak.getAktørId()).map(Personinfo::getKjønn).orElseThrow();
        return tilRelasjonskode(fagsak.getRelasjonsRolleType(), kjønn);
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
