package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp;

import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.Foreldrepenger;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;
import static no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseDagytelseDto.Aktivitetstatus;
import static no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseDagytelseDto.TilkjentYtelseAndelDto;
import static no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseDagytelseDto.TilkjentYtelsePeriodeDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.PeriodeBeregner;
import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.StønadskontoType;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Prosent;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Årsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.AnnenAktivitet;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.Arbeidsforhold;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.NaturalytelseEndringType;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.Næring;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.Vedtaksperiode;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.Foreldrepenger.Aktivitet;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.Foreldrepenger.Uttaksperiode;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.KodeverkMapper;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Fpsak;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagAndelDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.PeriodeÅrsakDto;

public final class VedtaksperiodeMapper {

    private VedtaksperiodeMapper() {
    }

    public static List<Vedtaksperiode> mapVedtaksperioder(List<TilkjentYtelsePeriodeDto> tilkjentYtelsePerioder,
                                                          Foreldrepenger foreldrepenger,
                                                          List<BeregningsgrunnlagPeriodeDto> beregningingsgrunnlagPerioder,
                                                          Språkkode språkkode,
                                                          UnaryOperator<String> hentNavn) {
        List<Vedtaksperiode> periodelisteFørSammenslåing = new ArrayList<>();
        var uttaksperioder = foreldrepenger.perioderSøker();
        // er avhengig av at listen er sortert med tidligste periode først
        var tilkjentYtelsePerioderSortert = tilkjentYtelsePerioder.stream()
            .sorted(Comparator.comparing(TilkjentYtelsePeriodeDto::fom))
            .toList();

        List<Uttaksperiode> uttaksperioderMedÅrsak = new ArrayList<>(uttaksperioder);

        for (var i = 0; i < tilkjentYtelsePerioderSortert.size(); i++) {
            var tilkjentYtelsePeriode = tilkjentYtelsePerioderSortert.get(i);
            var matchetUttaksperiode = PeriodeBeregner.finnUttaksperiode(tilkjentYtelsePeriode, uttaksperioder);
            if (avslåttManglendeSøktUtenTrekkdager(matchetUttaksperiode)) {
                continue;
            }
            uttaksperioderMedÅrsak.remove(matchetUttaksperiode);
            Vedtaksperiode periodeTilListen;
            var beregningsgrunnlagPeriode = PeriodeBeregner.finnBeregningsgrunnlagperiode(tilkjentYtelsePeriode, beregningingsgrunnlagPerioder);

            if (i == 0) {
                periodeTilListen = mapFørstePeriode(tilkjentYtelsePeriode, tilkjentYtelsePerioderSortert, matchetUttaksperiode, beregningsgrunnlagPeriode,
                    språkkode, hentNavn);
            } else {
                periodeTilListen = mapPerioderEtterFørste(tilkjentYtelsePeriode, tilkjentYtelsePerioderSortert, matchetUttaksperiode,
                    beregningsgrunnlagPeriode, språkkode, hentNavn);
            }
            periodelisteFørSammenslåing.add(periodeTilListen);
        }
        periodelisteFørSammenslåing.addAll(mapPerioderUtenBeregningsgrunnlag(uttaksperioderMedÅrsak, språkkode));
        periodelisteFørSammenslåing.sort(Comparator.comparing(Vedtaksperiode::getPeriodeFom));
        return new ArrayList<>(VedtaksperiodeMerger.mergePerioder(periodelisteFørSammenslåing));
    }

    public static Optional<LocalDate> finnStønadsperiodeFom(List<Vedtaksperiode> periodeListe) {
        return periodeListe.stream()
            .filter(Vedtaksperiode::isInnvilget)
            .map(Vedtaksperiode::getPeriodeFom)
            .min(Comparator.comparing(LocalDate::toEpochDay));
    }

