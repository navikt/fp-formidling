package no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp;

import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.UtbetalingsperiodeMerger.mergePerioder;
import static no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeBeregner.alleAktiviteterHarNullUtbetaling;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDatoNorsk;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import no.nav.foreldrepenger.melding.beregning.BeregningsresultatAndel;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndel;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.PeriodeÅrsak;
import no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeBeregner;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.AnnenAktivitet;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.Arbeidsforhold;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.NaturalytelseEndringType;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.Næring;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.Utbetalingsperiode;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPerioder;
import no.nav.foreldrepenger.melding.uttak.kodeliste.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;

public final class UtbetalingsperiodeMapper {

    public static List<Utbetalingsperiode> mapUtbetalingsperioder(List<BeregningsresultatPeriode> beregningsresultatPerioder,
            UttakResultatPerioder uttakResultatPerioder,
            List<BeregningsgrunnlagPeriode> beregningingsgrunnlagperioder) {
        List<Utbetalingsperiode> periodeliste = new ArrayList<>();
        List<Utbetalingsperiode> periodelisteFørSammenslåing = new ArrayList<>();
        List<UttakResultatPeriode> uttaksperioder = uttakResultatPerioder.getPerioder();
        // er avhengig av at listen er sortert med tidligste periode først
        List<BeregningsresultatPeriode> beregningsresultatperSortert = beregningsresultatPerioder.stream()
                .sorted(Comparator.comparing(BeregningsresultatPeriode::getBeregningsresultatPeriodeFom)).collect(Collectors.toList());

        List<UttakResultatPeriode> uttaksperioderMedÅrsak = new ArrayList<>(filtrerBortUkjentÅrsak(uttaksperioder));

        for (int i = 0; i < beregningsresultatperSortert.size(); i++) {
            var beregningsresultatPeriode = beregningsresultatperSortert.get(i);
            UttakResultatPeriode matchetUttaksperiode = PeriodeBeregner.finnUttaksPeriode(beregningsresultatPeriode, uttaksperioder);
            if (matchetUttaksperiode.getPeriodeResultatÅrsak().erUkjent() || avslåttManglendeSøktUtenTrekkdager(matchetUttaksperiode)) {
                continue;
            }
            uttaksperioderMedÅrsak.remove(matchetUttaksperiode);
            Utbetalingsperiode periodeTilListen;
            BeregningsgrunnlagPeriode beregningsgrunnlagPeriode = PeriodeBeregner.finnBeregninsgrunnlagperiode(beregningsresultatPeriode,
                    beregningingsgrunnlagperioder);

            if (i == 0) {
                periodeTilListen = mapFørstePeriode(beregningsresultatPeriode,
                        matchetUttaksperiode,
                        beregningsgrunnlagPeriode);
            } else {
                periodeTilListen = mapPerioderEtterFørste(beregningsresultatPeriode,
                        matchetUttaksperiode,
                        beregningsgrunnlagPeriode);
            }
            periodelisteFørSammenslåing.add(periodeTilListen);
        }
        periodelisteFørSammenslåing.addAll(mapPerioderUtenBeregningsgrunnlag(uttaksperioderMedÅrsak));
        periodeliste.addAll(mergePerioder(periodelisteFørSammenslåing));
        return periodeliste;
    }

    public static LocalDate finnStønadsperiodeFomHvisFinnes(List<Utbetalingsperiode> periodeListe) {
        return periodeListe.stream()
                .filter(p -> Boolean.TRUE.equals(p.isInnvilget()))
                .map(Utbetalingsperiode::getPeriodeFom)
                .min(Comparator.comparing(LocalDate::toEpochDay)).orElse(null);
    }

    public static LocalDate finnStønadsperiodeTomHvisFinnes(List<Utbetalingsperiode> periodeListe) {
        return periodeListe.stream()
                .filter(p -> Boolean.TRUE.equals(p.isInnvilget()))
                .map(Utbetalingsperiode::getPeriodeTom)
                .max(Comparator.comparing(LocalDate::toEpochDay)).orElse(null);
    }

