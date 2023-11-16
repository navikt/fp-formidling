package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.PeriodeBeregner.alleAktiviteterHarNullUtbetaling;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.VedtaksperiodeMerger.mergePerioder;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.Vedtaksperiode;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelseAndel;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelsePeriode;
import no.nav.foreldrepenger.fpformidling.uttak.fp.ForeldrepengerUttak;
import no.nav.foreldrepenger.fpformidling.uttak.fp.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.fpformidling.uttak.fp.StønadskontoType;
import no.nav.foreldrepenger.fpformidling.uttak.fp.UttakResultatPeriode;
import no.nav.foreldrepenger.fpformidling.uttak.fp.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.fpformidling.virksomhet.Arbeidsgiver;

public final class VedtaksperiodeMapper {

    private VedtaksperiodeMapper() {
    }

    public static List<Vedtaksperiode> mapVedtaksperioder(List<TilkjentYtelsePeriode> tilkjentYtelsePerioder,
                                                          ForeldrepengerUttak foreldrepengerUttak,
                                                          List<BeregningsgrunnlagPeriode> beregningingsgrunnlagPerioder,
                                                          Språkkode språkkode) {
        List<Vedtaksperiode> periodelisteFørSammenslåing = new ArrayList<>();
        var uttaksperioder = foreldrepengerUttak.perioder();
        // er avhengig av at listen er sortert med tidligste periode først
        var tilkjentYtelsePerioderSortert = tilkjentYtelsePerioder.stream()
            .sorted(Comparator.comparing(TilkjentYtelsePeriode::getPeriodeFom))
            .toList();

        List<UttakResultatPeriode> uttaksperioderMedÅrsak = new ArrayList<>(filtrerBortUkjentÅrsak(uttaksperioder));

        for (var i = 0; i < tilkjentYtelsePerioderSortert.size(); i++) {
            var tilkjentYtelsePeriode = tilkjentYtelsePerioderSortert.get(i);
            var matchetUttaksperiode = PeriodeBeregner.finnUttaksperiode(tilkjentYtelsePeriode, uttaksperioder);
            if (matchetUttaksperiode.getPeriodeResultatÅrsak().erUkjent() || avslåttManglendeSøktUtenTrekkdager(matchetUttaksperiode)) {
                continue;
            }
            uttaksperioderMedÅrsak.remove(matchetUttaksperiode);
            Vedtaksperiode periodeTilListen;
            var beregningsgrunnlagPeriode = PeriodeBeregner.finnBeregningsgrunnlagperiode(tilkjentYtelsePeriode, beregningingsgrunnlagPerioder);

            if (i == 0) {
                periodeTilListen = mapFørstePeriode(tilkjentYtelsePeriode, tilkjentYtelsePerioder, matchetUttaksperiode, beregningsgrunnlagPeriode,
                    språkkode);
            } else {
                periodeTilListen = mapPerioderEtterFørste(tilkjentYtelsePeriode, tilkjentYtelsePerioder, matchetUttaksperiode,
                    beregningsgrunnlagPeriode, språkkode);
            }
            periodelisteFørSammenslåing.add(periodeTilListen);
        }
        periodelisteFørSammenslåing.addAll(mapPerioderUtenBeregningsgrunnlag(uttaksperioderMedÅrsak, språkkode));
        periodelisteFørSammenslåing.sort(Comparator.comparing(Vedtaksperiode::getPeriodeFom));
        return new ArrayList<>(mergePerioder(periodelisteFørSammenslåing));
    }

    public static Optional<LocalDate> finnStønadsperiodeFom(List<Vedtaksperiode> periodeListe) {
        return periodeListe.stream()
            .filter(p -> Boolean.TRUE.equals(p.isInnvilget()))
            .map(Vedtaksperiode::getPeriodeFom)
            .min(Comparator.comparing(LocalDate::toEpochDay));
    }

    public static Optional<LocalDate> finnStønadsperiodeTom(List<Vedtaksperiode> periodeListe) {
        return periodeListe.stream()
            .filter(p -> Boolean.TRUE.equals(p.isInnvilget()))
            .map(Vedtaksperiode::getPeriodeTom)
            .max(Comparator.comparing(LocalDate::toEpochDay));
    }

