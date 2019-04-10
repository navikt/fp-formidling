package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.DokumentTypeFelles.finnDatoVerdiAvUtenTidSone;
import static no.nav.foreldrepenger.melding.datamapper.DokumentTypeFelles.finnOptionalDatoVerdiAvUtenTidSone;
import static no.nav.foreldrepenger.melding.datamapper.DokumentTypeFelles.finnOptionalVerdiAv;
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
import javax.xml.stream.XMLStreamException;

import org.xml.sax.SAXException;

import no.nav.foreldrepenger.melding.aktør.NavBrukerKjønn;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingResultatType;
import no.nav.foreldrepenger.melding.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatFP;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.melding.datamapper.DokumentTypeMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.BehandlingMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.BeregningsgrunnlagMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.BeregningsresultatMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.FamiliehendelseMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.SøknadMapper;
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
import no.nav.vedtak.felles.integrasjon.felles.ws.JaxbHelper;

@ApplicationScoped
@Named(DokumentMalType.INNVILGELSE_FORELDREPENGER_DOK)
public class InnvilgelseForeldrepengerMapper implements DokumentTypeMapper {
    public static final String ENDRING_BEREGNING_OG_UTTAK = "ENDRING_BEREGNING_OG_UTTAK";

    private ObjectFactory objectFactory = new ObjectFactory();
    private BehandlingMapper behandlingMapper;
    private BeregningsresultatMapper beregningsresultatMapper;
    private SøknadMapper søknadMapper;
    private FamiliehendelseMapper familiehendelseMapper;
    private BeregningsgrunnlagMapper beregningsgrunnlagMapper;
    private BrevParametere brevParametere;

    public InnvilgelseForeldrepengerMapper() {
        //CDI
    }

    @Inject
    public InnvilgelseForeldrepengerMapper(BehandlingMapper behandlingMapper,
                                           BeregningsresultatMapper beregningsresultatMapper,
                                           BeregningsgrunnlagMapper beregningsgrunnlagMapper,
                                           FamiliehendelseMapper familiehendelseMapper,
                                           BrevParametere brevParametere,
                                           SøknadMapper søknadMapper) {
        this.behandlingMapper = behandlingMapper;
        this.beregningsresultatMapper = beregningsresultatMapper;
        this.familiehendelseMapper = familiehendelseMapper;
        this.beregningsgrunnlagMapper = beregningsgrunnlagMapper;
        this.brevParametere = brevParametere;
        this.søknadMapper = søknadMapper;
    }

