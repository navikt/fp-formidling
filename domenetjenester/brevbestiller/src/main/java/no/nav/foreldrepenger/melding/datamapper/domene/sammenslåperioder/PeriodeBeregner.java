package no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder;

import static no.nav.foreldrepenger.melding.uttak.kodeliste.PeriodeResultatÅrsak.ARBEIDER_I_UTTAKSPERIODEN_MER_ENN_0_PROSENT;
import static no.nav.foreldrepenger.melding.uttak.kodeliste.PeriodeResultatÅrsak.AVSLAG_GRADERING_PÅ_GRUNN_AV_FOR_SEN_SØKNAD;
import static no.nav.foreldrepenger.melding.uttak.kodeliste.PeriodeResultatÅrsak.FOR_SEN_SØKNAD;
import static no.nav.foreldrepenger.melding.uttak.kodeliste.PeriodeResultatÅrsak.UTSETTELSE_GYLDIG_PGA_100_PROSENT_ARBEID;
import static no.nav.foreldrepenger.melding.uttak.kodeliste.PeriodeResultatÅrsak.UTSETTELSE_GYLDIG_PGA_ARBEID_KUN_FAR_HAR_RETT;
import static no.nav.foreldrepenger.melding.uttak.kodeliste.PeriodeResultatÅrsak.UTSETTELSE_GYLDIG_PGA_FERIE;
import static no.nav.foreldrepenger.melding.uttak.kodeliste.PeriodeResultatÅrsak.UTSETTELSE_GYLDIG_PGA_FERIE_KUN_FAR_HAR_RETT;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
import no.nav.foreldrepenger.melding.datamapper.DokumentBestillerFeil;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.PeriodeListeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.PeriodeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.UtbetaltKode;
import no.nav.foreldrepenger.melding.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.melding.uttak.Stønadskonto;
import no.nav.foreldrepenger.melding.uttak.StønadskontoType;
import no.nav.foreldrepenger.melding.uttak.UttakArbeidType;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatPeriode;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;
import no.nav.vedtak.feil.FeilFactory;

public class PeriodeBeregner {
    private static List<String> manglendeEllerForSenSøknadOmGraderingÅrsaker = List.of(
            ARBEIDER_I_UTTAKSPERIODEN_MER_ENN_0_PROSENT.getKode(),
            AVSLAG_GRADERING_PÅ_GRUNN_AV_FOR_SEN_SØKNAD.getKode(),
            FOR_SEN_SØKNAD.getKode());

    private static List<String> innvilgetUtsettelsePgaFerieÅrsaker = List.of(
            UTSETTELSE_GYLDIG_PGA_FERIE.getKode(),
            UTSETTELSE_GYLDIG_PGA_FERIE_KUN_FAR_HAR_RETT.getKode());

    private static List<String> innvilgetUtsettelsePgaArbeidÅrsaker = List.of(
            UTSETTELSE_GYLDIG_PGA_100_PROSENT_ARBEID.getKode(),
            UTSETTELSE_GYLDIG_PGA_ARBEID_KUN_FAR_HAR_RETT.getKode());

    private static Map<AktivitetStatus, UttakArbeidType> uttakAktivitetStatusMap = new HashMap<>();

    static {
        uttakAktivitetStatusMap.put(AktivitetStatus.ARBEIDSTAKER, UttakArbeidType.ORDINÆRT_ARBEID);
        uttakAktivitetStatusMap.put(AktivitetStatus.FRILANSER, UttakArbeidType.FRILANS);
        uttakAktivitetStatusMap.put(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE, UttakArbeidType.SELVSTENDIG_NÆRINGSDRIVENDE);
    }

    private PeriodeBeregner() {
        //for sonar
    }

    public static BeregningsgrunnlagPeriode finnBeregninsgrunnlagperiode(BeregningsresultatPeriode periode,
                                                                         List<BeregningsgrunnlagPeriode> beregningsgrunnlagPerioder) {
        for (BeregningsgrunnlagPeriode beregningsgrunnlagPeriode : beregningsgrunnlagPerioder) {
            if (!periode.getBeregningsresultatPeriodeFom().isBefore(beregningsgrunnlagPeriode.getBeregningsgrunnlagPeriodeFom()) &&
                    (beregningsgrunnlagPeriode.getBeregningsgrunnlagPeriodeTom() == null || (!periode.getBeregningsresultatPeriodeTom().isAfter(beregningsgrunnlagPeriode.getBeregningsgrunnlagPeriodeTom())))) {
                return beregningsgrunnlagPeriode;
            }
        }
        throw FeilFactory.create(DokumentBestillerFeil.class).kanIkkeMatchePerioder("beregningsgrunnlagperiode").toException();
    }

    public static UttakResultatPeriode finnUttaksPeriode(BeregningsresultatPeriode periode, List<UttakResultatPeriode> uttakPerioder) {
        for (UttakResultatPeriode uttakPeriode : uttakPerioder) {
            if (!periode.getBeregningsresultatPeriodeFom().isBefore(uttakPeriode.getFom()) && !periode.getBeregningsresultatPeriodeTom().isAfter(uttakPeriode.getTom())) {
                return uttakPeriode;
            }
        }
        throw FeilFactory.create(DokumentBestillerFeil.class).kanIkkeMatchePerioder("uttaksperiode").toException();
    }