    public static int finnAntallInnvilgedePerioder(List<Vedtaksperiode> vedtaksperioder) {
        return (int) vedtaksperioder.stream().filter(Vedtaksperiode::isInnvilget).count();
    }

    public static int finnAntallAvslåttePerioder(List<Vedtaksperiode> vedtaksperioder) {
        return (int) vedtaksperioder.stream().filter(Vedtaksperiode::isAvslått).count();
    }

    public static boolean finnesPeriodeMedIkkeOmsorg(List<Vedtaksperiode> vedtaksperioder) {
        return vedtaksperioder.stream()
            .map(Vedtaksperiode::getÅrsak)
            .anyMatch(årsak -> Årsak.of(PeriodeResultatÅrsak.MOR_HAR_IKKE_OMSORG.getKode()).equals(årsak) || Årsak.of(
                PeriodeResultatÅrsak.FAR_HAR_IKKE_OMSORG.getKode()).equals(årsak));
    }

    public static boolean sistePeriodeAvslåttPgaBarnOver3år(List<Vedtaksperiode> vedtaksperioder) {
        return vedtaksperioder.stream()
            .max(Comparator.comparing(Vedtaksperiode::getPeriodeFom))
            .map(sistePeriode -> Objects.equals(sistePeriode.getÅrsak().getKode(), PeriodeResultatÅrsak.BARN_OVER_3_ÅR.getKode()))
            .orElse(false);
    }

    private static List<Vedtaksperiode> mapPerioderUtenBeregningsgrunnlag(List<UttakResultatPeriode> perioderUtenBeregningsgrunnlag,
                                                                          Språkkode språkkode) {
        List<Vedtaksperiode> perioder = new ArrayList<>();
        for (var uttakperiode : perioderUtenBeregningsgrunnlag) {
            if (!avslåttManglendeSøktUtenTrekkdager(uttakperiode)) {
                perioder.add(mapEnkelUttaksperiode(uttakperiode, språkkode));
            }
        }
        return perioder;
    }

    private static Vedtaksperiode mapEnkelUttaksperiode(UttakResultatPeriode uttakperiode, Språkkode språkkode) {
        return Vedtaksperiode.ny()
            .medAntallTapteDager(mapAntallTapteDagerFra(uttakperiode.getAktiviteter()), BigDecimal.ZERO)
            .medInnvilget((uttakperiode.isInnvilget() && !erGraderingAvslått(uttakperiode)))
            .medPeriodeFom(uttakperiode.getFom(), språkkode)
            .medPeriodeTom(uttakperiode.getTom(), språkkode)
            .medÅrsak(Årsak.of(uttakperiode.getPeriodeResultatÅrsak().getKode()))
            .medStønadskontoType(hentStønadskontoType(uttakperiode)).build();
    }

    private static Vedtaksperiode mapPerioderEtterFørste(TilkjentYtelsePeriode tilkjentYtelsePeriode,
                                                         List<TilkjentYtelsePeriode> tilkjentPeriodeListe,
                                                         UttakResultatPeriode matchetUttaksperiode,
                                                         BeregningsgrunnlagPeriode beregningsgrunnlagPeriode,
                                                         Språkkode språkkode) {
        return mapEnkelPeriode(tilkjentYtelsePeriode, tilkjentPeriodeListe, matchetUttaksperiode, beregningsgrunnlagPeriode,
            tilkjentYtelsePeriode.getPeriodeFom(), språkkode);
    }

    private static List<UttakResultatPeriode> filtrerBortUkjentÅrsak(List<UttakResultatPeriode> uttaksperioder) {
        return uttaksperioder.stream().filter(Predicate.not(up -> up.getPeriodeResultatÅrsak().erUkjent())).toList();
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
            .max(BigDecimal::compareTo)
            .map(bd -> bd.setScale(1, RoundingMode.DOWN).intValue())
            .orElse(0) : 0;
    }

