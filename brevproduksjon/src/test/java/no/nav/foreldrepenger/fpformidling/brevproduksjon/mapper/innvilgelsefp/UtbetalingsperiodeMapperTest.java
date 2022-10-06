package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp;

import static java.util.List.of;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.UtbetalingsperiodeMapper.finnAntallAvslåttePerioder;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.UtbetalingsperiodeMapper.finnAntallInnvilgedePerioder;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.UtbetalingsperiodeMapper.finnAntallPerioder;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.UtbetalingsperiodeMapper.finnPrioritertUtbetalingsgrad;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.UtbetalingsperiodeMapper.finnStønadsperiodeFom;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.UtbetalingsperiodeMapper.finnStønadsperiodeTom;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.UtbetalingsperiodeMapper.finnesPeriodeMedIkkeOmsorg;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.BeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndel;
import no.nav.foreldrepenger.fpformidling.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Prosent;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Årsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.AnnenAktivitet;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.Arbeidsforhold;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.Næring;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.Utbetalingsperiode;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelseAndel;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelsePeriode;
import no.nav.foreldrepenger.fpformidling.typer.DatoIntervall;
import no.nav.foreldrepenger.fpformidling.uttak.ForeldrepengerUttak;
import no.nav.foreldrepenger.fpformidling.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.fpformidling.uttak.UttakAktivitet;
import no.nav.foreldrepenger.fpformidling.uttak.UttakArbeidType;
import no.nav.foreldrepenger.fpformidling.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.fpformidling.uttak.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.fpformidling.uttak.kodeliste.PeriodeResultatÅrsak;

public class UtbetalingsperiodeMapperTest {

