package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.PeriodeBeregner.alleAktiviteterHarNullUtbetaling;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.UtbetalingsperiodeMerger.mergePerioder;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.BeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndel;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.PeriodeÅrsak;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.PeriodeBeregner;
import no.nav.foreldrepenger.fpformidling.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Prosent;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Årsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.AnnenAktivitet;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.Arbeidsforhold;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.NaturalytelseEndringType;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.Næring;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.Utbetalingsperiode;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelseAndel;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelsePeriode;
import no.nav.foreldrepenger.fpformidling.uttak.ForeldrepengerUttak;
import no.nav.foreldrepenger.fpformidling.uttak.StønadskontoType;
import no.nav.foreldrepenger.fpformidling.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.fpformidling.uttak.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.fpformidling.uttak.kodeliste.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.fpformidling.virksomhet.Arbeidsgiver;

public final class UtbetalingsperiodeMapper {

    public static List<Utbetalingsperiode> mapUtbetalingsperioder(List<TilkjentYtelsePeriode> tilkjentYtelsePerioder,
                                                                  ForeldrepengerUttak foreldrepengerUttak,
                                                                  List<BeregningsgrunnlagPeriode> beregningingsgrunnlagPerioder,
                                                                  Språkkode språkkode) {
        List<Utbetalingsperiode> periodelisteFørSammenslåing = new ArrayList<>();
        List<UttakResultatPeriode> uttaksperioder = foreldrepengerUttak.perioder();
        // er avhengig av at listen er sortert med tidligste periode først
        List<TilkjentYtelsePeriode> tilkjentYtelsePerioderSortert = tilkjentYtelsePerioder.stream()
                .sorted(Comparator.comparing(TilkjentYtelsePeriode::getPeriodeFom)).toList();

        List<UttakResultatPeriode> uttaksperioderMedÅrsak = new ArrayList<>(filtrerBortUkjentÅrsak(uttaksperioder));

        for (int i = 0; i < tilkjentYtelsePerioderSortert.size(); i++) {
            var tilkjentYtelsePeriode = tilkjentYtelsePerioderSortert.get(i);
            UttakResultatPeriode matchetUttaksperiode = PeriodeBeregner.finnUttaksperiode(tilkjentYtelsePeriode, uttaksperioder);
            if (matchetUttaksperiode.getPeriodeResultatÅrsak().erUkjent() || avslåttManglendeSøktUtenTrekkdager(matchetUttaksperiode)) {
                continue;
            }
            uttaksperioderMedÅrsak.remove(matchetUttaksperiode);
            Utbetalingsperiode periodeTilListen;
            BeregningsgrunnlagPeriode beregningsgrunnlagPeriode = PeriodeBeregner.finnBeregningsgrunnlagperiode(tilkjentYtelsePeriode,
                    beregningingsgrunnlagPerioder);

            if (i == 0) {
                periodeTilListen = mapFørstePeriode(tilkjentYtelsePeriode,
                        matchetUttaksperiode,
                        beregningsgrunnlagPeriode,
                        språkkode);
            } else {
                periodeTilListen = mapPerioderEtterFørste(tilkjentYtelsePeriode,
                        matchetUttaksperiode,
                        beregningsgrunnlagPeriode,
                        språkkode);
            }
            periodelisteFørSammenslåing.add(periodeTilListen);
        }
        periodelisteFørSammenslåing.addAll(mapPerioderUtenBeregningsgrunnlag(uttaksperioderMedÅrsak, språkkode));
        periodelisteFørSammenslåing.sort(Comparator.comparing(Utbetalingsperiode::getPeriodeFom));
        return new ArrayList<>(mergePerioder(periodelisteFørSammenslåing));
    }

    public static Optional<LocalDate> finnStønadsperiodeFom(List<Utbetalingsperiode> periodeListe) {
        return periodeListe.stream()
                .filter(p -> Boolean.TRUE.equals(p.isInnvilget()))
                .map(Utbetalingsperiode::getPeriodeFom)
                .min(Comparator.comparing(LocalDate::toEpochDay));
    }

