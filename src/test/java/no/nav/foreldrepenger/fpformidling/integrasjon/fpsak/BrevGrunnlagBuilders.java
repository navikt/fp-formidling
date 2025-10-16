package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak;

import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.Barn;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.BehandlingType;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.Behandlingsresultat;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.BehandlingÅrsakType;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.Dekningsgrad;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.FagsakYtelseType;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.FamilieHendelse;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.Foreldrepenger;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.InnsynBehandling;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.Inntektsmelding;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.KlageBehandling;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.OriginalBehandling;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.PeriodeResultatType;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.RelasjonsRolleType;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.Rettigheter;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.Språkkode;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.Svangerskapspenger;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.TilkjentYtelse;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.UttakArbeidType;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.Verge;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagDto;
import no.nav.foreldrepenger.kontrakter.fpsak.inntektsmeldinger.ArbeidsforholdInntektsmeldingerDto;
import no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseDagytelseDto;
import no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseEngangsstønadDto;

public class BrevGrunnlagBuilders {

    public static BrevGrunnlagBuilder brevGrunnlag() {
        return new BrevGrunnlagBuilder();
    }

    public static class BrevGrunnlagBuilder {
        private UUID uuid;
        private String saksnummer;
        private FagsakYtelseType fagsakYtelseType;
        private RelasjonsRolleType relasjonsRolleType;
        private String aktørId;
        private BehandlingType behandlingType;
        private LocalDateTime opprettet;
        private LocalDateTime avsluttet;
        private String behandlendeEnhet;
        private Språkkode språkkode;
        private boolean automatiskBehandlet;
        private FamilieHendelse familieHendelse;
        private OriginalBehandling originalBehandling;
        private Behandlingsresultat behandlingsresultat;
        private List<BehandlingÅrsakType> behandlingÅrsakTyper;
        private TilkjentYtelse tilkjentYtelse;
        private BeregningsgrunnlagDto beregningsgrunnlag;
        private ArbeidsforholdInntektsmeldingerDto inntektsmeldingerStatus;
        private LocalDate førsteSøknadMottattDato;
        private LocalDate sisteSøknadMottattDato;
        private LocalDate søknadMottattDato;
        private List<Inntektsmelding> inntektsmeldinger;
        private Verge verge;
        private KlageBehandling klageBehandling;
        private InnsynBehandling innsynBehandling;
        private Svangerskapspenger svangerskapspenger;
        private Foreldrepenger foreldrepenger;

        public BrevGrunnlagBuilder uuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public BrevGrunnlagBuilder saksnummer(String saksnummer) {
            this.saksnummer = saksnummer;
            return this;
        }

        public BrevGrunnlagBuilder fagsakYtelseType(FagsakYtelseType fagsakYtelseType) {
            this.fagsakYtelseType = fagsakYtelseType;
            return this;
        }

        public BrevGrunnlagBuilder relasjonsRolleType(RelasjonsRolleType relasjonsRolleType) {
            this.relasjonsRolleType = relasjonsRolleType;
            return this;
        }

        public BrevGrunnlagBuilder aktørId(String aktørId) {
            this.aktørId = aktørId;
            return this;
        }

        public BrevGrunnlagBuilder behandlingType(BehandlingType behandlingType) {
            this.behandlingType = behandlingType;
            return this;
        }

        public BrevGrunnlagBuilder opprettet(LocalDateTime opprettet) {
            this.opprettet = opprettet;
            return this;
        }

        public BrevGrunnlagBuilder avsluttet(LocalDateTime avsluttet) {
            this.avsluttet = avsluttet;
            return this;
        }

        public BrevGrunnlagBuilder behandlendeEnhet(String behandlendeEnhet) {
            this.behandlendeEnhet = behandlendeEnhet;
            return this;
        }

        public BrevGrunnlagBuilder språkkode(Språkkode språkkode) {
            this.språkkode = språkkode;
            return this;
        }

        public BrevGrunnlagBuilder automatiskBehandlet(boolean automatiskBehandlet) {
            this.automatiskBehandlet = automatiskBehandlet;
            return this;
        }

        public BrevGrunnlagBuilder familieHendelse(FamilieHendelse familieHendelse) {
            this.familieHendelse = familieHendelse;
            return this;
        }

        public BrevGrunnlagBuilder originalBehandling(OriginalBehandling originalBehandling) {
            this.originalBehandling = originalBehandling;
            return this;
        }

        public BrevGrunnlagBuilder behandlingsresultat(Behandlingsresultat behandlingsresultat) {
            this.behandlingsresultat = behandlingsresultat;
            return this;
        }

        public BrevGrunnlagBuilder behandlingÅrsakTyper(List<BehandlingÅrsakType> behandlingÅrsakTyper) {
            this.behandlingÅrsakTyper = behandlingÅrsakTyper;
            return this;
        }

        public BrevGrunnlagBuilder tilkjentYtelse(TilkjentYtelse tilkjentYtelse) {
            this.tilkjentYtelse = tilkjentYtelse;
            return this;
        }