    @Test
    void skal_hente_dato_fra_uttaksperiode_når_denne_er_før_tilkjentytelseperioden_og_det_er_første_tilkjentYtelsePeriode() {
        // Arrange
        var tidsperiodeBp1 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 9, 30), LocalDate.of(2019, 10, 2));
        var tidsperiodeBp2 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 10, 3), LocalDate.of(2019, 10, 4));
        var tidsperiodeBp3 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 10, 7), LocalDate.of(2019, 12, 31));
        var tidsperiodeUp1 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 7, 3), LocalDate.of(2019, 10, 4));
        var tidsperiodeUp2 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 10, 7), LocalDate.of(2019, 12, 31));
        var beregningPer = DatoIntervall.fraOgMed(LocalDate.of(2019, 9, 30));

        var tyPeriode = TilkjentYtelsePeriode.ny()
                .medPeriode(tidsperiodeBp1)
                .medDagsats(0L)
                .build();
        var tyPeriode2 = TilkjentYtelsePeriode.ny()
                .medPeriode(tidsperiodeBp2)
                .medDagsats(0L)
                .build();
        var tyPeriode3 = TilkjentYtelsePeriode.ny()
                .medPeriode(tidsperiodeBp3)
                .medDagsats(620L)
                .build();
        var tilkjentYtelsePerioder = of(tyPeriode, tyPeriode2, tyPeriode3);

        var bgPeriode = BeregningsgrunnlagPeriode.ny()
                .medPeriode(beregningPer)
                .medDagsats(620L)
                .build();
        var beregningsgrunnlagPerioder = of(bgPeriode);

        var uttakAktivitet = UttakResultatPeriodeAktivitet.ny()
                .medTrekkdager(BigDecimal.TEN)
                .medUtbetalingsprosent(BigDecimal.ZERO)
                .build();
        var uPeriode = UttakResultatPeriode.ny()
                .medAktiviteter(of(uttakAktivitet))
                .medTidsperiode(tidsperiodeUp1)
                .medPeriodeResultatÅrsak(PeriodeResultatÅrsak.HULL_MELLOM_FORELDRENES_PERIODER)
                .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
                .build();
        var uttakAktivitet2 = UttakResultatPeriodeAktivitet.ny()
                .medTrekkdager(BigDecimal.ZERO)
                .medUtbetalingsprosent(BigDecimal.valueOf(100L))
                .build();
        var uPeriode2 = UttakResultatPeriode.ny()
                .medAktiviteter(of(uttakAktivitet2))
                .medTidsperiode(tidsperiodeUp2)
                .medPeriodeResultatÅrsak(PeriodeResultatÅrsak.OVERFORING_KVOTE_GYLDIG_KUN_FAR_HAR_RETT)
                .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
                .build();
        var uttaksPerioder = uttak(of(uPeriode, uPeriode2), of());

        // Act
        var resultat = UtbetalingsperiodeMapper.mapUtbetalingsperioder(tilkjentYtelsePerioder, uttaksPerioder, beregningsgrunnlagPerioder, Språkkode.NB);

        // Assert
        assertThat(resultat).hasSize(2);

        assertThat(resultat.get(0).getPeriodeFom()).isEqualTo(uPeriode.getFom());
        assertThat(resultat.get(0).getPeriodeTom()).isEqualTo(uPeriode.getTom());
        assertThat(resultat.get(0).isInnvilget()).isFalse();
        assertThat(resultat.get(0).getPeriodeDagsats()).isEqualTo(0L);
        assertThat(resultat.get(0).getÅrsak().getKode()).isEqualTo(PeriodeResultatÅrsak.HULL_MELLOM_FORELDRENES_PERIODER.getKode());

        assertThat(resultat.get(1).getPeriodeFom()).isEqualTo(tyPeriode3.getPeriodeFom());
        assertThat(resultat.get(1).getPeriodeTom()).isEqualTo(tyPeriode3.getPeriodeTom());
        assertThat(resultat.get(1).isInnvilget()).isTrue();
        assertThat(resultat.get(1).getPeriodeDagsats()).isEqualTo(620L);
        assertThat(resultat.get(1).getÅrsak().getKode()).isEqualTo(PeriodeResultatÅrsak.OVERFORING_KVOTE_GYLDIG_KUN_FAR_HAR_RETT.getKode());
    }

    @Test
    void verifisere_at_resultatet_blir_det_samme_om_periodene_ikke_er_sortert() {
        // Arrange
        var tidsperiodeBp1 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 9, 30), LocalDate.of(2019, 10, 2));
        var tidsperiodeBp2 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 10, 3), LocalDate.of(2019, 10, 4));
        var tidsperiodeBp3 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 10, 7), LocalDate.of(2019, 12, 31));
        var tidsperiodeUp1 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 7, 3), LocalDate.of(2019, 10, 4));
        var tidsperiodeUp2 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 10, 7), LocalDate.of(2019, 12, 31));
        var beregningPer = DatoIntervall.fraOgMed(LocalDate.of(2019, 9, 30));

        var tyPeriode = TilkjentYtelsePeriode.ny()
                .medPeriode(tidsperiodeBp1)
                .medDagsats(0L)
                .build();
        var tyPeriode2 = TilkjentYtelsePeriode.ny()
                .medPeriode(tidsperiodeBp2)
                .medDagsats(0L)
                .build();
        var tyPeriode3 = TilkjentYtelsePeriode.ny()
                .medPeriode(tidsperiodeBp3)
                .medDagsats(620L)
                .build();
        var tilkjentYtelsePerioder = of(tyPeriode3, tyPeriode2, tyPeriode);

        var bgPeriode = BeregningsgrunnlagPeriode.ny()
                .medPeriode(beregningPer)
                .medDagsats(620L)
                .build();
        var beregningsgrunnlagPerioder = of(bgPeriode);

        var uttakAktivitet = UttakResultatPeriodeAktivitet.ny()
                .medTrekkdager(BigDecimal.TEN)
                .medUtbetalingsprosent(BigDecimal.ZERO)
                .build();
        var uPeriode = UttakResultatPeriode.ny()
                .medAktiviteter(of(uttakAktivitet))
                .medTidsperiode(tidsperiodeUp1)
                .medPeriodeResultatÅrsak(PeriodeResultatÅrsak.HULL_MELLOM_FORELDRENES_PERIODER)
                .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
                .build();
        var uttakAktivitet2 = UttakResultatPeriodeAktivitet.ny()
                .medTrekkdager(BigDecimal.ZERO)
                .medUtbetalingsprosent(BigDecimal.valueOf(100L))
                .build();
        var uPeriode2 = UttakResultatPeriode.ny()
                .medAktiviteter(of(uttakAktivitet2))
                .medTidsperiode(tidsperiodeUp2)
                .medPeriodeResultatÅrsak(PeriodeResultatÅrsak.OVERFORING_KVOTE_GYLDIG_KUN_FAR_HAR_RETT)
                .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
                .build();
        var uttaksPerioder = uttak(of(uPeriode2, uPeriode), of());

        // Act
        var resultat = UtbetalingsperiodeMapper.mapUtbetalingsperioder(tilkjentYtelsePerioder, uttaksPerioder, beregningsgrunnlagPerioder, Språkkode.NB);

        // Assert
        assertThat(resultat).hasSize(2);
        assertThat(resultat.get(0).getPeriodeFom()).isEqualTo(uPeriode.getFom());
        assertThat(resultat.get(1).getPeriodeFom()).isEqualTo(tyPeriode3.getPeriodeFom());
    }

    private ForeldrepengerUttak uttak(List<UttakResultatPeriode> søkersPerioder, List<UttakResultatPeriode> annenPartPerioder) {
        return new ForeldrepengerUttak(søkersPerioder, annenPartPerioder, false, false, false, false);
    }

    @Test
    void skal_sortere_perioder_uten_beregningsgrunnlag_også() {
        // Arrange
        var tidsperiodeBp1 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 10, 7), LocalDate.of(2019, 12, 31));
        var tidsperiodeUp1 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 10, 7), LocalDate.of(2019, 12, 31));
        var tidsperiodeUp2 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 6, 20), LocalDate.of(2019, 6, 29)); // Skal sorteres først
        var beregningPer = DatoIntervall.fraOgMed(LocalDate.of(2019, 9, 30));

        var tyPeriode1 = TilkjentYtelsePeriode.ny()
                .medPeriode(tidsperiodeBp1)
                .medDagsats(620L)
                .build();
        var tilkjentYtelsePerioder = of(tyPeriode1);

        var bgPeriode = BeregningsgrunnlagPeriode.ny()
                .medPeriode(beregningPer)
                .medDagsats(620L)
                .build();
        var beregningsgrunnlagPerioder = of(bgPeriode);

        var uttakAktivitet1 = UttakResultatPeriodeAktivitet.ny()
                .medTrekkdager(BigDecimal.ZERO)
                .medUtbetalingsprosent(BigDecimal.valueOf(100L))
                .build();
        var uPeriode1 = UttakResultatPeriode.ny()
                .medAktiviteter(of(uttakAktivitet1))
                .medTidsperiode(tidsperiodeUp1)
                .medPeriodeResultatÅrsak(PeriodeResultatÅrsak.OVERFORING_KVOTE_GYLDIG_KUN_FAR_HAR_RETT)
                .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
                .build();
        var uttakAktivitet2 = UttakResultatPeriodeAktivitet.ny()
                .medTrekkdager(BigDecimal.TEN)
                .medUtbetalingsprosent(BigDecimal.ZERO)
                .build();
        var uPeriode2 = UttakResultatPeriode.ny()
                .medAktiviteter(of(uttakAktivitet2))
                .medTidsperiode(tidsperiodeUp2)
                .medPeriodeResultatÅrsak(PeriodeResultatÅrsak.MOR_HAR_IKKE_OMSORG)
                .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
                .build();
        var uttaksPerioder = uttak(of(uPeriode1, uPeriode2), of());

        // Act
        var resultat = UtbetalingsperiodeMapper.mapUtbetalingsperioder(tilkjentYtelsePerioder, uttaksPerioder, beregningsgrunnlagPerioder, Språkkode.NB);

        // Assert
        assertThat(resultat).hasSize(2);

        assertThat(resultat.get(0).getPeriodeFom()).isEqualTo(uPeriode2.getFom());
        assertThat(resultat.get(0).getPeriodeTom()).isEqualTo(uPeriode2.getTom());
        assertThat(resultat.get(0).isInnvilget()).isFalse();
        assertThat(resultat.get(0).getPeriodeDagsats()).isEqualTo(0L);
        assertThat(resultat.get(0).getÅrsak().getKode()).isEqualTo(PeriodeResultatÅrsak.MOR_HAR_IKKE_OMSORG.getKode());

        assertThat(resultat.get(1).getPeriodeFom()).isEqualTo(tyPeriode1.getPeriodeFom());
        assertThat(resultat.get(1).getPeriodeTom()).isEqualTo(tyPeriode1.getPeriodeTom());
        assertThat(resultat.get(1).isInnvilget()).isTrue();
        assertThat(resultat.get(1).getPeriodeDagsats()).isEqualTo(620L);
        assertThat(resultat.get(1).getÅrsak().getKode()).isEqualTo(PeriodeResultatÅrsak.OVERFORING_KVOTE_GYLDIG_KUN_FAR_HAR_RETT.getKode());
    }

    @Test
    void skal_mappe_annenAktitetListe_når_den_inneholder_annet() {
        // Arrange
        var tidsperiodeBp1 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 9, 30), LocalDate.of(2019, 10, 2));
        var tidsperiodeUp1 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 7, 3), LocalDate.of(2019, 10, 4));
        var beregningPer = DatoIntervall.fraOgMed(LocalDate.of(2019, 9, 30));

        var tilkjentYtelseAndel = TilkjentYtelseAndel.ny()
                .medAktivitetStatus(AktivitetStatus.KOMBINERT_AT_FL).build();
        var tyPeriode = TilkjentYtelsePeriode.ny()
                .medPeriode(tidsperiodeBp1)
                .medDagsats(0L)
                .medAndeler(of(tilkjentYtelseAndel))
                .build();

        var prStatusOgAndel = BeregningsgrunnlagPrStatusOgAndel.ny().medBeregningsperiode(tidsperiodeUp1).build();

        var tilkjentYtelsePerioder = of(tyPeriode);

        var bgPeriode = BeregningsgrunnlagPeriode.ny()
                .medPeriode(beregningPer)
                .medDagsats(620L)
                .medBeregningsgrunnlagPrStatusOgAndelList(of(prStatusOgAndel))
                .build();
        var beregningsgrunnlagPerioder = of(bgPeriode);

        var uttakAktivitet = UttakAktivitet.ny()
                .medUttakArbeidType(UttakArbeidType.ANNET)
                .build();

        var uttakResultatPeriodeAktivitet = UttakResultatPeriodeAktivitet.ny()
                .medTrekkdager(BigDecimal.TEN)
                .medUtbetalingsprosent(BigDecimal.ZERO)
                .medUttakAktivitet(uttakAktivitet)
                .medGraderingInnvilget(true)
                .medArbeidsprosent(BigDecimal.valueOf(20))
                .build();

        var uttakResultatPeriodeAktivitet2 = UttakResultatPeriodeAktivitet.ny()
                .medTrekkdager(BigDecimal.TEN)
                .medUtbetalingsprosent(BigDecimal.ZERO)
                .medUttakAktivitet(uttakAktivitet)
                .build();
        var uPeriode = UttakResultatPeriode.ny()
                .medAktiviteter(of(uttakResultatPeriodeAktivitet, uttakResultatPeriodeAktivitet2))
                .medTidsperiode(tidsperiodeUp1)
                .medPeriodeResultatÅrsak(PeriodeResultatÅrsak.UTSETTELSE_GYLDIG_PGA_ARBEID_KUN_FAR_HAR_RETT)
                .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
                .build();

        var uttaksPerioder = uttak(of(uPeriode), of());

        // Act
        var resultat = UtbetalingsperiodeMapper.mapUtbetalingsperioder(tilkjentYtelsePerioder, uttaksPerioder, beregningsgrunnlagPerioder, Språkkode.NB);

        // Assert
        assertThat(resultat).hasSize(1);

        assertThat(resultat.get(0).isInnvilget()).isTrue();
        assertThat(resultat.get(0).getPeriodeDagsats()).isEqualTo(0L);
        assertThat(resultat.get(0).getÅrsak().getKode()).isEqualTo(PeriodeResultatÅrsak.UTSETTELSE_GYLDIG_PGA_ARBEID_KUN_FAR_HAR_RETT.getKode());
        assertThat(resultat.get(0).getAnnenAktivitetsliste()).hasSize(1);
    }

    @Test
    void skal_ignorere_avslåtte_manglende_søkte_perioder_med_null_trekkdager_ved_mapping_av_periodeliste() {
        // Arrange
        var tidsperiode = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 9, 16), LocalDate.of(2019, 9, 16));
        var tyPeriode = TilkjentYtelsePeriode.ny()
                .medPeriode(tidsperiode)
                .build();
        var tilkjentYtelsePerioder = of(tyPeriode);
        var uttakAktivitet = UttakResultatPeriodeAktivitet.ny()
                .medTrekkdager(BigDecimal.ZERO)
                .medUtbetalingsprosent(BigDecimal.ZERO)
                .build();
        var uPeriode = UttakResultatPeriode.ny()
                .medAktiviteter(of(uttakAktivitet))
                .medTidsperiode(tidsperiode)
                .medPeriodeResultatÅrsak(PeriodeResultatÅrsak.HULL_MELLOM_FORELDRENES_PERIODER)
                .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
                .build();
        var uttaksPerioder = uttak(of(uPeriode), of());
        var bgPeriode = BeregningsgrunnlagPeriode.ny()
                .medPeriode(tidsperiode)
                .build();
        var beregningsgrunnlagPerioder = of(bgPeriode);

        // Act
        var resultat = UtbetalingsperiodeMapper.mapUtbetalingsperioder(tilkjentYtelsePerioder, uttaksPerioder, beregningsgrunnlagPerioder, Språkkode.NB);

        // Assert
        assertThat(resultat).hasSize(0);
    }

    @Test
    void skal_ta_med_avslåtte_manglende_søkte_perioder_med_trekkdager_ved_mapping_av_periodeliste() {
        // Arrange
        var tidsperiode = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 9, 16), LocalDate.of(2019, 9, 16));
        var tyPeriode = TilkjentYtelsePeriode.ny()
                .medPeriode(tidsperiode)
                .build();
        var tilkjentYtelsePerioder = of(tyPeriode);
        var uttakAktivitet = UttakResultatPeriodeAktivitet.ny()
                .medTrekkdager(BigDecimal.TEN)
                .medUtbetalingsprosent(BigDecimal.ZERO)
                .build();
        var uPeriode = UttakResultatPeriode.ny()
                .medAktiviteter(of(uttakAktivitet))
                .medTidsperiode(tidsperiode)
                .medPeriodeResultatÅrsak(PeriodeResultatÅrsak.HULL_MELLOM_FORELDRENES_PERIODER)
                .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
                .build();
        var uttaksPerioder = uttak(of(uPeriode), of());
        var bgPeriode = BeregningsgrunnlagPeriode.ny()
                .medPeriode(tidsperiode)
                .build();
        var beregningsgrunnlagPerioder = of(bgPeriode);

        // Act
        var resultat = UtbetalingsperiodeMapper.mapUtbetalingsperioder(tilkjentYtelsePerioder, uttaksPerioder, beregningsgrunnlagPerioder, Språkkode.NB);

        // Assert
        assertThat(resultat).hasSize(1);
    }

    @Test
    void skal_finne_første_og_siste_stønadsdato_og_håndtere_null() {
        // Arrange
        List<Utbetalingsperiode> utbetalingsperioder = new ArrayList<>();
        var førsteJanuarTjueAtten = LocalDate.of(2018, 1, 1);
        var trettiendeAprilTjueAtten = LocalDate.of(2018, 4, 30);

        leggtilPeriode(LocalDate.of(2017, 1, 1), LocalDate.of(2017, 1, 30), false, utbetalingsperioder);
        leggtilPeriode(førsteJanuarTjueAtten, LocalDate.of(2018, 1, 30), true, utbetalingsperioder);
        leggtilPeriode(LocalDate.of(2018, 2, 1), LocalDate.of(2018, 2, 25), true, utbetalingsperioder);
        leggtilPeriode(LocalDate.of(2018, 3, 1), LocalDate.of(2018, 3, 30), null, utbetalingsperioder);
        leggtilPeriode(LocalDate.of(2018, 4, 1), trettiendeAprilTjueAtten, true, utbetalingsperioder);
        leggtilPeriode(LocalDate.of(2019, 3, 1), LocalDate.of(2019, 3, 30), false, utbetalingsperioder);

        // Act + Assert
        assertThat(finnStønadsperiodeFom(utbetalingsperioder)).isEqualTo(Optional.of(førsteJanuarTjueAtten));
        assertThat(finnStønadsperiodeTom(utbetalingsperioder)).isEqualTo(Optional.of(trettiendeAprilTjueAtten));
    }

    @Test
    void skal_finne_antall_perioder() {
        // Arrange
        List<Utbetalingsperiode> utbetalingsperioder = new ArrayList<>();
        leggtilPeriode(LocalDate.of(2017, 1, 1), LocalDate.of(2017, 1, 30), false, utbetalingsperioder);
        leggtilPeriode(LocalDate.of(2018, 2, 1), LocalDate.of(2018, 2, 25), true, utbetalingsperioder);
        leggtilPeriode(LocalDate.of(2018, 3, 1), LocalDate.of(2018, 3, 30), true, utbetalingsperioder);

        // Act + Assert
        assertThat(finnAntallPerioder(utbetalingsperioder)).isEqualTo(3);
    }

    @Test
    void skal_finne_antall_innvilgede_perioder() {
        // Arrange
        List<Utbetalingsperiode> utbetalingsperioder = new ArrayList<>();
        leggtilPeriode(LocalDate.of(2017, 1, 1), LocalDate.of(2017, 1, 30), false, utbetalingsperioder);
        leggtilPeriode(LocalDate.of(2018, 2, 1), LocalDate.of(2018, 2, 25), true, utbetalingsperioder);
        leggtilPeriode(LocalDate.of(2018, 3, 1), LocalDate.of(2018, 3, 30), true, utbetalingsperioder);

        // Act + Assert
        assertThat(finnAntallInnvilgedePerioder(utbetalingsperioder)).isEqualTo(2);
    }

    @Test
    void skal_finne_antall_avslåtte_perioder() {
        // Arrange
        List<Utbetalingsperiode> utbetalingsperioder = new ArrayList<>();
        leggtilPeriode(LocalDate.of(2017, 1, 1), LocalDate.of(2017, 1, 30), false, utbetalingsperioder);
        leggtilPeriode(LocalDate.of(2018, 2, 1), LocalDate.of(2018, 2, 25), true, utbetalingsperioder);
        leggtilPeriode(LocalDate.of(2018, 3, 1), LocalDate.of(2018, 3, 30), true, utbetalingsperioder);

        // Act + Assert
        assertThat(finnAntallAvslåttePerioder(utbetalingsperioder)).isEqualTo(1);
    }

    @Test
    void skal_finne_periode_med_ikke_omsorg_mor() {
        // Arrange
        var utbetalingsperiode = Utbetalingsperiode.ny()
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
        var utbetalingsperiode = Utbetalingsperiode.ny()
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
        var utbetalingsperiode = Utbetalingsperiode.ny()
                .medPeriodeFom(LocalDate.now().minusDays(10), Språkkode.NB)
                .medPeriodeTom(LocalDate.now().plusDays(10), Språkkode.NB)
                .medÅrsak(Årsak.of(PeriodeResultatÅrsak.FØDSELSVILKÅRET_IKKE_OPPFYLT.getKode()))
                .build();

        // Act + Assert
        assertThat(finnesPeriodeMedIkkeOmsorg(of(utbetalingsperiode))).isFalse();
    }

    @Test
    void skal_hente_prioritert_utbetalingsgrad_fra_arbeidsforholdet_med_gradering_om_finnes() {
        var arbeidsforholdListe =
                List.of(opprettArbeidsforhold(false, Prosent.of(BigDecimal.valueOf(0)), Prosent.of(BigDecimal.valueOf(80))),
                        opprettArbeidsforhold(true, Prosent.of(BigDecimal.valueOf(20)), Prosent.of(BigDecimal.valueOf(100))),
                        opprettArbeidsforhold(false, Prosent.of(BigDecimal.valueOf(0.0)), Prosent.of(BigDecimal.valueOf(100))));

        assertThat(finnPrioritertUtbetalingsgrad(arbeidsforholdListe, null, null)).isEqualTo(Prosent.of(BigDecimal.valueOf(20)));
    }

    @Test
    void skal_hente_prioritert_utbetalingsgrad_fra_første_arbeidsfohold_med_utbetalingsgrad() {
        var arbeidsforholdListe =
                List.of(opprettArbeidsforhold(false, Prosent.of(BigDecimal.valueOf(0.0)), Prosent.of(BigDecimal.valueOf(0.0))),
                        opprettArbeidsforhold(false, Prosent.of(BigDecimal.valueOf(20)), Prosent.of(BigDecimal.valueOf(100))),
                        opprettArbeidsforhold(false, Prosent.of(BigDecimal.valueOf(0.0)), Prosent.of(BigDecimal.valueOf(0.0))));

        assertThat(finnPrioritertUtbetalingsgrad(arbeidsforholdListe, null, null)).isEqualTo(Prosent.of(BigDecimal.valueOf(20)));
    }
    @Test
    void skal_hente_prioritert_utbetalingsgrad_fra_næring_når_arbeidsforhold_ikke_finnes() {
        var næring = Næring.ny().medSistLignedeÅr(LocalDate.now().getYear())
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
        var annenAktivitetListe = List.of(opprettAnnenAktivitet(false, Prosent.of(BigDecimal.valueOf(0))), opprettAnnenAktivitet(true, Prosent.of(BigDecimal.valueOf(60))));

        assertThat(finnPrioritertUtbetalingsgrad(null, null, annenAktivitetListe)).isEqualTo(Prosent.of(BigDecimal.valueOf(60)));
    }

    @Test
    void skal_hente_prioritert_utbetalingsgrad_fra_første_aktivitet_med_utbetalingsgrad() {
        var annenAktivitetListe = List.of(opprettAnnenAktivitet(false, Prosent.of(BigDecimal.valueOf(0))), opprettAnnenAktivitet(false, Prosent.of(BigDecimal.valueOf(100))));

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
                .medAktivitetStatus(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE.getKode())
                .medUtbetalingsgrad(utbetalingsgrad)
                .medProsentArbeid(Prosent.of(BigDecimal.valueOf(0.0)))
                .build();

    }

    private void leggtilPeriode(LocalDate fom, LocalDate tom, Boolean innvilget, List<Utbetalingsperiode> utbetalingsperioder) {
        utbetalingsperioder.add(Utbetalingsperiode.ny()
                .medPeriodeFom(fom, Språkkode.NB)
                .medPeriodeTom(tom, Språkkode.NB)
                .medInnvilget(innvilget)
                .build());
    }
}