    public static Optional<LocalDate> finnStønadsperiodeTom(List<Utbetalingsperiode> periodeListe) {
        return periodeListe.stream()
                .filter(p -> Boolean.TRUE.equals(p.isInnvilget()))
                .map(Utbetalingsperiode::getPeriodeTom)
                .max(Comparator.comparing(LocalDate::toEpochDay));
    }

    public static int finnAntallPerioder(List<Utbetalingsperiode> utbetalingsperioder) {
        return utbetalingsperioder.size();
    }

    public static int finnAntallInnvilgedePerioder(List<Utbetalingsperiode> utbetalingsperioder) {
        return (int) utbetalingsperioder.stream().filter(Utbetalingsperiode::isInnvilget).count();
    }

    public static int finnAntallAvslåttePerioder(List<Utbetalingsperiode> utbetalingsperioder) {
        return (int) utbetalingsperioder.stream().filter(Utbetalingsperiode::isAvslått).count();
    }

    public static boolean finnesPeriodeMedIkkeOmsorg(List<Utbetalingsperiode> perioder) {
        return perioder.stream()
                .map(Utbetalingsperiode::getÅrsak)
                .anyMatch(årsak -> Årsak.of(PeriodeResultatÅrsak.MOR_HAR_IKKE_OMSORG.getKode()).equals(årsak)
                        || Årsak.of(PeriodeResultatÅrsak.FAR_HAR_IKKE_OMSORG.getKode()).equals(årsak));
    }

    private static List<Utbetalingsperiode> mapPerioderUtenBeregningsgrunnlag(List<UttakResultatPeriode> perioderUtenBeregningsgrunnlag,
                                                                              Språkkode språkkode) {
        List<Utbetalingsperiode> perioder = new ArrayList<>();
        for (UttakResultatPeriode uttakperiode : perioderUtenBeregningsgrunnlag) {
            if (!avslåttManglendeSøktUtenTrekkdager(uttakperiode)) {
                perioder.add(mapEnkelUttaksperiode(uttakperiode, språkkode));
            }
        }
        return perioder;
    }

    private static Utbetalingsperiode mapEnkelUttaksperiode(UttakResultatPeriode uttakperiode, Språkkode språkkode) {
        var utbetalingsPerioder = Utbetalingsperiode.ny()
                .medAntallTapteDager(((mapAntallTapteDagerFra(uttakperiode.getAktiviteter()))))
                .medInnvilget((uttakperiode.isInnvilget() && !erGraderingAvslått(uttakperiode)))
                .medPeriodeFom(uttakperiode.getFom(), språkkode)
                .medPeriodeTom(uttakperiode.getTom(), språkkode)
                .medÅrsak(Årsak.of(uttakperiode.getPeriodeResultatÅrsak().getKode()))
                .medStønadskontoType(hentStønadskontoType(uttakperiode));
        return utbetalingsPerioder.build();
    }

    private static Utbetalingsperiode mapPerioderEtterFørste(TilkjentYtelsePeriode tilkjentYtelsePeriode,
                                                             UttakResultatPeriode matchetUttaksperiode,
                                                             BeregningsgrunnlagPeriode beregningsgrunnlagPeriode,
                                                             Språkkode språkkode) {
        return mapEnkelPeriode(tilkjentYtelsePeriode, matchetUttaksperiode, beregningsgrunnlagPeriode,
                tilkjentYtelsePeriode.getPeriodeFom(), språkkode);
    }

