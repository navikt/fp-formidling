package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.BeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndel;
import no.nav.foreldrepenger.fpformidling.domene.tilkjentytelse.TilkjentYtelseAndel;
import no.nav.foreldrepenger.fpformidling.domene.tilkjentytelse.TilkjentYtelsePeriode;
import no.nav.foreldrepenger.fpformidling.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.SaldoVisningStønadskontoType;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.Stønadskonto;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.UttakArbeidType;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.UttakResultatPeriode;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.fpformidling.domene.virksomhet.Arbeidsgiver;
import no.nav.vedtak.exception.TekniskException;

public class PeriodeBeregner {

    private static final String FEILKODE = "FPFORMIDLING-368280";
    private static final String FEILMELDING = "Klarte ikke matche tilkjentYtelsePeriode og %S for brev";
    private static Map<AktivitetStatus, UttakArbeidType> uttakAktivitetStatusMap = new EnumMap<>(AktivitetStatus.class);

    static {
        uttakAktivitetStatusMap.put(AktivitetStatus.ARBEIDSTAKER, UttakArbeidType.ORDINÆRT_ARBEID);
        uttakAktivitetStatusMap.put(AktivitetStatus.FRILANSER, UttakArbeidType.FRILANS);
        uttakAktivitetStatusMap.put(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE, UttakArbeidType.SELVSTENDIG_NÆRINGSDRIVENDE);
    }

    private PeriodeBeregner() {
        // for sonar
    }

    public static BeregningsgrunnlagPeriode finnBeregningsgrunnlagperiode(TilkjentYtelsePeriode tilkjentPeriode,
                                                                          List<BeregningsgrunnlagPeriode> beregningsgrunnlagPerioder) {
        for (var beregningsgrunnlagPeriode : beregningsgrunnlagPerioder) {
            if (!tilkjentPeriode.getPeriodeFom().isBefore(beregningsgrunnlagPeriode.getBeregningsgrunnlagPeriodeFom()) && (
                beregningsgrunnlagPeriode.getBeregningsgrunnlagPeriodeTom() == null || (!tilkjentPeriode.getPeriodeTom()
                    .isAfter(beregningsgrunnlagPeriode.getBeregningsgrunnlagPeriodeTom())))) {
                return beregningsgrunnlagPeriode;
            }
        }
        throw new TekniskException(FEILKODE, String.format(FEILMELDING, "beregningsgrunnlagperiode"));
    }

    public static UttakResultatPeriode finnUttaksperiode(TilkjentYtelsePeriode tilkjentPeriode, List<UttakResultatPeriode> uttakPerioder) {
        for (var uttakPeriode : uttakPerioder) {
            if (!tilkjentPeriode.getPeriodeFom().isBefore(uttakPeriode.getFom()) && !tilkjentPeriode.getPeriodeTom().isAfter(uttakPeriode.getTom())) {
                return uttakPeriode;
            }
        }
        throw new TekniskException(FEILKODE, String.format(FEILMELDING, "uttaksperiode"));
    }

    public static int finnAntallTilkjentePerioderForUttaksperioden(List<TilkjentYtelsePeriode> tilkjentPeriodeListe,
                                                                   UttakResultatPeriode uttakPeriode) {
        return (int) tilkjentPeriodeListe.stream()
            .filter(tp -> uttakPeriode.getFom().isEqual(tp.getPeriodeFom()) && uttakPeriode.getTom().isAfter(tp.getPeriodeTom())
                || uttakPeriode.getTom().isEqual(tp.getPeriodeTom()) && uttakPeriode.getFom().isBefore(tp.getPeriodeFom()))
            .count();
    }

    public static Optional<BeregningsgrunnlagPrStatusOgAndel> finnBgPerStatusOgAndelHvisFinnes(List<BeregningsgrunnlagPrStatusOgAndel> bgPerStatusOgAndelListe,
                                                                                               TilkjentYtelseAndel andel) {
        for (var bgPerStatusOgAndel : bgPerStatusOgAndelListe) {
            if (andel.getAktivitetStatus().equals(bgPerStatusOgAndel.getAktivitetStatus())) {
                if (AktivitetStatus.ARBEIDSTAKER.equals(andel.getAktivitetStatus())) {
                    if (sammeArbeidsforhold(andel, bgPerStatusOgAndel)) {
                        return Optional.of(bgPerStatusOgAndel);
                    }
                } else {
                    return Optional.of(bgPerStatusOgAndel);
                }
            }
        }

        return Optional.empty();
    }

    private static boolean sammeArbeidsforhold(TilkjentYtelseAndel andel, BeregningsgrunnlagPrStatusOgAndel bgPerStatusOgAndel) {
        var arbeidsgiver = andel.getArbeidsgiver();
        if (arbeidsgiver.isEmpty()) {
            return false;
        }
        var arbeidsforholdRef = andel.getArbeidsforholdRef();
        return sammeArbeidsforhold(arbeidsgiver.get(), arbeidsforholdRef, bgPerStatusOgAndel);
    }

    private static boolean sammeArbeidsforhold(Arbeidsgiver arbeidsgiver,
                                               ArbeidsforholdRef arbeidsforholdRef,
                                               BeregningsgrunnlagPrStatusOgAndel bgPerStatusOgAndel) {
        return bgPerStatusOgAndel.gjelderSammeArbeidsforhold(arbeidsgiver, arbeidsforholdRef);
    }

    public static boolean alleAktiviteterHarNullUtbetaling(List<UttakResultatPeriodeAktivitet> uttakAktiviteter) {
        return uttakAktiviteter.stream().allMatch(aktivitet -> aktivitet.getUtbetalingsprosent().compareTo(BigDecimal.ZERO) == 0);
    }

    public static Optional<UttakResultatPeriodeAktivitet> finnAktivitetMedStatusHvisFinnes(List<UttakResultatPeriodeAktivitet> uttakAktiviteter,
                                                                                           TilkjentYtelseAndel andel) {
        var arbeidsgiver = andel.getArbeidsgiver();
        var arbeidsforholdRef = andel.getArbeidsforholdRef();

        for (var aktivitet : uttakAktiviteter) {
            if (uttakAktivitetStatusMap.getOrDefault(andel.getAktivitetStatus(), UttakArbeidType.ANNET).equals(aktivitet.getUttakArbeidType()) && (
                arbeidsgiver.isEmpty() || Objects.equals(arbeidsgiver.get().arbeidsgiverReferanse(), aktivitet.getArbeidsgiverIdentifikator())) && (
                arbeidsforholdRef == null || arbeidsforholdRef.getReferanse() == null || (arbeidsforholdRef.gjelderForSpesifiktArbeidsforhold()
                    && arbeidsforholdRef.getReferanse().equals(aktivitet.getArbeidsforholdId())))) {
                return Optional.of(aktivitet);
            }
        }
        return Optional.empty();
    }

    public static Optional<Stønadskonto> finnStønadsKontoMedType(Set<Stønadskonto> stønadskontoer, SaldoVisningStønadskontoType stønadskontoType) {
        return stønadskontoer.stream().filter(stønadskonto -> stønadskontoType.equals(stønadskonto.stønadskontoType())).findFirst();
    }
}