        public BrevGrunnlagBuilder beregningsgrunnlag(BeregningsgrunnlagDto beregningsgrunnlag) {
            this.beregningsgrunnlag = beregningsgrunnlag;
            return this;
        }

        public BrevGrunnlagBuilder inntektsmeldingerStatus(ArbeidsforholdInntektsmeldingerDto inntektsmeldingerStatus) {
            this.inntektsmeldingerStatus = inntektsmeldingerStatus;
            return this;
        }

        public BrevGrunnlagBuilder førsteSøknadMottattDato(LocalDate førsteSøknadMottattDato) {
            this.førsteSøknadMottattDato = førsteSøknadMottattDato;
            return this;
        }

        public BrevGrunnlagBuilder sisteSøknadMottattDato(LocalDate sisteSøknadMottattDato) {
            this.sisteSøknadMottattDato = sisteSøknadMottattDato;
            return this;
        }

        public BrevGrunnlagBuilder søknadMottattDato(LocalDate søknadMottattDato) {
            this.søknadMottattDato = søknadMottattDato;
            return this;
        }

        public BrevGrunnlagBuilder inntektsmeldinger(List<Inntektsmelding> inntektsmeldinger) {
            this.inntektsmeldinger = inntektsmeldinger;
            return this;
        }

        public BrevGrunnlagBuilder verge(Verge verge) {
            this.verge = verge;
            return this;
        }

        public BrevGrunnlagBuilder klageBehandling(KlageBehandling klageBehandling) {
            this.klageBehandling = klageBehandling;
            return this;
        }

        public BrevGrunnlagBuilder innsynBehandling(InnsynBehandling innsynBehandling) {
            this.innsynBehandling = innsynBehandling;
            return this;
        }

        public BrevGrunnlagBuilder svangerskapspenger(Svangerskapspenger svangerskapspenger) {
            this.svangerskapspenger = svangerskapspenger;
            return this;
        }

        public BrevGrunnlagBuilder foreldrepenger(Foreldrepenger foreldrepenger) {
            this.foreldrepenger = foreldrepenger;
            return this;
        }

        public BrevGrunnlagDto build() {
            return new BrevGrunnlagDto(uuid, saksnummer, fagsakYtelseType, relasjonsRolleType, aktørId, behandlingType, opprettet, avsluttet,
                behandlendeEnhet, språkkode, automatiskBehandlet, familieHendelse, originalBehandling, behandlingsresultat, behandlingÅrsakTyper,
                tilkjentYtelse, beregningsgrunnlag, inntektsmeldingerStatus, førsteSøknadMottattDato, sisteSøknadMottattDato, søknadMottattDato,
                inntektsmeldinger, verge, klageBehandling, innsynBehandling, svangerskapspenger, foreldrepenger);
        }
    }

    public static OriginalBehandlingBuilder originalBehandling() {
        return new OriginalBehandlingBuilder();
    }

    public static class OriginalBehandlingBuilder {
        private FamilieHendelse familieHendelse;
        private Behandlingsresultat.BehandlingResultatType originalBehandlingResultatType;
        private LocalDate førsteDagMedUtbetaltForeldrepenger;

        public OriginalBehandlingBuilder familieHendelse(FamilieHendelse familieHendelse) {
            this.familieHendelse = familieHendelse;
            return this;
        }

        public OriginalBehandlingBuilder originalBehandlingResultatType(Behandlingsresultat.BehandlingResultatType originalBehandlingResultatType) {
            this.originalBehandlingResultatType = originalBehandlingResultatType;
            return this;
        }

        public OriginalBehandlingBuilder førsteDagMedUtbetaltForeldrepenger(LocalDate førsteDagMedUtbetaltForeldrepenger) {
            this.førsteDagMedUtbetaltForeldrepenger = førsteDagMedUtbetaltForeldrepenger;
            return this;
        }

        public OriginalBehandling build() {
            return new OriginalBehandling(familieHendelse, originalBehandlingResultatType, førsteDagMedUtbetaltForeldrepenger);
        }
    }

    public static TilkjentYtelseBuilder tilkjentYtelse() {
        return new TilkjentYtelseBuilder();
    }

    public static class TilkjentYtelseBuilder {
        private TilkjentYtelseEngangsstønadDto engangsstønad;
        private TilkjentYtelseEngangsstønadDto originalBehandlingEngangsstønad;
        private TilkjentYtelseDagytelseDto dagytelse;

        public TilkjentYtelseBuilder engangsstønad(TilkjentYtelseEngangsstønadDto engangsstønad) {
            this.engangsstønad = engangsstønad;
            return this;
        }

        public TilkjentYtelseBuilder originalBehandlingEngangsstønad(TilkjentYtelseEngangsstønadDto originalBehandlingEngangsstønad) {
            this.originalBehandlingEngangsstønad = originalBehandlingEngangsstønad;
            return this;
        }