    private static List<UttakResultatPeriode> filtrerBortUkjentÅrsak(List<UttakResultatPeriode> uttaksperioder) {
        return uttaksperioder.stream()
                .filter(Predicate.not(up -> up.getPeriodeResultatÅrsak().erUkjent()))
                .toList();
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

    private static Utbetalingsperiode mapFørstePeriode(TilkjentYtelsePeriode tilkjentYtelsePeriode,
                                                       UttakResultatPeriode uttakResultatPeriode,
                                                       BeregningsgrunnlagPeriode beregningsgrunnlagPeriode,
                                                       Språkkode språkkode) {
        if (uttakResultatPeriode.getFom().isBefore(tilkjentYtelsePeriode.getPeriodeFom())) {
            return mapEnkelPeriode(tilkjentYtelsePeriode, uttakResultatPeriode, beregningsgrunnlagPeriode,
                    uttakResultatPeriode.getFom(), språkkode);
        }
        return mapEnkelPeriode(tilkjentYtelsePeriode, uttakResultatPeriode, beregningsgrunnlagPeriode,
                tilkjentYtelsePeriode.getPeriodeFom(), språkkode);
    }

    private static Utbetalingsperiode mapEnkelPeriode(TilkjentYtelsePeriode tilkjentYtelsePeriode,
                                                      UttakResultatPeriode uttakResultatPeriode,
                                                      BeregningsgrunnlagPeriode beregningsgrunnlagPeriode,
                                                      LocalDate fomDate,
                                                      Språkkode språkkode) {
        PeriodeResultatÅrsak periodeResultatÅrsak = utledÅrsakskode(uttakResultatPeriode);

        List<Arbeidsforhold> arbeidsfoholdListe = mapArbeidsforholdliste(tilkjentYtelsePeriode, uttakResultatPeriode, beregningsgrunnlagPeriode, språkkode);
        Næring næring = mapNæring(tilkjentYtelsePeriode, uttakResultatPeriode, beregningsgrunnlagPeriode);
        List<AnnenAktivitet> annenAktivitetListe = mapAnnenAktivtetListe(tilkjentYtelsePeriode, uttakResultatPeriode);

        var utbetalingsPerioder = Utbetalingsperiode.ny()
                .medAntallTapteDager(mapAntallTapteDagerFra(uttakResultatPeriode.getAktiviteter()))
                .medInnvilget(uttakResultatPeriode.isInnvilget() && !erGraderingAvslått(uttakResultatPeriode))
                .medÅrsak(Årsak.of(periodeResultatÅrsak.getKode()))
                .medStønadskontoType(hentStønadskontoType(uttakResultatPeriode))
                .medPeriodeFom(fomDate, språkkode)
                .medPeriodeTom(tilkjentYtelsePeriode.getPeriodeTom(), språkkode)
                .medArbeidsforhold(arbeidsfoholdListe)
                .medNæring(næring)
                .medAnnenAktivitet(annenAktivitetListe)
                .medPrioritertUtbetalingsgrad(finnPrioritertUtbetalingsgrad(arbeidsfoholdListe, næring, annenAktivitetListe));

        if (tilkjentYtelsePeriode.getDagsats() != null) {
            utbetalingsPerioder.medPeriodeDagsats(tilkjentYtelsePeriode.getDagsats());
        }

        return utbetalingsPerioder.build();
    }

    private static StønadskontoType hentStønadskontoType(UttakResultatPeriode uttakResultatPeriode) {
        return uttakResultatPeriode.getAktiviteter().stream().findFirst().map(UttakResultatPeriodeAktivitet::getTrekkonto).orElse(StønadskontoType.UDEFINERT);
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

    private static List<AnnenAktivitet> mapAnnenAktivtetListe(TilkjentYtelsePeriode tilkjentYtelsePeriode,
                                                              UttakResultatPeriode uttakPeriode) {
        List<AnnenAktivitet> annenAktivitetListe = new ArrayList<>();
        // - for å reverse rekkefølgen-gradering først i listen:
        Comparator<AnnenAktivitet> annenAktiviteTypeIsGraderingComparator = (a, b) -> - Boolean.compare(a.isGradering(), b.isGradering());
        finnAndelerOgUttakAnnenAktivitet(tilkjentYtelsePeriode, uttakPeriode)
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
                    annenAktivitetBuilder.medUtbetalingsgrad(Prosent.of(uttakAktivitet.getUtbetalingsprosent()));
                    annenAktivitetBuilder.medProsentArbeid(Prosent.of(uttakAktivitet.getArbeidsprosent()));
                    annenAktivitetBuilder.medAktivitetDagsats(tilkjentYtelseAndelMedTilhørendeUttaksaktivitet.andel.getDagsats());
                });
        return annenAktivitetBuilder.build();
    }