    public static boolean harInnvilgedePerioder(List<Utbetalingsperiode> utbetalingsperioder) {
        return utbetalingsperioder.stream().anyMatch(Utbetalingsperiode::isInnvilget);
    }

    public static int finnAntallPerioder(List<Utbetalingsperiode> utbetalingsperioder) {
        return utbetalingsperioder.size();
    }

    public static boolean finnesPeriodeMedIkkeOmsorg(List<Utbetalingsperiode> perioder) {
        return perioder.stream()
                .map(Utbetalingsperiode::getÅrsak)
                .anyMatch(årsak -> PeriodeResultatÅrsak.MOR_HAR_IKKE_OMSORG.getKode().equals(årsak)
                        || PeriodeResultatÅrsak.FAR_HAR_IKKE_OMSORG.getKode().equals(årsak));
    }

    private static List<Utbetalingsperiode> mapPerioderUtenBeregningsgrunnlag(List<UttakResultatPeriode> perioderUtenBeregningsgrunnlag) {
        List<Utbetalingsperiode> perioder = new ArrayList<>();
        for (UttakResultatPeriode uttakperiode : perioderUtenBeregningsgrunnlag) {
            if (!avslåttManglendeSøktUtenTrekkdager(uttakperiode)) {
                perioder.add(mapEnkelUttaksperiode(uttakperiode));
            }
        }
        return perioder;
    }

    private static Utbetalingsperiode mapEnkelUttaksperiode(UttakResultatPeriode uttakperiode) {
        var utbetalingsPerioder = Utbetalingsperiode.ny()
                .medAntallTapteDager(((mapAntallTapteDagerFra(uttakperiode.getAktiviteter()))))
                .medInnvilget((uttakperiode.isInnvilget() && !erGraderingAvslått(uttakperiode)))
                .medPeriodeFom(uttakperiode.getFom())
                .medPeriodeTom(uttakperiode.getTom())
                .medÅrsak((uttakperiode.getPeriodeResultatÅrsak().getKode()));
        return utbetalingsPerioder.build();
    }

    private static Utbetalingsperiode mapPerioderEtterFørste(BeregningsresultatPeriode beregningsresultatPeriode,
            UttakResultatPeriode matchetUttaksperiode,
            BeregningsgrunnlagPeriode beregningsgrunnlagPeriode) {
        return mapEnkelPeriode(beregningsresultatPeriode, matchetUttaksperiode, beregningsgrunnlagPeriode,
                beregningsresultatPeriode.getBeregningsresultatPeriodeFom());
    }

    private static List<UttakResultatPeriode> filtrerBortUkjentÅrsak(List<UttakResultatPeriode> uttaksperioder) {
        return uttaksperioder.stream()
                .filter(Predicate.not(up -> up.getPeriodeResultatÅrsak().erUkjent()))
                .collect(Collectors.toList());
    }

    private static boolean avslåttManglendeSøktUtenTrekkdager(UttakResultatPeriode uttakperiode) {
        return avslåttManglendeSøktPeriode(uttakperiode) && ingenTrekkdager(uttakperiode);
    }

    private static boolean avslåttManglendeSøktPeriode(UttakResultatPeriode matchetUttaksperiode) {
        return PeriodeResultatÅrsak.HULL_MELLOM_FORELDRENES_PERIODER.getKode().equals(matchetUttaksperiode.getPeriodeResultatÅrsak().getKode());
    }

    private static boolean ingenTrekkdager(UttakResultatPeriode p) {
        return mapAntallTapteDagerFra(p.getAktiviteter()) == 0;
    }

    private static int mapAntallTapteDagerFra(List<UttakResultatPeriodeAktivitet> uttakAktiviteter) {
        return alleAktiviteterHarNullUtbetaling(uttakAktiviteter) ? uttakAktiviteter.stream()
                .map(UttakResultatPeriodeAktivitet::getTrekkdager)
                .filter(Objects::nonNull)
                .mapToInt(BigDecimal::intValue)
                .max()
                .orElse(0) : 0;
    }

