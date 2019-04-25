package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.DokumentTypeFelles.finnDatoVerdiAvUtenTidSone;
import static no.nav.foreldrepenger.melding.datamapper.DokumentTypeFelles.finnVerdiAv;
import static no.nav.foreldrepenger.melding.datamapper.domene.BehandlingMapper.avklarFritekst;

import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import no.nav.foreldrepenger.melding.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatFP;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.melding.datamapper.DokumentTypeMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.domene.BehandlingMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.BeregningsgrunnlagMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.BeregningsresultatMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.UttakMapper;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentTypeData;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.BehandlingsResultatKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.BeregningsgrunnlagRegelListeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.BrevdataType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.FagType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.InnvilgetForeldrepengerConstants;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.KonsekvensForYtelseKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.LovhjemmelType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.ObjectFactory;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.PeriodeListeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.PersonstatusKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.RelasjonskodeKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.UtbetaltKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.VurderingsstatusKode;
import no.nav.foreldrepenger.melding.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.melding.søknad.Søknad;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPerioder;
import no.nav.vedtak.felles.integrasjon.felles.ws.JaxbHelper;

@ApplicationScoped
@Named(DokumentMalType.INNVILGELSE_FORELDREPENGER_DOK)
public class InnvilgelseForeldrepengerMapper implements DokumentTypeMapper {
    public static final String ENDRING_BEREGNING_OG_UTTAK = "ENDRING_BEREGNING_OG_UTTAK";

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
        FagType fagType = mapFagType(dokumentHendelse,
                behandling,
                beregningsresultatFP,
                familieHendelse,
                beregningsgrunnlag,
                originaltBeregningsgrunnlag,
                Collections.emptyList(),
                søknad,
                dokumentFelles,
                uttakResultatPerioder);
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
                               UttakResultatPerioder uttakResultatPerioder) {
        final FagType fagType = objectFactory.createFagType();

        fagType.setSokersNavn(dokumentFelles.getSakspartNavn());
        fagType.setRelasjonskode(tilRelasjonskode(behandling.getRelasjonsRolleType(), behandling.getPersonopplysning().getNavBrukerKjonn()));
        //Obligatoriske felter
        //Faktisk mappet
        mapFelterRelatertTilBehandling(behandling, fagType);
        mapFelterRelatertTilBeregningsgrunnlag(beregningsgrunnlag, originaltBeregningsgrunnlag, fagType);
        mapFelterRelatertTilPerioder(beregningsresultatFP, beregningsgrunnlag, uttakResultatPerioder, fagType);
        mapFelterRelatertTilStønadskontoer(fagType);
        mapFelterRelatertTilFamiliehendelse(familieHendelse, fagType);
        mapFelterRelatertTilSøknad(søknad, fagType);
        fagType.setKlageFristUker(BigInteger.valueOf(brevParametere.getKlagefristUker()));

        //Map
        //Optional
        avklarFritekst(dokumentHendelse, behandling).ifPresent(fagType::setFritekst);
        fagType.setPersonstatus(PersonstatusKode.fromValue(dokumentFelles.getSakspartPersonStatus()));

        //
        //Settes basert på Perioder, eventuelt søknad

        //Kan ikke mappes
        fagType.setDekningsgrad(BigInteger.valueOf(Integer.parseInt(finnVerdiAv("PLACEHOLDER", dokumentTypeDataListe)))); //Mangler fagsakrelasjon

        LovhjemmelType lovhjemmelType = objectFactory.createLovhjemmelType();
        lovhjemmelType.setBeregning(finnVerdiAv("PLACEHOLDER", dokumentTypeDataListe));
        lovhjemmelType.setVurdering(finnVerdiAv("PLACEHOLDER", dokumentTypeDataListe));
        fagType.setLovhjemmel(lovhjemmelType);
        fagType.setMottattDato(finnDatoVerdiAvUtenTidSone("PLACEHOLDER", dokumentTypeDataListe));

        fagType.setKonsekvensForYtelse(tilKonsekvensForYtelseKode(finnVerdiAv("PLACEHOLDER", dokumentTypeDataListe)));
        fagType.setBehandlingsResultat(tilBehandlingsResultatKode(finnVerdiAv("PLACEHOLDER", dokumentTypeDataListe)));
        fagType.setInntektMottattArbgiver(Boolean.parseBoolean(finnVerdiAv("PLACEHOLDER", dokumentTypeDataListe)));
        fagType.setForMyeUtbetalt(UtbetaltKode.fromValue(finnVerdiAv("PLACEHOLDER", dokumentTypeDataListe)));

        return fagType;
    }

    private void mapFelterRelatertTilSøknad(Søknad søknad, FagType fagType) {
        //Disse ser også på perioder
        fagType.setAnnenForelderHarRett(Boolean.parseBoolean(finnVerdiAv("PLACEHOLDER", Collections.emptyList())));
        fagType.setAleneomsorg(VurderingsstatusKode.fromValue("IKKE_VURDERT"));
    }

    private void mapFelterRelatertTilStønadskontoer(FagType fagType) {
        fagType.setDagerTaptFørTermin(BigInteger.valueOf(0)); /*TODO Mangler Stønadskonto - Finnes et endepunkt for dette*/
        fagType.setDisponibleDager(BigInteger.valueOf(0));/*TODO Bruker Stønadskonto*/
        fagType.setDisponibleFellesDager(BigInteger.valueOf(0))/*TODO Bruker Stønadskonto*/;
    }

    private void mapFelterRelatertTilPerioder(BeregningsresultatFP beregningsresultatFP, Beregningsgrunnlag beregningsgrunnlag, UttakResultatPerioder uttakResultatPerioder, FagType fagType) {
        //Match, Map, merge - Blæh
        fagType.setAntallArbeidsgivere(BeregningsresultatMapper.antallArbeidsgivere(beregningsresultatFP));
        PeriodeListeType periodeListe = BeregningsresultatMapper.mapPeriodeListe(beregningsresultatFP.getBeregningsresultatPerioder(), uttakResultatPerioder, beregningsgrunnlag.getBeregningsgrunnlagPerioder());
        fagType.setPeriodeListe(periodeListe);
        fagType.setAntallPerioder(BigInteger.valueOf(periodeListe.getPeriode().size()));
        fagType.setTotalArbeidsgiverAndel(BeregningsresultatMapper.finnTotalArbeidsgiverAndel(beregningsresultatFP));
        fagType.setTotalBrukerAndel(BeregningsresultatMapper.finnTotalBrukerAndel(beregningsresultatFP));
        fagType.setAntallAvslag(BeregningsresultatMapper.tellAntallAvslag(periodeListe));
        fagType.setAntallInnvilget(BeregningsresultatMapper.tellAntallInnvilget(periodeListe));
        fagType.setGraderingFinnes(BeregningsresultatMapper.graderingFinnes(periodeListe));
        fagType.setStønadsperiodeFom(BeregningsresultatMapper.finnStønadsperiodeFom(periodeListe));
        XMLGregorianCalendar sisteInnvilgedeDag = BeregningsresultatMapper.finnStønadsperiodeTom(periodeListe);
        fagType.setStønadsperiodeTom(sisteInnvilgedeDag);
        fagType.setSisteDagAvSistePeriode(sisteInnvilgedeDag);
        if (sisteInnvilgedeDag != null) {
            fagType.setSisteUtbetalingsdag(sisteInnvilgedeDag);
        }
        fagType.setIkkeOmsorg(UttakMapper.finnesPeriodeMedIkkeOmsorg(periodeListe.getPeriode()));
        UttakMapper.finnSisteDagIFelleseriodeHvisFinnes(uttakResultatPerioder).ifPresent(fagType::setSisteDagIFellesPeriode);
        UttakMapper.finnSisteDagMedUtsettelseHvisFinnes(uttakResultatPerioder).ifPresent(fagType::setSisteDagMedUtsettelse);
        //Optional.empty().ifPresent(fagType::setForeldrepengeperiodenUtvidetUker); /* TODO Mangler Tjeneste som regner ut dette */
    }

    private void mapFelterRelatertTilBehandling(Behandling behandling, FagType fagType) {
        fagType.setBehandlingsType(BehandlingMapper.utledBehandlingsTypeInnvilgetFP(behandling));
        fagType.setFødselsHendelse(BehandlingMapper.erRevurderingPgaFødselshendelse(behandling));
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

    private KonsekvensForYtelseKode tilKonsekvensForYtelseKode(String konsekvens) {
        Map<String, KonsekvensForYtelseKode> konsekvensForYtelseKodeMap = new HashMap<>();
        konsekvensForYtelseKodeMap.put(ENDRING_BEREGNING_OG_UTTAK, KonsekvensForYtelseKode.ENDRING_I_BEREGNING_OG_UTTAK);
        konsekvensForYtelseKodeMap.put(KonsekvensForYtelsen.ENDRING_I_BEREGNING.getKode(), KonsekvensForYtelseKode.ENDRING_I_BEREGNING);
        konsekvensForYtelseKodeMap.put(KonsekvensForYtelsen.INGEN_ENDRING.getKode(), KonsekvensForYtelseKode.INGEN_ENDRING);
        konsekvensForYtelseKodeMap.put(KonsekvensForYtelsen.ENDRING_I_UTTAK.getKode(), KonsekvensForYtelseKode.ENDRING_I_UTTAK);
        konsekvensForYtelseKodeMap.put(KonsekvensForYtelsen.FORELDREPENGER_OPPHØRER.getKode(), KonsekvensForYtelseKode.FORELDREPENGER_OPPHØRER);
        if (konsekvensForYtelseKodeMap.containsKey(konsekvens)) {
            return konsekvensForYtelseKodeMap.get(konsekvens);
        }
        return KonsekvensForYtelseKode.INGEN_ENDRING;
    }

    private BehandlingsResultatKode tilBehandlingsResultatKode(String behandlingsresultatkode) {
        if (BehandlingResultatType.INNVILGET.getKode().equals(behandlingsresultatkode)) {
            return BehandlingsResultatKode.INNVILGET;
        } else if (BehandlingResultatType.AVSLÅTT.getKode().equals(behandlingsresultatkode)) {
            return BehandlingsResultatKode.AVSLÅTT;
        } else if (BehandlingResultatType.OPPHØR.getKode().equals(behandlingsresultatkode)) {
            return BehandlingsResultatKode.OPPHØR;
        } else if (BehandlingResultatType.FORELDREPENGER_ENDRET.getKode().equals(behandlingsresultatkode)) {
            return BehandlingsResultatKode.FORELDREPENGER_ENDRET;
        } else {
            return BehandlingsResultatKode.INGEN_ENDRING;
        }
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