    private static Stream<BeregningsresOgUttaksAndel> finnAndelerOgUttakAnnenAktivitet(TilkjentYtelsePeriode tilkjentYtelsePeriode,
                                                                                       UttakResultatPeriode uttakPeriode) {
        return tilkjentYtelsePeriode.getAndeler().stream()
                .filter(Predicate.not(andel -> AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE.equals(andel.getAktivitetStatus())))
                .filter(Predicate.not(andel -> AktivitetStatus.ARBEIDSTAKER.equals(andel.getAktivitetStatus())))
                .map(andel -> matchTilkjentYtelseAndelMedUttaksaktivitet(andel, uttakPeriode));
    }

    private static BeregningsresOgUttaksAndel matchTilkjentYtelseAndelMedUttaksaktivitet(TilkjentYtelseAndel tilkjentYtelseAndel,
                                                                                         UttakResultatPeriode uttakPeriode) {
        return new BeregningsresOgUttaksAndel(tilkjentYtelseAndel, PeriodeBeregner.finnAktivitetMedStatusHvisFinnes(uttakPeriode.getAktiviteter(), tilkjentYtelseAndel));
    }

    private static record BeregningsresOgUttaksAndel(TilkjentYtelseAndel andel, Optional<UttakResultatPeriodeAktivitet> UttakAktivitet) {
    }

    private static Næring mapNæring(TilkjentYtelsePeriode tilkjentYtelsePeriode, UttakResultatPeriode uttakResultatPeriode,
                                    BeregningsgrunnlagPeriode beregningsgrunnlagPeriode) {
        return finnNæringsandeler(tilkjentYtelsePeriode).stream().map(andel -> mapNæringsandel(
                PeriodeBeregner.finnAktivitetMedStatusHvisFinnes(uttakResultatPeriode.getAktiviteter(), andel),
                PeriodeBeregner.finnBgPerStatusOgAndelHvisFinnes(beregningsgrunnlagPeriode.getBeregningsgrunnlagPrStatusOgAndelList(), andel)))
                .findFirst().orElse(null);
    }

    private static Næring mapNæringsandel(Optional<UttakResultatPeriodeAktivitet> uttakAktivitet,
            Optional<BeregningsgrunnlagPrStatusOgAndel> beregningsgrunnlagAndel) {
        var næringBuilder = Næring.ny();

        uttakAktivitet.ifPresent(u -> {
            næringBuilder.medUtbetalingsgrad(Prosent.of(u.getUtbetalingsprosent()));
            næringBuilder.medProsentArbeid(Prosent.of(u.getArbeidsprosent()));
            næringBuilder.medGradering(u.getGraderingInnvilget());
        });
        beregningsgrunnlagAndel.ifPresent(bgAndel -> {
            if (bgAndel.getBeregningsperiodeTom() != null) {
                næringBuilder.medSistLignedeÅr(bgAndel.getBeregningsperiodeTom().getYear());
            }
            næringBuilder.medAktivitetDagsats(bgAndel.getDagsats().intValue());
        });
        return næringBuilder.build();
    }

    private static List<TilkjentYtelseAndel> finnNæringsandeler(TilkjentYtelsePeriode tilkjentYtelsePeriode) {
        return tilkjentYtelsePeriode.getAndeler().stream()
                .filter(andel -> AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE.equals(andel.getAktivitetStatus()))
                .toList();
    }

