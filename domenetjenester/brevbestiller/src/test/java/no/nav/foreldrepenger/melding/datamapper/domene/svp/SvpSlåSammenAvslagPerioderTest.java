package no.nav.foreldrepenger.melding.datamapper.domene.svp;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Set;

import org.junit.Test;

import no.nav.foreldrepenger.melding.uttak.svp.PeriodeIkkeOppfyltÅrsak;

public class SvpSlåSammenAvslagPerioderTest {

    private static final LocalDate FØRSTE_OKTOBER = LocalDate.of(2019, 10, 1);

    @Test
    public void skal_merge_to_perioder_med_samme_fom_og_ulik_tom(){

        SvpAvslagPeriode periode1 = SvpAvslagPeriode.Builder.ny()
                .medPeriodeÅrsak(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT)
                .medFom(FØRSTE_OKTOBER)
                .medTom(FØRSTE_OKTOBER.plusWeeks(1))
                .build();

        SvpAvslagPeriode periode2 = SvpAvslagPeriode.Builder.ny()
                .medPeriodeÅrsak(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT)
                .medFom(FØRSTE_OKTOBER)
                .medTom(FØRSTE_OKTOBER.plusWeeks(2))
                .build();

        // Arrange
        Set<SvpAvslagPeriode> perioder = Set.of(periode1, periode2);

        // Act
        Set<SvpAvslagPeriode> sammenslåttePerioder = SvpSlåSammenAvslagPerioder.slåSammen(perioder);

        // Assert
        assertThat(sammenslåttePerioder).hasSize(1);
        assertThat(sammenslåttePerioder.iterator().next().getFom().toLocalDate()).isEqualTo(FØRSTE_OKTOBER);
        assertThat(sammenslåttePerioder.iterator().next().getTom().toLocalDate()).isEqualTo(FØRSTE_OKTOBER.plusWeeks(2));
        assertThat(sammenslåttePerioder.iterator().next().getAarsakskode()).isEqualTo(
                Integer.valueOf(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT.getKode()));

    }

    @Test
    public void skal_slå_sammen_perioder_når_kun_helg_er_mellom(){

        LocalDate fomP1 = LocalDate.of(2019, 9, 1);
        LocalDate tomP1 = LocalDate.of(2019, 9, 6);
        SvpAvslagPeriode periode1 = SvpAvslagPeriode.Builder.ny()
                .medPeriodeÅrsak(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT)
                .medFom(fomP1)
                .medTom(tomP1)
                .build();

        LocalDate fomP2 = LocalDate.of(2019, 9, 9);
        LocalDate tomP2 = LocalDate.of(2019, 9, 14);
        SvpAvslagPeriode periode2 = SvpAvslagPeriode.Builder.ny()
                .medPeriodeÅrsak(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT)
                .medFom(fomP2)
                .medTom(tomP2)
                .build();

        // Arrange
        Set<SvpAvslagPeriode> perioder = Set.of(periode1, periode2);

        // Act
        Set<SvpAvslagPeriode> sammenslåttePerioder = SvpSlåSammenAvslagPerioder.slåSammen(perioder);

        // Assert
        assertThat(sammenslåttePerioder).hasSize(1);
        assertThat(sammenslåttePerioder.iterator().next().getFom().toLocalDate()).isEqualTo(fomP1);
        assertThat(sammenslåttePerioder.iterator().next().getTom().toLocalDate()).isEqualTo(tomP2);
        assertThat(sammenslåttePerioder.iterator().next().getAarsakskode()).isEqualTo(
                Integer.valueOf(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT.getKode()));

    }

