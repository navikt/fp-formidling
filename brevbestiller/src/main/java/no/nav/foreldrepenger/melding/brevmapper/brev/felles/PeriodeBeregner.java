package no.nav.foreldrepenger.melding.brevmapper.brev.felles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.melding.beregning.BeregningsresultatAndel;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndel;
import no.nav.foreldrepenger.melding.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.melding.uttak.Stønadskonto;
import no.nav.foreldrepenger.melding.uttak.StønadskontoType;
import no.nav.foreldrepenger.melding.uttak.UttakArbeidType;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatPeriode;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;
import no.nav.vedtak.exception.TekniskException;

public class PeriodeBeregner {

    private static final String FEILKODE = "FPFORMIDLING-368280";
    private static final String FEILMELDING = "Klarte ikke matche beregningsresultatperiode og %S for brev";
    private static Map<AktivitetStatus, UttakArbeidType> uttakAktivitetStatusMap = new EnumMap<>(AktivitetStatus.class);

    static {
        uttakAktivitetStatusMap.put(AktivitetStatus.ARBEIDSTAKER, UttakArbeidType.ORDINÆRT_ARBEID);
        uttakAktivitetStatusMap.put(AktivitetStatus.FRILANSER, UttakArbeidType.FRILANS);
        uttakAktivitetStatusMap.put(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE, UttakArbeidType.SELVSTENDIG_NÆRINGSDRIVENDE);
    }

    private PeriodeBeregner() {
        // for sonar
    }

    public static BeregningsgrunnlagPeriode finnBeregningsgrunnlagperiode(BeregningsresultatPeriode periode,
                                                                          List<BeregningsgrunnlagPeriode> beregningsgrunnlagPerioder) {
        for (BeregningsgrunnlagPeriode beregningsgrunnlagPeriode : beregningsgrunnlagPerioder) {
            if (!periode.getBeregningsresultatPeriodeFom().isBefore(beregningsgrunnlagPeriode.getBeregningsgrunnlagPeriodeFom()) &&
                    (beregningsgrunnlagPeriode.getBeregningsgrunnlagPeriodeTom() == null
                            || (!periode.getBeregningsresultatPeriodeTom().isAfter(beregningsgrunnlagPeriode.getBeregningsgrunnlagPeriodeTom())))) {
                return beregningsgrunnlagPeriode;
            }
        }
        throw new TekniskException(FEILKODE, String.format(FEILMELDING, "beregningsgrunnlagperiode"));
    }

    public static UttakResultatPeriode finnUttaksperiode(BeregningsresultatPeriode periode, List<UttakResultatPeriode> uttakPerioder) {
        for (UttakResultatPeriode uttakPeriode : uttakPerioder) {
            if (!periode.getBeregningsresultatPeriodeFom().isBefore(uttakPeriode.getFom())
                    && !periode.getBeregningsresultatPeriodeTom().isAfter(uttakPeriode.getTom())) {
                return uttakPeriode;
            }
        }
        throw new TekniskException(FEILKODE, String.format(FEILMELDING, "uttaksperiode"));
    }

    public static List<SvpUttakResultatPeriode> finnUttakPeriodeKandidater(BeregningsresultatPeriode periode,
            List<SvpUttakResultatPeriode> uttakPerioder) {
        if (periode.getDagsats() > 0) {
            List<SvpUttakResultatPeriode> kandidater = new ArrayList<>();
            for (SvpUttakResultatPeriode uttakPeriode : uttakPerioder) {
                if (!periode.getBeregningsresultatPeriodeFom().isBefore(uttakPeriode.getFom())
                        && !periode.getBeregningsresultatPeriodeTom().isAfter(uttakPeriode.getTom())) {
                    kandidater.add(uttakPeriode);
                }
            }
            if (!kandidater.isEmpty()) {
                return kandidater.stream().filter(SvpUttakResultatPeriode::isInnvilget).collect(Collectors.toList());
            }
            throw new TekniskException(FEILKODE, String.format(FEILMELDING, "uttaksperiode"));
        }
        return Collections.emptyList();
    }

    // TODO - Skriv tester.. Dette oppfører seg annerledes i DTOene enn fpsak
    public static Optional<BeregningsgrunnlagPrStatusOgAndel> finnBgPerStatusOgAndelHvisFinnes(
            List<BeregningsgrunnlagPrStatusOgAndel> bgPerStatusOgAndelListe,
            BeregningsresultatAndel andel) {
        for (BeregningsgrunnlagPrStatusOgAndel bgPerStatusOgAndel : bgPerStatusOgAndelListe) {
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

    private static boolean sammeArbeidsforhold(BeregningsresultatAndel andel, BeregningsgrunnlagPrStatusOgAndel bgPerStatusOgAndel) {
        Optional<Arbeidsgiver> arbeidsgiver = andel.getArbeidsgiver();
        if (arbeidsgiver.isEmpty()) {
            return false;
        }
        ArbeidsforholdRef arbeidsforholdRef = andel.getArbeidsforholdRef();
        return sammeArbeidsforhold(arbeidsgiver.get(), arbeidsforholdRef, bgPerStatusOgAndel);
    }

    private static boolean sammeArbeidsforhold(Arbeidsgiver arbeidsgiver, ArbeidsforholdRef arbeidsforholdRef,
            BeregningsgrunnlagPrStatusOgAndel bgPerStatusOgAndel) {
        return bgPerStatusOgAndel.gjelderSammeArbeidsforhold(arbeidsgiver, arbeidsforholdRef);
    }

    public static boolean alleAktiviteterHarNullUtbetaling(List<UttakResultatPeriodeAktivitet> uttakAktiviteter) {
        return uttakAktiviteter.stream().allMatch(aktivitet -> aktivitet.getUtbetalingsprosent().compareTo(BigDecimal.ZERO) == 0);
    }

    public static Optional<UttakResultatPeriodeAktivitet> finnAktivitetMedStatusHvisFinnes(List<UttakResultatPeriodeAktivitet> uttakAktiviteter,
            BeregningsresultatAndel andel) {
        Optional<Arbeidsgiver> arbeidsgiver = andel.getArbeidsgiver();
        ArbeidsforholdRef arbeidsforholdRef = andel.getArbeidsforholdRef();

        for (UttakResultatPeriodeAktivitet aktivitet : uttakAktiviteter) {
            if (uttakAktivitetStatusMap.getOrDefault(andel.getAktivitetStatus(), UttakArbeidType.ANNET).equals(aktivitet.getUttakArbeidType())
                    && (arbeidsgiver.isEmpty()
                            || Objects.equals(arbeidsgiver.get().arbeidsgiverReferanse(), aktivitet.getArbeidsgiverIdentifikator()))
                    && (arbeidsforholdRef == null || arbeidsforholdRef.getReferanse() == null
                            || (arbeidsforholdRef.gjelderForSpesifiktArbeidsforhold()
                                    && arbeidsforholdRef.getReferanse().equals(aktivitet.getArbeidsforholdId())))) {
                return Optional.of(aktivitet);
            }
        }
        return Optional.empty();
    }

    public static Optional<Stønadskonto> finnStønadsKontoMedType(Set<Stønadskonto> stønadskontoer, StønadskontoType stønadskontoType) {
        return stønadskontoer.stream().filter(stønadskonto -> stønadskontoType.equals(stønadskonto.stønadskontoType()))
                .findFirst();
    }
}