        public TilkjentYtelseBuilder dagytelse(TilkjentYtelseDagytelseDto dagytelse) {
            this.dagytelse = dagytelse;
            return this;
        }

        public TilkjentYtelse build() {
            return new TilkjentYtelse(engangsstønad, originalBehandlingEngangsstønad, dagytelse);
        }
    }

    public static ForeldrepengerBuilder foreldrepenger() {
        return new ForeldrepengerBuilder();
    }

    public static class ForeldrepengerBuilder {
        private List<Foreldrepenger.Stønadskonto> stønadskontoer;
        private Integer tapteDagerFpff;
        private List<Foreldrepenger.Uttaksperiode> perioderSøker;
        private List<Foreldrepenger.Uttaksperiode> perioderAnnenpart;
        private Boolean ønskerJustertUttakVedFødsel;
        private Rettigheter rettigheter;
        private Dekningsgrad dekningsgrad;
        private LocalDate nyStartDatoVedUtsattOppstart;

        public ForeldrepengerBuilder rettigheter(Rettigheter rettigheter) {
            this.rettigheter = rettigheter;
            return this;
        }

        public ForeldrepengerBuilder dekningsgrad(Dekningsgrad dekningsgrad) {
            this.dekningsgrad = dekningsgrad;
            return this;
        }

        public ForeldrepengerBuilder nyStartDatoVedUtsattOppstart(LocalDate nyStartDatoVedUtsattOppstart) {
            this.nyStartDatoVedUtsattOppstart = nyStartDatoVedUtsattOppstart;
            return this;
        }

        public ForeldrepengerBuilder stønadskontoer(List<Foreldrepenger.Stønadskonto> stønadskontoer) {
            this.stønadskontoer = stønadskontoer;
            return this;
        }

        public ForeldrepengerBuilder tapteDagerFpff(int tapteDagerFpff) {
            this.tapteDagerFpff = tapteDagerFpff;
            return this;
        }

        public ForeldrepengerBuilder perioderSøker(List<Foreldrepenger.Uttaksperiode> perioderSøker) {
            this.perioderSøker = perioderSøker;
            return this;
        }

        public ForeldrepengerBuilder perioderAnnenpart(List<Foreldrepenger.Uttaksperiode> perioderAnnenpart) {
            this.perioderAnnenpart = perioderAnnenpart;
            return this;
        }

        public ForeldrepengerBuilder ønskerJustertUttakVedFødsel(boolean ønskerJustertUttakVedFødsel) {
            this.ønskerJustertUttakVedFødsel = ønskerJustertUttakVedFødsel;
            return this;
        }

        public Foreldrepenger build() {
            return new Foreldrepenger(dekningsgrad, rettigheter, stønadskontoer, tapteDagerFpff == null ? 0 : tapteDagerFpff, perioderSøker,
                perioderAnnenpart, ønskerJustertUttakVedFødsel != null && ønskerJustertUttakVedFødsel, nyStartDatoVedUtsattOppstart);
        }
    }

    public static ForeldrepengerUttakPeriodeBuilder foreldrepengerUttakPeriode() {
        return new ForeldrepengerUttakPeriodeBuilder();
    }

    public static class ForeldrepengerUttakPeriodeBuilder {
        private LocalDate fom;
        private LocalDate tom;
        private List<Foreldrepenger.Aktivitet> aktiviteter;
        private PeriodeResultatType periodeResultatType;
        private String periodeResultatÅrsak;
        private String graderingAvslagÅrsak;
        private String periodeResultatÅrsakLovhjemmel;
        private String graderingsAvslagÅrsakLovhjemmel;
        private LocalDate tidligstMottattDato;
        private Boolean erUtbetalingRedusertTilMorsStillingsprosent;

        public ForeldrepengerUttakPeriodeBuilder fom(LocalDate fom) {
            this.fom = fom;
            return this;
        }

        public ForeldrepengerUttakPeriodeBuilder tom(LocalDate tom) {
            this.tom = tom;
            return this;
        }

        public ForeldrepengerUttakPeriodeBuilder aktiviteter(List<Foreldrepenger.Aktivitet> aktiviteter) {
            this.aktiviteter = aktiviteter;
            return this;
        }

        public ForeldrepengerUttakPeriodeBuilder periodeResultatType(PeriodeResultatType periodeResultatType, String periodeResultatÅrsak) {
            this.periodeResultatType = periodeResultatType;
            return periodeResultatÅrsak(periodeResultatÅrsak);
        }

        public ForeldrepengerUttakPeriodeBuilder periodeResultatÅrsak(String periodeResultatÅrsak) {
            this.periodeResultatÅrsak = periodeResultatÅrsak;
            return this;
        }

        public ForeldrepengerUttakPeriodeBuilder graderingAvslagÅrsak(String graderingAvslagÅrsak) {
            this.graderingAvslagÅrsak = graderingAvslagÅrsak;
            return this;
        }