    @Test
    public void skal_slå_sammen_periode_hvor_P1_har_TOM_like_P2_FOM(){

        LocalDate tomP1 = FØRSTE_OKTOBER.plusWeeks(1);
        SvpAvslagPeriode periode1 = SvpAvslagPeriode.Builder.ny()
                .medPeriodeÅrsak(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT)
                .medFom(FØRSTE_OKTOBER)
                .medTom(tomP1)
                .build();

        LocalDate fomP2 = FØRSTE_OKTOBER.plusWeeks(1);
        LocalDate tomP2 = FØRSTE_OKTOBER.plusWeeks(2);
        SvpAvslagPeriode periode2 = SvpAvslagPeriode.Builder.ny()
                .medPeriodeÅrsak(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT)
                .medFom(fomP2)
                .medTom(tomP2)
                .build();

        // Arrange
        Set<SvpAvslagPeriode> perioder = Set.of(periode1, periode2);

        // Act
        Set<SvpAvslagPeriode> sammenslåttePerioder = SvpSlåSammenAvslagPerioder.slåSammen(perioder);

        // Assert
        assertThat(sammenslåttePerioder).hasSize(1);
        assertThat(sammenslåttePerioder.iterator().next().getFom().toLocalDate()).isEqualTo(FØRSTE_OKTOBER);
        assertThat(sammenslåttePerioder.iterator().next().getTom().toLocalDate()).isEqualTo(tomP2);
        assertThat(sammenslåttePerioder.iterator().next().getAarsakskode()).isEqualTo(
                Integer.valueOf(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT.getKode()));

    }

    @Test
    public void skal_slå_sammen_periode_hvor_P1_har_TOM_1_dag_før_P2_FOM(){

        LocalDate tomP1 = FØRSTE_OKTOBER.plusWeeks(1);
        SvpAvslagPeriode periode1 = SvpAvslagPeriode.Builder.ny()
                .medPeriodeÅrsak(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT)
                .medFom(FØRSTE_OKTOBER)
                .medTom(tomP1)
                .build();

        LocalDate fomP2 = FØRSTE_OKTOBER.plusWeeks(1).plusDays(1);
        LocalDate tomP2 = FØRSTE_OKTOBER.plusWeeks(2).plusDays(1);
        SvpAvslagPeriode periode2 = SvpAvslagPeriode.Builder.ny()
                .medPeriodeÅrsak(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT)
                .medFom(fomP2)
                .medTom(tomP2)
                .build();

        // Arrange
        Set<SvpAvslagPeriode> perioder = Set.of(periode1, periode2);

        // Act
        Set<SvpAvslagPeriode> sammenslåttePerioder = SvpSlåSammenAvslagPerioder.slåSammen(perioder);

        // Assert
        assertThat(sammenslåttePerioder).hasSize(1);
        assertThat(sammenslåttePerioder.iterator().next().getFom().toLocalDate()).isEqualTo(FØRSTE_OKTOBER);
        assertThat(sammenslåttePerioder.iterator().next().getTom().toLocalDate()).isEqualTo(tomP2);
        assertThat(sammenslåttePerioder.iterator().next().getAarsakskode()).isEqualTo(
                Integer.valueOf(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT.getKode()));

    }

    @Test
    public void skal_ikke_slå_sammen_periode_hvor_P1_har_TOM_2_dager_før_P2_FOM(){

        LocalDate tomP1 = FØRSTE_OKTOBER.plusWeeks(1);
        SvpAvslagPeriode periode1 = SvpAvslagPeriode.Builder.ny()
                .medPeriodeÅrsak(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT)
                .medFom(FØRSTE_OKTOBER)
                .medTom(tomP1)
                .build();

        LocalDate fomP2 = FØRSTE_OKTOBER.plusWeeks(1).plusDays(2);
        LocalDate tomP2 = FØRSTE_OKTOBER.plusWeeks(2).plusDays(2);
        SvpAvslagPeriode periode2 = SvpAvslagPeriode.Builder.ny()
                .medPeriodeÅrsak(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT)
                .medFom(fomP2)
                .medTom(tomP2)
                .build();

        // Arrange
        Set<SvpAvslagPeriode> perioder = Set.of(periode1, periode2);

        // Act
        Set<SvpAvslagPeriode> sammenslåttePerioder = SvpSlåSammenAvslagPerioder.slåSammen(perioder);

        // Assert
        assertThat(sammenslåttePerioder).hasSize(2);
        assertThat(sammenslåttePerioder).anySatisfy(p -> {
            assertThat(p.getFom().toLocalDate()).isEqualTo(FØRSTE_OKTOBER);
            assertThat(p.getTom().toLocalDate()).isEqualTo(tomP1);
            assertThat(p.getAarsakskode()).isEqualTo(Integer.valueOf(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT.getKode()));
        });
        assertThat(sammenslåttePerioder).anySatisfy(p -> {
            assertThat(p.getFom().toLocalDate()).isEqualTo(fomP2);
            assertThat(p.getTom().toLocalDate()).isEqualTo(tomP2);
            assertThat(p.getAarsakskode()).isEqualTo(Integer.valueOf(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT.getKode()));
        });

    }