    public static List<SvpUttakResultatPeriode> finnUttakPeriodeKandidater(BeregningsresultatPeriode periode, List<SvpUttakResultatPeriode> uttakPerioder) {
        if (periode.getDagsats() > 0) {
            List<SvpUttakResultatPeriode> kandidater = new ArrayList<>();
            for (SvpUttakResultatPeriode uttakPeriode : uttakPerioder) {
                if (!periode.getBeregningsresultatPeriodeFom().isBefore(uttakPeriode.getFom()) && !periode.getBeregningsresultatPeriodeTom().isAfter(uttakPeriode.getTom())) {
                    kandidater.add(uttakPeriode);
                }
            }
            if (!kandidater.isEmpty()) {
                return kandidater.stream().filter(SvpUttakResultatPeriode::isInnvilget).collect(Collectors.toList());
            }
            throw FeilFactory.create(DokumentBestillerFeil.class).kanIkkeMatchePerioder("uttaksperiode").toException();
        }
        return Collections.emptyList();
    }

    //TODO - Skriv tester.. Dette oppfører seg annerledes i DTOene enn fpsak
    public static Optional<BeregningsgrunnlagPrStatusOgAndel> finnBgPerStatusOgAndelHvisFinnes(List<BeregningsgrunnlagPrStatusOgAndel> bgPerStatusOgAndelListe,
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

    private static boolean sammeArbeidsforhold(Arbeidsgiver arbeidsgiver, ArbeidsforholdRef arbeidsforholdRef, BeregningsgrunnlagPrStatusOgAndel bgPerStatusOgAndel) {
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
            if (uttakAktivitetStatusMap.getOrDefault(andel.getAktivitetStatus(), UttakArbeidType.ANNET).equals(aktivitet.getUttakArbeidType())) {
                if (arbeidsgiver.isEmpty() || Objects.equals(arbeidsgiver.get().getIdentifikator(), aktivitet.getArbeidsgiverIdentifikator())) {
                    if (arbeidsforholdRef == null || arbeidsforholdRef.getReferanse() == null || (arbeidsforholdRef.gjelderForSpesifiktArbeidsforhold() && arbeidsforholdRef.getReferanse().equals(aktivitet.getArbeidsforholdId()))) {
                        return Optional.of(aktivitet);
                    }
                }
            }
        }
        return Optional.empty();
    }

    public static Optional<Stønadskonto> finnStønadsKontoMedType(Set<Stønadskonto> stønadskontoer, StønadskontoType stønadskontoType) {
        return stønadskontoer.stream().
                filter(stønadskonto -> stønadskontoType.equals(stønadskonto.getStønadskontoType()))
                .findFirst();
    }

    public static UtbetaltKode forMyeUtbetalt(PeriodeListeType periodeListe, LocalDate vedtaksdato) {
        LocalDate innvilgetUtsettelseFOM = null;

        boolean generell = false;
        boolean ferie = false;
        boolean jobb = false;

        for (PeriodeType periode : periodeListe.getPeriode()) {
            if (PeriodeVerktøy.periodeHarGradering(periode) || manglendeEllerForSenSøknadOmGraderingÅrsaker.contains(periode.getÅrsak())) {
                generell = true;
                break;
            }
            if (innvilgetUtsettelsePgaFerieÅrsaker.contains(periode.getÅrsak())) {
                ferie = true;
                innvilgetUtsettelseFOM = tidligsteAv(innvilgetUtsettelseFOM, PeriodeVerktøy.xmlGregorianTilLocalDate(periode.getPeriodeFom()));
            }
            if (innvilgetUtsettelsePgaArbeidÅrsaker.contains(periode.getÅrsak())) {
                jobb = true;
                innvilgetUtsettelseFOM = tidligsteAv(innvilgetUtsettelseFOM, PeriodeVerktøy.xmlGregorianTilLocalDate(periode.getPeriodeFom()));
            }
        }
        return forMyeUtbetaltKode(generell, ferie, jobb, innvilgetUtsettelseFOM, vedtaksdato);
    }

    private static UtbetaltKode forMyeUtbetaltKode(boolean generell, boolean ferie, boolean jobb,
                                                   LocalDate innvilgetUtsettelseFOM, LocalDate vedtaksdato) {
        if (generell) {
            return UtbetaltKode.GENERELL;
        }
        if ((ferie || jobb) && erInnvilgetUtsettelseInneværendeMånedEllerTidligere(innvilgetUtsettelseFOM, vedtaksdato)) {
            return ferie && jobb ? UtbetaltKode.GENERELL
                    : ferie ? UtbetaltKode.FERIE
                    : UtbetaltKode.JOBB;
        }
        return UtbetaltKode.INGEN;
    }

    private static boolean erInnvilgetUtsettelseInneværendeMånedEllerTidligere(LocalDate innvilgetUtsettelseFOM, LocalDate vedtaksdato) {
        LocalDate iDag = vedtaksdato != null ? vedtaksdato : LocalDate.now();
        return innvilgetUtsettelseFOM.isBefore(iDag.plusMonths(1).withDayOfMonth(1));
    }

    private static LocalDate tidligsteAv(LocalDate innvilgetUtsettelseFOM, LocalDate periodeFOM) {
        if (innvilgetUtsettelseFOM == null || periodeFOM.isBefore(innvilgetUtsettelseFOM)) {
            return periodeFOM;
        }
        return innvilgetUtsettelseFOM;
    }
}
