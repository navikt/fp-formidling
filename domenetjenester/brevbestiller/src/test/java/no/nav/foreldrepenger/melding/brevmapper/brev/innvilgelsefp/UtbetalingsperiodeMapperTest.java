package no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp;

import static java.util.List.of;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.UtbetalingsperiodeMapper.finnAntallPerioder;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.UtbetalingsperiodeMapper.finnStønadsperiodeFomHvisFinnes;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.UtbetalingsperiodeMapper.finnStønadsperiodeTomHvisFinnes;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.UtbetalingsperiodeMapper.finnesPeriodeMedIkkeOmsorg;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.UtbetalingsperiodeMapper.harInnvilgedePerioder;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.melding.beregning.BeregningsresultatAndel;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndel;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.Utbetalingsperiode;
import no.nav.foreldrepenger.melding.typer.DatoIntervall;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.melding.uttak.UttakAktivitet;
import no.nav.foreldrepenger.melding.uttak.UttakArbeidType;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPerioder;
import no.nav.foreldrepenger.melding.uttak.kodeliste.PeriodeResultatÅrsak;

public class UtbetalingsperiodeMapperTest {

    @Test
    public void skal_hente_dato_fra_uttaksperiode_når_denne_er_før_beregningsresulatperioden_og_det_er_første_beregningResPeriode() {
        // Arrange
        DatoIntervall tidsperiodeBp1 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 9, 30), LocalDate.of(2019, 10, 2));
        DatoIntervall tidsperiodeBp2 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 10, 3), LocalDate.of(2019, 10, 4));
        DatoIntervall tidsperiodeBp3 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 10, 7), LocalDate.of(2019, 12, 31));
        DatoIntervall tidsperiodeUp1 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 7, 3), LocalDate.of(2019, 10, 4));
        DatoIntervall tidsperiodeUp2 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 10, 7), LocalDate.of(2019, 12, 31));
        DatoIntervall beregningPer = DatoIntervall.fraOgMed(LocalDate.of(2019, 9, 30));

        BeregningsresultatPeriode brPeriode = BeregningsresultatPeriode.ny()
                .medPeriode(tidsperiodeBp1)
                .medDagsats(0L)
                .build();
        BeregningsresultatPeriode brPeriode2 = BeregningsresultatPeriode.ny()
                .medPeriode(tidsperiodeBp2)
                .medDagsats(0L)
                .build();
        BeregningsresultatPeriode brPeriode3 = BeregningsresultatPeriode.ny()
                .medPeriode(tidsperiodeBp3)
                .medDagsats(620L)
                .build();
        List<BeregningsresultatPeriode> beregningsresultatPerioder = of(brPeriode, brPeriode2, brPeriode3);

        BeregningsgrunnlagPeriode bgPeriode = BeregningsgrunnlagPeriode.ny()
                .medPeriode(beregningPer)
                .medDagsats(620L)
                .build();
        List<BeregningsgrunnlagPeriode> beregningsgrunnlagPerioder = of(bgPeriode);

        UttakResultatPeriodeAktivitet uttakAktivitet = UttakResultatPeriodeAktivitet.ny()
                .medTrekkdager(BigDecimal.TEN)
                .medUtbetalingsprosent(BigDecimal.ZERO)
                .build();
        UttakResultatPeriode uPeriode = UttakResultatPeriode.ny()
                .medAktiviteter(of(uttakAktivitet))
                .medTidsperiode(tidsperiodeUp1)
                .medPeriodeResultatÅrsak(PeriodeResultatÅrsak.HULL_MELLOM_FORELDRENES_PERIODER)
                .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
                .build();
        UttakResultatPeriodeAktivitet uttakAktivitet2 = UttakResultatPeriodeAktivitet.ny()
                .medTrekkdager(BigDecimal.ZERO)
                .medUtbetalingsprosent(BigDecimal.valueOf(100L))
                .build();
        UttakResultatPeriode uPeriode2 = UttakResultatPeriode.ny()
                .medAktiviteter(of(uttakAktivitet2))
                .medTidsperiode(tidsperiodeUp2)
                .medPeriodeResultatÅrsak(PeriodeResultatÅrsak.OVERFORING_KVOTE_GYLDIG_KUN_FAR_HAR_RETT)
                .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
                .build();
        UttakResultatPerioder uttaksPerioder = UttakResultatPerioder.ny().medPerioder(of(uPeriode, uPeriode2)).build();

        // Act
        List<Utbetalingsperiode> resultat = UtbetalingsperiodeMapper.mapUtbetalingsperioder(beregningsresultatPerioder, uttaksPerioder, beregningsgrunnlagPerioder);

        // Assert
        assertThat(resultat).hasSize(2);

        assertThat(resultat.get(0).getPeriodeFom()).isEqualTo(uPeriode.getFom());
        assertThat(resultat.get(0).getPeriodeTom()).isEqualTo(uPeriode.getTom());
        assertThat(resultat.get(0).isInnvilget()).isFalse();
        assertThat(resultat.get(0).getPeriodeDagsats()).isEqualTo(0L);
        assertThat(resultat.get(0).getÅrsak()).isEqualTo(PeriodeResultatÅrsak.HULL_MELLOM_FORELDRENES_PERIODER.getKode());

        assertThat(resultat.get(1).getPeriodeFom()).isEqualTo(brPeriode3.getBeregningsresultatPeriodeFom());
        assertThat(resultat.get(1).getPeriodeTom()).isEqualTo(brPeriode3.getBeregningsresultatPeriodeTom());
        assertThat(resultat.get(1).isInnvilget()).isTrue();
        assertThat(resultat.get(1).getPeriodeDagsats()).isEqualTo(620L);
        assertThat(resultat.get(1).getÅrsak()).isEqualTo(PeriodeResultatÅrsak.OVERFORING_KVOTE_GYLDIG_KUN_FAR_HAR_RETT.getKode());
    }

    @Test
    public void verifisere_at_resultatet_blir_det_samme_om_periodene_ikke_er_sortert() {
        // Arrange
        DatoIntervall tidsperiodeBp1 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 9, 30), LocalDate.of(2019, 10, 2));
        DatoIntervall tidsperiodeBp2 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 10, 3), LocalDate.of(2019, 10, 4));
        DatoIntervall tidsperiodeBp3 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 10, 7), LocalDate.of(2019, 12, 31));
        DatoIntervall tidsperiodeUp1 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 7, 3), LocalDate.of(2019, 10, 4));
        DatoIntervall tidsperiodeUp2 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 10, 7), LocalDate.of(2019, 12, 31));
        DatoIntervall beregningPer = DatoIntervall.fraOgMed(LocalDate.of(2019, 9, 30));

        BeregningsresultatPeriode brPeriode = BeregningsresultatPeriode.ny()
                .medPeriode(tidsperiodeBp1)
                .medDagsats(0L)
                .build();
        BeregningsresultatPeriode brPeriode2 = BeregningsresultatPeriode.ny()
                .medPeriode(tidsperiodeBp2)
                .medDagsats(0L)
                .build();
        BeregningsresultatPeriode brPeriode3 = BeregningsresultatPeriode.ny()
                .medPeriode(tidsperiodeBp3)
                .medDagsats(620L)
                .build();
        List<BeregningsresultatPeriode> beregningsresultatPerioder = of(brPeriode3, brPeriode2, brPeriode);

        BeregningsgrunnlagPeriode bgPeriode = BeregningsgrunnlagPeriode.ny()
                .medPeriode(beregningPer)
                .medDagsats(620L)
                .build();
        List<BeregningsgrunnlagPeriode> beregningsgrunnlagPerioder = of(bgPeriode);

        UttakResultatPeriodeAktivitet uttakAktivitet = UttakResultatPeriodeAktivitet.ny()
                .medTrekkdager(BigDecimal.TEN)
                .medUtbetalingsprosent(BigDecimal.ZERO)
                .build();
        UttakResultatPeriode uPeriode = UttakResultatPeriode.ny()
                .medAktiviteter(of(uttakAktivitet))
                .medTidsperiode(tidsperiodeUp1)
                .medPeriodeResultatÅrsak(PeriodeResultatÅrsak.HULL_MELLOM_FORELDRENES_PERIODER)
                .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
                .build();
        UttakResultatPeriodeAktivitet uttakAktivitet2 = UttakResultatPeriodeAktivitet.ny()
                .medTrekkdager(BigDecimal.ZERO)
                .medUtbetalingsprosent(BigDecimal.valueOf(100L))
                .build();
        UttakResultatPeriode uPeriode2 = UttakResultatPeriode.ny()
                .medAktiviteter(of(uttakAktivitet2))
                .medTidsperiode(tidsperiodeUp2)
                .medPeriodeResultatÅrsak(PeriodeResultatÅrsak.OVERFORING_KVOTE_GYLDIG_KUN_FAR_HAR_RETT)
                .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
                .build();
        UttakResultatPerioder uttaksPerioder = UttakResultatPerioder.ny().medPerioder(of(uPeriode2, uPeriode)).build();

        // Act
        List<Utbetalingsperiode> resultat = UtbetalingsperiodeMapper.mapUtbetalingsperioder(beregningsresultatPerioder, uttaksPerioder, beregningsgrunnlagPerioder);

        // Assert
        assertThat(resultat).hasSize(2);
        assertThat(resultat.get(0).getPeriodeFom()).isEqualTo(uPeriode.getFom());
        assertThat(resultat.get(1).getPeriodeFom()).isEqualTo(brPeriode3.getBeregningsresultatPeriodeFom());
    }

    @Test
    public void skal_mappe_annenAktitetListe_når_den_inneholder_annet() {
        // Arrange
        DatoIntervall tidsperiodeBp1 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 9, 30), LocalDate.of(2019, 10, 2));
        DatoIntervall tidsperiodeUp1 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 7, 3), LocalDate.of(2019, 10, 4));
        DatoIntervall beregningPer = DatoIntervall.fraOgMed(LocalDate.of(2019, 9, 30));

        BeregningsresultatAndel beregningsresultatAndel = BeregningsresultatAndel.ny()
                .medAktivitetStatus(AktivitetStatus.KOMBINERT_AT_FL).build();
        BeregningsresultatPeriode brPeriode = BeregningsresultatPeriode.ny()
                .medPeriode(tidsperiodeBp1)
                .medDagsats(0L)
                .medBeregningsresultatAndel(of(beregningsresultatAndel))
                .build();

        BeregningsgrunnlagPrStatusOgAndel prStatusOgAndel = BeregningsgrunnlagPrStatusOgAndel.ny().medBeregningsperiode(tidsperiodeUp1).build();

        List<BeregningsresultatPeriode> beregningsresultatPerioder = of(brPeriode);

        BeregningsgrunnlagPeriode bgPeriode = BeregningsgrunnlagPeriode.ny()
                .medPeriode(beregningPer)
                .medDagsats(620L)
                .medBeregningsgrunnlagPrStatusOgAndelList(of(prStatusOgAndel))
                .build();
        List<BeregningsgrunnlagPeriode> beregningsgrunnlagPerioder = of(bgPeriode);

        UttakAktivitet uttakAktivitet = UttakAktivitet.ny()
                .medUttakArbeidType(UttakArbeidType.ANNET)
                .build();

        UttakResultatPeriodeAktivitet uttakResultatPeriodeAktivitet = UttakResultatPeriodeAktivitet.ny()
                .medTrekkdager(BigDecimal.TEN)
                .medUtbetalingsprosent(BigDecimal.ZERO)
                .medUttakAktivitet(uttakAktivitet)
                .medGraderingInnvilget(true)
                .medArbeidsprosent(BigDecimal.valueOf(20))
                .build();

        UttakResultatPeriodeAktivitet uttakResultatPeriodeAktivitet2 = UttakResultatPeriodeAktivitet.ny()
                .medTrekkdager(BigDecimal.TEN)
                .medUtbetalingsprosent(BigDecimal.ZERO)
                .medUttakAktivitet(uttakAktivitet)
                .build();
        UttakResultatPeriode uPeriode = UttakResultatPeriode.ny()
                .medAktiviteter(of(uttakResultatPeriodeAktivitet, uttakResultatPeriodeAktivitet2))
                .medTidsperiode(tidsperiodeUp1)
                .medPeriodeResultatÅrsak(PeriodeResultatÅrsak.UTSETTELSE_GYLDIG_PGA_ARBEID_KUN_FAR_HAR_RETT)
                .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
                .build();

        UttakResultatPerioder uttaksPerioder = UttakResultatPerioder.ny().medPerioder(of(uPeriode)).build();

        // Act
        List<Utbetalingsperiode> resultat = UtbetalingsperiodeMapper.mapUtbetalingsperioder(beregningsresultatPerioder, uttaksPerioder, beregningsgrunnlagPerioder);

        // Assert
        assertThat(resultat).hasSize(1);

        assertThat(resultat.get(0).isInnvilget()).isTrue();
        assertThat(resultat.get(0).getPeriodeDagsats()).isEqualTo(0L);
        assertThat(resultat.get(0).getÅrsak()).isEqualTo(PeriodeResultatÅrsak.UTSETTELSE_GYLDIG_PGA_ARBEID_KUN_FAR_HAR_RETT.getKode());
        assertThat(resultat.get(0).getAnnenAktivitetsliste()).hasSize(1);
    }

    @Test
    public void skal_ignorere_avslåtte_manglende_søkte_perioder_med_null_trekkdager_ved_mapping_av_periodeliste() {
        // Arrange
        DatoIntervall tidsperiode = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 9, 16), LocalDate.of(2019, 9, 16));
        BeregningsresultatPeriode brPeriode = BeregningsresultatPeriode.ny()
                .medPeriode(tidsperiode)
                .build();
        List<BeregningsresultatPeriode> beregningsresultatPerioder = of(brPeriode);
        UttakResultatPeriodeAktivitet uttakAktivitet = UttakResultatPeriodeAktivitet.ny()
                .medTrekkdager(BigDecimal.ZERO)
                .medUtbetalingsprosent(BigDecimal.ZERO)
                .build();
        UttakResultatPeriode uPeriode = UttakResultatPeriode.ny()
                .medAktiviteter(of(uttakAktivitet))
                .medTidsperiode(tidsperiode)
                .medPeriodeResultatÅrsak(PeriodeResultatÅrsak.HULL_MELLOM_FORELDRENES_PERIODER)
                .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
                .build();
        UttakResultatPerioder uttaksPerioder = UttakResultatPerioder.ny().medPerioder(of(uPeriode)).build();
        BeregningsgrunnlagPeriode bgPeriode = BeregningsgrunnlagPeriode.ny()
                .medPeriode(tidsperiode)
                .build();
        List<BeregningsgrunnlagPeriode> beregningsgrunnlagPerioder = of(bgPeriode);

        // Act
        List<Utbetalingsperiode> resultat = UtbetalingsperiodeMapper.mapUtbetalingsperioder(beregningsresultatPerioder, uttaksPerioder, beregningsgrunnlagPerioder);

        // Assert
        assertThat(resultat).hasSize(0);
    }

    @Test
    public void skal_ta_med_avslåtte_manglende_søkte_perioder_med_trekkdager_ved_mapping_av_periodeliste() {
        // Arrange
        DatoIntervall tidsperiode = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 9, 16), LocalDate.of(2019, 9, 16));
        BeregningsresultatPeriode brPeriode = BeregningsresultatPeriode.ny()
                .medPeriode(tidsperiode)
                .build();
        List<BeregningsresultatPeriode> beregningsresultatPerioder = of(brPeriode);
        UttakResultatPeriodeAktivitet uttakAktivitet = UttakResultatPeriodeAktivitet.ny()
                .medTrekkdager(BigDecimal.TEN)
                .medUtbetalingsprosent(BigDecimal.ZERO)
                .build();
        UttakResultatPeriode uPeriode = UttakResultatPeriode.ny()
                .medAktiviteter(of(uttakAktivitet))
                .medTidsperiode(tidsperiode)
                .medPeriodeResultatÅrsak(PeriodeResultatÅrsak.HULL_MELLOM_FORELDRENES_PERIODER)
                .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
                .build();
        UttakResultatPerioder uttaksPerioder = UttakResultatPerioder.ny().medPerioder(of(uPeriode)).build();
        BeregningsgrunnlagPeriode bgPeriode = BeregningsgrunnlagPeriode.ny()
                .medPeriode(tidsperiode)
                .build();
        List<BeregningsgrunnlagPeriode> beregningsgrunnlagPerioder = of(bgPeriode);

        // Act
        List<Utbetalingsperiode> resultat = UtbetalingsperiodeMapper.mapUtbetalingsperioder(beregningsresultatPerioder, uttaksPerioder, beregningsgrunnlagPerioder);

        // Assert
        assertThat(resultat).hasSize(1);
    }

    @Test
    public void skal_finne_første_og_siste_stønadsdato_og_håndtere_null() {
        // Arrange
        List<Utbetalingsperiode> utbetalingsperioder = new ArrayList<>();
        LocalDate førsteJanuarTjueAtten = LocalDate.of(2018, 1, 1);
        LocalDate trettiendeAprilTjueAtten = LocalDate.of(2018, 4, 30);

        leggtilPeriode(LocalDate.of(2017, 1, 1), LocalDate.of(2017, 1, 30), false, utbetalingsperioder);
        leggtilPeriode(førsteJanuarTjueAtten, LocalDate.of(2018, 1, 30), true, utbetalingsperioder);
        leggtilPeriode(LocalDate.of(2018, 2, 1), LocalDate.of(2018, 2, 25), true, utbetalingsperioder);
        leggtilPeriode(LocalDate.of(2018, 3, 1), LocalDate.of(2018, 3, 30), null, utbetalingsperioder);
        leggtilPeriode(LocalDate.of(2018, 4, 1), trettiendeAprilTjueAtten, true, utbetalingsperioder);
        leggtilPeriode(LocalDate.of(2019, 3, 1), LocalDate.of(2019, 3, 30), false, utbetalingsperioder);

        // Act + Assert
        assertThat(finnStønadsperiodeFomHvisFinnes(utbetalingsperioder)).isEqualTo(førsteJanuarTjueAtten);
        assertThat(finnStønadsperiodeTomHvisFinnes(utbetalingsperioder)).isEqualTo(trettiendeAprilTjueAtten);
    }

    @Test
    public void skal_finne_innvilgede_perioder() {
        // Arrange
        List<Utbetalingsperiode> utbetalingsperioder = new ArrayList<>();
        leggtilPeriode(LocalDate.of(2018, 2, 1), LocalDate.of(2018, 2, 25), true, utbetalingsperioder);
        leggtilPeriode(LocalDate.of(2019, 3, 1), LocalDate.of(2019, 3, 30), false, utbetalingsperioder);

        // Act + Assert
        assertThat(harInnvilgedePerioder(utbetalingsperioder)).isTrue();
    }

    @Test
    public void skal_ikke_finne_innvilgede_perioder() {
        // Arrange
        List<Utbetalingsperiode> utbetalingsperioder = new ArrayList<>();
        leggtilPeriode(LocalDate.of(2018, 2, 1), LocalDate.of(2018, 2, 25), false, utbetalingsperioder);
        leggtilPeriode(LocalDate.of(2019, 3, 1), LocalDate.of(2019, 3, 30), false, utbetalingsperioder);

        // Act + Assert
        assertThat(harInnvilgedePerioder(utbetalingsperioder)).isFalse();
    }

    @Test
    public void skal_finne_antall_perioder() {
        // Arrange
        List<Utbetalingsperiode> utbetalingsperioder = new ArrayList<>();
        leggtilPeriode(LocalDate.of(2017, 1, 1), LocalDate.of(2017, 1, 30), false, utbetalingsperioder);
        leggtilPeriode(LocalDate.of(2018, 2, 1), LocalDate.of(2018, 2, 25), true, utbetalingsperioder);
        leggtilPeriode(LocalDate.of(2018, 3, 1), LocalDate.of(2018, 3, 30), true, utbetalingsperioder);

        // Act + Assert
        assertThat(finnAntallPerioder(utbetalingsperioder)).isEqualTo(3);
    }

    @Test
    public void skal_finne_periode_med_ikke_omsorg_mor() {
        // Arrange
        Utbetalingsperiode utbetalingsperiode = Utbetalingsperiode.ny()
                .medPeriodeFom(LocalDate.now().minusDays(10))
                .medPeriodeTom(LocalDate.now().plusDays(10))
                .medÅrsak(PeriodeResultatÅrsak.MOR_HAR_IKKE_OMSORG.getKode())
                .build();

        // Act + Assert
        assertThat(finnesPeriodeMedIkkeOmsorg(of(utbetalingsperiode))).isTrue();
    }

    @Test
    public void skal_finne_periode_med_ikke_omsorg_far() {
        // Arrange
        Utbetalingsperiode utbetalingsperiode = Utbetalingsperiode.ny()
                .medPeriodeFom(LocalDate.now().minusDays(10))
                .medPeriodeTom(LocalDate.now().plusDays(10))
                .medÅrsak(PeriodeResultatÅrsak.FAR_HAR_IKKE_OMSORG.getKode())
                .build();

        // Act + Assert
        assertThat(finnesPeriodeMedIkkeOmsorg(of(utbetalingsperiode))).isTrue();
    }

    @Test
    public void skal_ikke_finne_periode_med_ikke_omsorg() {
        // Arrange
        Utbetalingsperiode utbetalingsperiode = Utbetalingsperiode.ny()
                .medPeriodeFom(LocalDate.now().minusDays(10))
                .medPeriodeTom(LocalDate.now().plusDays(10))
                .medÅrsak(PeriodeResultatÅrsak.FØDSELSVILKÅRET_IKKE_OPPFYLT.getKode())
                .build();

        // Act + Assert
        assertThat(finnesPeriodeMedIkkeOmsorg(of(utbetalingsperiode))).isFalse();
    }

    @Test
    public void skal_hente_prioritert_utbetalingsgrad_fra_arbeidsforholdet_med_høyest() {
        //TODO(JEJ) når det evt. blir avklart at vi løser det slik
    }

    @Test
    public void skal_hente_prioritert_utbetalingsgrad_fra_næring_når_arbeidsforhold_ikke_finnes() {
        //TODO(JEJ) når det evt. blir avklart at vi løser det slik
    }

    @Test
    public void skal_hente_prioritert_utbetalingsgrad_fra_annen_aktivitet_med_høyest_når_verken_arbeidsforhold_eller_næring_finnes() {
        //TODO(JEJ) når det evt. blir avklart at vi løser det slik
    }

    private void leggtilPeriode(LocalDate fom, LocalDate tom, Boolean innvilget, List<Utbetalingsperiode> utbetalingsperioder) {
        utbetalingsperioder.add(Utbetalingsperiode.ny()
                .medPeriodeFom(fom)
                .medPeriodeTom(tom)
                .medInnvilget(innvilget)
                .build());
    }
}