    private static Utbetalingsperiode mapFørstePeriode(BeregningsresultatPeriode beregningsresultatPeriode,
            UttakResultatPeriode uttakResultatPeriode,
            BeregningsgrunnlagPeriode beregningsgrunnlagPeriode) {
        if (uttakResultatPeriode.getFom().isBefore(beregningsresultatPeriode.getBeregningsresultatPeriodeFom())) {
            return mapEnkelPeriode(beregningsresultatPeriode, uttakResultatPeriode, beregningsgrunnlagPeriode, uttakResultatPeriode.getFom());
        }
        return mapEnkelPeriode(beregningsresultatPeriode, uttakResultatPeriode, beregningsgrunnlagPeriode,
                beregningsresultatPeriode.getBeregningsresultatPeriodeFom());
    }

    private static Utbetalingsperiode mapEnkelPeriode(BeregningsresultatPeriode beregningsresultatPeriode,
            UttakResultatPeriode uttakResultatPeriode,
            BeregningsgrunnlagPeriode beregningsgrunnlagPeriode,
            LocalDate fomDate) {
        PeriodeResultatÅrsak periodeResultatÅrsak = utledÅrsakskode(uttakResultatPeriode);

        List<Arbeidsforhold> arbeidsfoholdListe = mapArbeidsforholdliste(beregningsresultatPeriode, uttakResultatPeriode, beregningsgrunnlagPeriode);
        Næring næring = mapNæring(beregningsresultatPeriode, uttakResultatPeriode, beregningsgrunnlagPeriode);
        List<AnnenAktivitet> annenAktivitetListe = mapAnnenAktivtetListe(beregningsresultatPeriode, uttakResultatPeriode);

        var utbetalingsPerioder = Utbetalingsperiode.ny()
                .medAntallTapteDager(mapAntallTapteDagerFra(uttakResultatPeriode.getAktiviteter()))
                .medInnvilget(uttakResultatPeriode.isInnvilget() && !erGraderingAvslått(uttakResultatPeriode))
                .medPeriodeFom(fomDate)
                .medPeriodeTom(beregningsresultatPeriode.getBeregningsresultatPeriodeTom())
                .medÅrsak(periodeResultatÅrsak.getKode())
                .medArbeidsforhold(arbeidsfoholdListe)
                .medNæring(næring)
                .medAnnenAktivitet(annenAktivitetListe)
                .medPrioritertUtbetalingsgrad(finnPrioritertUtbetalingsgrad(arbeidsfoholdListe, næring, annenAktivitetListe));

        if (beregningsresultatPeriode.getDagsats() != null) {
            utbetalingsPerioder.medPeriodeDagsats(beregningsresultatPeriode.getDagsats());
        }

        return utbetalingsPerioder.build();
    }

    private static PeriodeResultatÅrsak utledÅrsakskode(UttakResultatPeriode uttakPeriode) {
        if (erGraderingAvslått(uttakPeriode) && uttakPeriode.isInnvilget()) {
            return uttakPeriode.getGraderingAvslagÅrsak();
        } else if (uttakPeriode.getPeriodeResultatÅrsak() != null) {
            return uttakPeriode.getPeriodeResultatÅrsak();
        }
        return PeriodeResultatÅrsak.UKJENT;
    }

    private static boolean erGraderingAvslått(UttakResultatPeriode uttakPeriode) {
        return !uttakPeriode.erGraderingInnvilget() && !uttakPeriode.getGraderingAvslagÅrsak().erUkjent();
    }

    private static List<AnnenAktivitet> mapAnnenAktivtetListe(BeregningsresultatPeriode beregningsresultatPeriode,
            UttakResultatPeriode uttakPeriode) {
        List<AnnenAktivitet> annenAktivitetListe = new ArrayList<>();
        // - for å reverse rekkefølgen-gradering først i listen:
        Comparator<AnnenAktivitet> annenAktiviteTypeIsGraderingComparator = (a, b) -> - Boolean.compare(a.isGradering(), b.isGradering());
        finnAndelerOgUttakAnnenAktivitet(beregningsresultatPeriode, uttakPeriode)
                .map(UtbetalingsperiodeMapper::mapAnnenAktivitet)
                .sorted(annenAktiviteTypeIsGraderingComparator)
                .forEach(annenAktivitetListe::add);
        return annenAktivitetListe;
    }