        public ForeldrepengerUttakPeriodeBuilder periodeResultatÅrsakLovhjemmel(String periodeResultatÅrsakLovhjemmel) {
            this.periodeResultatÅrsakLovhjemmel = periodeResultatÅrsakLovhjemmel;
            return this;
        }

        public ForeldrepengerUttakPeriodeBuilder graderingsAvslagÅrsakLovhjemmel(String graderingsAvslagÅrsakLovhjemmel) {
            this.graderingsAvslagÅrsakLovhjemmel = graderingsAvslagÅrsakLovhjemmel;
            return this;
        }

        public ForeldrepengerUttakPeriodeBuilder tidligstMottattDato(LocalDate tidligstMottattDato) {
            this.tidligstMottattDato = tidligstMottattDato;
            return this;
        }

        public ForeldrepengerUttakPeriodeBuilder erUtbetalingRedusertTilMorsStillingsprosent(boolean erUtbetalingRedusertTilMorsStillingsprosent) {
            this.erUtbetalingRedusertTilMorsStillingsprosent = erUtbetalingRedusertTilMorsStillingsprosent;
            return this;
        }

        public Foreldrepenger.Uttaksperiode build() {
            return new Foreldrepenger.Uttaksperiode(fom, tom, aktiviteter, periodeResultatType, periodeResultatÅrsak, graderingAvslagÅrsak,
                periodeResultatÅrsakLovhjemmel, graderingsAvslagÅrsakLovhjemmel, tidligstMottattDato,
                erUtbetalingRedusertTilMorsStillingsprosent != null && erUtbetalingRedusertTilMorsStillingsprosent);
        }
    }

    public static ForeldrepengerUttakAktivitetBuilder foreldrepengerUttakAktivitet() {
        return new ForeldrepengerUttakAktivitetBuilder();
    }

    public static class ForeldrepengerUttakAktivitetBuilder {
        private Foreldrepenger.TrekkontoType trekkontoType;
        private BigDecimal trekkdager;
        private BigDecimal prosentArbeid;
        private String arbeidsgiverReferanse;
        private String arbeidsforholdId;
        private BigDecimal utbetalingsgrad;
        private UttakArbeidType uttakArbeidType;
        private Boolean gradering;

        public ForeldrepengerUttakAktivitetBuilder trekkontoType(Foreldrepenger.TrekkontoType trekkontoType) {
            this.trekkontoType = trekkontoType;
            return this;
        }

        public ForeldrepengerUttakAktivitetBuilder trekkdager(BigDecimal trekkdager) {
            this.trekkdager = trekkdager;
            return this;
        }

        public ForeldrepengerUttakAktivitetBuilder prosentArbeid(BigDecimal prosentArbeid) {
            this.prosentArbeid = prosentArbeid;
            return this;
        }

        public ForeldrepengerUttakAktivitetBuilder arbeidsgiverReferanse(String arbeidsgiverReferanse) {
            this.arbeidsgiverReferanse = arbeidsgiverReferanse;
            return this;
        }

        public ForeldrepengerUttakAktivitetBuilder arbeidsforholdId(String arbeidsforholdId) {
            this.arbeidsforholdId = arbeidsforholdId;
            return this;
        }

        public ForeldrepengerUttakAktivitetBuilder utbetalingsgrad(BigDecimal utbetalingsgrad) {
            this.utbetalingsgrad = utbetalingsgrad;
            return this;
        }

        public ForeldrepengerUttakAktivitetBuilder uttakArbeidType(UttakArbeidType uttakArbeidType) {
            this.uttakArbeidType = uttakArbeidType;
            return this;
        }

        public ForeldrepengerUttakAktivitetBuilder gradering(boolean gradering) {
            this.gradering = gradering;
            return this;
        }

        public Foreldrepenger.Aktivitet build() {
            return new Foreldrepenger.Aktivitet(trekkontoType, trekkdager, prosentArbeid, arbeidsgiverReferanse, arbeidsforholdId, utbetalingsgrad,
                uttakArbeidType, gradering != null && gradering);
        }
    }

    public static StønadskontoBuilder stønadskonto() {
        return new StønadskontoBuilder();
    }

    public static class StønadskontoBuilder {
        private Foreldrepenger.Stønadskonto.Type stønadskontotype;
        private Integer maxDager;
        private Integer saldo;
        private Foreldrepenger.KontoUtvidelser kontoUtvidelser;

        public StønadskontoBuilder stønadskontotype(Foreldrepenger.Stønadskonto.Type stønadskontotype) {
            this.stønadskontotype = stønadskontotype;
            return this;
        }

        public StønadskontoBuilder maxDager(int maxDager) {
            this.maxDager = maxDager;
            return this;
        }

        public StønadskontoBuilder saldo(int saldo) {
            this.saldo = saldo;
            return this;
        }

        public StønadskontoBuilder kontoUtvidelser(Foreldrepenger.KontoUtvidelser kontoUtvidelser) {
            this.kontoUtvidelser = kontoUtvidelser;
            return this;
        }

