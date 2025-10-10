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

public record BrevGrunnlag(UUID uuid, String saksnummer, FagsakYtelseType fagsakYtelseType, FagsakStatus fagsakStatus,
                           RelasjonsRolleType relasjonsRolleType, String aktørId, Dekningsgrad dekningsgrad, BehandlingType behandlingType,
                           LocalDateTime opprettet, LocalDateTime avsluttet, String behandlendeEnhet, Språkkode språkkode,
                           boolean automatiskBehandlet, FamilieHendelse familieHendelse, OriginalBehandling originalBehandling,
                           Rettigheter rettigheter, Behandlingsresultat behandlingsresultat, List<BehandlingÅrsakType> behandlingÅrsakTyper,
                           TilkjentYtelse tilkjentYtelse, BeregningsgrunnlagDto beregningsgrunnlag,
                           ArbeidsforholdInntektsmeldingerDto inntektsmeldingerStatus, LocalDate førsteSøknadMottattDato,
                           //Ser på mottatt dato på dokument
                           LocalDate sisteSøknadMottattDato, //Ser på mottatt dato på dokument
                           LocalDate søknadMottattDato, //Uttaksperiodegrense (når den anses å være mottatt)
                           List<Inntektsmelding> inntektsmeldinger, LocalDate nyStartDatoVedUtsattOppstart, Verge verge,
                           KlageBehandling klageBehandling, InnsynBehandling innsynBehandling, SvangerskapspengerUttak svangerskapspengerUttak,
                           ForeldrepengerUttak foreldrepengerUttak) {
    public boolean erRevurdering() {
        return behandlingType() == BehandlingType.REVURDERING;
    }

    public boolean erFørstegangssøknad() {
        return behandlingType() == BehandlingType.FØRSTEGANGSSØKNAD;
    }

    public boolean harBehandlingÅrsak(BehandlingÅrsakType behandlingÅrsakType) {
        return behandlingÅrsakTyper().stream().anyMatch(bå -> bå == behandlingÅrsakType);
    }

    public boolean erAnke() {
        return behandlingType() == BehandlingType.ANKE;
    }

    public boolean erInnsyn() {
        return behandlingType() == BehandlingType.INNSYN;
    }

    public boolean erKlage() {
        return behandlingType() == BehandlingType.KLAGE;
    }

    /**
     * Builder for BrevGrunnlag. All fields are duplicated here so they can be set fluently before calling build().
     */
    public static final class Builder {
        private UUID uuid;
        private String saksnummer;
        private FagsakYtelseType fagsakYtelseType;
        private FagsakStatus fagsakStatus;
        private RelasjonsRolleType relasjonsRolleType;
        private String aktørId;
        private Dekningsgrad dekningsgrad;
        private BehandlingType behandlingType;
        private LocalDateTime opprettet;
        private LocalDateTime avsluttet;
        private String behandlendeEnhet;
        private Språkkode språkkode;
        private boolean automatiskBehandlet;
        private FamilieHendelse familieHendelse;
        private OriginalBehandling originalBehandling;
        private Rettigheter rettigheter;
        private Behandlingsresultat behandlingsresultat;
        private List<BehandlingÅrsakType> behandlingÅrsakTyper;
        private TilkjentYtelse tilkjentYtelse;
        private BeregningsgrunnlagDto beregningsgrunnlag;
        private ArbeidsforholdInntektsmeldingerDto inntektsmeldingerStatus;
        private LocalDate førsteSøknadMottattDato;
        private LocalDate sisteSøknadMottattDato;
        private LocalDate søknadMottattDato;
        private List<Inntektsmelding> inntektsmeldinger;
        private LocalDate nyStartDatoVedUtsattOppstart;
        private Verge verge;
        private KlageBehandling klageBehandling;
        private InnsynBehandling innsynBehandling;
        private SvangerskapspengerUttak svangerskapspengerUttak;
        private ForeldrepengerUttak foreldrepengerUttak;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder uuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder saksnummer(String saksnummer) {
            this.saksnummer = saksnummer;
            return this;
        }

        public Builder fagsakYtelseType(FagsakYtelseType fagsakYtelseType) {
            this.fagsakYtelseType = fagsakYtelseType;
            return this;
        }

        public Builder fagsakStatus(FagsakStatus fagsakStatus) {
            this.fagsakStatus = fagsakStatus;
            return this;
        }

        public Builder relasjonsRolleType(RelasjonsRolleType relasjonsRolleType) {
            this.relasjonsRolleType = relasjonsRolleType;
            return this;
        }

        public Builder aktørId(String aktørId) {
            this.aktørId = aktørId;
            return this;
        }

        public Builder dekningsgrad(Dekningsgrad dekningsgrad) {
            this.dekningsgrad = dekningsgrad;
            return this;
        }

        public Builder behandlingType(BehandlingType behandlingType) {
            this.behandlingType = behandlingType;
            return this;
        }

        public Builder opprettet(LocalDateTime opprettet) {
            this.opprettet = opprettet;
            return this;
        }

        public Builder avsluttet(LocalDateTime avsluttet) {
            this.avsluttet = avsluttet;
            return this;
        }

        public Builder behandlendeEnhet(String behandlendeEnhet) {
            this.behandlendeEnhet = behandlendeEnhet;
            return this;
        }

        public Builder språkkode(Språkkode språkkode) {
            this.språkkode = språkkode;
            return this;
        }

        public Builder automatiskBehandlet(boolean automatiskBehandlet) {
            this.automatiskBehandlet = automatiskBehandlet;
            return this;
        }

        public Builder familieHendelse(FamilieHendelse familieHendelse) {
            this.familieHendelse = familieHendelse;
            return this;
        }

        public Builder originalBehandling(OriginalBehandling originalBehandling) {
            this.originalBehandling = originalBehandling;
            return this;
        }

        public Builder rettigheter(Rettigheter rettigheter) {
            this.rettigheter = rettigheter;
            return this;
        }

        public Builder behandlingsresultat(Behandlingsresultat behandlingsresultat) {
            this.behandlingsresultat = behandlingsresultat;
            return this;
        }

        public Builder behandlingÅrsakTyper(List<BehandlingÅrsakType> behandlingÅrsakTyper) {
            this.behandlingÅrsakTyper = behandlingÅrsakTyper;
            return this;
        }

        public Builder tilkjentYtelse(TilkjentYtelse tilkentYtelse) {
            this.tilkjentYtelse = tilkentYtelse;
            return this;
        }

        public Builder beregningsgrunnlag(BeregningsgrunnlagDto beregningsgrunnlag) {
            this.beregningsgrunnlag = beregningsgrunnlag;
            return this;
        }

        public Builder inntektsmeldingerStatus(ArbeidsforholdInntektsmeldingerDto inntektsmeldingerStatus) {
            this.inntektsmeldingerStatus = inntektsmeldingerStatus;
            return this;
        }

        public Builder førsteSøknadMottattDato(LocalDate førsteSøknadMottattDato) {
            this.førsteSøknadMottattDato = førsteSøknadMottattDato;
            return this;
        }

        public Builder sisteSøknadMottattDato(LocalDate sisteSøknadMottattDato) {
            this.sisteSøknadMottattDato = sisteSøknadMottattDato;
            return this;
        }

        public Builder søknadMottattDato(LocalDate søknadMottattDato) {
            this.søknadMottattDato = søknadMottattDato;
            return this;
        }

        public Builder inntektsmeldinger(List<Inntektsmelding> inntektsmeldinger) {
            this.inntektsmeldinger = inntektsmeldinger;
            return this;
        }

        public Builder nyStartDatoVedUtsattOppstart(LocalDate nyStartDatoVedUtsattOppstart) {
            this.nyStartDatoVedUtsattOppstart = nyStartDatoVedUtsattOppstart;
            return this;
        }

        public Builder verge(Verge verge) {
            this.verge = verge;
            return this;
        }

        public Builder klageBehandling(KlageBehandling klageBehandling) {
            this.klageBehandling = klageBehandling;
            return this;
        }

        public Builder innsynBehandling(InnsynBehandling innsynBehandling) {
            this.innsynBehandling = innsynBehandling;
            return this;
        }

        public Builder svangerskapspengerUttak(SvangerskapspengerUttak svangerskapspengerUttak) {
            this.svangerskapspengerUttak = svangerskapspengerUttak;
            return this;
        }

        public Builder foreldrepengerUttak(ForeldrepengerUttak foreldrepengerUttak) {
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

    public static Builder builder() {
        return Builder.builder();
    }

    public enum FagsakYtelseType {
        ENGANGSTØNAD,
        FORELDREPENGER,
        SVANGERSKAPSPENGER,
    }

    public enum FagsakStatus {
        OPPRETTET,
        UNDER_BEHANDLING,
        LØPENDE,
        AVSLUTTET,
    }

    public enum RelasjonsRolleType {
        FARA,
        MORA,
        MEDMOR,
    }

    public enum BehandlingType {
        FØRSTEGANGSSØKNAD,
        REVURDERING,
        KLAGE,
        ANKE,
        INNSYN,
        TILBAKEKREVING,
        TILBAKEKREVING_REVURDERING,
    }

    public enum Språkkode {
        BOKMÅL,
        NYNORSK,
        ENGELSK
    }

    public enum BehandlingÅrsakType {
        RE_FEIL_I_LOVANDVENDELSE,
        RE_FEIL_REGELVERKSFORSTÅELSE,
        RE_FEIL_ELLER_ENDRET_FAKTA,
        RE_FEIL_PROSESSUELL,
        RE_ANNET,
        RE_OPPLYSNINGER_OM_MEDLEMSKAP,
        RE_OPPLYSNINGER_OM_OPPTJENING,
        RE_OPPLYSNINGER_OM_FORDELING,
        RE_OPPLYSNINGER_OM_INNTEKT,
        RE_OPPLYSNINGER_OM_FØDSEL,
        RE_OPPLYSNINGER_OM_DØD,
        RE_OPPLYSNINGER_OM_SØKERS_REL,
        RE_OPPLYSNINGER_OM_SØKNAD_FRIST,
        RE_OPPLYSNINGER_OM_BEREGNINGSGRUNNLAG,
        RE_KLAGE_UTEN_END_INNTEKT,
        RE_KLAGE_MED_END_INNTEKT,
        ETTER_KLAGE,
        RE_MANGLER_FØDSEL,
        RE_MANGLER_FØDSEL_I_PERIODE,
        RE_AVVIK_ANTALL_BARN,
        RE_ENDRING_FRA_BRUKER,
        RE_ENDRET_INNTEKTSMELDING,
        BERØRT_BEHANDLING,
        REBEREGN_FERIEPENGER,
        RE_UTSATT_START,
        RE_SATS_REGULERING,
        ENDRE_DEKNINGSGRAD,
        INFOBREV_BEHANDLING,
        INFOBREV_OPPHOLD,
        INFOBREV_PÅMINNELSE,
        OPPHØR_YTELSE_NYTT_BARN,
        RE_HENDELSE_FØDSEL,
        RE_HENDELSE_DØD_FORELDER,
        RE_HENDELSE_DØD_BARN,
        RE_HENDELSE_DØDFØDSEL,
        RE_HENDELSE_UTFLYTTING,
        RE_VEDTAK_PLEIEPENGER,
        FEIL_PRAKSIS_UTSETTELSE,
        FEIL_PRAKSIS_IVERKS_UTSET,
        FEIL_PRAKSIS_BG_AAP_KOMBI,
        KLAGE_TILBAKEBETALING,
        RE_OPPLYSNINGER_OM_YTELSER,
        RE_REGISTEROPPLYSNING,
        KØET_BEHANDLING,
        RE_TILSTØTENDE_YTELSE_INNVILGET,
        RE_TILSTØTENDE_YTELSE_OPPHØRT,
        UDEFINERT,
    }

    public enum Dekningsgrad {
        HUNDRE,
        ÅTTI
    }

    public record OriginalBehandling(FamilieHendelse familieHendelse, Behandlingsresultat.BehandlingResultatType originalBehandlingResultatType,
                                     LocalDate førsteDagMedUtbetaltForeldrepenger) {
        public static Builder builder() {
            return new Builder();
        }

        public static final class Builder {
            private FamilieHendelse familieHendelse;
            private Behandlingsresultat.BehandlingResultatType originalBehandlingResultatType;
            private LocalDate førsteDagMedUtbetaltForeldrepenger;

            public Builder familieHendelse(FamilieHendelse familieHendelse) {
                this.familieHendelse = familieHendelse;
                return this;
            }

            public Builder originalBehandlingResultatType(Behandlingsresultat.BehandlingResultatType originalBehandlingResultatType) {
                this.originalBehandlingResultatType = originalBehandlingResultatType;
                return this;
            }

            public Builder førsteDagMedUtbetaltForeldrepenger(LocalDate førsteDagMedUtbetaltForeldrepenger) {
                this.førsteDagMedUtbetaltForeldrepenger = førsteDagMedUtbetaltForeldrepenger;
                return this;
            }

            public OriginalBehandling build() {
                return new OriginalBehandling(familieHendelse, originalBehandlingResultatType, førsteDagMedUtbetaltForeldrepenger);
            }
        }
    }

    public record TilkjentYtelse(TilkjentYtelseEngangsstønadDto engangsstønad, TilkjentYtelseEngangsstønadDto originalBehandlingEngangsstønad,
                                 TilkjentYtelseDagytelseDto dagytelse) {
        public static Builder builder() {
            return new Builder();
        }

        public static final class Builder {
            private TilkjentYtelseEngangsstønadDto engangsstønad;
            private TilkjentYtelseEngangsstønadDto originalBehandlingEngangsstønad;
            private TilkjentYtelseDagytelseDto dagytelse;

            public Builder engangsstønad(TilkjentYtelseEngangsstønadDto engangsstønad) {
                this.engangsstønad = engangsstønad;
                return this;
            }

            public Builder originalBehandlingEngangsstønad(TilkjentYtelseEngangsstønadDto originalBehandlingEngangsstønad) {
                this.originalBehandlingEngangsstønad = originalBehandlingEngangsstønad;
                return this;
            }

            public Builder dagytelse(TilkjentYtelseDagytelseDto dagytelse) {
                this.dagytelse = dagytelse;
                return this;
            }

            public TilkjentYtelse build() {
                return new TilkjentYtelse(engangsstønad, originalBehandlingEngangsstønad, dagytelse);
            }
        }
    }

    public record ForeldrepengerUttak(List<Stønadskonto> stønadskontoer, int tapteDagerFpff, List<Periode> perioderSøker,
                                      List<Periode> perioderAnnenpart, boolean ønskerJustertUttakVedFødsel) {
        public static Builder builder() {
            return new Builder();
        }

        public static final class Builder {
            private List<Stønadskonto> stønadskontoer;
            private Integer tapteDagerFpff;
            private List<Periode> perioderSøker;
            private List<Periode> perioderAnnenpart;
            private Boolean ønskerJustertUttakVedFødsel;

            public Builder stønadskontoer(List<Stønadskonto> stønadskontoer) {
                this.stønadskontoer = stønadskontoer;
                return this;
            }

            public Builder tapteDagerFpff(int tapteDagerFpff) {
                this.tapteDagerFpff = tapteDagerFpff;
                return this;
            }

            public Builder perioderSøker(List<Periode> perioderSøker) {
                this.perioderSøker = perioderSøker;
                return this;
            }

            public Builder perioderAnnenpart(List<Periode> perioderAnnenpart) {
                this.perioderAnnenpart = perioderAnnenpart;
                return this;
            }

            public Builder ønskerJustertUttakVedFødsel(boolean ønskerJustertUttakVedFødsel) {
                this.ønskerJustertUttakVedFødsel = ønskerJustertUttakVedFødsel;
                return this;
            }

            public ForeldrepengerUttak build() {
                return new ForeldrepengerUttak(stønadskontoer, tapteDagerFpff == null ? 0 : tapteDagerFpff, perioderSøker, perioderAnnenpart,
                    ønskerJustertUttakVedFødsel != null && ønskerJustertUttakVedFødsel);
            }
        }

        public record Periode(LocalDate fom, LocalDate tom, List<Aktivitet> aktiviteter, PeriodeResultatType periodeResultatType,
                              String periodeResultatÅrsak, String graderingAvslagÅrsak, String periodeResultatÅrsakLovhjemmel,
                              String graderingsAvslagÅrsakLovhjemmel, LocalDate tidligstMottattDato,
                              boolean erUtbetalingRedusertTilMorsStillingsprosent) {
            public boolean isInnvilget() {
                return periodeResultatType == PeriodeResultatType.INNVILGET;
            }

            public static Builder builder() {
                return new Builder();
            }

            public static final class Builder {
                private LocalDate fom;
                private LocalDate tom;
                private List<Aktivitet> aktiviteter;
                private PeriodeResultatType periodeResultatType;
                private String periodeResultatÅrsak;
                private String graderingAvslagÅrsak;
                private String periodeResultatÅrsakLovhjemmel;
                private String graderingsAvslagÅrsakLovhjemmel;
                private LocalDate tidligstMottattDato;
                private Boolean erUtbetalingRedusertTilMorsStillingsprosent;

                public Builder fom(LocalDate fom) {
                    this.fom = fom;
                    return this;
                }

                public Builder tom(LocalDate tom) {
                    this.tom = tom;
                    return this;
                }

                public Builder aktiviteter(List<Aktivitet> aktiviteter) {
                    this.aktiviteter = aktiviteter;
                    return this;
                }

                public Builder periodeResultatType(PeriodeResultatType periodeResultatType) {
                    this.periodeResultatType = periodeResultatType;
                    return this;
                }

                public Builder periodeResultatÅrsak(String periodeResultatÅrsak) {
                    this.periodeResultatÅrsak = periodeResultatÅrsak;
                    return this;
                }

                public Builder graderingAvslagÅrsak(String graderingAvslagÅrsak) {
                    this.graderingAvslagÅrsak = graderingAvslagÅrsak;
                    return this;
                }

                public Builder periodeResultatÅrsakLovhjemmel(String periodeResultatÅrsakLovhjemmel) {
                    this.periodeResultatÅrsakLovhjemmel = periodeResultatÅrsakLovhjemmel;
                    return this;
                }

                public Builder graderingsAvslagÅrsakLovhjemmel(String graderingsAvslagÅrsakLovhjemmel) {
                    this.graderingsAvslagÅrsakLovhjemmel = graderingsAvslagÅrsakLovhjemmel;
                    return this;
                }

                public Builder tidligstMottattDato(LocalDate tidligstMottattDato) {
                    this.tidligstMottattDato = tidligstMottattDato;
                    return this;
                }

                public Builder erUtbetalingRedusertTilMorsStillingsprosent(boolean erUtbetalingRedusertTilMorsStillingsprosent) {
                    this.erUtbetalingRedusertTilMorsStillingsprosent = erUtbetalingRedusertTilMorsStillingsprosent;
                    return this;
                }

                public Periode build() {
                    return new Periode(fom, tom, aktiviteter, periodeResultatType, periodeResultatÅrsak, graderingAvslagÅrsak,
                        periodeResultatÅrsakLovhjemmel, graderingsAvslagÅrsakLovhjemmel, tidligstMottattDato,
                        erUtbetalingRedusertTilMorsStillingsprosent != null && erUtbetalingRedusertTilMorsStillingsprosent);
                }
            }
        }

        public record Aktivitet(TrekkontoType trekkontoType, BigDecimal trekkdager, BigDecimal prosentArbeid, String arbeidsgiverReferanse,
                                String arbeidsforholdId, BigDecimal utbetalingsgrad, UttakArbeidType uttakArbeidType, boolean gradering) {
            public static Builder builder() {
                return new Builder();
            }

            public static final class Builder {
                private TrekkontoType trekkontoType;
                private BigDecimal trekkdager;
                private BigDecimal prosentArbeid;
                private String arbeidsgiverReferanse;
                private String arbeidsforholdId;
                private BigDecimal utbetalingsgrad;
                private UttakArbeidType uttakArbeidType;
                private Boolean gradering;

                public Builder trekkontoType(TrekkontoType trekkontoType) {
                    this.trekkontoType = trekkontoType;
                    return this;
                }

                public Builder trekkdager(BigDecimal trekkdager) {
                    this.trekkdager = trekkdager;
                    return this;
                }

                public Builder prosentArbeid(BigDecimal prosentArbeid) {
                    this.prosentArbeid = prosentArbeid;
                    return this;
                }

                public Builder arbeidsgiverReferanse(String arbeidsgiverReferanse) {
                    this.arbeidsgiverReferanse = arbeidsgiverReferanse;
                    return this;
                }

                public Builder arbeidsforholdId(String arbeidsforholdId) {
                    this.arbeidsforholdId = arbeidsforholdId;
                    return this;
                }

                public Builder utbetalingsgrad(BigDecimal utbetalingsgrad) {
                    this.utbetalingsgrad = utbetalingsgrad;
                    return this;
                }

                public Builder uttakArbeidType(UttakArbeidType uttakArbeidType) {
                    this.uttakArbeidType = uttakArbeidType;
                    return this;
                }

                public Builder gradering(boolean gradering) {
                    this.gradering = gradering;
                    return this;
                }

                public Aktivitet build() {
                    return new Aktivitet(trekkontoType, trekkdager, prosentArbeid, arbeidsgiverReferanse, arbeidsforholdId, utbetalingsgrad,
                        uttakArbeidType, gradering != null && gradering);
                }
            }
        }

        public record Stønadskonto(Type stønadskontotype, int maxDager, int saldo, KontoUtvidelser kontoUtvidelser) {
            public static Builder builder() {
                return new Builder();
            }

            public static final class Builder {
                private Type stønadskontotype;
                private Integer maxDager;
                private Integer saldo;
                private KontoUtvidelser kontoUtvidelser;

                public Builder stønadskontotype(Type stønadskontotype) {
                    this.stønadskontotype = stønadskontotype;
                    return this;
                }

                public Builder maxDager(int maxDager) {
                    this.maxDager = maxDager;
                    return this;
                }

                public Builder saldo(int saldo) {
                    this.saldo = saldo;
                    return this;
                }

                public Builder kontoUtvidelser(KontoUtvidelser kontoUtvidelser) {
                    this.kontoUtvidelser = kontoUtvidelser;
                    return this;
                }

                public Stønadskonto build() {
                    return new Stønadskonto(stønadskontotype, maxDager == null ? 0 : maxDager, saldo == null ? 0 : saldo, kontoUtvidelser);
                }
            }

            public enum Type {
                MØDREKVOTE,
                FEDREKVOTE,
                FELLESPERIODE,
                FORELDREPENGER,
                FORELDREPENGER_FØR_FØDSEL,
                FLERBARNSDAGER,
                UTEN_AKTIVITETSKRAV,
                MINSTERETT_NESTE_STØNADSPERIODE,
                MINSTERETT
            }
        }

        public record KontoUtvidelser(int prematurdager, int flerbarnsdager) {
            public static Builder builder() {
                return new Builder();
            }

            public static final class Builder {
                private Integer prematurdager;
                private Integer flerbarnsdager;

                public Builder prematurdager(int prematurdager) {
                    this.prematurdager = prematurdager;
                    return this;
                }

                public Builder flerbarnsdager(int flerbarnsdager) {
                    this.flerbarnsdager = flerbarnsdager;
                    return this;
                }

                public KontoUtvidelser build() {
                    return new KontoUtvidelser(prematurdager == null ? 0 : prematurdager, flerbarnsdager == null ? 0 : flerbarnsdager);
                }
            }
        }

        public enum TrekkontoType {
            FELLESPERIODE,
            MØDREKVOTE,
            FEDREKVOTE,
            FORELDREPENGER,
            FORELDREPENGER_FØR_FØDSEL,
            UDEFINERT
        }
    }

    public enum UttakArbeidType {
        ORDINÆRT_ARBEID,
        SELVSTENDIG_NÆRINGSDRIVENDE,
        FRILANS,
        ANNET
    }

    public record SvangerskapspengerUttak(List<UttakArbeidsforhold> uttakArbeidsforhold) {
        public static Builder builder() {
            return new Builder();
        }

        public static final class Builder {
            private List<UttakArbeidsforhold> uttakArbeidsforhold;

            public Builder uttakArbeidsforhold(List<UttakArbeidsforhold> uttakArbeidsforhold) {
                this.uttakArbeidsforhold = uttakArbeidsforhold;
                return this;
            }

            public SvangerskapspengerUttak build() {
                return new SvangerskapspengerUttak(uttakArbeidsforhold);
            }
        }

        public record UttakArbeidsforhold(String arbeidsforholdIkkeOppfyltÅrsak, String arbeidsgiverReferanse, UttakArbeidType arbeidType,
                                          List<Periode> perioder) {
            public static Builder builder() {
                return new Builder();
            }

            public static final class Builder {
                private String arbeidsforholdIkkeOppfyltÅrsak;
                private String arbeidsgiverReferanse;
                private UttakArbeidType arbeidType;
                private List<Periode> perioder;

                public Builder arbeidsforholdIkkeOppfyltÅrsak(String arbeidsforholdIkkeOppfyltÅrsak) {
                    this.arbeidsforholdIkkeOppfyltÅrsak = arbeidsforholdIkkeOppfyltÅrsak;
                    return this;
                }

                public Builder arbeidsgiverReferanse(String arbeidsgiverReferanse) {
                    this.arbeidsgiverReferanse = arbeidsgiverReferanse;
                    return this;
                }

                public Builder arbeidType(UttakArbeidType arbeidType) {
                    this.arbeidType = arbeidType;
                    return this;
                }

                public Builder perioder(List<Periode> perioder) {
                    this.perioder = perioder;
                    return this;
                }

                public UttakArbeidsforhold build() {
                    return new UttakArbeidsforhold(arbeidsforholdIkkeOppfyltÅrsak, arbeidsgiverReferanse, arbeidType, perioder);
                }
            }
        }

        public record Periode(LocalDate fom, LocalDate tom, BigDecimal utbetalingsgrad, PeriodeResultatType periodeResultatType,
                              String periodeIkkeOppfyltÅrsak) {
            public static Builder builder() {
                return new Builder();
            }

            public static final class Builder {
                private LocalDate fom;
                private LocalDate tom;
                private BigDecimal utbetalingsgrad;
                private PeriodeResultatType periodeResultatType;
                private String periodeIkkeOppfyltÅrsak;

                public Builder fom(LocalDate fom) {
                    this.fom = fom;
                    return this;
                }

                public Builder tom(LocalDate tom) {
                    this.tom = tom;
                    return this;
                }

                public Builder utbetalingsgrad(BigDecimal utbetalingsgrad) {
                    this.utbetalingsgrad = utbetalingsgrad;
                    return this;
                }

                public Builder periodeResultatType(PeriodeResultatType periodeResultatType) {
                    this.periodeResultatType = periodeResultatType;
                    return this;
                }

                public Builder periodeIkkeOppfyltÅrsak(String periodeIkkeOppfyltÅrsak) {
                    this.periodeIkkeOppfyltÅrsak = periodeIkkeOppfyltÅrsak;
                    return this;
                }

                public Periode build() {
                    return new Periode(fom, tom, utbetalingsgrad, periodeResultatType, periodeIkkeOppfyltÅrsak);
                }
            }
        }
    }

    public enum PeriodeResultatType {
        INNVILGET,
        AVSLÅTT,
        MANUELL_BEHANDLING
    }

    public record Inntektsmelding(String arbeidsgiverReferanse, LocalDateTime innsendingstidspunkt) {
        public static Builder builder() {
            return new Builder();
        }

        public static final class Builder {
            private String arbeidsgiverReferanse;
            private LocalDateTime innsendingstidspunkt;

            public Builder arbeidsgiverReferanse(String arbeidsgiverReferanse) {
                this.arbeidsgiverReferanse = arbeidsgiverReferanse;
                return this;
            }

            public Builder innsendingstidspunkt(LocalDateTime innsendingstidspunkt) {
                this.innsendingstidspunkt = innsendingstidspunkt;
                return this;
            }

            public Inntektsmelding build() {
                return new Inntektsmelding(arbeidsgiverReferanse, innsendingstidspunkt);
            }
        }
    }

    public record Rettigheter(Rettighetstype opprinnelig, Rettighetstype gjeldende, EøsUttak eøsUttak) {
        public static Builder builder() {
            return new Builder();
        }

        public static final class Builder {
            private Rettighetstype opprinnelig;
            private Rettighetstype gjeldende;
            private EøsUttak eøsUttak;

            public Builder opprinnelig(Rettighetstype opprinnelig) {
                this.opprinnelig = opprinnelig;
                return this;
            }

            public Builder gjeldende(Rettighetstype gjeldende) {
                this.gjeldende = gjeldende;
                return this;
            }

            public Builder eøsUttak(EøsUttak eøsUttak) {
                this.eøsUttak = eøsUttak;
                return this;
            }

            public Rettigheter build() {
                return new Rettigheter(opprinnelig, gjeldende, eøsUttak);
            }
        }

        public record EøsUttak(LocalDate fom, LocalDate tom, int forbruktFellesperiode, int fellesperiodeINorge) {
            public static Builder builder() {
                return new Builder();
            }

            public static final class Builder {
                private LocalDate fom;
                private LocalDate tom;
                private Integer forbruktFellesperiode;
                private Integer fellesperiodeINorge;

                public Builder fom(LocalDate fom) {
                    this.fom = fom;
                    return this;
                }

                public Builder tom(LocalDate tom) {
                    this.tom = tom;
                    return this;
                }

                public Builder forbruktFellesperiode(int forbruktFellesperiode) {
                    this.forbruktFellesperiode = forbruktFellesperiode;
                    return this;
                }

                public Builder fellesperiodeINorge(int fellesperiodeINorge) {
                    this.fellesperiodeINorge = fellesperiodeINorge;
                    return this;
                }

                public EøsUttak build() {
                    return new EøsUttak(fom, tom, forbruktFellesperiode == null ? 0 : forbruktFellesperiode,
                        fellesperiodeINorge == null ? 0 : fellesperiodeINorge);
                }
            }
        }

        public enum Rettighetstype {
            ALENEOMSORG,
            BEGGE_RETT,
            BEGGE_RETT_EØS,
            BARE_MOR_RETT,
            BARE_FAR_RETT,
            BARE_FAR_RETT_MOR_UFØR
        }
    }

    public record FamilieHendelse(List<Barn> barn, LocalDate termindato, int antallBarn, LocalDate omsorgsovertakelse) {
        public boolean gjelderFødsel() {
            return omsorgsovertakelse == null;
        }

        public boolean barnErFødt() {
            return !barn.isEmpty();
        }

        public static Builder builder() {
            return new Builder();
        }

        public static final class Builder {
            private List<Barn> barn;
            private LocalDate termindato;
            private Integer antallBarn;
            private LocalDate omsorgsovertakelse;

            public Builder barn(List<Barn> barn) {
                this.barn = barn;
                return this;
            }

            public Builder termindato(LocalDate termindato) {
                this.termindato = termindato;
                return this;
            }

            public Builder antallBarn(int antallBarn) {
                this.antallBarn = antallBarn;
                return this;
            }

            public Builder omsorgsovertakelse(LocalDate omsorgsovertakelse) {
                this.omsorgsovertakelse = omsorgsovertakelse;
                return this;
            }

            public FamilieHendelse build() {
                return new FamilieHendelse(barn, termindato, antallBarn == null ? 0 : antallBarn, omsorgsovertakelse);
            }
        }
    }

    public record Barn(LocalDate fødselsdato, LocalDate dødsdato) {
        public static Builder builder() {
            return new Builder();
        }

        public static final class Builder {
            private LocalDate fødselsdato;
            private LocalDate dødsdato;

            public Builder fødselsdato(LocalDate fødselsdato) {
                this.fødselsdato = fødselsdato;
                return this;
            }

            public Builder dødsdato(LocalDate dødsdato) {
                this.dødsdato = dødsdato;
                return this;
            }

            public Barn build() {
                return new Barn(fødselsdato, dødsdato);
            }
        }
    }

    public record Verge(String aktørId, String navn, String organisasjonsnummer, LocalDate gyldigFom, LocalDate gyldigTom, VergeType vergeType) {
        public static Builder builder() {
            return new Builder();
        }

        public static final class Builder {
            private String aktørId;
            private String navn;
            private String organisasjonsnummer;
            private LocalDate gyldigFom;
            private LocalDate gyldigTom;
            private VergeType vergeType;

            public Builder aktørId(String aktørId) {
                this.aktørId = aktørId;
                return this;
            }

            public Builder navn(String navn) {
                this.navn = navn;
                return this;
            }

            public Builder organisasjonsnummer(String organisasjonsnummer) {
                this.organisasjonsnummer = organisasjonsnummer;
                return this;
            }

            public Builder gyldigFom(LocalDate gyldigFom) {
                this.gyldigFom = gyldigFom;
                return this;
            }

            public Builder gyldigTom(LocalDate gyldigTom) {
                this.gyldigTom = gyldigTom;
                return this;
            }

            public Builder vergeType(VergeType vergeType) {
                this.vergeType = vergeType;
                return this;
            }

            public Verge build() {
                return new Verge(aktørId, navn, organisasjonsnummer, gyldigFom, gyldigTom, vergeType);
            }
        }

        enum VergeType {
            BARN,
            FBARN,
            VOKSEN,
            ADVOKAT,
            ANNEN_F
        }
    }

    public record InnsynBehandling(InnsynResultatType innsynResultatType, List<InnsynDokument> dokumenter) {
        public static Builder builder() {
            return new Builder();
        }

        public static final class Builder {
            private InnsynResultatType innsynResultatType;
            private List<InnsynDokument> dokumenter;

            public Builder innsynResultatType(InnsynResultatType innsynResultatType) {
                this.innsynResultatType = innsynResultatType;
                return this;
            }

            public Builder dokumenter(List<InnsynDokument> dokumenter) {
                this.dokumenter = dokumenter;
                return this;
            }

            public InnsynBehandling build() {
                return new InnsynBehandling(innsynResultatType, dokumenter);
            }
        }

        public enum InnsynResultatType {
            INNVILGET,
            DELVIS_INNVILGET,
            AVVIST,
            UDEFINERT
        }

        public record InnsynDokument(boolean fikkInnsyn, String journalpostId, String dokumentId) {
            public static Builder builder() {
                return new Builder();
            }

            public static final class Builder {
                private Boolean fikkInnsyn;
                private String journalpostId;
                private String dokumentId;

                public Builder fikkInnsyn(boolean fikkInnsyn) {
                    this.fikkInnsyn = fikkInnsyn;
                    return this;
                }

                public Builder journalpostId(String journalpostId) {
                    this.journalpostId = journalpostId;
                    return this;
                }

                public Builder dokumentId(String dokumentId) {
                    this.dokumentId = dokumentId;
                    return this;
                }

                public InnsynDokument build() {
                    return new InnsynDokument(fikkInnsyn != null && fikkInnsyn, journalpostId, dokumentId);
                }
            }
        }
    }

    public record KlageBehandling(KlageFormkravResultat klageFormkravResultatNFP, KlageVurderingResultat klageVurderingResultatNFP,
                                  KlageFormkravResultat klageFormkravResultatKA, KlageVurderingResultat klageVurderingResultatNK,
                                  List<KlageHjemmel> aktuelleHjemler, boolean underBehandlingKabal, boolean behandletAvKabal, LocalDate mottattDato) {
        public static Builder builder() {
            return new Builder();
        }

        public static final class Builder {
            private KlageFormkravResultat klageFormkravResultatNFP;
            private KlageVurderingResultat klageVurderingResultatNFP;
            private KlageFormkravResultat klageFormkravResultatKA;
            private KlageVurderingResultat klageVurderingResultatNK;
            private List<KlageHjemmel> aktuelleHjemler;
            private Boolean underBehandlingKabal;
            private Boolean behandletAvKabal;
            private LocalDate mottattDato;

            public Builder klageFormkravResultatNFP(KlageFormkravResultat v) {
                this.klageFormkravResultatNFP = v;
                return this;
            }

            public Builder klageVurderingResultatNFP(KlageVurderingResultat v) {
                this.klageVurderingResultatNFP = v;
                return this;
            }

            public Builder klageFormkravResultatKA(KlageFormkravResultat v) {
                this.klageFormkravResultatKA = v;
                return this;
            }

            public Builder klageVurderingResultatNK(KlageVurderingResultat v) {
                this.klageVurderingResultatNK = v;
                return this;
            }

            public Builder aktuelleHjemler(List<KlageHjemmel> v) {
                this.aktuelleHjemler = v;
                return this;
            }

            public Builder underBehandlingKabal(boolean v) {
                this.underBehandlingKabal = v;
                return this;
            }

            public Builder behandletAvKabal(boolean v) {
                this.behandletAvKabal = v;
                return this;
            }

            public Builder mottattDato(LocalDate v) {
                this.mottattDato = v;
                return this;
            }

            public KlageBehandling build() {
                return new KlageBehandling(klageFormkravResultatNFP, klageVurderingResultatNFP, klageFormkravResultatKA, klageVurderingResultatNK,
                    aktuelleHjemler, underBehandlingKabal != null && underBehandlingKabal, behandletAvKabal != null && behandletAvKabal, mottattDato);
            }
        }

        public record KlageFormkravResultat(Long påklagdBehandlingId, UUID påklagdBehandlingUuid, BehandlingType påklagdBehandlingType,
                                            String begrunnelse, boolean erKlagerPart, boolean erKlageKonkret, boolean erKlagefirstOverholdt,
                                            boolean erSignert, List<KlageAvvistÅrsak> avvistÅrsaker) {
            public static Builder builder() {
                return new Builder();
            }

            public static final class Builder {
                private Long påklagdBehandlingId;
                private UUID påklagdBehandlingUuid;
                private BehandlingType påklagdBehandlingType;
                private String begrunnelse;
                private Boolean erKlagerPart;
                private Boolean erKlageKonkret;
                private Boolean erKlagefirstOverholdt;
                private Boolean erSignert;
                private List<KlageAvvistÅrsak> avvistÅrsaker;

                public Builder påklagdBehandlingId(Long v) {
                    this.påklagdBehandlingId = v;
                    return this;
                }

                public Builder påklagdBehandlingUuid(UUID v) {
                    this.påklagdBehandlingUuid = v;
                    return this;
                }

                public Builder påklagdBehandlingType(BehandlingType v) {
                    this.påklagdBehandlingType = v;
                    return this;
                }

                public Builder begrunnelse(String v) {
                    this.begrunnelse = v;
                    return this;
                }

                public Builder erKlagerPart(boolean v) {
                    this.erKlagerPart = v;
                    return this;
                }

                public Builder erKlageKonkret(boolean v) {
                    this.erKlageKonkret = v;
                    return this;
                }

                public Builder erKlagefirstOverholdt(boolean v) {
                    this.erKlagefirstOverholdt = v;
                    return this;
                }

                public Builder erSignert(boolean v) {
                    this.erSignert = v;
                    return this;
                }

                public Builder avvistÅrsaker(List<KlageAvvistÅrsak> v) {
                    this.avvistÅrsaker = v;
                    return this;
                }

                public KlageFormkravResultat build() {
                    return new KlageFormkravResultat(påklagdBehandlingId, påklagdBehandlingUuid, påklagdBehandlingType, begrunnelse,
                        erKlagerPart != null && erKlagerPart, erKlageKonkret != null && erKlageKonkret,
                        erKlagefirstOverholdt != null && erKlagefirstOverholdt, erSignert != null && erSignert, avvistÅrsaker);
                }
            }
        }

        public record KlageVurderingResultat(String klageVurdertAv, KlageVurdering klageVurdering, String begrunnelse,
                                             KlageMedholdÅrsak klageMedholdÅrsak, KlageVurderingOmgjør klageVurderingOmgjør,
                                             KlageHjemmel klageHjemmel, boolean godkjentAvMedunderskriver, String fritekstTilBrev) {
            public static Builder builder() {
                return new Builder();
            }

            public static final class Builder {
                private String klageVurdertAv;
                private KlageVurdering klageVurdering;
                private String begrunnelse;
                private KlageMedholdÅrsak klageMedholdÅrsak;
                private KlageVurderingOmgjør klageVurderingOmgjør;
                private KlageHjemmel klageHjemmel;
                private Boolean godkjentAvMedunderskriver;
                private String fritekstTilBrev;

                public Builder klageVurdertAv(String v) {
                    this.klageVurdertAv = v;
                    return this;
                }

                public Builder klageVurdering(KlageVurdering v) {
                    this.klageVurdering = v;
                    return this;
                }

                public Builder begrunnelse(String v) {
                    this.begrunnelse = v;
                    return this;
                }

                public Builder klageMedholdÅrsak(KlageMedholdÅrsak v) {
                    this.klageMedholdÅrsak = v;
                    return this;
                }

                public Builder klageVurderingOmgjør(KlageVurderingOmgjør v) {
                    this.klageVurderingOmgjør = v;
                    return this;
                }

                public Builder klageHjemmel(KlageHjemmel v) {
                    this.klageHjemmel = v;
                    return this;
                }

                public Builder godkjentAvMedunderskriver(boolean v) {
                    this.godkjentAvMedunderskriver = v;
                    return this;
                }

                public Builder fritekstTilBrev(String v) {
                    this.fritekstTilBrev = v;
                    return this;
                }

                public KlageVurderingResultat build() {
                    return new KlageVurderingResultat(klageVurdertAv, klageVurdering, begrunnelse, klageMedholdÅrsak, klageVurderingOmgjør,
                        klageHjemmel, godkjentAvMedunderskriver != null && godkjentAvMedunderskriver, fritekstTilBrev);
                }
            }
        }

        enum KlageVurdering {
            OPPHEVE_YTELSESVEDTAK,
            STADFESTE_YTELSESVEDTAK,
            MEDHOLD_I_KLAGE,
            AVVIS_KLAGE,
            HJEMSENDE_UTEN_Å_OPPHEVE,
            UDEFINERT
        }

        enum KlageMedholdÅrsak {
            NYE_OPPLYSNINGER,
            ULIK_REGELVERKSTOLKNING,
            ULIK_VURDERING,
            PROSESSUELL_FEIL,
            UDEFINERT
        }

        enum KlageVurderingOmgjør {
            GUNST_MEDHOLD_I_KLAGE,
            DELVIS_MEDHOLD_I_KLAGE,
            UGUNST_MEDHOLD_I_KLAGE,
            UDEFINERT
        }

        public enum KlageAvvistÅrsak {
            KLAGET_FOR_SENT,
            KLAGE_UGYLDIG,
            IKKE_PÅKLAGD_VEDTAK,
            KLAGER_IKKE_PART,
            IKKE_KONKRET,
            IKKE_SIGNERT,
            UDEFINERT
        }

        enum KlageHjemmel {
            MEDLEM,
            SVANGERSKAP,
            FORELDRE,
            OPPTJENING,
            BEREGNING,
            DAGER,
            UTTAK,
            UTSETTELSE,
            KVOTER,
            AKTIVITET,
            BFHR,
            FARALENE,
            GRADERING,
            ENGANGS,
            OPPTJENINGSTID,
            OPPLYSNINGSPLIKT,
            FREMSETT,
            TILBAKE,
            EØS_YTELSE,
            EØS_OPPTJEN,
            UDEFINERT
        }
    }

    public record Behandlingsresultat(String medlemskapOpphørsårsak, LocalDate medlemskapFom, BehandlingResultatType behandlingResultatType,
                                      String avslagsårsak, Fritekst fritekst, Skjæringstidspunkt skjæringstidspunkt, boolean endretDekningsgrad,
                                      LocalDate opphørsdato, List<KonsekvensForYtelsen> konsekvenserForYtelsen, List<VilkårType> vilkårTyper) {
        public static Builder builder() {
            return new Builder();
        }

        public static final class Builder {
            private String medlemskapOpphørsårsak;
            private LocalDate medlemskapFom;
            private BehandlingResultatType behandlingResultatType;
            private String avslagsårsak;
            private Fritekst fritekst;
            private Skjæringstidspunkt skjæringstidspunkt;
            private Boolean endretDekningsgrad;
            private LocalDate opphørsdato;
            private List<KonsekvensForYtelsen> konsekvenserForYtelsen;
            private List<VilkårType> vilkårTyper;

            public Builder medlemskapOpphørsårsak(String medlemskapOpphørsårsak) {
                this.medlemskapOpphørsårsak = medlemskapOpphørsårsak;
                return this;
            }

            public Builder medlemskapFom(LocalDate medlemskapFom) {
                this.medlemskapFom = medlemskapFom;
                return this;
            }

            public Builder behandlingResultatType(BehandlingResultatType behandlingResultatType) {
                this.behandlingResultatType = behandlingResultatType;
                return this;
            }

            public Builder avslagsårsak(String avslagsårsak) {
                this.avslagsårsak = avslagsårsak;
                return this;
            }

            public Builder fritekst(Fritekst fritekst) {
                this.fritekst = fritekst;
                return this;
            }

            public Builder skjæringstidspunkt(Skjæringstidspunkt skjæringstidspunkt) {
                this.skjæringstidspunkt = skjæringstidspunkt;
                return this;
            }

            public Builder endretDekningsgrad(boolean endretDekningsgrad) {
                this.endretDekningsgrad = endretDekningsgrad;
                return this;
            }

            public Builder opphørsdato(LocalDate opphørsdato) {
                this.opphørsdato = opphørsdato;
                return this;
            }

            public Builder konsekvenserForYtelsen(List<KonsekvensForYtelsen> konsekvenserForYtelsen) {
                this.konsekvenserForYtelsen = konsekvenserForYtelsen;
                return this;
            }

            public Builder vilkårTyper(List<VilkårType> vilkårTyper) {
                this.vilkårTyper = vilkårTyper;
                return this;
            }

            public Behandlingsresultat build() {
                return new Behandlingsresultat(medlemskapOpphørsårsak, medlemskapFom, behandlingResultatType, avslagsårsak, fritekst,
                    skjæringstidspunkt, endretDekningsgrad != null && endretDekningsgrad, opphørsdato, konsekvenserForYtelsen, vilkårTyper);
            }
        }

        public record Fritekst(String overskrift, String brødtekst, String avslagsarsakFritekst) {
            public static Builder builder() {
                return new Builder();
            }

            public static final class Builder {
                private String overskrift;
                private String brødtekst;
                private String avslagsarsakFritekst;

                public Builder overskrift(String v) {
                    this.overskrift = v;
                    return this;
                }

                public Builder brødtekst(String v) {
                    this.brødtekst = v;
                    return this;
                }

                public Builder avslagsarsakFritekst(String v) {
                    this.avslagsarsakFritekst = v;
                    return this;
                }

                public Fritekst build() {
                    return new Fritekst(overskrift, brødtekst, avslagsarsakFritekst);
                }
            }
        }

        public record Skjæringstidspunkt(LocalDate dato, boolean utenMinsterett) {
            public static Builder builder() {
                return new Builder();
            }

            public static final class Builder {
                private LocalDate dato;
                private Boolean utenMinsterett;

                public Builder dato(LocalDate v) {
                    this.dato = v;
                    return this;
                }

                public Builder utenMinsterett(boolean v) {
                    this.utenMinsterett = v;
                    return this;
                }

                public Skjæringstidspunkt build() {
                    return new Skjæringstidspunkt(dato, utenMinsterett != null && utenMinsterett);
                }
            }
        }

        public enum VilkårType {
            FØDSELSVILKÅRET_MOR,
            FØDSELSVILKÅRET_FAR_MEDMOR,
            ADOPSJONSVILKARET_FORELDREPENGER,
            MEDLEMSKAPSVILKÅRET,
            MEDLEMSKAPSVILKÅRET_FORUTGÅENDE,
            MEDLEMSKAPSVILKÅRET_LØPENDE,
            SØKNADSFRISTVILKÅRET,
            ADOPSJONSVILKÅRET_ENGANGSSTØNAD,
            OMSORGSVILKÅRET,
            FORELDREANSVARSVILKÅRET_2_LEDD,
            FORELDREANSVARSVILKÅRET_4_LEDD,
            SØKERSOPPLYSNINGSPLIKT,
            OPPTJENINGSPERIODEVILKÅR,
            OPPTJENINGSVILKÅRET,
            BEREGNINGSGRUNNLAGVILKÅR,
            SVANGERSKAPSPENGERVILKÅR,
            UDEFINERT
        }

        public enum KonsekvensForYtelsen {
            FORELDREPENGER_OPPHØRER,
            ENDRING_I_BEREGNING,
            ENDRING_I_UTTAK,
            ENDRING_I_FORDELING_AV_YTELSEN,
            INGEN_ENDRING,
            UDEFINERT
        }

        public enum BehandlingResultatType {
            IKKE_FASTSATT,
            INNVILGET,
            AVSLÅTT,
            OPPHØR,
            HENLAGT_SØKNAD_TRUKKET,
            HENLAGT_FEILOPPRETTET,
            HENLAGT_BRUKER_DØD,
            MERGET_OG_HENLAGT,
            HENLAGT_SØKNAD_MANGLER,
            FORELDREPENGER_ENDRET,
            FORELDREPENGER_SENERE,
            INGEN_ENDRING,
            MANGLER_BEREGNINGSREGLER,
            KLAGE_AVVIST,
            KLAGE_MEDHOLD,
            KLAGE_DELVIS_MEDHOLD,
            KLAGE_OMGJORT_UGUNST,
            KLAGE_YTELSESVEDTAK_OPPHEVET,
            KLAGE_YTELSESVEDTAK_STADFESTET,
            KLAGE_TILBAKEKREVING_VEDTAK_STADFESTET,
            HENLAGT_KLAGE_TRUKKET,
            HJEMSENDE_UTEN_OPPHEVE,
            ANKE_AVVIST,
            ANKE_MEDHOLD,
            ANKE_DELVIS_MEDHOLD,
            ANKE_OMGJORT_UGUNST,
            ANKE_OPPHEVE_OG_HJEMSENDE,
            ANKE_HJEMSENDE_UTEN_OPPHEV,
            ANKE_YTELSESVEDTAK_STADFESTET,
            HENLAGT_ANKE_TRUKKET,
            INNSYN_INNVILGET,
            INNSYN_DELVIS_INNVILGET,
            INNSYN_AVVIST,
            HENLAGT_INNSYN_TRUKKET
        }
    }
}