    @Override
    public String mapTilBrevXML(FellesType fellesType,
                                DokumentFelles dokumentFelles,
                                DokumentHendelse dokumentHendelse,
                                Behandling behandling) throws JAXBException, SAXException, XMLStreamException {
        //TODO - Burde vi lage et wrapper objekt for inputobjektene når det er så mange??
        BeregningsresultatFP beregningsresultatFP = null;
        Beregningsgrunnlag beregningsgrunnlag = beregningsgrunnlagMapper.hentBeregningsgrunnlag(behandling);
        Behandling originalBehandling;
        Beregningsgrunnlag originaltBeregningsgrunnlag = null;
        if (behandling.getOriginalBehandlingId() != null) {
            originalBehandling = behandlingMapper.hentBehandling(behandling.getOriginalBehandlingId());
            originaltBeregningsgrunnlag = beregningsgrunnlagMapper.hentBeregningsgrunnlag(originalBehandling);
        }
        FamilieHendelse familieHendelse = familiehendelseMapper.hentFamiliehendelse(behandling);
        Søknad søknad = søknadMapper.hentSøknad(behandling);
        FagType fagType = mapFagType(dokumentHendelse, behandling, beregningsresultatFP, familieHendelse, beregningsgrunnlag, originaltBeregningsgrunnlag, Collections.emptyList(), søknad, dokumentFelles);
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
                               DokumentFelles dokumentFelles) {
        final FagType fagType = objectFactory.createFagType();

        //Obligatoriske felter
        //Faktisk mappet
        mapFelterRelatertTilBehandling(behandling, fagType);
        mapFelterRelatertTilBeregningsgrunnlag(beregningsgrunnlag, originaltBeregningsgrunnlag, fagType);
        mapFelterRelatertTilBeregningsresultat(beregningsresultatFP, fagType);
        mapFelterRelatertTilFamiliehendelse(familieHendelse, fagType);
        mapFelterRelatertTilSøknad(søknad, fagType);
        fagType.setKlageFristUker(BigInteger.valueOf(brevParametere.getKlagefristUker()));

        //Map
        //Loop gjennom perioder
        fagType.setIkkeOmsorg(Boolean.parseBoolean((finnVerdiAv("PLACEHOLDER", dokumentTypeDataListe))));

        //Optional
        avklarFritekst(dokumentHendelse, behandling).ifPresent(fagType::setFritekst);
        fagType.setPersonstatus(PersonstatusKode.fromValue(dokumentFelles.getSakspartPersonStatus()));

        //
        //Settes basert på Perioder, eventuelt søknad

        //Kan ikke mappes

        fagType.setDagerTaptFørTermin(BigInteger.valueOf(Integer.parseInt(finnVerdiAv("PLACEHOLDER", dokumentTypeDataListe))));
        fagType.setDekningsgrad(BigInteger.valueOf(Integer.parseInt(finnVerdiAv("PLACEHOLDER", dokumentTypeDataListe))));
        fagType.setDisponibleDager(BigInteger.valueOf(Integer.parseInt(finnVerdiAv("PLACEHOLDER", dokumentTypeDataListe))));
        fagType.setDisponibleFellesDager(BigInteger.valueOf(Integer.parseInt(finnVerdiAv("PLACEHOLDER", dokumentTypeDataListe))));
        fagType.setAntallInnvilget(BigInteger.valueOf(Integer.parseInt(finnVerdiAv("PLACEHOLDER", dokumentTypeDataListe))));
        fagType.setAntallAvslag(BigInteger.valueOf(Integer.parseInt(finnVerdiAv("PLACEHOLDER", dokumentTypeDataListe))));
        fagType.setGraderingFinnes(Boolean.parseBoolean(finnVerdiAv("PLACEHOLDER", dokumentTypeDataListe)));
        LovhjemmelType lovhjemmelType = objectFactory.createLovhjemmelType();
        lovhjemmelType.setBeregning(finnVerdiAv("PLACEHOLDER", dokumentTypeDataListe));
        lovhjemmelType.setVurdering(finnVerdiAv("PLACEHOLDER", dokumentTypeDataListe));
        fagType.setLovhjemmel(lovhjemmelType);
        fagType.setMottattDato(finnDatoVerdiAvUtenTidSone("PLACEHOLDER", dokumentTypeDataListe));

        fagType.setRelasjonskode(tilRelasjonskode(finnVerdiAv("PLACEHOLDER", dokumentTypeDataListe), finnVerdiAv("PLACEHOLDER", dokumentTypeDataListe)));
        fagType.setSeksG(Long.parseLong(finnVerdiAv("PLACEHOLDER", dokumentTypeDataListe)));
        fagType.setSisteDagAvSistePeriode(finnDatoVerdiAvUtenTidSone("PLACEHOLDER", dokumentTypeDataListe));
        fagType.setSokersNavn(finnVerdiAv("PLACEHOLDER", dokumentTypeDataListe));
        fagType.setStønadsperiodeFom(finnDatoVerdiAvUtenTidSone("PLACEHOLDER", dokumentTypeDataListe));
        fagType.setStønadsperiodeTom(finnDatoVerdiAvUtenTidSone("PLACEHOLDER", dokumentTypeDataListe));
        fagType.setTotalArbeidsgiverAndel(Long.parseLong(finnVerdiAv("PLACEHOLDER", dokumentTypeDataListe)));
        fagType.setTotalBrukerAndel(Long.parseLong(finnVerdiAv("PLACEHOLDER", dokumentTypeDataListe)));
        fagType.setKonsekvensForYtelse(tilKonsekvensForYtelseKode(finnVerdiAv("PLACEHOLDER", dokumentTypeDataListe)));
        fagType.setBehandlingsResultat(tilBehandlingsResultatKode(finnVerdiAv("PLACEHOLDER", dokumentTypeDataListe)));
        fagType.setInntektMottattArbgiver(Boolean.parseBoolean(finnVerdiAv("PLACEHOLDER", dokumentTypeDataListe)));
        PeriodeListeType konvertertPeriodeListe = konverterPeriodeListe(dokumentTypeDataListe);
        fagType.setPeriodeListe(konvertertPeriodeListe);
        fagType.setAntallPerioder(BigInteger.valueOf(konvertertPeriodeListe.getPeriode().size()));
        fagType.setForMyeUtbetalt(UtbetaltKode.fromValue(finnVerdiAv("PLACEHOLDER", dokumentTypeDataListe)));

        //ikke obligatoriske felter
        finnOptionalDatoVerdiAvUtenTidSone("PLACEHOLDER", dokumentTypeDataListe).ifPresent(fagType::setSisteDagIFellesPeriode);
        finnOptionalDatoVerdiAvUtenTidSone("PLACEHOLDER", dokumentTypeDataListe).ifPresent(fagType::setSisteDagMedUtsettelse);
        finnOptionalDatoVerdiAvUtenTidSone("PLACEHOLDER", dokumentTypeDataListe).ifPresent(fagType::setSisteUtbetalingsdag);
        finnOptionalVerdiAv("PLACEHOLDER", dokumentTypeDataListe).map(BigInteger::new).ifPresent(fagType::setForeldrepengeperiodenUtvidetUker);

        return fagType;
    }