        public Foreldrepenger.Stønadskonto build() {
            return new Foreldrepenger.Stønadskonto(stønadskontotype, maxDager == null ? 0 : maxDager, saldo == null ? 0 : saldo, kontoUtvidelser);
        }
    }

    public static KontoUtvidelserBuilder kontoUtvidelser() {
        return new KontoUtvidelserBuilder();
    }

    public static class KontoUtvidelserBuilder {
        private Integer prematurdager;
        private Integer flerbarnsdager;

        public KontoUtvidelserBuilder prematurdager(int prematurdager) {
            this.prematurdager = prematurdager;
            return this;
        }

        public KontoUtvidelserBuilder flerbarnsdager(int flerbarnsdager) {
            this.flerbarnsdager = flerbarnsdager;
            return this;
        }

        public Foreldrepenger.KontoUtvidelser build() {
            return new Foreldrepenger.KontoUtvidelser(prematurdager == null ? 0 : prematurdager, flerbarnsdager == null ? 0 : flerbarnsdager);
        }
    }

    public static SvangerskapspengerBuilder svangerskapspenger() {
        return new SvangerskapspengerBuilder();
    }

    public static class SvangerskapspengerBuilder {
        private List<Svangerskapspenger.UttakArbeidsforhold> uttakArbeidsforhold;

        public SvangerskapspengerBuilder uttakArbeidsforhold(List<Svangerskapspenger.UttakArbeidsforhold> uttakArbeidsforhold) {
            this.uttakArbeidsforhold = uttakArbeidsforhold;
            return this;
        }

        public Svangerskapspenger build() {
            return new Svangerskapspenger(uttakArbeidsforhold);
        }
    }

    public static SvangerskapspengerUttakArbeidsforholdBuilder svangerskapspengerUttakArbeidsforhold() {
        return new SvangerskapspengerUttakArbeidsforholdBuilder();
    }

    public static class SvangerskapspengerUttakArbeidsforholdBuilder {
        private String arbeidsforholdIkkeOppfyltÅrsak;
        private String arbeidsgiverReferanse;
        private UttakArbeidType arbeidType;
        private List<Svangerskapspenger.Uttaksperiode> perioder;

        public SvangerskapspengerUttakArbeidsforholdBuilder arbeidsforholdIkkeOppfyltÅrsak(String arbeidsforholdIkkeOppfyltÅrsak) {
            this.arbeidsforholdIkkeOppfyltÅrsak = arbeidsforholdIkkeOppfyltÅrsak;
            return this;
        }

        public SvangerskapspengerUttakArbeidsforholdBuilder arbeidsgiverReferanse(String arbeidsgiverReferanse) {
            this.arbeidsgiverReferanse = arbeidsgiverReferanse;
            return this;
        }

        public SvangerskapspengerUttakArbeidsforholdBuilder arbeidType(UttakArbeidType arbeidType) {
            this.arbeidType = arbeidType;
            return this;
        }

        public SvangerskapspengerUttakArbeidsforholdBuilder perioder(List<Svangerskapspenger.Uttaksperiode> perioder) {
            this.perioder = perioder;
            return this;
        }

        public Svangerskapspenger.UttakArbeidsforhold build() {
            return new Svangerskapspenger.UttakArbeidsforhold(arbeidsforholdIkkeOppfyltÅrsak, arbeidsgiverReferanse, arbeidType, perioder);
        }
    }

    public static SvangerskapspengerUttakPeriodeBuilder svangerskapspengerUttakPeriode() {
        return new SvangerskapspengerUttakPeriodeBuilder();
    }

    public static class SvangerskapspengerUttakPeriodeBuilder {
        private LocalDate fom;
        private LocalDate tom;
        private PeriodeResultatType periodeResultatType;
        private String periodeIkkeOppfyltÅrsak;

        public SvangerskapspengerUttakPeriodeBuilder fom(LocalDate fom) {
            this.fom = fom;
            return this;
        }

        public SvangerskapspengerUttakPeriodeBuilder tom(LocalDate tom) {
            this.tom = tom;
            return this;
        }

        public SvangerskapspengerUttakPeriodeBuilder periodeResultatType(PeriodeResultatType periodeResultatType) {
            this.periodeResultatType = periodeResultatType;
            return this;
        }

        public SvangerskapspengerUttakPeriodeBuilder periodeIkkeOppfyltÅrsak(String periodeIkkeOppfyltÅrsak) {
            this.periodeIkkeOppfyltÅrsak = periodeIkkeOppfyltÅrsak;
            return this;
        }

        public Svangerskapspenger.Uttaksperiode build() {
            return new Svangerskapspenger.Uttaksperiode(fom, tom, periodeResultatType, periodeIkkeOppfyltÅrsak);
        }
    }

    public static InntektsmeldingBuilder inntektsmelding() {
        return new InntektsmeldingBuilder();
    }