    private static AnnenAktivitet mapAnnenAktivitet(BeregningsresOgUttaksAndel tilkjentYtelseAndelMedTilhørendeUttaksaktivitet) {
        var annenAktivitetBuilder = AnnenAktivitet.ny()
                .medAktivitetStatus((tilkjentYtelseAndelMedTilhørendeUttaksaktivitet.andel.getAktivitetStatus().name()));
        tilkjentYtelseAndelMedTilhørendeUttaksaktivitet.UttakAktivitet.ifPresent(
                uttakAktivitet -> {
                    annenAktivitetBuilder.medGradering(uttakAktivitet.getGraderingInnvilget());
                    annenAktivitetBuilder.medUtbetalingsgrad(uttakAktivitet.getUtbetalingsprosent());
                    annenAktivitetBuilder.medProsentArbeid(uttakAktivitet.getArbeidsprosent());
                });
        return annenAktivitetBuilder.build();
    }

    private static Stream<BeregningsresOgUttaksAndel> finnAndelerOgUttakAnnenAktivitet(BeregningsresultatPeriode beregningsresultatPeriode,
            UttakResultatPeriode uttakPeriode) {
        return beregningsresultatPeriode.getBeregningsresultatAndelList().stream()
                .filter(Predicate.not(andel -> AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE.equals(andel.getAktivitetStatus())))
                .filter(Predicate.not(andel -> AktivitetStatus.ARBEIDSTAKER.equals(andel.getAktivitetStatus())))
                .map(andel -> matchBeregningsresultatAndelMedUttaksaktivitet(andel, uttakPeriode));
    }

    private static BeregningsresOgUttaksAndel matchBeregningsresultatAndelMedUttaksaktivitet(BeregningsresultatAndel andel,
            UttakResultatPeriode uttakPeriode) {
        return new BeregningsresOgUttaksAndel(andel, PeriodeBeregner.finnAktivitetMedStatusHvisFinnes(uttakPeriode.getAktiviteter(), andel));
    }

    private static record BeregningsresOgUttaksAndel(BeregningsresultatAndel andel, Optional<UttakResultatPeriodeAktivitet> UttakAktivitet) {
    }

    private static Næring mapNæring(BeregningsresultatPeriode beregningsresultatPeriode, UttakResultatPeriode uttakResultatPeriode,
            BeregningsgrunnlagPeriode beregningsgrunnlagPeriode) {
        return finnNæringsandeler(beregningsresultatPeriode).stream().map(andel -> mapNæringsandel(
                PeriodeBeregner.finnAktivitetMedStatusHvisFinnes(uttakResultatPeriode.getAktiviteter(), andel),
                PeriodeBeregner.finnBgPerStatusOgAndelHvisFinnes(beregningsgrunnlagPeriode.getBeregningsgrunnlagPrStatusOgAndelList(), andel)))
                .findFirst().orElse(null);
    }

    private static Næring mapNæringsandel(Optional<UttakResultatPeriodeAktivitet> uttakAktivitet,
            Optional<BeregningsgrunnlagPrStatusOgAndel> beregningsgrunnlagAndel) {
        var næringBuilder = Næring.ny();

        if (uttakAktivitet.isPresent()) {
            næringBuilder.medUtbetalingsgrad((uttakAktivitet.get().getUtbetalingsprosent()));
            næringBuilder.medProsentArbeid(uttakAktivitet.get().getArbeidsprosent());
            næringBuilder.medGradering(uttakAktivitet.get().getGraderingInnvilget());
        }
        beregningsgrunnlagAndel.ifPresent(bgAndel -> {
            if (bgAndel.getBeregningsperiodeTom() != null) {
                næringBuilder.medSistLignedeÅr(bgAndel.getBeregningsperiodeTom().getYear());
            }
        });
        return næringBuilder.build();
    }