    private static Vedtaksperiode mapFørstePeriode(TilkjentYtelsePeriode tilkjentYtelsePeriode,
                                                   List<TilkjentYtelsePeriode> tilkjentPeriodeListe,
                                                   UttakResultatPeriode uttakResultatPeriode,
                                                   BeregningsgrunnlagPeriode beregningsgrunnlagPeriode,
                                                   Språkkode språkkode) {
        if (uttakResultatPeriode.getFom().isBefore(tilkjentYtelsePeriode.getPeriodeFom())) {
            return mapEnkelPeriode(tilkjentYtelsePeriode, tilkjentPeriodeListe, uttakResultatPeriode, beregningsgrunnlagPeriode,
                uttakResultatPeriode.getFom(), språkkode);
        }
        return mapEnkelPeriode(tilkjentYtelsePeriode, tilkjentPeriodeListe, uttakResultatPeriode, beregningsgrunnlagPeriode,
            tilkjentYtelsePeriode.getPeriodeFom(), språkkode);
    }

    private static Vedtaksperiode mapEnkelPeriode(TilkjentYtelsePeriode tilkjentYtelsePeriode,
                                                  List<TilkjentYtelsePeriode> tilkjentPeriodeListe,
                                                  UttakResultatPeriode uttakResultatPeriode,
                                                  BeregningsgrunnlagPeriode beregningsgrunnlagPeriode,
                                                  LocalDate fomDate,
                                                  Språkkode språkkode) {
        var periodeResultatÅrsak = utledÅrsakskode(uttakResultatPeriode);

        var arbeidsfoholdListe = mapArbeidsforholdliste(tilkjentYtelsePeriode, uttakResultatPeriode, beregningsgrunnlagPeriode, språkkode);
        var næring = mapNæring(tilkjentYtelsePeriode, uttakResultatPeriode, beregningsgrunnlagPeriode);
        var annenAktivitetListe = mapAnnenAktivtetListe(tilkjentYtelsePeriode, uttakResultatPeriode);
        //TFP-5200 Hack for å forhindre dobbelt antall tapte dager om det er flere tilkjentperioder for en uttaksperiode
        var antallTilkjentPerioder = PeriodeBeregner.finnAntallTilkjentePerioderForUttaksperioden(tilkjentPeriodeListe, uttakResultatPeriode);
        var tapteDagerForUttaksperioden = mapAntallTapteDagerFra(uttakResultatPeriode.getAktiviteter());
        var tapteDagerHvisFlereTilkjentPerioder = BigDecimal.ZERO;
        if (antallTilkjentPerioder > 1) {
            tapteDagerHvisFlereTilkjentPerioder = BigDecimal.valueOf(tapteDagerForUttaksperioden)
                .divide(BigDecimal.valueOf(antallTilkjentPerioder), 2, RoundingMode.HALF_UP);
        }

        var vedtaksperioder = Vedtaksperiode.ny()
            .medAntallTapteDager(tapteDagerForUttaksperioden, tapteDagerHvisFlereTilkjentPerioder)
            .medInnvilget(uttakResultatPeriode.isInnvilget() && !erGraderingAvslått(uttakResultatPeriode))
            .medÅrsak(Årsak.of(periodeResultatÅrsak.getKode()))
            .medStønadskontoType(hentStønadskontoType(uttakResultatPeriode))
            .medPeriodeFom(fomDate, språkkode)
            .medPeriodeTom(tilkjentYtelsePeriode.getPeriodeTom(), språkkode)
            .medArbeidsforhold(arbeidsfoholdListe)
            .medNæring(næring)
            .medAnnenAktivitet(annenAktivitetListe)
            .medPrioritertUtbetalingsgrad(finnPrioritertUtbetalingsgrad(arbeidsfoholdListe, næring, annenAktivitetListe))
            .medFullUtbetaling(erFullUtbetaling(uttakResultatPeriode))
            .medTidligstMottattDato(uttakResultatPeriode.getTidligstMottattDato(), språkkode);

        if (tilkjentYtelsePeriode.getDagsats() != null) {
            vedtaksperioder.medPeriodeDagsats(tilkjentYtelsePeriode.getDagsats());
        }

        return vedtaksperioder.build();
    }

