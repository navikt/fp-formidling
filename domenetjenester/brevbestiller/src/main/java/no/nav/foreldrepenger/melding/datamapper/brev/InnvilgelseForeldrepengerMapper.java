package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.DokumentTypeFelles.finnDatoVerdiAvUtenTidSone;
import static no.nav.foreldrepenger.melding.datamapper.DokumentTypeFelles.finnOptionalDatoVerdiAvUtenTidSone;
import static no.nav.foreldrepenger.melding.datamapper.DokumentTypeFelles.finnOptionalVerdiAv;
import static no.nav.foreldrepenger.melding.datamapper.DokumentTypeFelles.finnVerdiAv;

import java.math.BigInteger;
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
import no.nav.vedtak.felles.integrasjon.felles.ws.JaxbHelper;

@ApplicationScoped
@Named(DokumentMalType.INNVILGELSE_FORELDREPENGER_DOK)
public class InnvilgelseForeldrepengerMapper implements DokumentTypeMapper {
    public static final String ENDRING_BEREGNING_OG_UTTAK = "ENDRING_BEREGNING_OG_UTTAK";

    private ObjectFactory objectFactory;
    private BehandlingMapper behandlingMapper;
    BeregningsresultatMapper beregningsresultatMapper;
    private FamiliehendelseMapper familiehendelseMapper;
    private BeregningsgrunnlagMapper beregningsgrunnlagMapper;

    public InnvilgelseForeldrepengerMapper() {
        //CDI
    }

    @Inject
    public InnvilgelseForeldrepengerMapper(BehandlingMapper behandlingMapper,
                                           BeregningsresultatMapper beregningsresultatMapper,
                                           BeregningsgrunnlagMapper beregningsgrunnlagMapper,
                                           FamiliehendelseMapper familiehendelseMapper) {
        this.behandlingMapper = behandlingMapper;
        this.beregningsresultatMapper = beregningsresultatMapper;
        this.familiehendelseMapper = familiehendelseMapper;
        this.beregningsgrunnlagMapper = beregningsgrunnlagMapper;
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
        FagType fagType = mapFagType(dokumentFelles.getDokumentTypeDataListe(), behandling, beregningsresultatFP, familieHendelse, beregningsgrunnlag, originaltBeregningsgrunnlag);
        JAXBElement<BrevdataType> brevdataTypeJAXBElement = mapintoBrevdataType(fellesType, fagType);
        return JaxbHelper.marshalNoNamespaceXML(InnvilgetForeldrepengerConstants.JAXB_CLASS, brevdataTypeJAXBElement, InnvilgetForeldrepengerConstants.XSD_LOCATION);
    }