    public static class InntektsmeldingBuilder {
        private String arbeidsgiverReferanse;
        private LocalDateTime innsendingstidspunkt;

        public InntektsmeldingBuilder arbeidsgiverReferanse(String arbeidsgiverReferanse) {
            this.arbeidsgiverReferanse = arbeidsgiverReferanse;
            return this;
        }

        public InntektsmeldingBuilder innsendingstidspunkt(LocalDateTime innsendingstidspunkt) {
            this.innsendingstidspunkt = innsendingstidspunkt;
            return this;
        }

        public Inntektsmelding build() {
            return new Inntektsmelding(arbeidsgiverReferanse, innsendingstidspunkt);
        }
    }

    public static RettighetBuilder rettigheter() {
        return new RettighetBuilder();
    }

    public static class RettighetBuilder {
        private Rettigheter.Rettighetstype opprinnelig;
        private Rettigheter.Rettighetstype gjeldende;
        private Rettigheter.EøsUttak eøsUttak;

        public RettighetBuilder opprinnelig(Rettigheter.Rettighetstype opprinnelig) {
            this.opprinnelig = opprinnelig;
            return this;
        }

        public RettighetBuilder gjeldende(Rettigheter.Rettighetstype gjeldende) {
            this.gjeldende = gjeldende;
            return this;
        }

        public RettighetBuilder eøsUttak(Rettigheter.EøsUttak eøsUttak) {
            this.eøsUttak = eøsUttak;
            return this;
        }

        public Rettigheter build() {
            return new Rettigheter(opprinnelig, gjeldende, eøsUttak);
        }
    }

    public static EøsUttakBuilder eøsUttak() {
        return new EøsUttakBuilder();
    }

    public static class EøsUttakBuilder {
        private LocalDate fom;
        private LocalDate tom;
        private Integer forbruktFellesperiode;
        private Integer fellesperiodeINorge;

        public EøsUttakBuilder fom(LocalDate fom) {
            this.fom = fom;
            return this;
        }

        public EøsUttakBuilder tom(LocalDate tom) {
            this.tom = tom;
            return this;
        }

        public EøsUttakBuilder forbruktFellesperiode(int forbruktFellesperiode) {
            this.forbruktFellesperiode = forbruktFellesperiode;
            return this;
        }

        public EøsUttakBuilder fellesperiodeINorge(int fellesperiodeINorge) {
            this.fellesperiodeINorge = fellesperiodeINorge;
            return this;
        }

        public Rettigheter.EøsUttak build() {
            return new Rettigheter.EøsUttak(fom, tom, forbruktFellesperiode == null ? 0 : forbruktFellesperiode,
                fellesperiodeINorge == null ? 0 : fellesperiodeINorge);
        }
    }

    public static FamilieHendelseBuilder familieHendelse() {
        return new FamilieHendelseBuilder();
    }

    public static class FamilieHendelseBuilder {
        private List<Barn> barn;
        private LocalDate termindato;
        private Integer antallBarn;
        private LocalDate omsorgsovertakelse;

        public FamilieHendelseBuilder barn(List<Barn> barn) {
            this.barn = barn;
            return this;
        }

        public FamilieHendelseBuilder termindato(LocalDate termindato) {
            this.termindato = termindato;
            return this;
        }

        public FamilieHendelseBuilder antallBarn(int antallBarn) {
            this.antallBarn = antallBarn;
            return this;
        }

        public FamilieHendelseBuilder omsorgsovertakelse(LocalDate omsorgsovertakelse) {
            this.omsorgsovertakelse = omsorgsovertakelse;
            return this;
        }

        public FamilieHendelse build() {
            return new FamilieHendelse(barn, termindato, antallBarn == null ? 0 : antallBarn, omsorgsovertakelse);
        }
    }

    public static BarnBuilder barn() {
        return new BarnBuilder();
    }

    public static class BarnBuilder {
        private LocalDate fødselsdato;
        private LocalDate dødsdato;

        public BarnBuilder fødselsdato(LocalDate fødselsdato) {
            this.fødselsdato = fødselsdato;
            return this;
        }

        public BarnBuilder dødsdato(LocalDate dødsdato) {
            this.dødsdato = dødsdato;
            return this;
        }

        public Barn build() {
            if (fødselsdato == null) {
                throw new IllegalStateException("Barn uten fødselsdato!");
            }
            return new Barn(fødselsdato, dødsdato);
        }
    }

    public static VergeBuilder verge() {
        return new VergeBuilder();
    }

    public static class VergeBuilder {
        private String aktørId;
        private String navn;
        private String organisasjonsnummer;
        private LocalDate gyldigFom;
        private LocalDate gyldigTom;

        public VergeBuilder aktørId(String aktørId) {
            this.aktørId = aktørId;
            return this;
        }

        public VergeBuilder navn(String navn) {
            this.navn = navn;
            return this;
        }

        public VergeBuilder organisasjonsnummer(String organisasjonsnummer) {
            this.organisasjonsnummer = organisasjonsnummer;
            return this;
        }