    private static boolean erFullUtbetaling(UttakResultatPeriode uttakResultatPeriode) {
        return uttakResultatPeriode.getAktiviteter().stream().allMatch(a -> Prosent.of(a.getUtbetalingsprosent()).erFull());
    }

    private static StønadskontoType hentStønadskontoType(UttakResultatPeriode uttakResultatPeriode) {
        return uttakResultatPeriode.getAktiviteter()
            .stream()
            .findFirst()
            .map(UttakResultatPeriodeAktivitet::getTrekkonto)
            .orElse(StønadskontoType.UDEFINERT);
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

    private static List<AnnenAktivitet> mapAnnenAktivtetListe(TilkjentYtelsePeriode tilkjentYtelsePeriode, UttakResultatPeriode uttakPeriode) {
        List<AnnenAktivitet> annenAktivitetListe = new ArrayList<>();
        // - for å reverse rekkefølgen-gradering først i listen:
        var annenAktiviteTypeIsGraderingComparator = (Comparator<AnnenAktivitet>) (a, b) -> -Boolean.compare(a.isGradering(), b.isGradering());
        finnAndelerOgUttakAnnenAktivitet(tilkjentYtelsePeriode, uttakPeriode).map(VedtaksperiodeMapper::mapAnnenAktivitet)
            .sorted(annenAktiviteTypeIsGraderingComparator)
            .forEach(annenAktivitetListe::add);
        return annenAktivitetListe;
    }

    private static AnnenAktivitet mapAnnenAktivitet(BeregningsresOgUttaksAndel tilkjentYtelseAndelMedTilhørendeUttaksaktivitet) {
        var annenAktivitetBuilder = AnnenAktivitet.ny()
            .medAktivitetStatus((tilkjentYtelseAndelMedTilhørendeUttaksaktivitet.andel.getAktivitetStatus().name()));
        tilkjentYtelseAndelMedTilhørendeUttaksaktivitet.UttakAktivitet.ifPresent(uttakAktivitet -> {
            annenAktivitetBuilder.medGradering(uttakAktivitet.getGraderingInnvilget());
            annenAktivitetBuilder.medUtbetalingsgrad(Prosent.of(uttakAktivitet.getUtbetalingsprosent()));
            annenAktivitetBuilder.medProsentArbeid(Prosent.of(uttakAktivitet.getArbeidsprosent()));
            annenAktivitetBuilder.medAktivitetDagsats(tilkjentYtelseAndelMedTilhørendeUttaksaktivitet.andel.getDagsats());
        });
        return annenAktivitetBuilder.build();
    }

    private static Stream<BeregningsresOgUttaksAndel> finnAndelerOgUttakAnnenAktivitet(TilkjentYtelsePeriode tilkjentYtelsePeriode,
                                                                                       UttakResultatPeriode uttakPeriode) {
        return tilkjentYtelsePeriode.getAndeler()
            .stream()
            .filter(Predicate.not(andel -> AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE.equals(andel.getAktivitetStatus())))
            .filter(Predicate.not(andel -> AktivitetStatus.ARBEIDSTAKER.equals(andel.getAktivitetStatus())))
            .map(andel -> matchTilkjentYtelseAndelMedUttaksaktivitet(andel, uttakPeriode));
    }

    private static BeregningsresOgUttaksAndel matchTilkjentYtelseAndelMedUttaksaktivitet(TilkjentYtelseAndel tilkjentYtelseAndel,
                                                                                         UttakResultatPeriode uttakPeriode) {
        return new BeregningsresOgUttaksAndel(tilkjentYtelseAndel,
            PeriodeBeregner.finnAktivitetMedStatusHvisFinnes(uttakPeriode.getAktiviteter(), tilkjentYtelseAndel));
    }

    private record BeregningsresOgUttaksAndel(TilkjentYtelseAndel andel, Optional<UttakResultatPeriodeAktivitet> UttakAktivitet) {
    }

    private static Næring mapNæring(TilkjentYtelsePeriode tilkjentYtelsePeriode,
                                    UttakResultatPeriode uttakResultatPeriode,
                                    BeregningsgrunnlagPeriode beregningsgrunnlagPeriode) {
        return finnNæringsandeler(tilkjentYtelsePeriode).stream()
            .map(andel -> mapNæringsandel(PeriodeBeregner.finnAktivitetMedStatusHvisFinnes(uttakResultatPeriode.getAktiviteter(), andel),
                PeriodeBeregner.finnBgPerStatusOgAndelHvisFinnes(beregningsgrunnlagPeriode.getBeregningsgrunnlagPrStatusOgAndelList(), andel)))
            .findFirst()
            .orElse(null);
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
        return tilkjentYtelsePeriode.getAndeler()
            .stream()
            .filter(andel -> AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE.equals(andel.getAktivitetStatus()))
            .toList();
    }

    private static List<Arbeidsforhold> mapArbeidsforholdliste(TilkjentYtelsePeriode tilkjentYtelsePeriode,
                                                               UttakResultatPeriode uttakResultatPeriode,
                                                               BeregningsgrunnlagPeriode beregningsgrunnlagPeriode,
                                                               Språkkode språkkode) {
        List<Arbeidsforhold> arbeidsforholdListe = new ArrayList<>();
        for (var andel : finnArbeidsandeler(tilkjentYtelsePeriode)) {
            arbeidsforholdListe.add(mapArbeidsforholdAndel(tilkjentYtelsePeriode, andel,
                PeriodeBeregner.finnAktivitetMedStatusHvisFinnes(uttakResultatPeriode.getAktiviteter(), andel),
                PeriodeBeregner.finnBgPerStatusOgAndelHvisFinnes(beregningsgrunnlagPeriode.getBeregningsgrunnlagPrStatusOgAndelList(), andel),
                beregningsgrunnlagPeriode, språkkode));
        }
        // - for reverse order:
        var arbeidsforholdTypeIsGraderingComparator = (Comparator<Arbeidsforhold>) (a, b) -> -Boolean.compare(a.isGradering(), b.isGradering());
        arbeidsforholdListe.sort(arbeidsforholdTypeIsGraderingComparator);
        return arbeidsforholdListe;
    }

    private static List<TilkjentYtelseAndel> finnArbeidsandeler(TilkjentYtelsePeriode tilkjentYtelsePeriode) {
        return tilkjentYtelsePeriode.getAndeler().stream().filter(andel -> AktivitetStatus.ARBEIDSTAKER.equals(andel.getAktivitetStatus())).toList();
    }

    private static Arbeidsforhold mapArbeidsforholdAndel(TilkjentYtelsePeriode tilkjentYtelsePeriode,
                                                         TilkjentYtelseAndel tilkjentYtelseAndel,
                                                         Optional<UttakResultatPeriodeAktivitet> uttakAktivitet,
                                                         Optional<BeregningsgrunnlagPrStatusOgAndel> beregningsgrunnlagAndel,
                                                         BeregningsgrunnlagPeriode beregningsgrunnlagPeriode,
                                                         Språkkode språkkode) {
        var arbeidsforhold = Arbeidsforhold.ny().medArbeidsgiverNavn((tilkjentYtelseAndel.getArbeidsgiver().map(Arbeidsgiver::navn).orElse("Andel")));
        arbeidsforhold.medAktivitetDagsats(tilkjentYtelseAndel.getDagsats());
        if (uttakAktivitet.isPresent()) {
            arbeidsforhold.medProsentArbeid(Prosent.of(uttakAktivitet.get().getArbeidsprosent()));
            arbeidsforhold.medUtbetalingsgrad(Prosent.of(uttakAktivitet.get().getUtbetalingsprosent()));
            arbeidsforhold.medGradering(uttakAktivitet.get().getGraderingInnvilget());
        }
        arbeidsforhold.medStillingsprosent(Prosent.of(tilkjentYtelseAndel.getStillingsprosent()));
        beregningsgrunnlagAndel.ifPresent(bgAndel -> {
            final var statusOgAndel = beregningsgrunnlagAndel.get();
            statusOgAndel.getBgAndelArbeidsforhold().ifPresent(bgAndelArbeidsforhold -> {
                if (bgAndelArbeidsforhold.naturalytelseBortfaltPrÅr() != null || bgAndelArbeidsforhold.naturalytelseTilkommetPrÅr() != null) {
                    mapNaturalytelse(arbeidsforhold, beregningsgrunnlagPeriode, tilkjentYtelsePeriode, språkkode);
                }
            });
        });
        return arbeidsforhold.build();
    }

    private static void mapNaturalytelse(Arbeidsforhold.Builder arbeidsforholdBuilder,
                                         BeregningsgrunnlagPeriode beregningsgrunnlagPeriode,
                                         TilkjentYtelsePeriode tilkjentYtelsePeriode,
                                         Språkkode språkkode) {
        for (var årsak : beregningsgrunnlagPeriode.getPeriodeÅrsakKoder()) {
            if (PeriodeÅrsak.NATURALYTELSE_BORTFALT.equals(årsak)) {
                arbeidsforholdBuilder.medNaturalytelseEndringType(NaturalytelseEndringType.STOPP);
                arbeidsforholdBuilder.medNaturalytelseEndringDato(formaterDato(tilkjentYtelsePeriode.getPeriodeFom(), språkkode));
                arbeidsforholdBuilder.medBruttoInkludertBortfaltNaturalytelsePrAar(beregningsgrunnlagPeriode.getAvkortetPrÅr().longValue());
            } else if (PeriodeÅrsak.NATURALYTELSE_TILKOMMER.equals(årsak)) {
                arbeidsforholdBuilder.medNaturalytelseEndringType((NaturalytelseEndringType.START));
                arbeidsforholdBuilder.medNaturalytelseEndringDato(formaterDato(tilkjentYtelsePeriode.getPeriodeFom(), språkkode));
                arbeidsforholdBuilder.medBruttoInkludertBortfaltNaturalytelsePrAar(beregningsgrunnlagPeriode.getAvkortetPrÅr().longValue());
            } else {
                arbeidsforholdBuilder.medNaturalytelseEndringType(NaturalytelseEndringType.INGEN_ENDRING);
            }
        }
    }

    public static Prosent finnPrioritertUtbetalingsgrad(List<Arbeidsforhold> arbeidsfoholdListe,
                                                        Næring næring,
                                                        List<AnnenAktivitet> annenAktivitetListe) {
        var resultat = Prosent.NULL;

        if (arbeidsfoholdListe != null) {
            resultat = arbeidsfoholdListe.stream()
                .filter(Arbeidsforhold::isGradering)
                .findFirst()
                .map(Arbeidsforhold::getUtbetalingsgrad)
                .orElseGet(() -> arbeidsfoholdListe.stream()
                    .filter(a -> a.getUtbetalingsgrad() != null && !a.getUtbetalingsgrad().equals(Prosent.NULL))
                    .findFirst()
                    .map(Arbeidsforhold::getUtbetalingsgrad)
                    .orElse(Prosent.NULL));
        }

        if (resultat.equals(Prosent.NULL) && næring != null) {
            resultat = næring.getUtbetalingsgrad() != null ? næring.getUtbetalingsgrad() : Prosent.NULL;
        }

        if (resultat.equals(Prosent.NULL) && annenAktivitetListe != null) {
            resultat = annenAktivitetListe.stream()
                .filter(AnnenAktivitet::isGradering)
                .findFirst()
                .map(AnnenAktivitet::getUtbetalingsgrad)
                .orElseGet(() -> annenAktivitetListe.stream()
                    .filter(a -> a.getUtbetalingsgrad() != null && !a.getUtbetalingsgrad().equals(Prosent.NULL))
                    .findFirst()
                    .map(AnnenAktivitet::getUtbetalingsgrad)
                    .orElse(Prosent.NULL));
        }

        if (resultat.erStørreEnnHundreProsent()) {
            resultat = Prosent.HUNDRE;
        }
        return resultat;
    }
}