    @Test
    public void skal_ikke_slå_sammen_periode_hvor_P1_og_P2_har_ulike_årsaker(){

        SvpAvslagPeriode periode1 = SvpAvslagPeriode.Builder.ny()
                .medPeriodeÅrsak(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT)
                .medFom(FØRSTE_OKTOBER)
                .medTom(FØRSTE_OKTOBER.plusWeeks(1))
                .build();

        SvpAvslagPeriode periode2 = SvpAvslagPeriode.Builder.ny()
                .medPeriodeÅrsak(PeriodeIkkeOppfyltÅrsak.PERIODE_SAMTIDIG_SOM_FERIE)
                .medFom(FØRSTE_OKTOBER)
                .medTom(FØRSTE_OKTOBER.plusWeeks(1))
                .build();

        // Arrange
        Set<SvpAvslagPeriode> perioder = Set.of(periode1, periode2);

        // Act
        Set<SvpAvslagPeriode> sammenslåttePerioder = SvpSlåSammenAvslagPerioder.slåSammen(perioder);

        // Assert
        assertThat(sammenslåttePerioder).hasSize(2);
        assertThat(sammenslåttePerioder).anySatisfy(p -> {
            assertThat(p.getFom().toLocalDate()).isEqualTo(FØRSTE_OKTOBER);
            assertThat(p.getTom().toLocalDate()).isEqualTo(FØRSTE_OKTOBER.plusWeeks(1));
            assertThat(p.getAarsakskode()).isEqualTo(Integer.valueOf(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT.getKode()));
        });
        assertThat(sammenslåttePerioder).anySatisfy(p -> {
            assertThat(p.getFom().toLocalDate()).isEqualTo(FØRSTE_OKTOBER);
            assertThat(p.getTom().toLocalDate()).isEqualTo(FØRSTE_OKTOBER.plusWeeks(1));
            assertThat(p.getAarsakskode()).isEqualTo(Integer.valueOf(PeriodeIkkeOppfyltÅrsak.PERIODE_SAMTIDIG_SOM_FERIE.getKode()));

        });

    }

