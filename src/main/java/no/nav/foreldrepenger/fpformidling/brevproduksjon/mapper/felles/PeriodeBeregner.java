package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.Foreldrepenger;
import static no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseDagytelseDto.TilkjentYtelsePeriodeDto;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagAndelDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BgAndelArbeidsforholdDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.AktivitetStatusDto;
import no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseDagytelseDto;
import no.nav.vedtak.exception.TekniskException;

public class PeriodeBeregner {

    private static final String FEILKODE = "FPFORMIDLING-368280";
    private static final String FEILMELDING = "Klarte ikke matche tilkjentYtelsePeriode og %S for brev";
    private static final Map<TilkjentYtelseDagytelseDto.Aktivitetstatus, BrevGrunnlagDto.UttakArbeidType> uttakAktivitetStatusMap = new EnumMap<>(
        TilkjentYtelseDagytelseDto.Aktivitetstatus.class);

    static {
        uttakAktivitetStatusMap.put(TilkjentYtelseDagytelseDto.Aktivitetstatus.ARBEIDSTAKER, BrevGrunnlagDto.UttakArbeidType.ORDINÆRT_ARBEID);
        uttakAktivitetStatusMap.put(TilkjentYtelseDagytelseDto.Aktivitetstatus.FRILANSER, BrevGrunnlagDto.UttakArbeidType.FRILANS);
        uttakAktivitetStatusMap.put(TilkjentYtelseDagytelseDto.Aktivitetstatus.SELVSTENDIG_NÆRINGSDRIVENDE,
            BrevGrunnlagDto.UttakArbeidType.SELVSTENDIG_NÆRINGSDRIVENDE);
    }

    private PeriodeBeregner() {
        // for sonar
    }

    public static BeregningsgrunnlagPeriodeDto finnBeregningsgrunnlagperiode(TilkjentYtelsePeriodeDto tilkjentPeriode,
                                                                             List<BeregningsgrunnlagPeriodeDto> beregningsgrunnlagPerioder) {
        for (var beregningsgrunnlagPeriode : beregningsgrunnlagPerioder) {
            if (!tilkjentPeriode.fom().isBefore(beregningsgrunnlagPeriode.beregningsgrunnlagperiodeFom()) && (
                beregningsgrunnlagPeriode.beregningsgrunnlagperiodeTom() == null || (!tilkjentPeriode.tom()
                    .isAfter(beregningsgrunnlagPeriode.beregningsgrunnlagperiodeTom())))) {
                return beregningsgrunnlagPeriode;
            }
        }
        throw new TekniskException(FEILKODE, String.format(FEILMELDING, "beregningsgrunnlagperiode"));
    }

    public static Foreldrepenger.Uttaksperiode finnUttaksperiode(TilkjentYtelsePeriodeDto tilkjentPeriode,
                                                                 List<Foreldrepenger.Uttaksperiode> uttakPerioder) {
        for (var uttakPeriode : uttakPerioder) {
            if (!tilkjentPeriode.fom().isBefore(uttakPeriode.fom()) && !tilkjentPeriode.tom().isAfter(uttakPeriode.tom())) {
                return uttakPeriode;
            }
        }
        throw new TekniskException(FEILKODE, String.format(FEILMELDING, "uttaksperiode"));
    }

    public static int finnAntallTilkjentePerioderForUttaksperioden(List<TilkjentYtelsePeriodeDto> tilkjentPeriodeListe,
                                                                   Foreldrepenger.Uttaksperiode uttakPeriode) {
        return (int) tilkjentPeriodeListe.stream()
            .filter(tp -> uttakPeriode.fom().isEqual(tp.fom()) && uttakPeriode.tom().isAfter(tp.tom())
                || uttakPeriode.tom().isEqual(tp.tom()) && uttakPeriode.fom().isBefore(tp.fom()))
            .count();
    }

    public static Optional<BeregningsgrunnlagAndelDto> finnBgPerStatusOgAndelHvisFinnes(List<BeregningsgrunnlagAndelDto> andeler,
                                                                                        TilkjentYtelseDagytelseDto.TilkjentYtelseAndelDto andel) {
        for (var bgPeradatatusOgAndel : andeler) {
            if (erLikStatus(andel.aktivitetstatus(), bgPeradatatusOgAndel.aktivitetStatus())) {
                if (TilkjentYtelseDagytelseDto.Aktivitetstatus.ARBEIDSTAKER.equals(andel.aktivitetstatus())) {
                    if (sammeArbeidsforhold(andel, bgPeradatatusOgAndel)) {
                        return Optional.of(bgPeradatatusOgAndel);
                    }
                } else {
                    return Optional.of(bgPeradatatusOgAndel);
                }
            }
        }

        return Optional.empty();
    }