    private static List<Arbeidsforhold> mapArbeidsforholdliste(TilkjentYtelsePeriode tilkjentYtelsePeriode,
                                                               UttakResultatPeriode uttakResultatPeriode, BeregningsgrunnlagPeriode beregningsgrunnlagPeriode, Språkkode språkkode) {
        List<Arbeidsforhold> arbeidsforholdListe = new ArrayList<>();
        for (TilkjentYtelseAndel andel : finnArbeidsandeler(tilkjentYtelsePeriode)) {
            arbeidsforholdListe
                    .add(mapArbeidsforholdAndel(tilkjentYtelsePeriode, andel,
                            PeriodeBeregner.finnAktivitetMedStatusHvisFinnes(uttakResultatPeriode.getAktiviteter(), andel), PeriodeBeregner
                                    .finnBgPerStatusOgAndelHvisFinnes(beregningsgrunnlagPeriode.getBeregningsgrunnlagPrStatusOgAndelList(), andel),
                            beregningsgrunnlagPeriode, språkkode));
        }
        // - for reverse order:
        Comparator<Arbeidsforhold> arbeidsforholdTypeIsGraderingComparator = (a, b) -> - Boolean.compare(a.isGradering(), b.isGradering());
        arbeidsforholdListe.sort(arbeidsforholdTypeIsGraderingComparator);
        return arbeidsforholdListe;
    }

    private static List<TilkjentYtelseAndel> finnArbeidsandeler(TilkjentYtelsePeriode tilkjentYtelsePeriode) {
        return tilkjentYtelsePeriode.getAndeler().stream()
                .filter(andel -> AktivitetStatus.ARBEIDSTAKER.equals(andel.getAktivitetStatus()))
                .toList();
    }

    private static Arbeidsforhold mapArbeidsforholdAndel(TilkjentYtelsePeriode tilkjentYtelsePeriode,
                                                         TilkjentYtelseAndel tilkjentYtelseAndel,
                                                         Optional<UttakResultatPeriodeAktivitet> uttakAktivitet,
                                                         Optional<BeregningsgrunnlagPrStatusOgAndel> beregningsgrunnlagAndel,
                                                         BeregningsgrunnlagPeriode beregningsgrunnlagPeriode,
                                                         Språkkode språkkode) {
        var arbeidsforhold = Arbeidsforhold.ny()
                .medArbeidsgiverNavn((tilkjentYtelseAndel.getArbeidsgiver().map(Arbeidsgiver::navn).orElse("Andel")));
        arbeidsforhold.medAktivitetDagsats(tilkjentYtelseAndel.getDagsats());
        if (uttakAktivitet.isPresent()) {
            arbeidsforhold.medProsentArbeid(Prosent.of(uttakAktivitet.get().getArbeidsprosent()));
            arbeidsforhold.medUtbetalingsgrad(Prosent.of(uttakAktivitet.get().getUtbetalingsprosent()));
            arbeidsforhold.medGradering(uttakAktivitet.get().getGraderingInnvilget());
        }
        arbeidsforhold.medStillingsprosent(Prosent.of(tilkjentYtelseAndel.getStillingsprosent()));
        beregningsgrunnlagAndel.ifPresent(bgAndel -> {
            final BeregningsgrunnlagPrStatusOgAndel statusOgAndel = beregningsgrunnlagAndel.get();
            statusOgAndel.getBgAndelArbeidsforhold().ifPresent(bgAndelArbeidsforhold -> {
                if (bgAndelArbeidsforhold.naturalytelseBortfaltPrÅr() != null ||
                        bgAndelArbeidsforhold.naturalytelseTilkommetPrÅr() != null)
                    mapNaturalytelse(arbeidsforhold, beregningsgrunnlagPeriode, tilkjentYtelsePeriode, språkkode);
            });
        });
        return arbeidsforhold.build();
    }

