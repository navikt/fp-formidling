package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagDto;
import no.nav.foreldrepenger.kontrakter.fpsak.inntektsmeldinger.ArbeidsforholdInntektsmeldingerDto;
import no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseDagytelseDto;
import no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseEngangsstønadDto;

public record BrevGrunnlagDto(UUID uuid, String saksnummer, FagsakYtelseType fagsakYtelseType, RelasjonsRolleType relasjonsRolleType, String aktørId,
                              BehandlingType behandlingType, LocalDateTime opprettet, LocalDateTime avsluttet, String behandlendeEnhet,
                              Språkkode språkkode, boolean automatiskBehandlet, FamilieHendelse familieHendelse,
                              OriginalBehandling originalBehandling, Behandlingsresultat behandlingsresultat,
                              List<BehandlingÅrsakType> behandlingÅrsakTyper, TilkjentYtelse tilkjentYtelse, BeregningsgrunnlagDto beregningsgrunnlag,
                              ArbeidsforholdInntektsmeldingerDto inntektsmeldingerStatus, LocalDate førsteSøknadMottattDato,
                              //Ser på mottatt dato på dokument
                              LocalDate sisteSøknadMottattDato, //Ser på mottatt dato på dokument
                              LocalDate søknadMottattDato, //Uttaksperiodegrense (når den anses å være mottatt)
                              List<Inntektsmelding> inntektsmeldinger, Verge verge, KlageBehandling klageBehandling,
                              InnsynBehandling innsynBehandling, Svangerskapspenger svangerskapspenger, Foreldrepenger foreldrepenger) {
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

    public enum FagsakYtelseType {
        ENGANGSTØNAD,
        FORELDREPENGER,
        SVANGERSKAPSPENGER,
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

    public record OriginalBehandling(FamilieHendelse familieHendelse, Behandlingsresultat.BehandlingResultatType behandlingResultatType,
                                     LocalDate førsteDagMedUtbetaltForeldrepenger) {
    }

    public record TilkjentYtelse(TilkjentYtelseEngangsstønadDto engangsstønad, TilkjentYtelseEngangsstønadDto originalBehandlingEngangsstønad,
                                 TilkjentYtelseDagytelseDto dagytelse) {
    }

    public record Foreldrepenger(Dekningsgrad dekningsgrad, Rettigheter rettigheter, List<Stønadskonto> stønadskontoer, int tapteDagerFpff,
                                 List<Uttaksperiode> perioderSøker, List<Uttaksperiode> perioderAnnenpart, boolean ønskerJustertUttakVedFødsel,
                                 LocalDate nyStartDatoVedUtsattOppstart) {
        public record Uttaksperiode(LocalDate fom, LocalDate tom, List<Aktivitet> aktiviteter, PeriodeResultatType periodeResultatType,
                                    String periodeResultatÅrsak, String graderingAvslagÅrsak, LocalDate tidligstMottattDato,
                                    boolean erUtbetalingRedusertTilMorsStillingsprosent, Set<String> lovhjemler) {
            public boolean isInnvilget() {
                return periodeResultatType == PeriodeResultatType.INNVILGET;
            }
        }

        public record Aktivitet(TrekkontoType trekkontoType, BigDecimal trekkdager, BigDecimal prosentArbeid, String arbeidsgiverReferanse,
                                String arbeidsforholdId, BigDecimal utbetalingsgrad, UttakArbeidType uttakArbeidType, boolean gradering) {
        }

        public record Stønadskonto(Type stønadskontotype, int maxDager, int saldo, KontoUtvidelser kontoUtvidelser) {
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

    public record Svangerskapspenger(List<UttakArbeidsforhold> uttakArbeidsforhold) {
        public record UttakArbeidsforhold(String arbeidsforholdIkkeOppfyltÅrsak, String arbeidsgiverReferanse, UttakArbeidType arbeidType,
                                          List<Uttaksperiode> perioder) {
        }

        public record Uttaksperiode(LocalDate fom, LocalDate tom, PeriodeResultatType periodeResultatType, String periodeIkkeOppfyltÅrsak) {
        }
    }

    public enum PeriodeResultatType {
        INNVILGET,
        AVSLÅTT,
        MANUELL_BEHANDLING
    }

    public record Inntektsmelding(String arbeidsgiverReferanse, LocalDateTime innsendingstidspunkt) {
    }

    public record Rettigheter(Rettighetstype opprinnelig, Rettighetstype gjeldende, EøsUttak eøsUttak) {
        public record EøsUttak(LocalDate fom, LocalDate tom, int forbruktFellesperiode, int fellesperiodeINorge) {
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
    }

    public record Barn(LocalDate fødselsdato, LocalDate dødsdato) {
    }

    public record Verge(String aktørId, String navn, String organisasjonsnummer, LocalDate gyldigFom, LocalDate gyldigTom) {

    }

    public record InnsynBehandling(InnsynResultatType innsynResultatType, List<InnsynDokument> dokumenter) {
        public enum InnsynResultatType {
            INNVILGET,
            DELVIS_INNVILGET,
            AVVIST,
            UDEFINERT
        }

        public record InnsynDokument(String journalpostId, String dokumentId) {
        }
    }

    public record KlageBehandling(KlageFormkravResultat klageFormkravResultatNFP, KlageVurderingResultat klageVurderingResultatNFP,
                                  KlageFormkravResultat klageFormkravResultatKA, KlageVurderingResultat klageVurderingResultatNK,
                                  LocalDate mottattDato) {
        public record KlageFormkravResultat(BehandlingType påklagdBehandlingType, List<KlageAvvistÅrsak> avvistÅrsaker) {
        }

        public record KlageVurderingResultat(String fritekstTilBrev) {
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
    }

    public record Behandlingsresultat(String medlemskapOpphørsårsak, LocalDate medlemskapFom, BehandlingResultatType behandlingResultatType,
                                      String avslagsårsak, Fritekst fritekst, Skjæringstidspunkt skjæringstidspunkt, boolean endretDekningsgrad,
                                      LocalDate opphørsdato, List<KonsekvensForYtelsen> konsekvenserForYtelsen, List<VilkårType> vilkårTyper) {
        public record Fritekst(String overskrift, String brødtekst, String avslagsarsakFritekst) {
        }

        public record Skjæringstidspunkt(LocalDate dato, boolean utenMinsterett) {
        }

        public enum VilkårType {
            FØDSELSVILKÅRET_MOR,
            FØDSELSVILKÅRET_FAR_MEDMOR,
            MEDLEMSKAPSVILKÅRET,
            MEDLEMSKAPSVILKÅRET_FORUTGÅENDE,
            MEDLEMSKAPSVILKÅRET_LØPENDE,
            SØKNADSFRISTVILKÅRET,
            SØKERSOPPLYSNINGSPLIKT,
            OPPTJENINGSPERIODEVILKÅR,
            OPPTJENINGSVILKÅRET,
            BEREGNINGSGRUNNLAGVILKÅR,
            SVANGERSKAPSPENGERVILKÅR,
            OMSORGSOVERTAKELSEVILKÅR,
            UDEFINERT
        }

        public enum KonsekvensForYtelsen {
            FORELDREPENGER_OPPHØRER,
            ENDRING_I_BEREGNING,
            ENDRING_I_UTTAK,
            ENDRING_I_FORDELING_AV_YTELSEN,
            INGEN_ENDRING,
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