    @Test
    public void skal_sortere_basert_på_periode_FOM_og_slå_sammen_perioder_med_samme_årsak_som_overlapper(){

        LocalDate fomP1 = LocalDate.of(2019, 10, 1);
        LocalDate tomP1 = LocalDate.of(2019, 10, 8);
        SvpAvslagPeriode periode1 = SvpAvslagPeriode.Builder.ny()
                .medPeriodeÅrsak(PeriodeIkkeOppfyltÅrsak.PERIODE_SAMTIDIG_SOM_FERIE)
                .medFom(fomP1)
                .medTom(tomP1)
                .build();

        LocalDate fomP2 = LocalDate.of(2019, 11, 5);
        LocalDate tomP2 = LocalDate.of(2019, 11, 12);
        SvpAvslagPeriode periode2 = SvpAvslagPeriode.Builder.ny()
                .medPeriodeÅrsak(PeriodeIkkeOppfyltÅrsak.PERIODE_SAMTIDIG_SOM_FERIE)
                .medFom(fomP2)
                .medTom(tomP2)
                .build();

        LocalDate fomP3 = LocalDate.of(2019, 11, 13);
        LocalDate tomP3 = LocalDate.of(2019, 11, 20);
        SvpAvslagPeriode periode3 = SvpAvslagPeriode.Builder.ny()
                .medPeriodeÅrsak(PeriodeIkkeOppfyltÅrsak.PERIODE_SAMTIDIG_SOM_FERIE)
                .medFom(fomP3)
                .medTom(tomP3)
                .build();

        LocalDate fomP4 = LocalDate.of(2019, 10, 9);
        LocalDate tomP4 = LocalDate.of(2019, 10, 18);
        SvpAvslagPeriode periode4 = SvpAvslagPeriode.Builder.ny()
                .medPeriodeÅrsak(PeriodeIkkeOppfyltÅrsak.PERIODE_SAMTIDIG_SOM_FERIE)
                .medFom(fomP4)
                .medTom(tomP4)
                .build();

        LocalDate fomP5 = LocalDate.of(2019, 12, 3);
        LocalDate tomP5 = LocalDate.of(2019, 12, 10);
        SvpAvslagPeriode periode5 = SvpAvslagPeriode.Builder.ny()
                .medPeriodeÅrsak(PeriodeIkkeOppfyltÅrsak.PERIODE_SAMTIDIG_SOM_FERIE)
                .medFom(fomP5)
                .medTom(tomP5)
                .build();

        LocalDate fomP6 = LocalDate.of(2019, 12, 3);
        LocalDate tomP6 = LocalDate.of(2019, 12, 10);
        SvpAvslagPeriode periode6 = SvpAvslagPeriode.Builder.ny()
                .medPeriodeÅrsak(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT)
                .medFom(fomP5)
                .medTom(tomP5)
                .build();

        LocalDate fomP7 = LocalDate.of(2019, 10, 21);
        LocalDate tomP7 = LocalDate.of(2019, 10, 24);
        SvpAvslagPeriode periode7 = SvpAvslagPeriode.Builder.ny()
                .medPeriodeÅrsak(PeriodeIkkeOppfyltÅrsak.PERIODE_SAMTIDIG_SOM_FERIE)
                .medFom(fomP7)
                .medTom(tomP7)
                .build();

        // Arrange
        Set<SvpAvslagPeriode> perioder = Set.of(periode1, periode2, periode3, periode4, periode5, periode6, periode7);

        // Act
        Set<SvpAvslagPeriode> sammenslåttePerioder = SvpSlåSammenAvslagPerioder.slåSammen(perioder);

        // Assert
        assertThat(sammenslåttePerioder).hasSize(4);
        assertThat(sammenslåttePerioder).anySatisfy(p -> {
            assertThat(p.getFom().toLocalDate()).isEqualTo(fomP1);
            assertThat(p.getTom().toLocalDate()).isEqualTo(tomP7);
            assertThat(p.getAarsakskode()).isEqualTo(Integer.valueOf(PeriodeIkkeOppfyltÅrsak.PERIODE_SAMTIDIG_SOM_FERIE.getKode()));
        });
        assertThat(sammenslåttePerioder).anySatisfy(p -> {
            assertThat(p.getFom().toLocalDate()).isEqualTo(fomP5);
            assertThat(p.getTom().toLocalDate()).isEqualTo(tomP5);
            assertThat(p.getAarsakskode()).isEqualTo(Integer.valueOf(PeriodeIkkeOppfyltÅrsak.PERIODE_SAMTIDIG_SOM_FERIE.getKode()));
        });
        assertThat(sammenslåttePerioder).anySatisfy(p -> {
            assertThat(p.getFom().toLocalDate()).isEqualTo(fomP2);
            assertThat(p.getTom().toLocalDate()).isEqualTo(tomP3);
            assertThat(p.getAarsakskode()).isEqualTo(Integer.valueOf(PeriodeIkkeOppfyltÅrsak.PERIODE_SAMTIDIG_SOM_FERIE.getKode()));
        });
        assertThat(sammenslåttePerioder).anySatisfy(p -> {
            assertThat(p.getFom().toLocalDate()).isEqualTo(fomP6);
            assertThat(p.getTom().toLocalDate()).isEqualTo(tomP6);
            assertThat(p.getAarsakskode()).isEqualTo(Integer.valueOf(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT.getKode()));
        });
    }


}