        public VergeBuilder gyldigFom(LocalDate gyldigFom) {
            this.gyldigFom = gyldigFom;
            return this;
        }

        public VergeBuilder gyldigTom(LocalDate gyldigTom) {
            this.gyldigTom = gyldigTom;
            return this;
        }

        public Verge build() {
            return new Verge(aktørId, navn, organisasjonsnummer, gyldigFom, gyldigTom);
        }
    }

    public static InnsynBehandlingBuilder innsynBehandling() {
        return new InnsynBehandlingBuilder();
    }

    public static class InnsynBehandlingBuilder {
        private InnsynBehandling.InnsynResultatType innsynResultatType;
        private List<InnsynBehandling.InnsynDokument> dokumenter;

        public InnsynBehandlingBuilder innsynResultatType(InnsynBehandling.InnsynResultatType innsynResultatType) {
            this.innsynResultatType = innsynResultatType;
            return this;
        }

        public InnsynBehandlingBuilder dokumenter(List<InnsynBehandling.InnsynDokument> dokumenter) {
            this.dokumenter = dokumenter;
            return this;
        }

        public InnsynBehandling build() {
            return new InnsynBehandling(innsynResultatType, dokumenter);
        }
    }

    public static InnsynDokumentBuilder innsynDokument() {
        return new InnsynDokumentBuilder();
    }

    public static class InnsynDokumentBuilder {
        private String journalpostId;
        private String dokumentId;

        public InnsynDokumentBuilder journalpostId(String journalpostId) {
            this.journalpostId = journalpostId;
            return this;
        }

        public InnsynDokumentBuilder dokumentId(String dokumentId) {
            this.dokumentId = dokumentId;
            return this;
        }

        public InnsynBehandling.InnsynDokument build() {
            return new InnsynBehandling.InnsynDokument(journalpostId, dokumentId);
        }
    }

    public static KlageBehandlingBuilder klageBehandling() {
        return new KlageBehandlingBuilder();
    }

    public static class KlageBehandlingBuilder {
        private KlageBehandling.KlageFormkravResultat klageFormkravResultatNFP;
        private KlageBehandling.KlageVurderingResultat klageVurderingResultatNFP;
        private KlageBehandling.KlageFormkravResultat klageFormkravResultatKA;
        private KlageBehandling.KlageVurderingResultat klageVurderingResultatNK;
        private LocalDate mottattDato;

        public KlageBehandlingBuilder klageFormkravResultatNFP(KlageBehandling.KlageFormkravResultat klageFormkravResultatNFP) {
            this.klageFormkravResultatNFP = klageFormkravResultatNFP;
            return this;
        }

        public KlageBehandlingBuilder klageVurderingResultatNFP(KlageBehandling.KlageVurderingResultat klageVurderingResultatNFP) {
            this.klageVurderingResultatNFP = klageVurderingResultatNFP;
            return this;
        }

        public KlageBehandlingBuilder klageFormkravResultatKA(KlageBehandling.KlageFormkravResultat klageFormkravResultatKA) {
            this.klageFormkravResultatKA = klageFormkravResultatKA;
            return this;
        }

        public KlageBehandlingBuilder klageVurderingResultatNK(KlageBehandling.KlageVurderingResultat klageVurderingResultatNK) {
            this.klageVurderingResultatNK = klageVurderingResultatNK;
            return this;
        }

        public KlageBehandlingBuilder mottattDato(LocalDate mottattDato) {
            this.mottattDato = mottattDato;
            return this;
        }

        public KlageBehandling build() {
            return new KlageBehandling(klageFormkravResultatNFP, klageVurderingResultatNFP, klageFormkravResultatKA, klageVurderingResultatNK,
                mottattDato);
        }
    }

    public static KlageFormkravResultatBuilder klageFormkravResultat() {
        return new KlageFormkravResultatBuilder();
    }

    public static class KlageFormkravResultatBuilder {
        private BehandlingType påklagdBehandlingType;
        private List<KlageBehandling.KlageAvvistÅrsak> avvistÅrsaker;

        public KlageFormkravResultatBuilder påklagdBehandlingType(BehandlingType påklagdBehandlingType) {
            this.påklagdBehandlingType = påklagdBehandlingType;
            return this;
        }

        public KlageFormkravResultatBuilder avvistÅrsaker(List<KlageBehandling.KlageAvvistÅrsak> avvistÅrsaker) {
            this.avvistÅrsaker = avvistÅrsaker;
            return this;
        }

        public KlageBehandling.KlageFormkravResultat build() {
            return new KlageBehandling.KlageFormkravResultat(påklagdBehandlingType, avvistÅrsaker);
        }
    }

    public static KlageVurderingResultatBuilder klageVurderingResultat() {
        return new KlageVurderingResultatBuilder();
    }

    public static class KlageVurderingResultatBuilder {
        private String fritekstTilBrev;