    private static boolean erLikStatus(TilkjentYtelseDagytelseDto.Aktivitetstatus aktivitetstatus, AktivitetStatusDto aktivitetStatusDto) {
        var mapped = switch (aktivitetStatusDto) {
            case ARBEIDSAVKLARINGSPENGER -> TilkjentYtelseDagytelseDto.Aktivitetstatus.ARBEIDSAVKLARINGSPENGER;
            case ARBEIDSTAKER -> TilkjentYtelseDagytelseDto.Aktivitetstatus.ARBEIDSTAKER;
            case DAGPENGER -> TilkjentYtelseDagytelseDto.Aktivitetstatus.DAGPENGER;
            case FRILANSER -> TilkjentYtelseDagytelseDto.Aktivitetstatus.FRILANSER;
            case MILITÆR_ELLER_SIVIL -> TilkjentYtelseDagytelseDto.Aktivitetstatus.MILITÆR_ELLER_SIVIL;
            case SELVSTENDIG_NÆRINGSDRIVENDE -> TilkjentYtelseDagytelseDto.Aktivitetstatus.SELVSTENDIG_NÆRINGSDRIVENDE;
            case KOMBINERT_AT_FL -> TilkjentYtelseDagytelseDto.Aktivitetstatus.KOMBINERT_AT_FL;
            case KOMBINERT_AT_SN -> TilkjentYtelseDagytelseDto.Aktivitetstatus.KOMBINERT_AT_SN;
            case KOMBINERT_FL_SN -> TilkjentYtelseDagytelseDto.Aktivitetstatus.KOMBINERT_FL_SN;
            case KOMBINERT_AT_FL_SN -> TilkjentYtelseDagytelseDto.Aktivitetstatus.KOMBINERT_AT_FL_SN;
            case BRUKERS_ANDEL -> TilkjentYtelseDagytelseDto.Aktivitetstatus.BRUKERS_ANDEL;
            case KUN_YTELSE -> TilkjentYtelseDagytelseDto.Aktivitetstatus.KUN_YTELSE;
            case TTLSTØTENDE_YTELSE -> TilkjentYtelseDagytelseDto.Aktivitetstatus.TTLSTØTENDE_YTELSE;
            case VENTELØNN_VARTPENGER -> TilkjentYtelseDagytelseDto.Aktivitetstatus.VENTELØNN_VARTPENGER;
        };
        return aktivitetstatus == mapped;
    }

    private static boolean sammeArbeidsforhold(TilkjentYtelseDagytelseDto.TilkjentYtelseAndelDto tilkjentAndel, BeregningsgrunnlagAndelDto bgAndel) {
        var tilkjentArbeidsforholdIdent = tilkjentAndel.arbeidsgiverReferanse();
        var tilkjentArbeidsforholdId = tilkjentAndel.arbeidsforholdId();

        var bgArbeidsforholdIdent = Optional.ofNullable(bgAndel.arbeidsforhold()).map(BgAndelArbeidsforholdDto::arbeidsgiverIdent).orElse(null);
        var bgArbeidsforholdId = Optional.ofNullable(bgAndel.arbeidsforhold()).map(BgAndelArbeidsforholdDto::arbeidsforholdRef).orElse(null);

        return Objects.equals(tilkjentArbeidsforholdIdent, bgArbeidsforholdIdent) && Objects.equals(tilkjentArbeidsforholdId, bgArbeidsforholdId);
    }

    public static boolean alleAktiviteterHarNullUtbetaling(List<Foreldrepenger.Aktivitet> uttakAktiviteter) {
        return uttakAktiviteter.stream().allMatch(aktivitet -> aktivitet.utbetalingsgrad().compareTo(BigDecimal.ZERO) == 0);
    }

    public static Optional<Foreldrepenger.Aktivitet> finnAktivitetMedStatusHvisFinnes(List<Foreldrepenger.Aktivitet> uttakAktiviteter,
                                                                                      TilkjentYtelseDagytelseDto.TilkjentYtelseAndelDto andel) {
        var arbeidsgiver = andel.arbeidsgiverReferanse();
        var arbeidsforholdId = andel.arbeidsforholdId();

        for (var aktivitet : uttakAktiviteter) {
            if (uttakAktivitetStatusMap.getOrDefault(andel.aktivitetstatus(), BrevGrunnlagDto.UttakArbeidType.ANNET).equals(aktivitet.uttakArbeidType())
                && (arbeidsgiver == null || Objects.equals(arbeidsgiver, aktivitet.arbeidsgiverReferanse())) && (arbeidsforholdId == null || Objects.equals(arbeidsforholdId, aktivitet.arbeidsforholdId()))) {
                return Optional.of(aktivitet);
            }
        } return Optional.empty();
    }

    public static Optional<Foreldrepenger.Stønadskonto> finnStønadsKontoMedType(List<Foreldrepenger.Stønadskonto> stønadskontoer,
                                                                                Foreldrepenger.Stønadskonto.Type stønadskontoType) {
        return stønadskontoer.stream().filter(stønadskonto -> stønadskontoType.equals(stønadskonto.stønadskontotype())).findFirst();
    }
}
