package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak;

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
        private BrevGrunnlag.FagsakYtelseType fagsakYtelseType;
        private BrevGrunnlag.FagsakStatus fagsakStatus;
        private BrevGrunnlag.RelasjonsRolleType relasjonsRolleType;
        private String aktørId;
        private BrevGrunnlag.Dekningsgrad dekningsgrad;
        private BrevGrunnlag.BehandlingType behandlingType;
        private LocalDateTime opprettet;
        private LocalDateTime avsluttet;
        private String behandlendeEnhet;
        private BrevGrunnlag.Språkkode språkkode;
        private boolean automatiskBehandlet;
        private BrevGrunnlag.FamilieHendelse familieHendelse;
        private BrevGrunnlag.OriginalBehandling originalBehandling;
        private BrevGrunnlag.Rettigheter rettigheter;
        private BrevGrunnlag.Behandlingsresultat behandlingsresultat;
        private List<BrevGrunnlag.BehandlingÅrsakType> behandlingÅrsakTyper;
        private BrevGrunnlag.TilkjentYtelse tilkjentYtelse;
        private BeregningsgrunnlagDto beregningsgrunnlag;
        private ArbeidsforholdInntektsmeldingerDto inntektsmeldingerStatus;
        private LocalDate førsteSøknadMottattDato;
        private LocalDate sisteSøknadMottattDato;
        private LocalDate søknadMottattDato;
        private List<BrevGrunnlag.Inntektsmelding> inntektsmeldinger;
        private LocalDate nyStartDatoVedUtsattOppstart;
        private BrevGrunnlag.Verge verge;
        private BrevGrunnlag.KlageBehandling klageBehandling;
        private BrevGrunnlag.InnsynBehandling innsynBehandling;
        private BrevGrunnlag.SvangerskapspengerUttak svangerskapspengerUttak;
        private BrevGrunnlag.ForeldrepengerUttak foreldrepengerUttak;

        public BrevGrunnlagBuilder uuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public BrevGrunnlagBuilder saksnummer(String saksnummer) {
            this.saksnummer = saksnummer;
            return this;
        }

        public BrevGrunnlagBuilder fagsakYtelseType(BrevGrunnlag.FagsakYtelseType fagsakYtelseType) {
            this.fagsakYtelseType = fagsakYtelseType;
            return this;
        }

        public BrevGrunnlagBuilder fagsakStatus(BrevGrunnlag.FagsakStatus fagsakStatus) {
            this.fagsakStatus = fagsakStatus;
            return this;
        }

        public BrevGrunnlagBuilder relasjonsRolleType(BrevGrunnlag.RelasjonsRolleType relasjonsRolleType) {
            this.relasjonsRolleType = relasjonsRolleType;
            return this;
        }

        public BrevGrunnlagBuilder aktørId(String aktørId) {
            this.aktørId = aktørId;
            return this;
        }

        public BrevGrunnlagBuilder dekningsgrad(BrevGrunnlag.Dekningsgrad dekningsgrad) {
            this.dekningsgrad = dekningsgrad;
            return this;
        }

        public BrevGrunnlagBuilder behandlingType(BrevGrunnlag.BehandlingType behandlingType) {
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

        public BrevGrunnlagBuilder språkkode(BrevGrunnlag.Språkkode språkkode) {
            this.språkkode = språkkode;
            return this;
        }

        public BrevGrunnlagBuilder automatiskBehandlet(boolean automatiskBehandlet) {
            this.automatiskBehandlet = automatiskBehandlet;
            return this;
        }

        public BrevGrunnlagBuilder familieHendelse(BrevGrunnlag.FamilieHendelse familieHendelse) {
            this.familieHendelse = familieHendelse;
            return this;
        }

        public BrevGrunnlagBuilder originalBehandling(BrevGrunnlag.OriginalBehandling originalBehandling) {
            this.originalBehandling = originalBehandling;
            return this;
        }

        public BrevGrunnlagBuilder rettigheter(BrevGrunnlag.Rettigheter rettigheter) {
            this.rettigheter = rettigheter;
            return this;
        }

        public BrevGrunnlagBuilder behandlingsresultat(BrevGrunnlag.Behandlingsresultat behandlingsresultat) {
            this.behandlingsresultat = behandlingsresultat;
            return this;
        }

        public BrevGrunnlagBuilder behandlingÅrsakTyper(List<BrevGrunnlag.BehandlingÅrsakType> behandlingÅrsakTyper) {
            this.behandlingÅrsakTyper = behandlingÅrsakTyper;
            return this;
        }

        public BrevGrunnlagBuilder tilkjentYtelse(BrevGrunnlag.TilkjentYtelse tilkjentYtelse) {
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

        public BrevGrunnlagBuilder inntektsmeldinger(List<BrevGrunnlag.Inntektsmelding> inntektsmeldinger) {
            this.inntektsmeldinger = inntektsmeldinger;
            return this;
        }

        public BrevGrunnlagBuilder nyStartDatoVedUtsattOppstart(LocalDate nyStartDatoVedUtsattOppstart) {
            this.nyStartDatoVedUtsattOppstart = nyStartDatoVedUtsattOppstart;
            return this;
        }

        public BrevGrunnlagBuilder verge(BrevGrunnlag.Verge verge) {
            this.verge = verge;
            return this;
        }

        public BrevGrunnlagBuilder klageBehandling(BrevGrunnlag.KlageBehandling klageBehandling) {
            this.klageBehandling = klageBehandling;
            return this;
        }

        public BrevGrunnlagBuilder innsynBehandling(BrevGrunnlag.InnsynBehandling innsynBehandling) {
            this.innsynBehandling = innsynBehandling;
            return this;
        }

        public BrevGrunnlagBuilder svangerskapspengerUttak(BrevGrunnlag.SvangerskapspengerUttak svangerskapspengerUttak) {
            this.svangerskapspengerUttak = svangerskapspengerUttak;
            return this;
        }

        public BrevGrunnlagBuilder foreldrepengerUttak(BrevGrunnlag.ForeldrepengerUttak foreldrepengerUttak) {
            this.foreldrepengerUttak = foreldrepengerUttak;
            return this;
        }

        public BrevGrunnlag build() {
            return new BrevGrunnlag(uuid, saksnummer, fagsakYtelseType, fagsakStatus, relasjonsRolleType, aktørId, dekningsgrad, behandlingType,
                opprettet, avsluttet, behandlendeEnhet, språkkode, automatiskBehandlet, familieHendelse, originalBehandling, rettigheter,
                behandlingsresultat, behandlingÅrsakTyper, tilkjentYtelse, beregningsgrunnlag, inntektsmeldingerStatus, førsteSøknadMottattDato,
                sisteSøknadMottattDato, søknadMottattDato, inntektsmeldinger, nyStartDatoVedUtsattOppstart, verge, klageBehandling, innsynBehandling,
                svangerskapspengerUttak, foreldrepengerUttak);
        }
    }

    public static OriginalBehandlingBuilder originalBehandling() {
        return new OriginalBehandlingBuilder();
    }

    public static class OriginalBehandlingBuilder {
        private BrevGrunnlag.FamilieHendelse familieHendelse;
        private BrevGrunnlag.Behandlingsresultat.BehandlingResultatType originalBehandlingResultatType;
        private LocalDate førsteDagMedUtbetaltForeldrepenger;

        public OriginalBehandlingBuilder familieHendelse(BrevGrunnlag.FamilieHendelse familieHendelse) {
            this.familieHendelse = familieHendelse;
            return this;
        }

        public OriginalBehandlingBuilder originalBehandlingResultatType(BrevGrunnlag.Behandlingsresultat.BehandlingResultatType originalBehandlingResultatType) {
            this.originalBehandlingResultatType = originalBehandlingResultatType;
            return this;
        }

        public OriginalBehandlingBuilder førsteDagMedUtbetaltForeldrepenger(LocalDate førsteDagMedUtbetaltForeldrepenger) {
            this.førsteDagMedUtbetaltForeldrepenger = førsteDagMedUtbetaltForeldrepenger;
            return this;
        }

        public BrevGrunnlag.OriginalBehandling build() {
            return new BrevGrunnlag.OriginalBehandling(familieHendelse, originalBehandlingResultatType, førsteDagMedUtbetaltForeldrepenger);
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

        public BrevGrunnlag.TilkjentYtelse build() {
            return new BrevGrunnlag.TilkjentYtelse(engangsstønad, originalBehandlingEngangsstønad, dagytelse);
        }
    }

    public static ForeldrepengerUttakBuilder foreldrepengerUttak() {
        return new ForeldrepengerUttakBuilder();
    }

    public static class ForeldrepengerUttakBuilder {
        private List<BrevGrunnlag.ForeldrepengerUttak.Stønadskonto> stønadskontoer;
        private Integer tapteDagerFpff;
        private List<BrevGrunnlag.ForeldrepengerUttak.Periode> perioderSøker;
        private List<BrevGrunnlag.ForeldrepengerUttak.Periode> perioderAnnenpart;
        private Boolean ønskerJustertUttakVedFødsel;

        public ForeldrepengerUttakBuilder stønadskontoer(List<BrevGrunnlag.ForeldrepengerUttak.Stønadskonto> stønadskontoer) {
            this.stønadskontoer = stønadskontoer;
            return this;
        }

        public ForeldrepengerUttakBuilder tapteDagerFpff(int tapteDagerFpff) {
            this.tapteDagerFpff = tapteDagerFpff;
            return this;
        }

        public ForeldrepengerUttakBuilder perioderSøker(List<BrevGrunnlag.ForeldrepengerUttak.Periode> perioderSøker) {
            this.perioderSøker = perioderSøker;
            return this;
        }

        public ForeldrepengerUttakBuilder perioderAnnenpart(List<BrevGrunnlag.ForeldrepengerUttak.Periode> perioderAnnenpart) {
            this.perioderAnnenpart = perioderAnnenpart;
            return this;
        }

        public ForeldrepengerUttakBuilder ønskerJustertUttakVedFødsel(boolean ønskerJustertUttakVedFødsel) {
            this.ønskerJustertUttakVedFødsel = ønskerJustertUttakVedFødsel;
            return this;
        }

        public BrevGrunnlag.ForeldrepengerUttak build() {
            return new BrevGrunnlag.ForeldrepengerUttak(stønadskontoer, tapteDagerFpff == null ? 0 : tapteDagerFpff, perioderSøker, perioderAnnenpart,
                ønskerJustertUttakVedFødsel != null && ønskerJustertUttakVedFødsel);
        }
    }

    public static ForeldrepengerUttakPeriodeBuilder foreldrepengerUttakPeriode() {
        return new ForeldrepengerUttakPeriodeBuilder();
    }

    public static class ForeldrepengerUttakPeriodeBuilder {
        private LocalDate fom;
        private LocalDate tom;
        private List<BrevGrunnlag.ForeldrepengerUttak.Aktivitet> aktiviteter;
        private BrevGrunnlag.PeriodeResultatType periodeResultatType;
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

        public ForeldrepengerUttakPeriodeBuilder aktiviteter(List<BrevGrunnlag.ForeldrepengerUttak.Aktivitet> aktiviteter) {
            this.aktiviteter = aktiviteter;
            return this;
        }

        public ForeldrepengerUttakPeriodeBuilder periodeResultatType(BrevGrunnlag.PeriodeResultatType periodeResultatType) {
            this.periodeResultatType = periodeResultatType;
            return this;
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

        public BrevGrunnlag.ForeldrepengerUttak.Periode build() {
            return new BrevGrunnlag.ForeldrepengerUttak.Periode(fom, tom, aktiviteter, periodeResultatType, periodeResultatÅrsak,
                graderingAvslagÅrsak, periodeResultatÅrsakLovhjemmel, graderingsAvslagÅrsakLovhjemmel, tidligstMottattDato,
                erUtbetalingRedusertTilMorsStillingsprosent != null && erUtbetalingRedusertTilMorsStillingsprosent);
        }
    }

    public static ForeldrepengerUttakAktivitetBuilder foreldrepengerUttakAktivitet() {
        return new ForeldrepengerUttakAktivitetBuilder();
    }

    public static class ForeldrepengerUttakAktivitetBuilder {
        private BrevGrunnlag.ForeldrepengerUttak.TrekkontoType trekkontoType;
        private BigDecimal trekkdager;
        private BigDecimal prosentArbeid;
        private String arbeidsgiverReferanse;
        private String arbeidsforholdId;
        private BigDecimal utbetalingsgrad;
        private BrevGrunnlag.UttakArbeidType uttakArbeidType;
        private Boolean gradering;

        public ForeldrepengerUttakAktivitetBuilder trekkontoType(BrevGrunnlag.ForeldrepengerUttak.TrekkontoType trekkontoType) {
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

        public ForeldrepengerUttakAktivitetBuilder uttakArbeidType(BrevGrunnlag.UttakArbeidType uttakArbeidType) {
            this.uttakArbeidType = uttakArbeidType;
            return this;
        }

        public ForeldrepengerUttakAktivitetBuilder gradering(boolean gradering) {
            this.gradering = gradering;
            return this;
        }

        public BrevGrunnlag.ForeldrepengerUttak.Aktivitet build() {
            return new BrevGrunnlag.ForeldrepengerUttak.Aktivitet(trekkontoType, trekkdager, prosentArbeid, arbeidsgiverReferanse, arbeidsforholdId,
                utbetalingsgrad, uttakArbeidType, gradering != null && gradering);
        }
    }

    public static StønadskontoBuilder stønadskonto() {
        return new StønadskontoBuilder();
    }

    public static class StønadskontoBuilder {
        private BrevGrunnlag.ForeldrepengerUttak.Stønadskonto.Type stønadskontotype;
        private Integer maxDager;
        private Integer saldo;
        private BrevGrunnlag.ForeldrepengerUttak.KontoUtvidelser kontoUtvidelser;

        public StønadskontoBuilder stønadskontotype(BrevGrunnlag.ForeldrepengerUttak.Stønadskonto.Type stønadskontotype) {
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

        public StønadskontoBuilder kontoUtvidelser(BrevGrunnlag.ForeldrepengerUttak.KontoUtvidelser kontoUtvidelser) {
            this.kontoUtvidelser = kontoUtvidelser;
            return this;
        }

        public BrevGrunnlag.ForeldrepengerUttak.Stønadskonto build() {
            return new BrevGrunnlag.ForeldrepengerUttak.Stønadskonto(stønadskontotype, maxDager == null ? 0 : maxDager, saldo == null ? 0 : saldo,
                kontoUtvidelser);
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

        public BrevGrunnlag.ForeldrepengerUttak.KontoUtvidelser build() {
            return new BrevGrunnlag.ForeldrepengerUttak.KontoUtvidelser(prematurdager == null ? 0 : prematurdager,
                flerbarnsdager == null ? 0 : flerbarnsdager);
        }
    }

    public static SvangerskapspengerUttakBuilder svangerskapspengerUttak() {
        return new SvangerskapspengerUttakBuilder();
    }

    public static class SvangerskapspengerUttakBuilder {
        private List<BrevGrunnlag.SvangerskapspengerUttak.UttakArbeidsforhold> uttakArbeidsforhold;

        public SvangerskapspengerUttakBuilder uttakArbeidsforhold(List<BrevGrunnlag.SvangerskapspengerUttak.UttakArbeidsforhold> uttakArbeidsforhold) {
            this.uttakArbeidsforhold = uttakArbeidsforhold;
            return this;
        }

        public BrevGrunnlag.SvangerskapspengerUttak build() {
            return new BrevGrunnlag.SvangerskapspengerUttak(uttakArbeidsforhold);
        }
    }

    public static SvangerskapspengerUttakArbeidsforholdBuilder svangerskapspengerUttakArbeidsforhold() {
        return new SvangerskapspengerUttakArbeidsforholdBuilder();
    }

    public static class SvangerskapspengerUttakArbeidsforholdBuilder {
        private String arbeidsforholdIkkeOppfyltÅrsak;
        private String arbeidsgiverReferanse;
        private BrevGrunnlag.UttakArbeidType arbeidType;
        private List<BrevGrunnlag.SvangerskapspengerUttak.Periode> perioder;

        public SvangerskapspengerUttakArbeidsforholdBuilder arbeidsforholdIkkeOppfyltÅrsak(String arbeidsforholdIkkeOppfyltÅrsak) {
            this.arbeidsforholdIkkeOppfyltÅrsak = arbeidsforholdIkkeOppfyltÅrsak;
            return this;
        }

        public SvangerskapspengerUttakArbeidsforholdBuilder arbeidsgiverReferanse(String arbeidsgiverReferanse) {
            this.arbeidsgiverReferanse = arbeidsgiverReferanse;
            return this;
        }

        public SvangerskapspengerUttakArbeidsforholdBuilder arbeidType(BrevGrunnlag.UttakArbeidType arbeidType) {
            this.arbeidType = arbeidType;
            return this;
        }

        public SvangerskapspengerUttakArbeidsforholdBuilder perioder(List<BrevGrunnlag.SvangerskapspengerUttak.Periode> perioder) {
            this.perioder = perioder;
            return this;
        }

        public BrevGrunnlag.SvangerskapspengerUttak.UttakArbeidsforhold build() {
            return new BrevGrunnlag.SvangerskapspengerUttak.UttakArbeidsforhold(arbeidsforholdIkkeOppfyltÅrsak, arbeidsgiverReferanse, arbeidType,
                perioder);
        }
    }

    public static SvangerskapspengerUttakPeriodeBuilder svangerskapspengerUttakPeriode() {
        return new SvangerskapspengerUttakPeriodeBuilder();
    }

    public static class SvangerskapspengerUttakPeriodeBuilder {
        private LocalDate fom;
        private LocalDate tom;
        private BigDecimal utbetalingsgrad;
        private BrevGrunnlag.PeriodeResultatType periodeResultatType;
        private String periodeIkkeOppfyltÅrsak;

        public SvangerskapspengerUttakPeriodeBuilder fom(LocalDate fom) {
            this.fom = fom;
            return this;
        }

        public SvangerskapspengerUttakPeriodeBuilder tom(LocalDate tom) {
            this.tom = tom;
            return this;
        }

        public SvangerskapspengerUttakPeriodeBuilder utbetalingsgrad(BigDecimal utbetalingsgrad) {
            this.utbetalingsgrad = utbetalingsgrad;
            return this;
        }

        public SvangerskapspengerUttakPeriodeBuilder periodeResultatType(BrevGrunnlag.PeriodeResultatType periodeResultatType) {
            this.periodeResultatType = periodeResultatType;
            return this;
        }

        public SvangerskapspengerUttakPeriodeBuilder periodeIkkeOppfyltÅrsak(String periodeIkkeOppfyltÅrsak) {
            this.periodeIkkeOppfyltÅrsak = periodeIkkeOppfyltÅrsak;
            return this;
        }

        public BrevGrunnlag.SvangerskapspengerUttak.Periode build() {
            return new BrevGrunnlag.SvangerskapspengerUttak.Periode(fom, tom, utbetalingsgrad, periodeResultatType, periodeIkkeOppfyltÅrsak);
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

        public BrevGrunnlag.Inntektsmelding build() {
            return new BrevGrunnlag.Inntektsmelding(arbeidsgiverReferanse, innsendingstidspunkt);
        }
    }

    public static RettighetBuilder rettigheter() {
        return new RettighetBuilder();
    }

    public static class RettighetBuilder {
        private BrevGrunnlag.Rettigheter.Rettighetstype opprinnelig;
        private BrevGrunnlag.Rettigheter.Rettighetstype gjeldende;
        private BrevGrunnlag.Rettigheter.EøsUttak eøsUttak;

        public RettighetBuilder opprinnelig(BrevGrunnlag.Rettigheter.Rettighetstype opprinnelig) {
            this.opprinnelig = opprinnelig;
            return this;
        }

        public RettighetBuilder gjeldende(BrevGrunnlag.Rettigheter.Rettighetstype gjeldende) {
            this.gjeldende = gjeldende;
            return this;
        }

        public RettighetBuilder eøsUttak(BrevGrunnlag.Rettigheter.EøsUttak eøsUttak) {
            this.eøsUttak = eøsUttak;
            return this;
        }

        public BrevGrunnlag.Rettigheter build() {
            return new BrevGrunnlag.Rettigheter(opprinnelig, gjeldende, eøsUttak);
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

        public BrevGrunnlag.Rettigheter.EøsUttak build() {
            return new BrevGrunnlag.Rettigheter.EøsUttak(fom, tom, forbruktFellesperiode == null ? 0 : forbruktFellesperiode,
                fellesperiodeINorge == null ? 0 : fellesperiodeINorge);
        }
    }

    public static FamilieHendelseBuilder familieHendelse() {
        return new FamilieHendelseBuilder();
    }

    public static class FamilieHendelseBuilder {
        private List<BrevGrunnlag.Barn> barn;
        private LocalDate termindato;
        private Integer antallBarn;
        private LocalDate omsorgsovertakelse;

        public FamilieHendelseBuilder barn(List<BrevGrunnlag.Barn> barn) {
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

        public BrevGrunnlag.FamilieHendelse build() {
            return new BrevGrunnlag.FamilieHendelse(barn, termindato, antallBarn == null ? 0 : antallBarn, omsorgsovertakelse);
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

        public BrevGrunnlag.Barn build() {
            return new BrevGrunnlag.Barn(fødselsdato, dødsdato);
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
        private BrevGrunnlag.Verge.VergeType vergeType;

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

        public VergeBuilder vergeType(BrevGrunnlag.Verge.VergeType vergeType) {
            this.vergeType = vergeType;
            return this;
        }

        public BrevGrunnlag.Verge build() {
            return new BrevGrunnlag.Verge(aktørId, navn, organisasjonsnummer, gyldigFom, gyldigTom, vergeType);
        }
    }

    public static InnsynBehandlingBuilder innsynBehandling() {
        return new InnsynBehandlingBuilder();
    }

    public static class InnsynBehandlingBuilder {
        private BrevGrunnlag.InnsynBehandling.InnsynResultatType innsynResultatType;
        private List<BrevGrunnlag.InnsynBehandling.InnsynDokument> dokumenter;

        public InnsynBehandlingBuilder innsynResultatType(BrevGrunnlag.InnsynBehandling.InnsynResultatType innsynResultatType) {
            this.innsynResultatType = innsynResultatType;
            return this;
        }

        public InnsynBehandlingBuilder dokumenter(List<BrevGrunnlag.InnsynBehandling.InnsynDokument> dokumenter) {
            this.dokumenter = dokumenter;
            return this;
        }

        public BrevGrunnlag.InnsynBehandling build() {
            return new BrevGrunnlag.InnsynBehandling(innsynResultatType, dokumenter);
        }
    }

    public static InnsynDokumentBuilder innsynDokument() {
        return new InnsynDokumentBuilder();
    }

    public static class InnsynDokumentBuilder {
        private Boolean fikkInnsyn;
        private String journalpostId;
        private String dokumentId;

        public InnsynDokumentBuilder fikkInnsyn(boolean fikkInnsyn) {
            this.fikkInnsyn = fikkInnsyn;
            return this;
        }

        public InnsynDokumentBuilder journalpostId(String journalpostId) {
            this.journalpostId = journalpostId;
            return this;
        }

        public InnsynDokumentBuilder dokumentId(String dokumentId) {
            this.dokumentId = dokumentId;
            return this;
        }

        public BrevGrunnlag.InnsynBehandling.InnsynDokument build() {
            return new BrevGrunnlag.InnsynBehandling.InnsynDokument(fikkInnsyn != null && fikkInnsyn, journalpostId, dokumentId);
        }
    }

    public static KlageBehandlingBuilder klageBehandling() {
        return new KlageBehandlingBuilder();
    }

    public static class KlageBehandlingBuilder {
        private BrevGrunnlag.KlageBehandling.KlageFormkravResultat klageFormkravResultatNFP;
        private BrevGrunnlag.KlageBehandling.KlageVurderingResultat klageVurderingResultatNFP;
        private BrevGrunnlag.KlageBehandling.KlageFormkravResultat klageFormkravResultatKA;
        private BrevGrunnlag.KlageBehandling.KlageVurderingResultat klageVurderingResultatNK;
        private List<BrevGrunnlag.KlageBehandling.KlageHjemmel> aktuelleHjemler;
        private Boolean underBehandlingKabal;
        private Boolean behandletAvKabal;
        private LocalDate mottattDato;

        public KlageBehandlingBuilder klageFormkravResultatNFP(BrevGrunnlag.KlageBehandling.KlageFormkravResultat klageFormkravResultatNFP) {
            this.klageFormkravResultatNFP = klageFormkravResultatNFP;
            return this;
        }

        public KlageBehandlingBuilder klageVurderingResultatNFP(BrevGrunnlag.KlageBehandling.KlageVurderingResultat klageVurderingResultatNFP) {
            this.klageVurderingResultatNFP = klageVurderingResultatNFP;
            return this;
        }

        public KlageBehandlingBuilder klageFormkravResultatKA(BrevGrunnlag.KlageBehandling.KlageFormkravResultat klageFormkravResultatKA) {
            this.klageFormkravResultatKA = klageFormkravResultatKA;
            return this;
        }

        public KlageBehandlingBuilder klageVurderingResultatNK(BrevGrunnlag.KlageBehandling.KlageVurderingResultat klageVurderingResultatNK) {
            this.klageVurderingResultatNK = klageVurderingResultatNK;
            return this;
        }

        public KlageBehandlingBuilder aktuelleHjemler(List<BrevGrunnlag.KlageBehandling.KlageHjemmel> aktuelleHjemler) {
            this.aktuelleHjemler = aktuelleHjemler;
            return this;
        }

        public KlageBehandlingBuilder underBehandlingKabal(boolean underBehandlingKabal) {
            this.underBehandlingKabal = underBehandlingKabal;
            return this;
        }

        public KlageBehandlingBuilder behandletAvKabal(boolean behandletAvKabal) {
            this.behandletAvKabal = behandletAvKabal;
            return this;
        }

        public KlageBehandlingBuilder mottattDato(LocalDate mottattDato) {
            this.mottattDato = mottattDato;
            return this;
        }

        public BrevGrunnlag.KlageBehandling build() {
            return new BrevGrunnlag.KlageBehandling(klageFormkravResultatNFP, klageVurderingResultatNFP, klageFormkravResultatKA,
                klageVurderingResultatNK, aktuelleHjemler, underBehandlingKabal != null && underBehandlingKabal,
                behandletAvKabal != null && behandletAvKabal, mottattDato);
        }
    }

    public static KlageFormkravResultatBuilder klageFormkravResultat() {
        return new KlageFormkravResultatBuilder();
    }

    public static class KlageFormkravResultatBuilder {
        private Long påklagdBehandlingId;
        private UUID påklagdBehandlingUuid;
        private BrevGrunnlag.BehandlingType påklagdBehandlingType;
        private String begrunnelse;
        private Boolean erKlagerPart;
        private Boolean erKlageKonkret;
        private Boolean erKlagefirstOverholdt;
        private Boolean erSignert;
        private List<BrevGrunnlag.KlageBehandling.KlageAvvistÅrsak> avvistÅrsaker;

        public KlageFormkravResultatBuilder påklagdBehandlingId(Long påklagdBehandlingId) {
            this.påklagdBehandlingId = påklagdBehandlingId;
            return this;
        }

        public KlageFormkravResultatBuilder påklagdBehandlingUuid(UUID påklagdBehandlingUuid) {
            this.påklagdBehandlingUuid = påklagdBehandlingUuid;
            return this;
        }

        public KlageFormkravResultatBuilder påklagdBehandlingType(BrevGrunnlag.BehandlingType påklagdBehandlingType) {
            this.påklagdBehandlingType = påklagdBehandlingType;
            return this;
        }

        public KlageFormkravResultatBuilder begrunnelse(String begrunnelse) {
            this.begrunnelse = begrunnelse;
            return this;
        }

        public KlageFormkravResultatBuilder erKlagerPart(boolean erKlagerPart) {
            this.erKlagerPart = erKlagerPart;
            return this;
        }

        public KlageFormkravResultatBuilder erKlageKonkret(boolean erKlageKonkret) {
            this.erKlageKonkret = erKlageKonkret;
            return this;
        }

        public KlageFormkravResultatBuilder erKlagefirstOverholdt(boolean erKlagefirstOverholdt) {
            this.erKlagefirstOverholdt = erKlagefirstOverholdt;
            return this;
        }

        public KlageFormkravResultatBuilder erSignert(boolean erSignert) {
            this.erSignert = erSignert;
            return this;
        }

        public KlageFormkravResultatBuilder avvistÅrsaker(List<BrevGrunnlag.KlageBehandling.KlageAvvistÅrsak> avvistÅrsaker) {
            this.avvistÅrsaker = avvistÅrsaker;
            return this;
        }

        public BrevGrunnlag.KlageBehandling.KlageFormkravResultat build() {
            return new BrevGrunnlag.KlageBehandling.KlageFormkravResultat(påklagdBehandlingId, påklagdBehandlingUuid, påklagdBehandlingType,
                begrunnelse, erKlagerPart != null && erKlagerPart, erKlageKonkret != null && erKlageKonkret,
                erKlagefirstOverholdt != null && erKlagefirstOverholdt, erSignert != null && erSignert, avvistÅrsaker);
        }
    }

    public static KlageVurderingResultatBuilder klageVurderingResultat() {
        return new KlageVurderingResultatBuilder();
    }

    public static class KlageVurderingResultatBuilder {
        private String klageVurdertAv;
        private BrevGrunnlag.KlageBehandling.KlageVurdering klageVurdering;
        private String begrunnelse;
        private BrevGrunnlag.KlageBehandling.KlageMedholdÅrsak klageMedholdÅrsak;
        private BrevGrunnlag.KlageBehandling.KlageVurderingOmgjør klageVurderingOmgjør;
        private BrevGrunnlag.KlageBehandling.KlageHjemmel klageHjemmel;
        private Boolean godkjentAvMedunderskriver;
        private String fritekstTilBrev;

        public KlageVurderingResultatBuilder klageVurdertAv(String klageVurdertAv) {
            this.klageVurdertAv = klageVurdertAv;
            return this;
        }

        public KlageVurderingResultatBuilder klageVurdering(BrevGrunnlag.KlageBehandling.KlageVurdering klageVurdering) {
            this.klageVurdering = klageVurdering;
            return this;
        }

        public KlageVurderingResultatBuilder begrunnelse(String begrunnelse) {
            this.begrunnelse = begrunnelse;
            return this;
        }

        public KlageVurderingResultatBuilder klageMedholdÅrsak(BrevGrunnlag.KlageBehandling.KlageMedholdÅrsak klageMedholdÅrsak) {
            this.klageMedholdÅrsak = klageMedholdÅrsak;
            return this;
        }

        public KlageVurderingResultatBuilder klageVurderingOmgjør(BrevGrunnlag.KlageBehandling.KlageVurderingOmgjør klageVurderingOmgjør) {
            this.klageVurderingOmgjør = klageVurderingOmgjør;
            return this;
        }

        public KlageVurderingResultatBuilder klageHjemmel(BrevGrunnlag.KlageBehandling.KlageHjemmel klageHjemmel) {
            this.klageHjemmel = klageHjemmel;
            return this;
        }

        public KlageVurderingResultatBuilder godkjentAvMedunderskriver(boolean godkjentAvMedunderskriver) {
            this.godkjentAvMedunderskriver = godkjentAvMedunderskriver;
            return this;
        }

        public KlageVurderingResultatBuilder fritekstTilBrev(String fritekstTilBrev) {
            this.fritekstTilBrev = fritekstTilBrev;
            return this;
        }

        public BrevGrunnlag.KlageBehandling.KlageVurderingResultat build() {
            return new BrevGrunnlag.KlageBehandling.KlageVurderingResultat(klageVurdertAv, klageVurdering, begrunnelse, klageMedholdÅrsak,
                klageVurderingOmgjør, klageHjemmel, godkjentAvMedunderskriver != null && godkjentAvMedunderskriver, fritekstTilBrev);
        }
    }

    public static BehandlingsresultatBuilder behandlingsresultat() {
        return new BehandlingsresultatBuilder();
    }

    public static class BehandlingsresultatBuilder {
        private String medlemskapOpphørsårsak;
        private LocalDate medlemskapFom;
        private BrevGrunnlag.Behandlingsresultat.BehandlingResultatType behandlingResultatType;
        private String avslagsårsak;
        private BrevGrunnlag.Behandlingsresultat.Fritekst fritekst;
        private BrevGrunnlag.Behandlingsresultat.Skjæringstidspunkt skjæringstidspunkt;
        private Boolean endretDekningsgrad;
        private LocalDate opphørsdato;
        private List<BrevGrunnlag.Behandlingsresultat.KonsekvensForYtelsen> konsekvenserForYtelsen;
        private List<BrevGrunnlag.Behandlingsresultat.VilkårType> vilkårTyper;

        public BehandlingsresultatBuilder medlemskapOpphørsårsak(String medlemskapOpphørsårsak) {
            this.medlemskapOpphørsårsak = medlemskapOpphørsårsak;
            return this;
        }

        public BehandlingsresultatBuilder medlemskapFom(LocalDate medlemskapFom) {
            this.medlemskapFom = medlemskapFom;
            return this;
        }

        public BehandlingsresultatBuilder behandlingResultatType(BrevGrunnlag.Behandlingsresultat.BehandlingResultatType behandlingResultatType) {
            this.behandlingResultatType = behandlingResultatType;
            return this;
        }

        public BehandlingsresultatBuilder avslagsårsak(String avslagsårsak) {
            this.avslagsårsak = avslagsårsak;
            return this;
        }

        public BehandlingsresultatBuilder fritekst(BrevGrunnlag.Behandlingsresultat.Fritekst fritekst) {
            this.fritekst = fritekst;
            return this;
        }

        public BehandlingsresultatBuilder skjæringstidspunkt(BrevGrunnlag.Behandlingsresultat.Skjæringstidspunkt skjæringstidspunkt) {
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

        public BehandlingsresultatBuilder konsekvenserForYtelsen(List<BrevGrunnlag.Behandlingsresultat.KonsekvensForYtelsen> konsekvenserForYtelsen) {
            this.konsekvenserForYtelsen = konsekvenserForYtelsen;
            return this;
        }

        public BehandlingsresultatBuilder vilkårTyper(List<BrevGrunnlag.Behandlingsresultat.VilkårType> vilkårTyper) {
            this.vilkårTyper = vilkårTyper;
            return this;
        }

        public BrevGrunnlag.Behandlingsresultat build() {
            return new BrevGrunnlag.Behandlingsresultat(medlemskapOpphørsårsak, medlemskapFom, behandlingResultatType, avslagsårsak, fritekst,
                skjæringstidspunkt, endretDekningsgrad != null && endretDekningsgrad, opphørsdato, konsekvenserForYtelsen, vilkårTyper);
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

        public BrevGrunnlag.Behandlingsresultat.Fritekst build() {
            return new BrevGrunnlag.Behandlingsresultat.Fritekst(overskrift, brødtekst, avslagsarsakFritekst);
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

        public BrevGrunnlag.Behandlingsresultat.Skjæringstidspunkt build() {
            return new BrevGrunnlag.Behandlingsresultat.Skjæringstidspunkt(dato, utenMinsterett != null && utenMinsterett);
        }
    }
}