    private static List<BeregningsresultatAndel> finnNæringsandeler(BeregningsresultatPeriode beregningsresultatPeriode) {
        return beregningsresultatPeriode.getBeregningsresultatAndelList().stream()
                .filter(andel -> AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE.equals(andel.getAktivitetStatus()))
                .collect(Collectors.toList());
    }

    private static List<Arbeidsforhold> mapArbeidsforholdliste(BeregningsresultatPeriode beregningsresultatPeriode,
            UttakResultatPeriode uttakResultatPeriode, BeregningsgrunnlagPeriode beregningsgrunnlagPeriode) {
        List<Arbeidsforhold> arbeidsforholdListe = new ArrayList<>();
        for (BeregningsresultatAndel andel : finnArbeidsandeler(beregningsresultatPeriode)) {
            arbeidsforholdListe
                    .add(mapArbeidsforholdAndel(beregningsresultatPeriode, andel,
                            PeriodeBeregner.finnAktivitetMedStatusHvisFinnes(uttakResultatPeriode.getAktiviteter(), andel), PeriodeBeregner
                                    .finnBgPerStatusOgAndelHvisFinnes(beregningsgrunnlagPeriode.getBeregningsgrunnlagPrStatusOgAndelList(), andel),
                            beregningsgrunnlagPeriode));
        }
        // - for reverse order:
        Comparator<Arbeidsforhold> arbeidsforholdTypeIsGraderingComparator = (a, b) -> - Boolean.compare(a.isGradering(), b.isGradering());
        arbeidsforholdListe.sort(arbeidsforholdTypeIsGraderingComparator);
        return arbeidsforholdListe;
    }

    private static List<BeregningsresultatAndel> finnArbeidsandeler(BeregningsresultatPeriode beregningsresultatPeriode) {
        return beregningsresultatPeriode.getBeregningsresultatAndelList().stream()
                .filter(andel -> AktivitetStatus.ARBEIDSTAKER.equals(andel.getAktivitetStatus()))
                .collect(Collectors.toList());
    }

    private static Arbeidsforhold mapArbeidsforholdAndel(BeregningsresultatPeriode beregningsresultatPeriode,
            BeregningsresultatAndel beregningsresultatAndel, Optional<UttakResultatPeriodeAktivitet> uttakAktivitet,
            Optional<BeregningsgrunnlagPrStatusOgAndel> beregningsgrunnlagAndel, BeregningsgrunnlagPeriode beregningsgrunnlagPeriode) {
        var arbeidsforhold = Arbeidsforhold.ny()
                .medArbeidsgiverNavn((beregningsresultatAndel.getArbeidsgiver().map(Arbeidsgiver::navn).orElse("Andel")));
        if (uttakAktivitet.isPresent()) {
            arbeidsforhold.medProsentArbeid(uttakAktivitet.get().getArbeidsprosent());
            arbeidsforhold.medUtbetalingsgrad(uttakAktivitet.get().getUtbetalingsprosent());
            arbeidsforhold.medGradering(uttakAktivitet.get().getGraderingInnvilget());
        }
        arbeidsforhold.medStillingsprosent(beregningsresultatAndel.getStillingsprosent());
        beregningsgrunnlagAndel.ifPresent(bgAndel -> {
            final BeregningsgrunnlagPrStatusOgAndel statusOgAndel = beregningsgrunnlagAndel.get();
            statusOgAndel.getBgAndelArbeidsforhold().ifPresent(bgAndelArbeidsforhold -> {
                if (bgAndelArbeidsforhold.naturalytelseBortfaltPrÅr() != null ||
                        bgAndelArbeidsforhold.naturalytelseTilkommetPrÅr() != null)
                    mapNaturalytelse(arbeidsforhold, beregningsgrunnlagPeriode, beregningsresultatPeriode);
            });
        });
        return arbeidsforhold.build();
    }