    private void mapFelterRelatertTilSøknad(Søknad søknad, FagType fagType) {
        //Disse ser også på perioder
        fagType.setAnnenForelderHarRett(Boolean.parseBoolean(finnVerdiAv("PLACEHOLDER", Collections.emptyList())));
        fagType.setAleneomsorg(VurderingsstatusKode.fromValue("IKKE_VURDERT"));
    }

    private void mapFelterRelatertTilBeregningsresultat(BeregningsresultatFP beregningsresultatFP, FagType fagType) {
        fagType.setAntallArbeidsgivere(beregningsresultatMapper.antallArbeidsgivere(beregningsresultatFP));
    }

    private void mapFelterRelatertTilBehandling(Behandling behandling, FagType fagType) {
        fagType.setBehandlingsType(behandlingMapper.utledBehandlingsTypeInnvilgetFP(behandling));
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

    private PeriodeListeType konverterPeriodeListe(List<DokumentTypeData> dokumentTypeDataListe) {
        PeriodeListeType liste = objectFactory.createPeriodeListeType();
//        List<PeriodeDto> periodeDtos = new ArrayList<>();
//        for (DokumentTypeData data : finnListeMedVerdierAv("PLACEHOLDER", dokumentTypeDataListe)) {
//            String feltNavnMedIndeks = data.getDoksysId();
//            String strukturertFelt = finnStrukturertVerdiAv(feltNavnMedIndeks, dokumentTypeDataListe);
//            PeriodeDto dto = FlettefeltJsonObjectMapper.readValue(strukturertFelt, PeriodeDto.class);
//            periodeDtos.add(dto);
//        }
//        periodeDtos.forEach(dto -> {
//            PeriodeType periode = objectFactory.createPeriodeType();
//            periode.setInnvilget(dto.getInnvilget());
//            periode.setÅrsak(dto.getÅrsak());
//            periode.setPeriodeFom(finnDatoVerdiAvUtenTidSone(dto.getPeriodeFom()));
//            periode.setPeriodeTom(finnDatoVerdiAvUtenTidSone(dto.getPeriodeTom()));
//            periode.setPeriodeDagsats(dto.getPeriodeDagsats());
//            periode.setAntallTapteDager(BigInteger.valueOf(dto.getAntallTapteDager()));
//            if (!dto.getArbeidsforhold().isEmpty()) {
//                periode.setArbeidsforholdListe(fraArbeidsforhold(dto.getArbeidsforhold()));
//            }
//            if (dto.getNæring() != null) {
//                periode.setNæringListe(fraNæring(dto.getNæring()));
//            }
//            if (!dto.getAnnenAktivitet().isEmpty()) {
//                periode.setAnnenAktivitetListe(fraAnnenAktivitet(dto.getAnnenAktivitet()));
//            }
//            liste.getPeriode().add(periode);
//        });

        return liste;
    }
/*

    private AnnenAktivitetListeType fraAnnenAktivitet(List<AnnenAktivitetDto> dtoListe) {
        AnnenAktivitetListeType liste = objectFactory.createAnnenAktivitetListeType();
        dtoListe.forEach(dto -> {
            AnnenAktivitetType aktivitet = objectFactory.createAnnenAktivitetType();
            aktivitet.setAktivitetType(tilStatusTypeKode(dto.getAktivitetType()));
            aktivitet.setGradering(dto.getGradering());
            aktivitet.setProsentArbeid(BigInteger.valueOf(dto.getProsentArbeid()));
            aktivitet.setUttaksgrad(BigInteger.valueOf(dto.getUttaksgrad()));
            aktivitet.setAktivitetDagsats(dto.getDagsats());
            liste.getAnnenAktivitet().add(aktivitet);
        });
        return liste;
    }

    private NæringListeType fraNæring(NæringDto dto) {
        NæringListeType liste = null;
        if (dto != null) {
            liste = objectFactory.createNæringListeType();
            NæringType næring = objectFactory.createNæringType();
            næring.setGradering(dto.getGradering());
            næring.setInntekt1(dto.getInntekt1());
            næring.setInntekt2(dto.getInntekt2());
            næring.setInntekt3(dto.getInntekt3());
            næring.setAktivitetDagsats(dto.getDagsats());
            næring.setUttaksgrad(BigInteger.valueOf(dto.getUttaksgrad()));
            næring.setProsentArbeid(BigInteger.valueOf(dto.getProsentArbeid()));
            næring.setSistLignedeÅr(BigInteger.valueOf(dto.getSistLignedeÅr()));
            liste.setNæring(næring);
        }
        return liste;
    }

    private ArbeidsforholdListeType fraArbeidsforhold(List<ArbeidsforholdDto> dtoListe) {
        ArbeidsforholdListeType liste = objectFactory.createArbeidsforholdListeType();
        dtoListe.forEach(dto -> {
            ArbeidsforholdType arbeidsforhold = objectFactory.createArbeidsforholdType();
            arbeidsforhold.setArbeidsgiverNavn(dto.getArbeidsgiverNavn());
            arbeidsforhold.setGradering(dto.getGradering());
            arbeidsforhold.setAktivitetDagsats(dto.getDagsats());
            arbeidsforhold.setProsentArbeid(BigInteger.valueOf(dto.getProsentArbeid()));
            arbeidsforhold.setStillingsprosent(BigInteger.valueOf(dto.getStillingsprosent()));
            arbeidsforhold.setUttaksgrad(BigInteger.valueOf(dto.getUttaksgrad()));
            arbeidsforhold.setUtbetalingsgrad(BigInteger.valueOf(dto.getUtbetalingsgrad()));
            if (dto.getNaturalYtelseDto() != null) {
                Optional.ofNullable(dto.getNaturalYtelseDto().getNaturalYtelseEndringStatus()).ifPresent(naturalYtelseEndringStatus -> arbeidsforhold.setNaturalytelseEndringType(NaturalytelseEndringTypeKode.valueOf(naturalYtelseEndringStatus.toString())));
                Optional.ofNullable(dto.getNaturalYtelseDto().getNaturalYtelseEndringDato()).ifPresent(localDate -> arbeidsforhold.setNaturalytelseEndringDato(finnDatoVerdiAvUtenTidSone(localDate.toString())));
                Optional.ofNullable(dto.getNaturalYtelseDto().getNaturalYtelseDagsats()).ifPresent(arbeidsforhold::setNaturalytelseNyDagsats);
            }
            liste.getArbeidsforhold().add(arbeidsforhold);
        });
        return liste;
    }
*/

    private BeregningsgrunnlagRegelListeType konverterBeregningsgrunnlagRegelListe(List<DokumentTypeData> dokumentTypeDataListe) {
        BeregningsgrunnlagRegelListeType liste = objectFactory.createBeregningsgrunnlagRegelListeType();
/*        for (DokumentTypeData data : finnListeMedVerdierAv("PLACEHOLDER", dokumentTypeDataListe)) {
            String feltNavnMedIndeks = data.getDoksysId();
            String strukturertFelt = finnStrukturertVerdiAv(feltNavnMedIndeks, dokumentTypeDataListe);
            BeregningsgrunnlagRegelDto dto = FlettefeltJsonObjectMapper.readValue(strukturertFelt, BeregningsgrunnlagRegelDto.class);
            BeregningsgrunnlagRegelType beregningsgrunnlagRegel = objectFactory.createBeregningsgrunnlagRegelType();
            beregningsgrunnlagRegel.setRegelStatus(tilStatusTypeKode(dto.getStatus()));
            beregningsgrunnlagRegel.setAntallArbeidsgivereIBeregningUtenEtterlønnSluttpakke(BigInteger.valueOf(dto.getAntallArbeidsgivereIBeregning()));
            beregningsgrunnlagRegel.setBesteBeregning(dto.getBesteBeregning());
            beregningsgrunnlagRegel.setSNNyoppstartet(Boolean.parseBoolean(dto.getSNnyoppstartet()));
            beregningsgrunnlagRegel.setAndelListe(fraAndelListe(dto.getBeregningsgrunnlagAndelDto()));
            liste.getBeregningsgrunnlagRegel().add(beregningsgrunnlagRegel);
        }*/
        return liste;
    }
/*

    private AndelListeType fraAndelListe(List<BeregningsgrunnlagAndelDto> beregningsgrunnlagAndelDto) {
        AndelListeType liste = objectFactory.createAndelListeType();
        beregningsgrunnlagAndelDto.forEach(dto -> {
            AndelType andel = objectFactory.createAndelType();
            andel.setStatus(tilStatusTypeKode(dto.getStatus()));
            andel.setDagsats(Long.parseLong(dto.getDagsats()));
            andel.setMånedsinntekt(Long.parseLong(dto.getMånedsinntekt()));
            andel.setÅrsinntekt(Long.parseLong(dto.getÅrsinntekt()));
            if (StatusTypeKode.ARBEIDSTAKER.equals(andel.getStatus())) {
                andel.setArbeidsgiverNavn(dto.getArbeidsgiverNavn());
            } else if (StatusTypeKode.SELVSTENDIG_NÆRINGSDRIVENDE.equals(andel.getStatus())) {
                Optional.ofNullable(dto.getSisteLignedeÅr()).map(Long::parseLong).ifPresent(andel::setSisteLignedeÅr);
                Optional.ofNullable(dto.getPensjonsgivendeInntekt()).map(Long::parseLong).ifPresent(andel::setPensjonsgivendeInntekt);
            }
            andel.setEtterlønnSluttpakke(Boolean.parseBoolean(dto.getEtterlønnSluttpakke()));
            liste.getAndel().add(andel);
        });
        return liste;
    }
*/

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

    private RelasjonskodeKode tilRelasjonskode(String brukerRolle, String navBrukerKjønn) {
        if (RelasjonsRolleType.MORA.getKode().equals(brukerRolle)) {
            return RelasjonskodeKode.MOR;
        } else if (NavBrukerKjønn.MANN.getKode().equals(navBrukerKjønn)) {
            return RelasjonskodeKode.FAR;
        } else {
            return RelasjonskodeKode.MEDMOR;
        }
    }
}