        public KlageVurderingResultatBuilder fritekstTilBrev(String fritekstTilBrev) {
            this.fritekstTilBrev = fritekstTilBrev;
            return this;
        }

        public KlageBehandling.KlageVurderingResultat build() {
            return new KlageBehandling.KlageVurderingResultat(fritekstTilBrev);
        }
    }

    public static BehandlingsresultatBuilder behandlingsresultat() {
        return new BehandlingsresultatBuilder();
    }

    public static class BehandlingsresultatBuilder {
        private String medlemskapOpphørsårsak;
        private LocalDate medlemskapFom;
        private Behandlingsresultat.BehandlingResultatType behandlingResultatType;
        private String avslagsårsak;
        private Behandlingsresultat.Fritekst fritekst;
        private Behandlingsresultat.Skjæringstidspunkt skjæringstidspunkt;
        private Boolean endretDekningsgrad;
        private LocalDate opphørsdato;
        private List<Behandlingsresultat.KonsekvensForYtelsen> konsekvenserForYtelsen = List.of();
        private List<Behandlingsresultat.VilkårType> vilkårTyper = List.of();

        public BehandlingsresultatBuilder medlemskapOpphørsårsak(String medlemskapOpphørsårsak) {
            this.medlemskapOpphørsårsak = medlemskapOpphørsårsak;
            return this;
        }

        public BehandlingsresultatBuilder medlemskapFom(LocalDate medlemskapFom) {
            this.medlemskapFom = medlemskapFom;
            return this;
        }

        public BehandlingsresultatBuilder behandlingResultatType(Behandlingsresultat.BehandlingResultatType behandlingResultatType) {
            this.behandlingResultatType = behandlingResultatType;
            return this;
        }

        public BehandlingsresultatBuilder avslagsårsak(String avslagsårsak) {
            this.avslagsårsak = avslagsårsak;
            return this;
        }

        public BehandlingsresultatBuilder fritekst(Behandlingsresultat.Fritekst fritekst) {
            this.fritekst = fritekst;
            return this;
        }

        public BehandlingsresultatBuilder skjæringstidspunkt(Behandlingsresultat.Skjæringstidspunkt skjæringstidspunkt) {
            this.skjæringstidspunkt = skjæringstidspunkt;
            return this;
        }

        public BehandlingsresultatBuilder endretDekningsgrad(boolean endretDekningsgrad) {
            this.endretDekningsgrad = endretDekningsgrad;
            return this;
        }

        public BehandlingsresultatBuilder opphørsdato(LocalDate opphørsdato) {
            this.opphørsdato = opphørsdato;
            return this;
        }

        public BehandlingsresultatBuilder konsekvenserForYtelsen(List<Behandlingsresultat.KonsekvensForYtelsen> konsekvenserForYtelsen) {
            this.konsekvenserForYtelsen = konsekvenserForYtelsen;
            return this;
        }

        public BehandlingsresultatBuilder vilkårTyper(List<Behandlingsresultat.VilkårType> vilkårTyper) {
            this.vilkårTyper = vilkårTyper;
            return this;
        }

        public Behandlingsresultat build() {
            return new Behandlingsresultat(medlemskapOpphørsårsak, medlemskapFom, behandlingResultatType, avslagsårsak, fritekst, skjæringstidspunkt,
                endretDekningsgrad != null && endretDekningsgrad, opphørsdato, konsekvenserForYtelsen, vilkårTyper);
        }
    }

    public static FritekstBuilder fritekst() {
        return new FritekstBuilder();
    }

    public static class FritekstBuilder {
        private String overskrift;
        private String brødtekst;
        private String avslagsarsakFritekst;

        public FritekstBuilder overskrift(String overskrift) {
            this.overskrift = overskrift;
            return this;
        }

        public FritekstBuilder brødtekst(String brødtekst) {
            this.brødtekst = brødtekst;
            return this;
        }

        public FritekstBuilder avslagsarsakFritekst(String avslagsarsakFritekst) {
            this.avslagsarsakFritekst = avslagsarsakFritekst;
            return this;
        }

        public Behandlingsresultat.Fritekst build() {
            return new Behandlingsresultat.Fritekst(overskrift, brødtekst, avslagsarsakFritekst);
        }
    }

    public static SkjæringstidspunktBuilder skjæringstidspunkt() {
        return new SkjæringstidspunktBuilder();
    }

    public static class SkjæringstidspunktBuilder {
        private LocalDate dato;
        private Boolean utenMinsterett;

        public SkjæringstidspunktBuilder dato(LocalDate dato) {
            this.dato = dato;
            return this;
        }

        public SkjæringstidspunktBuilder utenMinsterett(boolean utenMinsterett) {
            this.utenMinsterett = utenMinsterett;
            return this;
        }

        public Behandlingsresultat.Skjæringstidspunkt build() {
            return new Behandlingsresultat.Skjæringstidspunkt(dato, utenMinsterett != null && utenMinsterett);
        }
    }
}

