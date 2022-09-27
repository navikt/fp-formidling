package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.avslagfp;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.avslagfp.AvslåttPeriode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Årsak;

public class AvslåttPeriodeMergerTest {

    private static final LocalDate PERIODE1_FOM = LocalDate.now().minusDays(10);
    private static final LocalDate PERIODE1_TOM = LocalDate.now().plusDays(5);
    private static final LocalDate PERIODE2_FOM = LocalDate.now().plusDays(6);
    private static final LocalDate PERIODE2_TOM = LocalDate.now().plusDays(10);
    private static final LocalDate PERIODE3_FOM = LocalDate.now().plusDays(20);
    private static final LocalDate PERIODE3_TOM = LocalDate.now().plusDays(30);

    @Test
    public void skal_slå_sammen_perioder_som_er_sammenhengende() {
        // Arrange
        var periode1 = AvslåttPeriode.ny().medPeriodeFom(PERIODE1_FOM, Språkkode.NB).medPeriodeTom(PERIODE1_TOM, Språkkode.NB).medAntallTapteDager(4, BigDecimal.ZERO).build();
        var periode2 = AvslåttPeriode.ny().medPeriodeFom(PERIODE2_FOM, Språkkode.NB).medPeriodeTom(PERIODE2_TOM, Språkkode.NB).medAntallTapteDager(5, BigDecimal.ZERO).build();
        var periode3 = AvslåttPeriode.ny().medPeriodeFom(PERIODE3_FOM, Språkkode.NB).medPeriodeTom(PERIODE3_TOM, Språkkode.NB).medAntallTapteDager(6, BigDecimal.ZERO).build();

        // Act
        var resultat = AvslåttPeriodeMerger.mergePerioder(asList(periode1, periode2, periode3));

        // Assert
        assertThat(resultat).hasSize(2);
        assertThat(resultat.get(0).getPeriodeFom()).isEqualTo(PERIODE1_FOM);
        assertThat(resultat.get(0).getPeriodeTom()).isEqualTo(PERIODE2_TOM);
        assertThat(resultat.get(0).getAntallTapteDager()).isEqualTo(9);
        assertThat(resultat.get(1).getPeriodeFom()).isEqualTo(PERIODE3_FOM);
        assertThat(resultat.get(1).getPeriodeTom()).isEqualTo(PERIODE3_TOM);
        assertThat(resultat.get(1).getAntallTapteDager()).isEqualTo(6);
    }

    @Test
    public void skal_slå_sammen_perioder_med_samme_avslagsårsak() {
        // Arrange
        var periode1 = AvslåttPeriode.ny().medPeriodeFom(PERIODE1_FOM, Språkkode.NB).medPeriodeTom(PERIODE1_TOM, Språkkode.NB).medAvslagsårsak(Årsak.of("4086")).medAntallTapteDager(4, BigDecimal.ZERO).build();
        var periode2 = AvslåttPeriode.ny().medPeriodeFom(PERIODE2_FOM, Språkkode.NB).medPeriodeTom(PERIODE2_TOM, Språkkode.NB).medAvslagsårsak(Årsak.of("4086")).medAntallTapteDager(5, BigDecimal.ZERO).build();

        // Act
        var resultat = AvslåttPeriodeMerger.mergePerioder(asList(periode1, periode2));

        // Assert
        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getAvslagsårsak()).isEqualTo(Årsak.of("4086"));
        assertThat(resultat.get(0).getPeriodeFom()).isEqualTo(PERIODE1_FOM);
        assertThat(resultat.get(0).getPeriodeTom()).isEqualTo(PERIODE2_TOM);
        assertThat(resultat.get(0).getAntallTapteDager()).isEqualTo(9);
    }

    @Test
    public void skal_ikke_slå_sammen_perioder_med_forskjellig_avslagsårsak() {
        // Arrange
        var periode1 = AvslåttPeriode.ny().medPeriodeFom(PERIODE1_FOM, Språkkode.NB).medPeriodeTom(PERIODE1_TOM, Språkkode.NB).medAvslagsårsak(Årsak.of("4086")).medAntallTapteDager(4, BigDecimal.ZERO).build();
        var periode2 = AvslåttPeriode.ny().medPeriodeFom(PERIODE2_FOM, Språkkode.NB).medPeriodeTom(PERIODE2_TOM, Språkkode.NB).medAvslagsårsak(Årsak.of("4055")).medAntallTapteDager(5, BigDecimal.ZERO).build();

        // Act
        var resultat = AvslåttPeriodeMerger.mergePerioder(asList(periode1, periode2));

        // Assert
        assertThat(resultat).hasSize(2);
        assertThat(resultat.get(0).getAvslagsårsak()).isEqualTo(Årsak.of("4086"));
        assertThat(resultat.get(0).getPeriodeFom()).isEqualTo(PERIODE1_FOM);
        assertThat(resultat.get(0).getPeriodeTom()).isEqualTo(PERIODE1_TOM);
        assertThat(resultat.get(0).getAntallTapteDager()).isEqualTo(4);
        assertThat(resultat.get(1).getAvslagsårsak()).isEqualTo(Årsak.of("4055"));
        assertThat(resultat.get(1).getPeriodeFom()).isEqualTo(PERIODE2_FOM);
        assertThat(resultat.get(1).getPeriodeTom()).isEqualTo(PERIODE2_TOM);
        assertThat(resultat.get(1).getAntallTapteDager()).isEqualTo(5);
    }

    @Test
    public void skal_slå_sammen_perioder_med_forskjellige_årsaker_som_er_regnet_som_like() {
        // Arrange
        var periode1 = AvslåttPeriode.ny().medPeriodeFom(PERIODE1_FOM, Språkkode.NB).medPeriodeTom(PERIODE1_TOM, Språkkode.NB).medAvslagsårsak(Årsak.of("4040")).medAntallTapteDager(4, BigDecimal.valueOf(4)).build();
        var periode2 = AvslåttPeriode.ny().medPeriodeFom(PERIODE2_FOM, Språkkode.NB).medPeriodeTom(PERIODE2_TOM, Språkkode.NB).medAvslagsårsak(Årsak.of("4112")).medAntallTapteDager(5, BigDecimal.valueOf(5.5)).build();

        // Act
        var resultat = AvslåttPeriodeMerger.mergePerioder(asList(periode1, periode2));

        // Assert
        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getAvslagsårsak()).isEqualTo(Årsak.of("4040"));
        assertThat(resultat.get(0).getPeriodeFom()).isEqualTo(PERIODE1_FOM);
        assertThat(resultat.get(0).getPeriodeTom()).isEqualTo(PERIODE2_TOM);
        assertThat(resultat.get(0).getAntallTapteDager()).isEqualTo(9);
    }

}