    private static void mapNaturalytelse(Arbeidsforhold.Builder arbeidsforholdBuilder, BeregningsgrunnlagPeriode beregningsgrunnlagPeriode,
                                         TilkjentYtelsePeriode tilkjentYtelsePeriode, Språkkode språkkode) {
        for (PeriodeÅrsak årsak : beregningsgrunnlagPeriode.getPeriodeÅrsakKoder()) {
            if (PeriodeÅrsak.NATURALYTELSE_BORTFALT.equals(årsak)) {
                arbeidsforholdBuilder.medNaturalytelseEndringType(NaturalytelseEndringType.STOPP);
                arbeidsforholdBuilder.medNaturalytelseNyDagsats((beregningsgrunnlagPeriode.getDagsats()));
                arbeidsforholdBuilder.medNaturalytelseEndringDato(formaterDato(tilkjentYtelsePeriode.getPeriodeFom(), språkkode));
            } else if (PeriodeÅrsak.NATURALYTELSE_TILKOMMER.equals(årsak)) {
                arbeidsforholdBuilder.medNaturalytelseEndringType((NaturalytelseEndringType.START));
                arbeidsforholdBuilder.medNaturalytelseNyDagsats(beregningsgrunnlagPeriode.getDagsats());
                arbeidsforholdBuilder.medNaturalytelseEndringDato(formaterDato(tilkjentYtelsePeriode.getPeriodeFom(), språkkode));
            } else {
                arbeidsforholdBuilder.medNaturalytelseEndringType(NaturalytelseEndringType.INGEN_ENDRING);
            }
        }
    }

    public static Prosent finnPrioritertUtbetalingsgrad(List<Arbeidsforhold> arbeidsfoholdListe, Næring næring, List<AnnenAktivitet> annenAktivitetListe) {
        Prosent resultat = Prosent.NULL;
        if (arbeidsfoholdListe != null && arbeidsfoholdListe.size() == 1) {
            resultat = arbeidsfoholdListe.get(0).getUtbetalingsgrad() != null ? arbeidsfoholdListe.get(0).getUtbetalingsgrad() : Prosent.NULL;
        } else if (arbeidsfoholdListe != null && arbeidsfoholdListe.size() > 1) {
            Optional<Arbeidsforhold> arbeidsforholdMedGradering = arbeidsfoholdListe.stream().filter(Arbeidsforhold::isGradering).findFirst();
            if (arbeidsforholdMedGradering.isPresent()) {
                resultat = arbeidsforholdMedGradering.get().getUtbetalingsgrad() != null ? arbeidsforholdMedGradering.get().getUtbetalingsgrad() : Prosent.NULL;
            } else {
                resultat = arbeidsfoholdListe.stream().filter(a -> !a.getUtbetalingsgrad().equals(Prosent.NULL)).findFirst().map(Arbeidsforhold::getUtbetalingsgrad).orElse(Prosent.NULL);
            }
        }

        if (resultat.equals(Prosent.NULL) && næring != null) {
            resultat = næring.getUtbetalingsgrad() != null ? næring.getUtbetalingsgrad() : Prosent.NULL;
        }

        if (resultat.equals(Prosent.NULL) && annenAktivitetListe != null && annenAktivitetListe.size() == 1) {
            resultat = annenAktivitetListe.get(0).getUtbetalingsgrad() != null ? annenAktivitetListe.get(0).getUtbetalingsgrad() : Prosent.NULL;
        } else if (annenAktivitetListe != null && annenAktivitetListe.size() > 1) {
            Optional<AnnenAktivitet> annenAktivitetMedGradering = annenAktivitetListe.stream().filter(AnnenAktivitet::isGradering).findFirst();
            if (annenAktivitetMedGradering.isPresent()) {
                resultat = annenAktivitetMedGradering.get().getUtbetalingsgrad() != null ? annenAktivitetMedGradering.get().getUtbetalingsgrad() : Prosent.NULL ;
            } else {
                resultat = annenAktivitetListe.stream().filter(a -> !a.getUtbetalingsgrad().equals(Prosent.NULL)).findFirst().map(AnnenAktivitet::getUtbetalingsgrad).orElse(Prosent.NULL);
            }
        }

        if (resultat.erStørreEnnHundreProsent()) {
            resultat = Prosent.HUNDRE;
        }
        return resultat;
    }
}
