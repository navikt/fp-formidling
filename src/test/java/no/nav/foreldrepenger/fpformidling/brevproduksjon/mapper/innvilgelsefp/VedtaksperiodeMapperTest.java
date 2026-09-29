package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp;

import static java.util.List.of;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.VedtaksperiodeMapper.finnAntallAvslåttePerioder;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.VedtaksperiodeMapper.finnAntallInnvilgedePerioder;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.VedtaksperiodeMapper.finnPrioritertUtbetalingsgrad;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.VedtaksperiodeMapper.finnStønadsperiodeFom;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.VedtaksperiodeMapper.finnStønadsperiodeTom;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.VedtaksperiodeMapper.finnesPeriodeMedIkkeOmsorg;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.Foreldrepenger;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.PeriodeResultatType;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.UttakArbeidType;
import static no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseDagytelseDto.Aktivitetstatus;
import static no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseDagytelseDto.TilkjentYtelseAndelDto;
import static no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseDagytelseDto.TilkjentYtelsePeriodeDto;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Prosent;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Årsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.AnnenAktivitet;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.Arbeidsforhold;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.Næring;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.Vedtaksperiode;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders;
import no.nav.foreldrepenger.fpformidling.typer.DatoIntervall;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagAndelDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.AktivitetStatusDto;

class VedtaksperiodeMapperTest {

    public static final UnaryOperator<String> HENT_NAVN = _ -> "Navn";

