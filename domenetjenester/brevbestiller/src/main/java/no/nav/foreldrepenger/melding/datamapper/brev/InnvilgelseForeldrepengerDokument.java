package no.nav.foreldrepenger.melding.datamapper.brev;

import java.util.List;

import no.nav.foreldrepenger.melding.aktør.PersonstatusType;
import no.nav.foreldrepenger.melding.datamapper.mal.DokumentType;
import no.nav.foreldrepenger.melding.datamapper.mal.Flettefelt;

@Deprecated
public class InnvilgelseForeldrepengerDokument implements DokumentType {
    //Denne blir slette etter alle felter er mappet
    public static final String DEKNINGSGRAD = "dekningsgrad";
    public static final String DAGSATS = "dagsats";
    public static final String MÅNEDSBELØP = "månedsbeløp";
    public static final String STØNADSPERIODEFOM = "stønadsperiodeFom";
    public static final String STØNADSPERIODETOM = "stønadsperiodeTom";
    public static final String TOTALBRUKERANDEL = "totalBrukerAndel";
    public static final String TOTALARBEIDSGIVERANDEL = "totalArbeidsgiverAndel";
    public static final String ANTALLARBEIDSGIVERE = "antallArbeidsgivere";
    public static final String ANNENFORELDERHARRETT = "annenForelderHarRett";
    public static final String ALENEOMSORG = "aleneomsorg";
    public static final String BARNERFØDT = "barnErFødt";
    public static final String RE_FØDSELSHENDELSE = "fødselsHendelse";
    public static final String DAGERTAPTFØRTERMIN = "dagerTaptFørTermin";
    public static final String ANTALLPERIODER = "antallPerioder";
    public static final String IKKE_OMSORG = "ikkeOmsorg";
    public static final String ANTALLAVSLAG = "antallAvslag";
    public static final String SISTEUTBETALINGSDAG = "sisteUtbetalingsdag";
    public static final String SISTEDAGMEDUTSETTELSE = "sisteDagMedUtsettelse";
    public static final String DISPONIBLEDAGER = "disponibleDager";
    public static final String DISPONIBLEFELLESDAGER = "disponibleFellesDager";
    public static final String SEKSG = "seksG";
    public static final String LOVHJEMMEL_VURDERING = "lovhjemmelVurdering";
    public static final String LOVHJEMMEL_BEREGNING = "lovhjemmelBeregning";
    public static final String BRUTTOBEREGNINGSGRUNNLAG = "bruttoBeregningsgrunnlag";
    public static final String BEREGNINGSGRUNNLAGREGEL = "beregningsgrunnlagRegel";
    public static final String FORELDREPENGEPERIODENUTVIDETUKER = "foreldrepengeperiodenUtvidetUker";
    public static final String INNTEKTOVERSEKSG = "inntektOverSeksG";
    public static final String ANTALLINNVILGET = "antallInnvilget";
    public static final String GRADERINGFINNES = "graderingFinnes";
    public static final String SISTEDAGAVSISTEPERIODE = "sisteDagAvSistePeriode";
    public static final String INNTEKTMOTTATTARBGIVER = "inntektMottattArbgiver";
    public static final String BEHANDLINGSRESULTAT = "behandlingsResultat";
    public static final String OVERBETALING = "overbetaling";
    public static final String KONSEKVENSFORYTELSE = "konsekvensForYtelse";
    public static final String OVERSTYRT_BEREGNING = "overstyrBeregning";
    public static final String ANTALL_BG_REGLER = "antallBgRegler";
    public static final String FOR_MYE_UTBETALT = "forMyeUtbetalt";


    //Felles
    public static final String BEHANDLINGSTYPE = "behandlingstype";
    public static final String SØKERSNAVN = "sokersNavn";
    public static final String PERSON_STATUS = "personstatus";
    public static final String MOTTATT_DATO = "mottattDato";
    public static final String RELASJONSKODE = "relasjonskode";
    public static final String GJELDER_FØDSEL = "gjelderFoedsel";
    public static final String ANTALL_BARN = "antallBarn";
    public static final String HALV_G = "halvG";
    public static final String SISTE_DAG_I_FELLES_PERIODE = "sisteDagIFellesPeriode";
    public static final String YTELSE_TYPE = "ytelseType";
    public static final String FØDSELSDATO_PASSERT = "foedseldatoPassert";
    public static final String FRITEKST = "fritekst";
    public static final String VILKÅR_TYPE = "vilkaarType";
    public static final String KJØNN = "kjoenn";
    public static final String FRIST_DATO = "fristDato";
    public static final String BELØP = "belop";
    public static final String DIFFERANSE = "differanse";
    //Perioder
    public static final String PERIODE = "periode";
    //Avslag, opphør
    public static final String AVSLAGSAARSAK = "avslagsAarsak";
    public static final String LOV_HJEMMEL_FOR_AVSLAG = "lovhjemmelForAvslag";
    public static final String STONADSDATO_FOM = "fomStonadsdato";
    public static final String STONADSDATO_TOM = "tomStonadsdato";
    public static final String OPPHORDATO = "opphorDato";
    public static final String DODSDATO = "dodsdato";
    //Konfig
    public static final String KLAGE_FRIST_UKER = "klageFristUker";
    public static final String UKER_ETTER_FELLES_PERIODE = "ukerEtterfellesPeriode";
    //Innhentopplysninger
    public static final String SØKNAD_DATO = "soknadDato";
    //Forlenget
    public static final String SOKNAD_DATO = "soknadsdato";
    public static final String BEHANDLINGSFRIST_UKER = "behandlingsfristUker";
    public static final String FORLENGET_BEHANDLINGSFRIST = "forlengetBehandlingsfrist";
    public static final String VARIANT = "variant";
    //Innsynskrav svar
    public static final String INNSYN_RESULTAT_TYPE = "innsynResultatType";
    //Inntektsmelding kommet før søknad
    public static final String ARBEIDSGIVER_NAVN = "arbeidsgiverNavn";
    public static final String PERIODE_LISTE = "periodeListe";
    public static final String SOK_ANTALL_UKER_FOR = "sokAntallUkerFor";
    //Klage
    public static final String AVVIST_GRUNN_LISTE = "avvistGrunnListe";
    public static final String OPPHEVET = "opphevet";
    public static final String ANTALL_UKER = "antallUker";
    //Revurdering
    public static final String TERMIN_DATO = "terminDato";
    public static final String ADVARSEL_KODE = "advarselKode";
    public static final String ENDRING_I_FREMTID = "endringIFremtid";
    //Fritekstbrev
    public static final String HOVED_OVERSKRIFT = "hovedoverskrift";
    public static final String BRØDTEKST = "brødtekst";
    //Medhold
    public static final String OPPHAV_TYPE = "opphavType";


    @Override
    public String getDokumentMalType() {
        return null;
    }

    @Override
    public List<Flettefelt> getFlettefelter(DokumentType dokumentType) {
        return null;
    }

    @Override
    public String getPersonstatusVerdi(PersonstatusType personstatus) {
        return null;
    }
}