    private static void mapNaturalytelse(Arbeidsforhold.Builder arbeidsforholdBuilder, BeregningsgrunnlagPeriode beregningsgrunnlagPeriode,
            BeregningsresultatPeriode beregningsresultatPeriode) {
        for (String årsak : beregningsgrunnlagPeriode.getPeriodeÅrsakKoder()) {
            if (PeriodeÅrsak.NATURALYTELSE_BORTFALT.getKode().equals(årsak)) {
                arbeidsforholdBuilder.medNaturalytelseEndringType(NaturalytelseEndringType.STOPP);
                arbeidsforholdBuilder.medNaturalytelseNyDagsats((beregningsgrunnlagPeriode.getDagsats()));
                arbeidsforholdBuilder.medNaturalytelseEndringDato(formaterDatoNorsk(beregningsresultatPeriode.getBeregningsresultatPeriodeFom()));
            } else if (PeriodeÅrsak.NATURALYTELSE_TILKOMMER.getKode().equals(årsak)) {
                arbeidsforholdBuilder.medNaturalytelseEndringType((NaturalytelseEndringType.START));
                arbeidsforholdBuilder.medNaturalytelseNyDagsats(beregningsgrunnlagPeriode.getDagsats());
                arbeidsforholdBuilder.medNaturalytelseEndringDato(formaterDatoNorsk(beregningsresultatPeriode.getBeregningsresultatPeriodeFom()));
            } else {
                arbeidsforholdBuilder.medNaturalytelseEndringType(NaturalytelseEndringType.INGEN_ENDRING);
            }
        }
    }

    private static BigDecimal finnPrioritertUtbetalingsgrad(List<Arbeidsforhold> arbeidsfoholdListe, Næring næring, List<AnnenAktivitet> annenAktivitetListe) {
        // TODO(JEJ): Denne logikken er midlertidig og skal byttes ut når vi får avklart reglene
        BigDecimal resultat = BigDecimal.ZERO;
        if (arbeidsfoholdListe != null && arbeidsfoholdListe.size() == 1) {
            resultat = arbeidsfoholdListe.get(0).getUtbetalingsgrad() != null ? arbeidsfoholdListe.get(0).getUtbetalingsgrad() : BigDecimal.ZERO;
        } else if (arbeidsfoholdListe != null && arbeidsfoholdListe.size() > 1) {
            Optional<Arbeidsforhold> arbeidsforholdMedGradering = arbeidsfoholdListe.stream().filter(Arbeidsforhold::isGradering).findFirst();
            if (arbeidsforholdMedGradering.isPresent()) {
                resultat = arbeidsforholdMedGradering.get().getUtbetalingsgrad();
            } else {
                resultat = arbeidsfoholdListe.stream().filter(a -> a.getUtbetalingsgrad() != null).findFirst().map(Arbeidsforhold::getUtbetalingsgrad).orElse(BigDecimal.ZERO);
            }
        }

        if (resultat.equals(BigDecimal.ZERO) && næring != null) {
            resultat = næring.getUtbetalingsgrad() != null ? næring.getUtbetalingsgrad() : BigDecimal.ZERO;
        }

        if (resultat.equals(BigDecimal.ZERO) && annenAktivitetListe != null && annenAktivitetListe.size() == 1) {
            resultat = annenAktivitetListe.get(0).getUtbetalingsgrad() != null ? annenAktivitetListe.get(0).getUtbetalingsgrad() : BigDecimal.ZERO;
        } else if (annenAktivitetListe != null && annenAktivitetListe.size() > 1) {
            Optional<AnnenAktivitet> annenAktivitetMedGradering = annenAktivitetListe.stream().filter(AnnenAktivitet::isGradering).findFirst();
            if (annenAktivitetMedGradering.isPresent()) {
                resultat = annenAktivitetMedGradering.get().getUtbetalingsgrad() != null ? annenAktivitetMedGradering.get().getUtbetalingsgrad() : BigDecimal.ZERO;
            } else {
                resultat = annenAktivitetListe.stream().filter(a -> a.getUtbetalingsgrad() != null).findFirst().map(AnnenAktivitet::getUtbetalingsgrad).orElse(BigDecimal.ZERO);
            }
        }

        if (resultat.compareTo(BigDecimal.valueOf(100)) > 0) {
            resultat = BigDecimal.valueOf(100);
        }
        return resultat;
    }
}