    private FagType mapFagType(List<DokumentTypeData> dokumentTypeDataListe, Behandling behandling, BeregningsresultatFP beregningsresultatFP, FamilieHendelse familieHendelse, Beregningsgrunnlag beregningsgrunnlag, Beregningsgrunnlag originaltBeregningsgrunnlag) {
        final FagType fagType = objectFactory.createFagType();

        //Obligatoriske felter
        //Faktisk mappet
        fagType.setBehandlingsType(behandlingMapper.utledBehandlingsTypeInnvilgetFP(behandling));
        fagType.setAntallArbeidsgivere(beregningsresultatMapper.antallArbeidsgivere(beregningsresultatFP));
        fagType.setAntallBarn(familieHendelse.getAntallBarn());
        fagType.setBarnErFødt(familieHendelse.isBarnErFødt());
        fagType.setFødselsHendelse(BehandlingMapper.erRevurderingPgaFødselshendelse(behandling));
        fagType.setOverbetaling(BeregningsgrunnlagMapper.erOverbetalt(beregningsgrunnlag, originaltBeregningsgrunnlag));
        fagType.setBruttoBeregningsgrunnlag(BeregningsgrunnlagMapper.finnBrutto(beregningsgrunnlag));
        BeregningsgrunnlagRegelListeType beregningsgrunnlagRegelListe = BeregningsgrunnlagMapper.mapRegelListe(beregningsgrunnlag);
        fagType.setBeregningsgrunnlagRegelListe(beregningsgrunnlagRegelListe);
        fagType.setAntallBeregningsgrunnlagRegeler(BigInteger.valueOf(beregningsgrunnlagRegelListe.getBeregningsgrunnlagRegel().size()));

        //Periodelister
        fagType.setIkkeOmsorg(Boolean.parseBoolean((finnVerdiAv(InnvilgelseForeldrepengerDokument.IKKE_OMSORG, dokumentTypeDataListe))));
        //Settes basert på Perioder, eventuelt søknad
        fagType.setAnnenForelderHarRett(Boolean.parseBoolean(finnVerdiAv(InnvilgelseForeldrepengerDokument.ANNENFORELDERHARRETT, dokumentTypeDataListe)));
        fagType.setGjelderFoedsel(Boolean.parseBoolean(finnVerdiAv(InnvilgelseForeldrepengerDokument.GJELDER_FØDSEL, dokumentTypeDataListe)));

        //Kan ikke mappes
        fagType.setAleneomsorg(VurderingsstatusKode.fromValue("IKKE_VURDERT"));

        fagType.setDagerTaptFørTermin(BigInteger.valueOf(Integer.parseInt(finnVerdiAv(InnvilgelseForeldrepengerDokument.DAGERTAPTFØRTERMIN, dokumentTypeDataListe))));
        fagType.setDagsats(Long.parseLong(finnVerdiAv(InnvilgelseForeldrepengerDokument.DAGSATS, dokumentTypeDataListe)));
        fagType.setDekningsgrad(BigInteger.valueOf(Integer.parseInt(finnVerdiAv(InnvilgelseForeldrepengerDokument.DEKNINGSGRAD, dokumentTypeDataListe))));
        fagType.setDisponibleDager(BigInteger.valueOf(Integer.parseInt(finnVerdiAv(InnvilgelseForeldrepengerDokument.DISPONIBLEDAGER, dokumentTypeDataListe))));
        fagType.setDisponibleFellesDager(BigInteger.valueOf(Integer.parseInt(finnVerdiAv(InnvilgelseForeldrepengerDokument.DISPONIBLEFELLESDAGER, dokumentTypeDataListe))));
        fagType.setInntektOverSeksG(Boolean.parseBoolean(finnVerdiAv(InnvilgelseForeldrepengerDokument.INNTEKTOVERSEKSG, dokumentTypeDataListe)));
        fagType.setAntallInnvilget(BigInteger.valueOf(Integer.parseInt(finnVerdiAv(InnvilgelseForeldrepengerDokument.ANTALLINNVILGET, dokumentTypeDataListe))));
        fagType.setAntallAvslag(BigInteger.valueOf(Integer.parseInt(finnVerdiAv(InnvilgelseForeldrepengerDokument.ANTALLAVSLAG, dokumentTypeDataListe))));
        fagType.setGraderingFinnes(Boolean.parseBoolean(finnVerdiAv(InnvilgelseForeldrepengerDokument.GRADERINGFINNES, dokumentTypeDataListe)));
        fagType.setKlageFristUker(BigInteger.valueOf(Integer.parseInt(finnVerdiAv(InnvilgelseForeldrepengerDokument.KLAGE_FRIST_UKER, dokumentTypeDataListe))));
        LovhjemmelType lovhjemmelType = objectFactory.createLovhjemmelType();
        lovhjemmelType.setBeregning(finnVerdiAv(InnvilgelseForeldrepengerDokument.LOVHJEMMEL_BEREGNING, dokumentTypeDataListe));
        lovhjemmelType.setVurdering(finnVerdiAv(InnvilgelseForeldrepengerDokument.LOVHJEMMEL_VURDERING, dokumentTypeDataListe));
        fagType.setLovhjemmel(lovhjemmelType);
        fagType.setMottattDato(finnDatoVerdiAvUtenTidSone(InnvilgelseForeldrepengerDokument.MOTTATT_DATO, dokumentTypeDataListe));
        fagType.setMånedsbeløp(Long.parseLong(finnVerdiAv(InnvilgelseForeldrepengerDokument.MÅNEDSBELØP, dokumentTypeDataListe)));
        fagType.setPersonstatus(PersonstatusKode.fromValue(finnVerdiAv(InnvilgelseForeldrepengerDokument.PERSON_STATUS, dokumentTypeDataListe)));
        fagType.setRelasjonskode(tilRelasjonskode(finnVerdiAv(InnvilgelseForeldrepengerDokument.RELASJONSKODE, dokumentTypeDataListe), finnVerdiAv(InnvilgelseForeldrepengerDokument.KJØNN, dokumentTypeDataListe)));
        fagType.setSeksG(Long.parseLong(finnVerdiAv(InnvilgelseForeldrepengerDokument.SEKSG, dokumentTypeDataListe)));
        fagType.setSisteDagAvSistePeriode(finnDatoVerdiAvUtenTidSone(InnvilgelseForeldrepengerDokument.SISTEDAGAVSISTEPERIODE, dokumentTypeDataListe));
        fagType.setSokersNavn(finnVerdiAv(InnvilgelseForeldrepengerDokument.SØKERSNAVN, dokumentTypeDataListe));
        fagType.setStønadsperiodeFom(finnDatoVerdiAvUtenTidSone(InnvilgelseForeldrepengerDokument.STØNADSPERIODEFOM, dokumentTypeDataListe));
        fagType.setStønadsperiodeTom(finnDatoVerdiAvUtenTidSone(InnvilgelseForeldrepengerDokument.STØNADSPERIODETOM, dokumentTypeDataListe));
        fagType.setTotalArbeidsgiverAndel(Long.parseLong(finnVerdiAv(InnvilgelseForeldrepengerDokument.TOTALARBEIDSGIVERANDEL, dokumentTypeDataListe)));
        fagType.setTotalBrukerAndel(Long.parseLong(finnVerdiAv(InnvilgelseForeldrepengerDokument.TOTALBRUKERANDEL, dokumentTypeDataListe)));
        fagType.setKonsekvensForYtelse(tilKonsekvensForYtelseKode(finnVerdiAv(InnvilgelseForeldrepengerDokument.KONSEKVENSFORYTELSE, dokumentTypeDataListe)));
        fagType.setBehandlingsResultat(tilBehandlingsResultatKode(finnVerdiAv(InnvilgelseForeldrepengerDokument.BEHANDLINGSRESULTAT, dokumentTypeDataListe)));
        fagType.setInntektMottattArbgiver(Boolean.parseBoolean(finnVerdiAv(InnvilgelseForeldrepengerDokument.INNTEKTMOTTATTARBGIVER, dokumentTypeDataListe)));
        PeriodeListeType konvertertPeriodeListe = konverterPeriodeListe(dokumentTypeDataListe);
        fagType.setPeriodeListe(konvertertPeriodeListe);
        fagType.setAntallPerioder(BigInteger.valueOf(konvertertPeriodeListe.getPeriode().size()));
        fagType.setForMyeUtbetalt(UtbetaltKode.fromValue(finnVerdiAv(InnvilgelseForeldrepengerDokument.FOR_MYE_UTBETALT, dokumentTypeDataListe)));

        //ikke obligatoriske felter
        finnOptionalDatoVerdiAvUtenTidSone(InnvilgelseForeldrepengerDokument.SISTE_DAG_I_FELLES_PERIODE, dokumentTypeDataListe).ifPresent(fagType::setSisteDagIFellesPeriode);
        finnOptionalDatoVerdiAvUtenTidSone(InnvilgelseForeldrepengerDokument.SISTEDAGMEDUTSETTELSE, dokumentTypeDataListe).ifPresent(fagType::setSisteDagMedUtsettelse);
        finnOptionalDatoVerdiAvUtenTidSone(InnvilgelseForeldrepengerDokument.SISTEUTBETALINGSDAG, dokumentTypeDataListe).ifPresent(fagType::setSisteUtbetalingsdag);
        finnOptionalVerdiAv(InnvilgelseForeldrepengerDokument.FORELDREPENGEPERIODENUTVIDETUKER, dokumentTypeDataListe).map(BigInteger::new).ifPresent(fagType::setForeldrepengeperiodenUtvidetUker);
        finnOptionalVerdiAv(InnvilgelseForeldrepengerDokument.FRITEKST, dokumentTypeDataListe).ifPresent(fagType::setFritekst);

        return fagType;
    }

    private PeriodeListeType konverterPeriodeListe(List<DokumentTypeData> dokumentTypeDataListe) {
        PeriodeListeType liste = objectFactory.createPeriodeListeType();
//        List<PeriodeDto> periodeDtos = new ArrayList<>();
//        for (DokumentTypeData data : finnListeMedVerdierAv(InnvilgelseForeldrepengerDokument.PERIODE, dokumentTypeDataListe)) {
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
/*        for (DokumentTypeData data : finnListeMedVerdierAv(InnvilgelseForeldrepengerDokument.BEREGNINGSGRUNNLAGREGEL, dokumentTypeDataListe)) {
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