    public static Optional<LocalDate> finnStønadsperiodeTom(List<Vedtaksperiode> periodeListe) {
        return periodeListe.stream()
            .filter(Vedtaksperiode::isInnvilget)
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

    public static List<Vedtaksperiode> mapPerioderUtenBeregningsgrunnlag(List<Uttaksperiode> perioderUtenBeregningsgrunnlag,
                                                                          Språkkode språkkode) {
        List<Vedtaksperiode> perioder = new ArrayList<>();
        for (var uttakperiode : perioderUtenBeregningsgrunnlag) {
            if (!avslåttManglendeSøktUtenTrekkdager(uttakperiode)) {
                perioder.add(mapEnkelUttaksperiode(uttakperiode, språkkode));
            }
        }
        return perioder;
    }

    private static Vedtaksperiode mapEnkelUttaksperiode(Uttaksperiode uttakperiode, Språkkode språkkode) {
        return Vedtaksperiode.ny()
            .medAntallTapteDager(mapAntallTapteDagerFra(uttakperiode.aktiviteter()), BigDecimal.ZERO)
            .medInnvilget((uttakperiode.isInnvilget() && !erGraderingAvslått(uttakperiode)))
            .medPeriodeFom(uttakperiode.fom(), språkkode)
            .medPeriodeTom(uttakperiode.tom(), språkkode)
            .medÅrsak(Årsak.of(uttakperiode.periodeResultatÅrsak()))
            .medStønadskontoType(hentStønadskontoType(uttakperiode))
            .medTidligstMottattDato(uttakperiode.tidligstMottattDato(), språkkode).build();
    }

    private static Vedtaksperiode mapPerioderEtterFørste(TilkjentYtelsePeriodeDto tilkjentYtelsePeriode,
                                                         List<TilkjentYtelsePeriodeDto> tilkjentPeriodeListe,
                                                         Uttaksperiode matchetUttaksperiode,
                                                         BeregningsgrunnlagPeriodeDto beregningsgrunnlagPeriode,
                                                         Språkkode språkkode,
                                                         UnaryOperator<String> hentNavn) {
        return mapEnkelPeriode(tilkjentYtelsePeriode, tilkjentPeriodeListe, matchetUttaksperiode, beregningsgrunnlagPeriode,
            tilkjentYtelsePeriode.fom(), språkkode, hentNavn);
    }

    private static boolean avslåttManglendeSøktUtenTrekkdager(Uttaksperiode uttakperiode) {
        return avslåttManglendeSøktPeriode(uttakperiode) && ingenTrekkdager(uttakperiode);
    }

    private static boolean avslåttManglendeSøktPeriode(Uttaksperiode matchetUttaksperiode) {
        return PeriodeResultatÅrsak.HULL_MELLOM_FORELDRENES_PERIODER.getKode().equals(matchetUttaksperiode.periodeResultatÅrsak());
    }

    private static boolean ingenTrekkdager(Uttaksperiode p) {
        return mapAntallTapteDagerFra(p.aktiviteter()) == 0;
    }

    private static int mapAntallTapteDagerFra(List<Aktivitet> uttakAktiviteter) {
        return PeriodeBeregner.alleAktiviteterHarNullUtbetaling(uttakAktiviteter) ? uttakAktiviteter.stream()
            .map(Aktivitet::trekkdager)
            .filter(Objects::nonNull)
            .max(BigDecimal::compareTo)
            .map(bd -> bd.setScale(1, RoundingMode.DOWN).intValue())
            .orElse(0) : 0;
    }

    private static Vedtaksperiode mapFørstePeriode(TilkjentYtelsePeriodeDto tilkjentYtelsePeriode,
                                                   List<TilkjentYtelsePeriodeDto> tilkjentPeriodeListe,
                                                   Uttaksperiode uttakResultatPeriode,
                                                   BeregningsgrunnlagPeriodeDto beregningsgrunnlagPeriode,
                                                   Språkkode språkkode,
                                                   UnaryOperator<String> hentNavn) {
        if (uttakResultatPeriode.fom().isBefore(tilkjentYtelsePeriode.fom())) {
            return mapEnkelPeriode(tilkjentYtelsePeriode, tilkjentPeriodeListe, uttakResultatPeriode, beregningsgrunnlagPeriode,
                uttakResultatPeriode.fom(), språkkode, hentNavn);
        }
        return mapEnkelPeriode(tilkjentYtelsePeriode, tilkjentPeriodeListe, uttakResultatPeriode, beregningsgrunnlagPeriode,
            tilkjentYtelsePeriode.fom(), språkkode, hentNavn);
    }

    private static Vedtaksperiode mapEnkelPeriode(TilkjentYtelsePeriodeDto tilkjentYtelsePeriode,
                                                  List<TilkjentYtelsePeriodeDto> tilkjentPeriodeListe,
                                                  Uttaksperiode uttakResultatPeriode,
                                                  BeregningsgrunnlagPeriodeDto beregningsgrunnlagPeriode,
                                                  LocalDate fomDate,
                                                  Språkkode språkkode,
                                                  UnaryOperator<String> hentNavn) {
        var periodeResultatÅrsak = utledÅrsakskode(uttakResultatPeriode);

        var arbeidsfoholdListe = mapArbeidsforholdliste(tilkjentYtelsePeriode, uttakResultatPeriode, beregningsgrunnlagPeriode, språkkode,
            hentNavn);
        var næring = mapNæring(tilkjentYtelsePeriode, uttakResultatPeriode, beregningsgrunnlagPeriode);
        var annenAktivitetListe = mapAnnenAktivtetListe(tilkjentYtelsePeriode, uttakResultatPeriode);
        //TFP-5200 Hack for å forhindre dobbelt antall tapte dager om det er flere tilkjentperioder for en uttaksperiode
        var antallTilkjentPerioder = PeriodeBeregner.finnAntallTilkjentePerioderForUttaksperioden(tilkjentPeriodeListe, uttakResultatPeriode);
        var tapteDagerForUttaksperioden = mapAntallTapteDagerFra(uttakResultatPeriode.aktiviteter());
        var tapteDagerHvisFlereTilkjentPerioder = BigDecimal.ZERO;
        if (antallTilkjentPerioder > 1) {
            tapteDagerHvisFlereTilkjentPerioder = BigDecimal.valueOf(tapteDagerForUttaksperioden)
                .divide(BigDecimal.valueOf(antallTilkjentPerioder), 2, RoundingMode.HALF_UP);
        }

        var vedtaksperioder = Vedtaksperiode.ny()
            .medAntallTapteDager(tapteDagerForUttaksperioden, tapteDagerHvisFlereTilkjentPerioder)
            .medInnvilget(uttakResultatPeriode.isInnvilget() && !erGraderingAvslått(uttakResultatPeriode))
            .medÅrsak(Årsak.of(periodeResultatÅrsak))
            .medStønadskontoType(hentStønadskontoType(uttakResultatPeriode))
            .medPeriodeFom(fomDate, språkkode)
            .medPeriodeTom(tilkjentYtelsePeriode.tom(), språkkode)
            .medArbeidsforhold(arbeidsfoholdListe)
            .medNæring(næring)
            .medAnnenAktivitet(annenAktivitetListe)
            .medPrioritertUtbetalingsgrad(finnPrioritertUtbetalingsgrad(arbeidsfoholdListe, næring, annenAktivitetListe))
            .medFullUtbetaling(erFullUtbetaling(uttakResultatPeriode))
            .medTidligstMottattDato(uttakResultatPeriode.tidligstMottattDato(), språkkode)
            .medErUtbetalingRedusertTilMorsStillingsprosent(uttakResultatPeriode.erUtbetalingRedusertTilMorsStillingsprosent());

        if (tilkjentYtelsePeriode.dagsats() != null) {
            vedtaksperioder.medPeriodeDagsats(tilkjentYtelsePeriode.dagsats());
        }

        return vedtaksperioder.build();
    }

    private static boolean erFullUtbetaling(Uttaksperiode uttakResultatPeriode) {
        return uttakResultatPeriode.aktiviteter().stream().allMatch(a -> Prosent.of(a.utbetalingsgrad()).erFull());
    }

    private static StønadskontoType hentStønadskontoType(Uttaksperiode uttakResultatPeriode) {
        return uttakResultatPeriode.aktiviteter()
            .stream()
            .findFirst()
            .map(Aktivitet::trekkontoType)
            .map(KodeverkMapper::mapStønadskontoType)
            .orElse(StønadskontoType.UDEFINERT);
    }

    private static String utledÅrsakskode(Uttaksperiode uttakPeriode) {
        if (erGraderingAvslått(uttakPeriode) && uttakPeriode.isInnvilget()) {
            return uttakPeriode.graderingAvslagÅrsak();
        } else if (uttakPeriode.periodeResultatÅrsak() != null) {
            return uttakPeriode.periodeResultatÅrsak();
        }
        return PeriodeResultatÅrsak.UKJENT.getKode();
    }

    private static boolean erGraderingAvslått(Uttaksperiode uttakPeriode) {
        return uttakPeriode.graderingAvslagÅrsak() != null && !uttakPeriode.graderingAvslagÅrsak().equals(Fpsak.STANDARDKODE_UDEFINERT);
    }

    private static List<AnnenAktivitet> mapAnnenAktivtetListe(TilkjentYtelsePeriodeDto tilkjentYtelsePeriode, Uttaksperiode uttakPeriode) {
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
            .medAktivitetStatus(map(tilkjentYtelseAndelMedTilhørendeUttaksaktivitet.andel.aktivitetstatus()));
        tilkjentYtelseAndelMedTilhørendeUttaksaktivitet.UttakAktivitet.ifPresent(uttakAktivitet -> {
            annenAktivitetBuilder.medGradering(uttakAktivitet.gradering());
            annenAktivitetBuilder.medUtbetalingsgrad(Prosent.of(uttakAktivitet.utbetalingsgrad()));
            annenAktivitetBuilder.medProsentArbeid(Prosent.of(uttakAktivitet.prosentArbeid()));
            annenAktivitetBuilder.medAktivitetDagsats(summerDagsats(tilkjentYtelseAndelMedTilhørendeUttaksaktivitet.andel));
        });
        return annenAktivitetBuilder.build();
    }

    private static AktivitetStatus map(Aktivitetstatus aktivitetstatus) {
        return switch (aktivitetstatus) {
            case ARBEIDSAVKLARINGSPENGER -> AktivitetStatus.ARBEIDSAVKLARINGSPENGER;
            case ARBEIDSTAKER -> AktivitetStatus.ARBEIDSTAKER;
            case DAGPENGER -> AktivitetStatus.DAGPENGER;
            case FRILANSER -> AktivitetStatus.FRILANSER;
            case MILITÆR_ELLER_SIVIL -> AktivitetStatus.MILITÆR_ELLER_SIVIL;
            case SELVSTENDIG_NÆRINGSDRIVENDE -> AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE;
            case KOMBINERT_AT_FL -> AktivitetStatus.KOMBINERT_AT_FL;
            case KOMBINERT_AT_SN -> AktivitetStatus.KOMBINERT_AT_SN;
            case KOMBINERT_FL_SN -> AktivitetStatus.KOMBINERT_FL_SN;
            case KOMBINERT_AT_FL_SN -> AktivitetStatus.KOMBINERT_AT_FL_SN;
            case BRUKERS_ANDEL -> AktivitetStatus.BRUKERS_ANDEL;
            case KUN_YTELSE -> AktivitetStatus.KUN_YTELSE;
            case TTLSTØTENDE_YTELSE -> AktivitetStatus.TTLSTØTENDE_YTELSE;
            case VENTELØNN_VARTPENGER -> AktivitetStatus.VENTELØNN_VARTPENGER;
            case UDEFINERT -> throw new IllegalStateException("Kan ikke mappe UDEFINERT aktivitetstatus");
        };
    }

    private static Stream<BeregningsresOgUttaksAndel> finnAndelerOgUttakAnnenAktivitet(TilkjentYtelsePeriodeDto tilkjentYtelsePeriode,
                                                                                       Uttaksperiode uttakPeriode) {
        return tilkjentYtelsePeriode.andeler()
            .stream()
            .filter(Predicate.not(andel -> Aktivitetstatus.SELVSTENDIG_NÆRINGSDRIVENDE.equals(andel.aktivitetstatus())))
            .filter(Predicate.not(andel -> Aktivitetstatus.ARBEIDSTAKER.equals(andel.aktivitetstatus())))
            .map(andel -> matchTilkjentYtelseAndelMedUttaksaktivitet(andel, uttakPeriode));
    }

    private static BeregningsresOgUttaksAndel matchTilkjentYtelseAndelMedUttaksaktivitet(TilkjentYtelseAndelDto tilkjentYtelseAndel,
                                                                                         Uttaksperiode uttakPeriode) {
        return new BeregningsresOgUttaksAndel(tilkjentYtelseAndel,
            PeriodeBeregner.finnAktivitetMedStatusHvisFinnes(uttakPeriode.aktiviteter(), tilkjentYtelseAndel));
    }

    private record BeregningsresOgUttaksAndel(TilkjentYtelseAndelDto andel, Optional<Aktivitet> UttakAktivitet) {
    }

    private static Næring mapNæring(TilkjentYtelsePeriodeDto tilkjentYtelsePeriode,
                                    Uttaksperiode uttakResultatPeriode,
                                    BeregningsgrunnlagPeriodeDto beregningsgrunnlagPeriode) {
        return finnNæringsandeler(tilkjentYtelsePeriode).stream()
            .map(andel -> {
                var beregningsgrunnlagAndelDto = PeriodeBeregner.finnBgPerStatusOgAndelHvisFinnes(
                    beregningsgrunnlagPeriode.beregningsgrunnlagandeler(), andel);
                var aktivitet = PeriodeBeregner.finnAktivitetMedStatusHvisFinnes(uttakResultatPeriode.aktiviteter(), andel);
                return mapNæringsandel(aktivitet, beregningsgrunnlagAndelDto);
            })
            .findFirst()
            .orElse(null);
    }

    private static Næring mapNæringsandel(Optional<Aktivitet> uttakAktivitet,
                                          Optional<BeregningsgrunnlagAndelDto> beregningsgrunnlagAndel) {
        var næringBuilder = Næring.ny();

        uttakAktivitet.ifPresent(u -> {
            næringBuilder.medUtbetalingsgrad(Prosent.of(u.utbetalingsgrad()));
            næringBuilder.medProsentArbeid(Prosent.of(u.prosentArbeid()));
            næringBuilder.medGradering(u.gradering());
        });
        beregningsgrunnlagAndel.ifPresent(bgAndel -> {
            if (bgAndel.beregningsperiodeTom() != null) {
                næringBuilder.medSistLignedeÅr(bgAndel.beregningsperiodeTom().getYear());
            }
            næringBuilder.medAktivitetDagsats(bgAndel.dagsats().intValue());
        });
        return næringBuilder.build();
    }

    private static List<TilkjentYtelseAndelDto> finnNæringsandeler(TilkjentYtelsePeriodeDto tilkjentYtelsePeriode) {
        return tilkjentYtelsePeriode.andeler()
            .stream()
            .filter(andel -> Aktivitetstatus.SELVSTENDIG_NÆRINGSDRIVENDE.equals(andel.aktivitetstatus()))
            .toList();
    }

    private static List<Arbeidsforhold> mapArbeidsforholdliste(TilkjentYtelsePeriodeDto tilkjentYtelsePeriode,
                                                               Uttaksperiode uttakResultatPeriode,
                                                               BeregningsgrunnlagPeriodeDto beregningsgrunnlagPeriode,
                                                               Språkkode språkkode,
                                                               UnaryOperator<String> hentNavn) {
        List<Arbeidsforhold> arbeidsforholdListe = new ArrayList<>();
        for (var andel : finnArbeidsandeler(tilkjentYtelsePeriode)) {
            arbeidsforholdListe.add(mapArbeidsforholdAndel(tilkjentYtelsePeriode, andel,
                PeriodeBeregner.finnAktivitetMedStatusHvisFinnes(uttakResultatPeriode.aktiviteter(), andel),
                PeriodeBeregner.finnBgPerStatusOgAndelHvisFinnes(beregningsgrunnlagPeriode.beregningsgrunnlagandeler(), andel),
                beregningsgrunnlagPeriode, språkkode, hentNavn));
        }
        // - for reverse order:
        var arbeidsforholdTypeIsGraderingComparator = (Comparator<Arbeidsforhold>) (a, b) -> -Boolean.compare(a.isGradering(), b.isGradering());
        arbeidsforholdListe.sort(arbeidsforholdTypeIsGraderingComparator);
        return arbeidsforholdListe;
    }

    private static List<TilkjentYtelseAndelDto> finnArbeidsandeler(TilkjentYtelsePeriodeDto tilkjentYtelsePeriode) {
        return tilkjentYtelsePeriode.andeler()
            .stream()
            .filter(andel -> Aktivitetstatus.ARBEIDSTAKER.equals(andel.aktivitetstatus()))
            .toList();
    }

    private static Arbeidsforhold mapArbeidsforholdAndel(TilkjentYtelsePeriodeDto tilkjentYtelsePeriode,
                                                         TilkjentYtelseAndelDto tilkjentYtelseAndel,
                                                         Optional<Aktivitet> uttakAktivitet,
                                                         Optional<BeregningsgrunnlagAndelDto> beregningsgrunnlagAndel,
                                                         BeregningsgrunnlagPeriodeDto beregningsgrunnlagPeriode,
                                                         Språkkode språkkode,
                                                         UnaryOperator<String> hentNavn) {
        var arbeidsforhold = Arbeidsforhold.ny().medArbeidsgiverNavn((Optional.ofNullable(tilkjentYtelseAndel.arbeidsgiverReferanse()).map(
            hentNavn).orElse("Andel")));
        arbeidsforhold.medAktivitetDagsats(summerDagsats(tilkjentYtelseAndel));
        if (uttakAktivitet.isPresent()) {
            arbeidsforhold.medProsentArbeid(Prosent.of(uttakAktivitet.get().prosentArbeid()));
            arbeidsforhold.medUtbetalingsgrad(Prosent.of(uttakAktivitet.get().utbetalingsgrad()));
            arbeidsforhold.medGradering(uttakAktivitet.get().gradering());
        }
        arbeidsforhold.medStillingsprosent(Prosent.of(tilkjentYtelseAndel.stillingsprosent()));
        beregningsgrunnlagAndel.ifPresent(bgAndel -> {
            final var statusOgAndel = beregningsgrunnlagAndel.get();
            Optional.ofNullable(statusOgAndel.arbeidsforhold()).ifPresent(bgAndelArbeidsforhold -> {
                if (bgAndelArbeidsforhold.naturalytelseBortfaltPrÅr() != null || bgAndelArbeidsforhold.naturalytelseTilkommetPrÅr() != null) {
                    mapNaturalytelse(arbeidsforhold, beregningsgrunnlagPeriode, tilkjentYtelsePeriode, språkkode);
                }
            });
        });
        return arbeidsforhold.build();
    }

    private static int summerDagsats(TilkjentYtelseAndelDto dto) {
        var sum = 0;
        if (dto.tilSoker() != null) {
            sum += dto.tilSoker();
        }
        if (dto.refusjon() != null) {
            sum += dto.refusjon();
        }
        return sum;
    }

    private static void mapNaturalytelse(Arbeidsforhold.Builder arbeidsforholdBuilder,
                                         BeregningsgrunnlagPeriodeDto beregningsgrunnlagPeriode,
                                         TilkjentYtelsePeriodeDto tilkjentYtelsePeriode,
                                         Språkkode språkkode) {
        for (var årsak : beregningsgrunnlagPeriode.periodeårsaker()) {
            if (PeriodeÅrsakDto.NATURALYTELSE_BORTFALT.equals(årsak)) {
                arbeidsforholdBuilder.medNaturalytelseEndringType(NaturalytelseEndringType.STOPP);
                arbeidsforholdBuilder.medNaturalytelseEndringDato(formaterDato(tilkjentYtelsePeriode.fom(), språkkode));
                arbeidsforholdBuilder.medBruttoInkludertBortfaltNaturalytelsePrAar(beregningsgrunnlagPeriode.avkortetPrÅr().longValue());
            } else if (PeriodeÅrsakDto.NATURALYTELSE_TILKOMMER.equals(årsak)) {
                arbeidsforholdBuilder.medNaturalytelseEndringType((NaturalytelseEndringType.START));
                arbeidsforholdBuilder.medNaturalytelseEndringDato(formaterDato(tilkjentYtelsePeriode.fom(), språkkode));
                arbeidsforholdBuilder.medBruttoInkludertBortfaltNaturalytelsePrAar(beregningsgrunnlagPeriode.avkortetPrÅr().longValue());
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