    @Test
    void skal_hente_dato_fra_uttaksperiode_når_denne_er_før_tilkjentytelseperioden_og_det_er_første_tilkjentYtelsePeriode() {
        // Arrange
        var tidsperiodeTilkjent1 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 9, 30), LocalDate.of(2019, 10, 2));
        var tidsperiodeTilkjent2 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 10, 3), LocalDate.of(2019, 10, 4));
        var tidsperiodeTilkjent3 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 10, 7), LocalDate.of(2019, 12, 31));
        var tidsperiodeUp1 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 7, 3), LocalDate.of(2019, 10, 4));
        var tidsperiodeUp2 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 10, 7), LocalDate.of(2019, 12, 31));
        var beregningPer = DatoIntervall.fraOgMed(LocalDate.of(2019, 9, 30));

        var tyPeriode = new TilkjentYtelsePeriodeDto(tidsperiodeTilkjent1.getFomDato(), tidsperiodeTilkjent1.getTomDato(), 0, List.of());
        var tyPeriode2 = new TilkjentYtelsePeriodeDto(tidsperiodeTilkjent2.getFomDato(), tidsperiodeTilkjent2.getTomDato(), 0, List.of());
        var tyPeriode3 = new TilkjentYtelsePeriodeDto(tidsperiodeTilkjent3.getFomDato(), tidsperiodeTilkjent3.getTomDato(), 620, List.of());
        var tilkjentYtelsePerioder = of(tyPeriode, tyPeriode2, tyPeriode3);

        var bgPeriode = new BeregningsgrunnlagPeriodeDto(620L, null, null, List.of(), beregningPer.getFomDato(), beregningPer.getTomDato(), List.of());
        var beregningsgrunnlagPerioder = of(bgPeriode);

        var uttakAktivitet = new Foreldrepenger.Aktivitet(Foreldrepenger.TrekkontoType.FELLESPERIODE, BigDecimal.TEN,
            null, null, null, BigDecimal.ZERO, UttakArbeidType.FRILANS, false);
        var uPeriode = new Foreldrepenger.Uttaksperiode(tidsperiodeUp1.getFomDato(), tidsperiodeUp1.getTomDato(), of(uttakAktivitet),
            PeriodeResultatType.AVSLÅTT, PeriodeResultatÅrsak.HULL_MELLOM_FORELDRENES_PERIODER.getKode(), null,
            tidsperiodeTilkjent1.getFomDato().minusWeeks(1), false, Set.of("14-10"));

        var uttakAktivitet2 = new Foreldrepenger.Aktivitet(Foreldrepenger.TrekkontoType.FELLESPERIODE, BigDecimal.ZERO,
            null, null, null, BigDecimal.valueOf(100), UttakArbeidType.FRILANS, false);
        var uPeriode2 = new Foreldrepenger.Uttaksperiode(tidsperiodeUp2.getFomDato(), tidsperiodeUp2.getTomDato(), of(uttakAktivitet2),
            PeriodeResultatType.INNVILGET, PeriodeResultatÅrsak.OVERFORING_KVOTE_GYLDIG_KUN_FAR_HAR_RETT.getKode(), null,
            tidsperiodeTilkjent1.getFomDato().minusWeeks(1), false, Set.of("14-10"));
        var uttaksPerioder = foreldrepenger(of(uPeriode, uPeriode2), of());

        // Act
        var resultat = VedtaksperiodeMapper.mapVedtaksperioder(tilkjentYtelsePerioder, uttaksPerioder, beregningsgrunnlagPerioder,
            Språkkode.NB, HENT_NAVN);

        // Assert
        assertThat(resultat).hasSize(2);

        assertThat(resultat.getFirst().getPeriodeFom()).isEqualTo(uPeriode.fom());
        assertThat(resultat.getFirst().getPeriodeTom()).isEqualTo(uPeriode.tom());
        assertThat(resultat.getFirst().isInnvilget()).isFalse();
        assertThat(resultat.get(0).getPeriodeDagsats()).isZero();
        assertThat(resultat.get(0).getÅrsak().getKode()).isEqualTo(PeriodeResultatÅrsak.HULL_MELLOM_FORELDRENES_PERIODER.getKode());

        assertThat(resultat.get(1).getPeriodeFom()).isEqualTo(tyPeriode3.fom());
        assertThat(resultat.get(1).getPeriodeTom()).isEqualTo(tyPeriode3.tom());
        assertThat(resultat.get(1).isInnvilget()).isTrue();
        assertThat(resultat.get(1).getPeriodeDagsats()).isEqualTo(620L);
        assertThat(resultat.get(1).getÅrsak().getKode()).isEqualTo(PeriodeResultatÅrsak.OVERFORING_KVOTE_GYLDIG_KUN_FAR_HAR_RETT.getKode());
    }

    @Test
    void verifisere_at_resultatet_blir_det_samme_om_periodene_ikke_er_sortert() {
        // Arrange
        var tidsperiodeTilkjent1 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 9, 30), LocalDate.of(2019, 10, 2));
        var tidsperiodeTilkjent2 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 10, 3), LocalDate.of(2019, 10, 4));
        var tidsperiodeTilkjent3 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 10, 7), LocalDate.of(2019, 12, 31));
        var tidsperiodeUp1 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 7, 3), LocalDate.of(2019, 10, 4));
        var tidsperiodeUp2 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 10, 7), LocalDate.of(2019, 12, 31));
        var beregningPer = DatoIntervall.fraOgMed(LocalDate.of(2019, 9, 30));

        var tyPeriode = new TilkjentYtelsePeriodeDto(tidsperiodeTilkjent1.getFomDato(), tidsperiodeTilkjent1.getTomDato(), 0, List.of());
        var tyPeriode2 = new TilkjentYtelsePeriodeDto(tidsperiodeTilkjent2.getFomDato(), tidsperiodeTilkjent2.getTomDato(), 0, List.of());
        var tyPeriode3 = new TilkjentYtelsePeriodeDto(tidsperiodeTilkjent3.getFomDato(), tidsperiodeTilkjent3.getTomDato(), 620, List.of());
        var tilkjentYtelsePerioder = of(tyPeriode3, tyPeriode2, tyPeriode);

        var bgPeriode = new BeregningsgrunnlagPeriodeDto(620L, null, null, List.of(), beregningPer.getFomDato(), beregningPer.getTomDato(), List.of());
        var beregningsgrunnlagPerioder = of(bgPeriode);

        var uttakAktivitet = new Foreldrepenger.Aktivitet(Foreldrepenger.TrekkontoType.FELLESPERIODE, BigDecimal.TEN,
            null, null, null, BigDecimal.ZERO, UttakArbeidType.FRILANS, false);
        var uPeriode = new Foreldrepenger.Uttaksperiode(tidsperiodeUp1.getFomDato(), tidsperiodeUp1.getTomDato(), of(uttakAktivitet),
            PeriodeResultatType.AVSLÅTT, PeriodeResultatÅrsak.HULL_MELLOM_FORELDRENES_PERIODER.getKode(), null,
            tidsperiodeTilkjent1.getFomDato().minusWeeks(1), false, Set.of("14-10"));

        var uttakAktivitet2 = new Foreldrepenger.Aktivitet(Foreldrepenger.TrekkontoType.FELLESPERIODE, BigDecimal.ZERO,
            null, null, null, BigDecimal.valueOf(100), UttakArbeidType.FRILANS, false);
        var uPeriode2 = new Foreldrepenger.Uttaksperiode(tidsperiodeUp2.getFomDato(), tidsperiodeUp2.getTomDato(), of(uttakAktivitet2),
            PeriodeResultatType.INNVILGET, PeriodeResultatÅrsak.OVERFORING_KVOTE_GYLDIG_KUN_FAR_HAR_RETT.getKode(), null,
            tidsperiodeTilkjent1.getFomDato().minusWeeks(1), false, Set.of("14-10"));
        var foreldrepenger = foreldrepenger(of(uPeriode2, uPeriode), of());

        // Act
        var resultat = VedtaksperiodeMapper.mapVedtaksperioder(tilkjentYtelsePerioder, foreldrepenger, beregningsgrunnlagPerioder,
            Språkkode.NB, HENT_NAVN);

        // Assert
        assertThat(resultat).hasSize(2);
        assertThat(resultat.get(0).getPeriodeFom()).isEqualTo(uPeriode.fom());
        assertThat(resultat.get(1).getPeriodeFom()).isEqualTo(tyPeriode3.fom());
    }

    private Foreldrepenger foreldrepenger(List<Foreldrepenger.Uttaksperiode> søkersPerioder, List<Foreldrepenger.Uttaksperiode> annenPartPerioder) {
        return BrevGrunnlagBuilders.foreldrepenger().perioderSøker(søkersPerioder).perioderAnnenpart(annenPartPerioder).build();
    }

    @Test
    void skal_sortere_perioder_uten_beregningsgrunnlag_også() {
        // Arrange
        var tidsperiodeTilkjent1 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 10, 7), LocalDate.of(2019, 12, 31));
        var tidsperiodeUp1 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 10, 7), LocalDate.of(2019, 12, 31));
        var tidsperiodeUp2 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 6, 20), LocalDate.of(2019, 6, 29)); // Skal sorteres først
        var beregningPer = DatoIntervall.fraOgMed(LocalDate.of(2019, 9, 30));

        var tyPeriode1 = new TilkjentYtelsePeriodeDto(tidsperiodeTilkjent1.getFomDato(), tidsperiodeTilkjent1.getTomDato(), 620, List.of());
        var tilkjentYtelsePerioder = of(tyPeriode1);

        var bgPeriode = new BeregningsgrunnlagPeriodeDto(620L, null, null, List.of(), beregningPer.getFomDato(), beregningPer.getTomDato(), List.of());
        var beregningsgrunnlagPerioder = of(bgPeriode);

        var uttakAktivitet1 = new Foreldrepenger.Aktivitet(Foreldrepenger.TrekkontoType.FELLESPERIODE, BigDecimal.ZERO,
            null, null, null, BigDecimal.valueOf(100), UttakArbeidType.FRILANS, false);
        var uPeriode1 = new Foreldrepenger.Uttaksperiode(tidsperiodeUp1.getFomDato(), tidsperiodeUp1.getTomDato(), of(uttakAktivitet1),
            PeriodeResultatType.INNVILGET, PeriodeResultatÅrsak.OVERFORING_KVOTE_GYLDIG_KUN_FAR_HAR_RETT.getKode(), null,
            tidsperiodeTilkjent1.getFomDato().minusWeeks(1), false, Set.of("14-10"));

        var uttakAktivitet2 = new Foreldrepenger.Aktivitet(Foreldrepenger.TrekkontoType.FELLESPERIODE, BigDecimal.TEN,
            null, null, null, BigDecimal.ZERO, UttakArbeidType.FRILANS, false);
        var uPeriode2 = new Foreldrepenger.Uttaksperiode(tidsperiodeUp2.getFomDato(), tidsperiodeUp2.getTomDato(), of(uttakAktivitet2),
            PeriodeResultatType.AVSLÅTT, PeriodeResultatÅrsak.MOR_HAR_IKKE_OMSORG.getKode(), null,
            tidsperiodeTilkjent1.getFomDato().minusWeeks(1), false, Set.of("14-10"));

        var uttaksPerioder = foreldrepenger(of(uPeriode1, uPeriode2), of());

        // Act
        var resultat = VedtaksperiodeMapper.mapVedtaksperioder(tilkjentYtelsePerioder, uttaksPerioder, beregningsgrunnlagPerioder,
            Språkkode.NB, HENT_NAVN);

        // Assert
        assertThat(resultat).hasSize(2);

        assertThat(resultat.getFirst().getPeriodeFom()).isEqualTo(uPeriode2.fom());
        assertThat(resultat.getFirst().getPeriodeTom()).isEqualTo(uPeriode2.tom());
        assertThat(resultat.getFirst().isInnvilget()).isFalse();
        assertThat(resultat.get(0).getPeriodeDagsats()).isZero();
        assertThat(resultat.get(0).getÅrsak().getKode()).isEqualTo(PeriodeResultatÅrsak.MOR_HAR_IKKE_OMSORG.getKode());

        assertThat(resultat.get(1).getPeriodeFom()).isEqualTo(tyPeriode1.fom());
        assertThat(resultat.get(1).getPeriodeTom()).isEqualTo(tyPeriode1.tom());
        assertThat(resultat.get(1).isInnvilget()).isTrue();
        assertThat(resultat.get(1).getPeriodeDagsats()).isEqualTo(620L);
        assertThat(resultat.get(1).getÅrsak().getKode()).isEqualTo(PeriodeResultatÅrsak.OVERFORING_KVOTE_GYLDIG_KUN_FAR_HAR_RETT.getKode());
    }

    @Test
    void skal_mappe_annenAktitetListe_når_den_inneholder_annet() {
        // Arrange
        var tidsperiodeTilkjent1 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 9, 30), LocalDate.of(2019, 10, 2));
        var tidsperiodeUp1 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 7, 3), LocalDate.of(2019, 10, 4));
        var beregningPer = DatoIntervall.fraOgMed(LocalDate.of(2019, 9, 30));

        var tilkjentYtelseAndel = new TilkjentYtelseAndelDto(null, 0, 0, Aktivitetstatus.KOMBINERT_AT_FL, null, null, null);

        var tyPeriode = new TilkjentYtelsePeriodeDto(tidsperiodeTilkjent1.getFomDato(), tidsperiodeTilkjent1.getTomDato(), 0, List.of(tilkjentYtelseAndel));
        var tilkjentYtelsePerioder = of(tyPeriode);

        var bgPeriode = new BeregningsgrunnlagPeriodeDto(620L, null, null, List.of(), beregningPer.getFomDato(), beregningPer.getTomDato(), List.of(new BeregningsgrunnlagAndelDto(620L,
            AktivitetStatusDto.KOMBINERT_AT_FL, null, null, false, null, tidsperiodeTilkjent1.getFomDato(), tidsperiodeTilkjent1.getTomDato(), null, false)));
        var beregningsgrunnlagPerioder = of(bgPeriode);

        var uttakResultatPeriodeAktivitet = new Foreldrepenger.Aktivitet(Foreldrepenger.TrekkontoType.FELLESPERIODE, BigDecimal.TEN,
            BigDecimal.valueOf(20), null, null, BigDecimal.ZERO, UttakArbeidType.ANNET, false);
        var uttakResultatPeriodeAktivitet2 = new Foreldrepenger.Aktivitet(Foreldrepenger.TrekkontoType.FELLESPERIODE, BigDecimal.TEN,
            null, null, null, BigDecimal.ZERO, UttakArbeidType.ANNET, false);


        var uPeriode = new Foreldrepenger.Uttaksperiode(tidsperiodeUp1.getFomDato(), tidsperiodeUp1.getTomDato(), of(uttakResultatPeriodeAktivitet, uttakResultatPeriodeAktivitet2),
            PeriodeResultatType.INNVILGET, PeriodeResultatÅrsak.UTSETTELSE_GYLDIG_PGA_ARBEID_KUN_FAR_HAR_RETT.getKode(), null,
            tidsperiodeTilkjent1.getFomDato().minusWeeks(1), false, Set.of("14-10"));

        var uttaksPerioder = foreldrepenger(of(uPeriode), of());

        // Act
        var resultat = VedtaksperiodeMapper.mapVedtaksperioder(tilkjentYtelsePerioder, uttaksPerioder, beregningsgrunnlagPerioder,
            Språkkode.NB, HENT_NAVN);

        // Assert
        assertThat(resultat).hasSize(1);

        assertThat(resultat.getFirst().isInnvilget()).isTrue();
        assertThat(resultat.getFirst().getPeriodeDagsats()).isZero();
        assertThat(resultat.getFirst().getÅrsak().getKode()).isEqualTo(PeriodeResultatÅrsak.UTSETTELSE_GYLDIG_PGA_ARBEID_KUN_FAR_HAR_RETT.getKode());
        assertThat(resultat.getFirst().getAnnenAktivitetsliste()).hasSize(1);
    }

    @Test
    void skal_ignorere_avslåtte_manglende_søkte_perioder_med_null_trekkdager_ved_mapping_av_periodeliste() {
        // Arrange
        var tidsperiode = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 9, 16), LocalDate.of(2019, 9, 16));

        var tyPeriode = new TilkjentYtelsePeriodeDto(tidsperiode.getFomDato(), tidsperiode.getTomDato(), null, List.of());
        var tilkjentYtelsePerioder = of(tyPeriode);

        var uttakAktivitet = new Foreldrepenger.Aktivitet(Foreldrepenger.TrekkontoType.FELLESPERIODE, BigDecimal.ZERO,
            null, null, null, BigDecimal.ZERO, UttakArbeidType.FRILANS, false);
        var uPeriode = new Foreldrepenger.Uttaksperiode(tidsperiode.getFomDato(), tidsperiode.getTomDato(), of(uttakAktivitet),
            PeriodeResultatType.AVSLÅTT, PeriodeResultatÅrsak.HULL_MELLOM_FORELDRENES_PERIODER.getKode(), null,
            tidsperiode.getFomDato().minusWeeks(1), false, Set.of("14-10"));

        var uttaksPerioder = foreldrepenger(of(uPeriode), of());

        var bgPeriode = new BeregningsgrunnlagPeriodeDto(null, null, null, List.of(), tidsperiode.getFomDato(), tidsperiode.getTomDato(), List.of());
        var beregningsgrunnlagPerioder = of(bgPeriode);
        // Act
        var resultat = VedtaksperiodeMapper.mapVedtaksperioder(tilkjentYtelsePerioder, uttaksPerioder, beregningsgrunnlagPerioder,
            Språkkode.NB, HENT_NAVN);

        // Assert
        assertThat(resultat).isEmpty();
    }

    @Test
    void skal_ta_med_avslåtte_manglende_søkte_perioder_med_trekkdager_ved_mapping_av_periodeliste() {
        // Arrange
        var tidsperiode = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 9, 16), LocalDate.of(2019, 9, 16));

        var tyPeriode = new TilkjentYtelsePeriodeDto(tidsperiode.getFomDato(), tidsperiode.getTomDato(), null, List.of());
        var tilkjentYtelsePerioder = of(tyPeriode);

        var uttakAktivitet = new Foreldrepenger.Aktivitet(Foreldrepenger.TrekkontoType.FELLESPERIODE, BigDecimal.TEN,
            null, null, null, BigDecimal.ZERO, UttakArbeidType.FRILANS, false);
        var uPeriode = new Foreldrepenger.Uttaksperiode(tidsperiode.getFomDato(), tidsperiode.getTomDato(), of(uttakAktivitet),
            PeriodeResultatType.AVSLÅTT, PeriodeResultatÅrsak.HULL_MELLOM_FORELDRENES_PERIODER.getKode(), null,
            tidsperiode.getFomDato(), false, Set.of("14-10"));

        var uttaksPerioder = foreldrepenger(of(uPeriode), of());

        var bgPeriode = new BeregningsgrunnlagPeriodeDto(null, null, null, List.of(), tidsperiode.getFomDato(), tidsperiode.getTomDato(), List.of());
        var beregningsgrunnlagPerioder = of(bgPeriode);
        // Act
        var resultat = VedtaksperiodeMapper.mapVedtaksperioder(tilkjentYtelsePerioder, uttaksPerioder, beregningsgrunnlagPerioder,
            Språkkode.NB, HENT_NAVN);

        // Assert
        assertThat(resultat).hasSize(1);
    }

    @Test
    void skal_finne_første_og_siste_stønadsdato_og_håndtere_null() {
        // Arrange
        List<Vedtaksperiode> vedtaksperioder = new ArrayList<>();
        var førsteJanuarTjueAtten = LocalDate.of(2018, 1, 1);
        var trettiendeAprilTjueAtten = LocalDate.of(2018, 4, 30);

        leggtilPeriode(LocalDate.of(2017, 1, 1), LocalDate.of(2017, 1, 30), false, vedtaksperioder);
        leggtilPeriode(førsteJanuarTjueAtten, LocalDate.of(2018, 1, 30), true, vedtaksperioder);
        leggtilPeriode(LocalDate.of(2018, 2, 1), LocalDate.of(2018, 2, 25), true, vedtaksperioder);
        leggtilPeriode(LocalDate.of(2018, 3, 1), LocalDate.of(2018, 3, 30), null, vedtaksperioder);
        leggtilPeriode(LocalDate.of(2018, 4, 1), trettiendeAprilTjueAtten, true, vedtaksperioder);
        leggtilPeriode(LocalDate.of(2019, 3, 1), LocalDate.of(2019, 3, 30), false, vedtaksperioder);

        // Act + Assert
        assertThat(finnStønadsperiodeFom(vedtaksperioder)).isEqualTo(Optional.of(førsteJanuarTjueAtten));
        assertThat(finnStønadsperiodeTom(vedtaksperioder)).isEqualTo(Optional.of(trettiendeAprilTjueAtten));
    }

    @Test
    void skal_finne_antall_innvilgede_perioder() {
        // Arrange
        List<Vedtaksperiode> vedtaksperioder = new ArrayList<>();
        leggtilPeriode(LocalDate.of(2017, 1, 1), LocalDate.of(2017, 1, 30), false, vedtaksperioder);
        leggtilPeriode(LocalDate.of(2018, 2, 1), LocalDate.of(2018, 2, 25), true, vedtaksperioder);
        leggtilPeriode(LocalDate.of(2018, 3, 1), LocalDate.of(2018, 3, 30), true, vedtaksperioder);

        // Act + Assert
        assertThat(finnAntallInnvilgedePerioder(vedtaksperioder)).isEqualTo(2);
    }

    @Test
    void skal_finne_antall_avslåtte_perioder() {
        // Arrange
        List<Vedtaksperiode> vedtaksperioder = new ArrayList<>();
        leggtilPeriode(LocalDate.of(2017, 1, 1), LocalDate.of(2017, 1, 30), false, vedtaksperioder);
        leggtilPeriode(LocalDate.of(2018, 2, 1), LocalDate.of(2018, 2, 25), true, vedtaksperioder);
        leggtilPeriode(LocalDate.of(2018, 3, 1), LocalDate.of(2018, 3, 30), true, vedtaksperioder);

        // Act + Assert
        assertThat(finnAntallAvslåttePerioder(vedtaksperioder)).isEqualTo(1);
    }

    @Test
    void skal_finne_periode_med_ikke_omsorg_mor() {
        // Arrange
        var utbetalingsperiode = Vedtaksperiode.ny()
            .medPeriodeFom(LocalDate.now().minusDays(10), Språkkode.NB)
            .medPeriodeTom(LocalDate.now().plusDays(10), Språkkode.NB)
            .medÅrsak(Årsak.of(PeriodeResultatÅrsak.MOR_HAR_IKKE_OMSORG.getKode()))
            .build();

        // Act + Assert
        assertThat(finnesPeriodeMedIkkeOmsorg(of(utbetalingsperiode))).isTrue();
    }

    @Test
    void skal_finne_periode_med_ikke_omsorg_far() {
        // Arrange
        var utbetalingsperiode = Vedtaksperiode.ny()
            .medPeriodeFom(LocalDate.now().minusDays(10), Språkkode.NB)
            .medPeriodeTom(LocalDate.now().plusDays(10), Språkkode.NB)
            .medÅrsak(Årsak.of(PeriodeResultatÅrsak.FAR_HAR_IKKE_OMSORG.getKode()))
            .build();

        // Act + Assert
        assertThat(finnesPeriodeMedIkkeOmsorg(of(utbetalingsperiode))).isTrue();
    }

    @Test
    void skal_ikke_finne_periode_med_ikke_omsorg() {
        // Arrange
        var utbetalingsperiode = Vedtaksperiode.ny()
            .medPeriodeFom(LocalDate.now().minusDays(10), Språkkode.NB)
            .medPeriodeTom(LocalDate.now().plusDays(10), Språkkode.NB)
            .medÅrsak(Årsak.of(PeriodeResultatÅrsak.FØDSELSVILKÅRET_IKKE_OPPFYLT.getKode()))
            .build();

        // Act + Assert
        assertThat(finnesPeriodeMedIkkeOmsorg(of(utbetalingsperiode))).isFalse();
    }

    @Test
    void skal_hente_prioritert_utbetalingsgrad_fra_arbeidsforholdet_med_gradering_om_finnes() {
        var arbeidsforholdListe = List.of(opprettArbeidsforhold(false, Prosent.of(BigDecimal.valueOf(0)), Prosent.of(BigDecimal.valueOf(80))),
            opprettArbeidsforhold(true, Prosent.of(BigDecimal.valueOf(20)), Prosent.of(BigDecimal.valueOf(100))),
            opprettArbeidsforhold(false, Prosent.of(BigDecimal.valueOf(0.0)), Prosent.of(BigDecimal.valueOf(100))));

        assertThat(finnPrioritertUtbetalingsgrad(arbeidsforholdListe, null, null)).isEqualTo(Prosent.of(BigDecimal.valueOf(20)));
    }

    @Test
    void skal_hente_prioritert_utbetalingsgrad_fra_første_arbeidsfohold_med_utbetalingsgrad() {
        var arbeidsforholdListe = List.of(opprettArbeidsforhold(false, Prosent.of(BigDecimal.valueOf(0.0)), Prosent.of(BigDecimal.valueOf(0.0))),
            opprettArbeidsforhold(false, Prosent.of(BigDecimal.valueOf(20)), Prosent.of(BigDecimal.valueOf(100))),
            opprettArbeidsforhold(false, Prosent.of(BigDecimal.valueOf(0.0)), Prosent.of(BigDecimal.valueOf(0.0))));

        assertThat(finnPrioritertUtbetalingsgrad(arbeidsforholdListe, null, null)).isEqualTo(Prosent.of(BigDecimal.valueOf(20)));
    }

    @Test
    void skal_hente_prioritert_utbetalingsgrad_fra_næring_når_arbeidsforhold_ikke_finnes() {
        var næring = Næring.ny()
            .medSistLignedeÅr(LocalDate.now().getYear())
            .medGradering(false)
            .medUtbetalingsgrad(Prosent.of(BigDecimal.valueOf(100)))
            .medProsentArbeid(Prosent.of(BigDecimal.valueOf(0.0)))
            .build();

        assertThat(finnPrioritertUtbetalingsgrad(null, næring, null)).isEqualTo(Prosent.of(BigDecimal.valueOf(100)));
    }

    @Test
    void skal_hente_prioritert_utbetalingsgrad_fra_annen_aktivitet_når_verken_arbeidsforhold_eller_næring_finnes() {
        var annenAktivitetListe = List.of(opprettAnnenAktivitet(false, Prosent.of(BigDecimal.valueOf(100))));

        assertThat(finnPrioritertUtbetalingsgrad(null, null, annenAktivitetListe)).isEqualTo(Prosent.of(BigDecimal.valueOf(100)));
    }

    @Test
    void skal_hente_prioritert_utbetalingsgrad_fra_aktiviteten_med_gradering_hvis_finnes() {
        var annenAktivitetListe = List.of(opprettAnnenAktivitet(false, Prosent.of(BigDecimal.valueOf(0))),
            opprettAnnenAktivitet(true, Prosent.of(BigDecimal.valueOf(60))));

        assertThat(finnPrioritertUtbetalingsgrad(null, null, annenAktivitetListe)).isEqualTo(Prosent.of(BigDecimal.valueOf(60)));
    }

    @Test
    void skal_hente_prioritert_utbetalingsgrad_fra_første_aktivitet_med_utbetalingsgrad() {
        var annenAktivitetListe = List.of(opprettAnnenAktivitet(false, Prosent.of(BigDecimal.valueOf(0))),
            opprettAnnenAktivitet(false, Prosent.of(BigDecimal.valueOf(100))));

        assertThat(finnPrioritertUtbetalingsgrad(null, null, annenAktivitetListe)).isEqualTo(Prosent.of(BigDecimal.valueOf(100)));
    }


    private Arbeidsforhold opprettArbeidsforhold(boolean gradering, Prosent utbetalingsgrad, Prosent prosentArbeid) {
        return Arbeidsforhold.ny()
            .medNaturalytelseEndringType(null)
            .medGradering(gradering)
            .medUtbetalingsgrad(utbetalingsgrad)
            .medAktivitetDagsats(223)
            .medArbeidsgiverNavn("ARBERIDSGVIER AS")
            .medProsentArbeid(prosentArbeid)
            .medStillingsprosent(Prosent.of(BigDecimal.valueOf(100)))
            .build();
    }

    private AnnenAktivitet opprettAnnenAktivitet(boolean gradering, Prosent utbetalingsgrad) {
        return AnnenAktivitet.ny()
            .medGradering(gradering)
            .medAktivitetStatus(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE)
            .medUtbetalingsgrad(utbetalingsgrad)
            .medProsentArbeid(Prosent.of(BigDecimal.valueOf(0.0)))
            .build();

    }

    private void leggtilPeriode(LocalDate fom, LocalDate tom, Boolean innvilget, List<Vedtaksperiode> vedtaksperioder) {
        vedtaksperioder.add(
            Vedtaksperiode.ny().medPeriodeFom(fom, Språkkode.NB).medPeriodeTom(tom, Språkkode.NB).medInnvilget(innvilget).build());
    }
}
