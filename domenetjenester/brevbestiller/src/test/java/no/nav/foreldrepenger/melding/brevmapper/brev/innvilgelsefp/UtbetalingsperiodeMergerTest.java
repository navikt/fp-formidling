package no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp;

import static java.util.Arrays.asList;
import static java.util.List.of;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.UtbetalingsperiodeMerger.erFomRettEtterTomDato;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.UtbetalingsperiodeMerger.likeAktiviteter;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.AnnenAktivitet;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.Arbeidsforhold;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.Næring;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.Prosent;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.Utbetalingsperiode;

public class UtbetalingsperiodeMergerTest {

    private static final LocalDate PERIODE1_FOM = LocalDate.now().minusDays(10);
    private static final LocalDate PERIODE1_TOM = LocalDate.now().plusDays(5);
    private static final LocalDate PERIODE2_FOM = LocalDate.now().plusDays(6);
    private static final LocalDate PERIODE2_TOM = LocalDate.now().plusDays(10);
    private static final LocalDate PERIODE3_FOM = LocalDate.now().plusDays(20);
    private static final LocalDate PERIODE3_TOM = LocalDate.now().plusDays(30);

    @Test
    public void skal_slå_sammen_perioder_som_er_sammenhengende() {
        // Arrange
        Utbetalingsperiode utbetalingsperiode1 = Utbetalingsperiode.ny().medPeriodeFom(PERIODE1_FOM).medPeriodeTom(PERIODE1_TOM).medAntallTapteDager(4).build();
        Utbetalingsperiode utbetalingsperiode2 = Utbetalingsperiode.ny().medPeriodeFom(PERIODE2_FOM).medPeriodeTom(PERIODE2_TOM).medAntallTapteDager(5).build();
        Utbetalingsperiode utbetalingsperiode3 = Utbetalingsperiode.ny().medPeriodeFom(PERIODE3_FOM).medPeriodeTom(PERIODE3_TOM).medAntallTapteDager(6).build();

        // Act
        List<Utbetalingsperiode> resultat = UtbetalingsperiodeMerger.mergePerioder(asList(utbetalingsperiode1, utbetalingsperiode2, utbetalingsperiode3));

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
    public void skal_ikke_slå_sammen_perioder_med_forskjellig_dagsats() {
        // Arrange
        Utbetalingsperiode utbetalingsperiode1 = Utbetalingsperiode.ny().medPeriodeFom(PERIODE1_FOM).medPeriodeTom(PERIODE1_TOM).medPeriodeDagsats(1000).build();
        Utbetalingsperiode utbetalingsperiode2 = Utbetalingsperiode.ny().medPeriodeFom(PERIODE2_FOM).medPeriodeTom(PERIODE2_TOM).medPeriodeDagsats(2000).build();

        // Act
        List<Utbetalingsperiode> resultat = UtbetalingsperiodeMerger.mergePerioder(asList(utbetalingsperiode1, utbetalingsperiode2));

        // Assert
        assertThat(resultat).hasSize(2);
        assertThat(resultat.get(0).getPeriodeFom()).isEqualTo(PERIODE1_FOM);
        assertThat(resultat.get(0).getPeriodeTom()).isEqualTo(PERIODE1_TOM);
        assertThat(resultat.get(1).getPeriodeFom()).isEqualTo(PERIODE2_FOM);
        assertThat(resultat.get(1).getPeriodeTom()).isEqualTo(PERIODE2_TOM);
    }

    @Test
    public void skal_returnere_enkeltperiode() {
        // Arrange
        List<Utbetalingsperiode> utbetalingsperiode = of(Utbetalingsperiode.ny().medPeriodeFom(PERIODE1_FOM).medPeriodeTom(PERIODE1_TOM).medAntallTapteDager(5).build());

        // Act
        List<Utbetalingsperiode> resultat = UtbetalingsperiodeMerger.mergePerioder(utbetalingsperiode);

        // Assert
        assertThat(resultat).isEqualTo(utbetalingsperiode);
    }

    @Test
    public void skal_finne_at_periode_2_er_rett_etter_periode_1() {
        // Arrange
        Utbetalingsperiode utbetalingsperiode1 = Utbetalingsperiode.ny().medPeriodeFom(PERIODE1_FOM).medPeriodeTom(PERIODE1_TOM).build();
        Utbetalingsperiode utbetalingsperiode2 = Utbetalingsperiode.ny().medPeriodeFom(PERIODE2_FOM).medPeriodeTom(PERIODE2_TOM).build();

        // Act
        boolean resultat = erFomRettEtterTomDato(utbetalingsperiode1, utbetalingsperiode2);

        // Assert
        assertThat(resultat).isTrue();
    }

    @Test
    public void skal_finne_at_periode_2_ikke_er_rett_etter_periode_1() {
        // Arrange
        Utbetalingsperiode utbetalingsperiode1 = Utbetalingsperiode.ny().medPeriodeFom(LocalDate.now().minusDays(10)).medPeriodeTom(LocalDate.now().plusDays(3)).build();
        Utbetalingsperiode utbetalingsperiode2 = Utbetalingsperiode.ny().medPeriodeFom(LocalDate.now().plusDays(8)).medPeriodeTom(LocalDate.now().plusDays(10)).build();

        // Act
        boolean resultat = erFomRettEtterTomDato(utbetalingsperiode1, utbetalingsperiode2);

        // Assert
        assertThat(resultat).isFalse();
    }

    @Test
    public void skal_finne_like_aktiviteter_når_ikke_satt() {
        assertThat(likeAktiviteter(Utbetalingsperiode.ny().build(), Utbetalingsperiode.ny().build())).isTrue();
    }

    @Test
    public void skal_finne_at_aktiviteter_er_ulike() {
        Utbetalingsperiode utbetalingsperiode1 = Utbetalingsperiode.ny().build();
        Utbetalingsperiode utbetalingsperiode2 = Utbetalingsperiode.ny().medNæring(Næring.ny().build()).build();
        assertThat(likeAktiviteter(utbetalingsperiode1, utbetalingsperiode2)).isFalse();

        utbetalingsperiode2 = Utbetalingsperiode.ny().medArbeidsforhold(of(Arbeidsforhold.ny().build())).build();
        assertThat(likeAktiviteter(utbetalingsperiode1, utbetalingsperiode2)).isFalse();

        utbetalingsperiode2 = Utbetalingsperiode.ny().medAnnenAktivitet(of(AnnenAktivitet.ny().build())).build();
        assertThat(likeAktiviteter(utbetalingsperiode1, utbetalingsperiode2)).isFalse();
    }

    @Test
    public void skal_finne_like_aktiviteter_kompleks_næring() {
        Utbetalingsperiode utbetalingsperiode1 = Utbetalingsperiode.ny().build();
        Utbetalingsperiode utbetalingsperiode2 = Utbetalingsperiode.ny().build();

        assertThat(likeAktiviteter(utbetalingsperiode1, utbetalingsperiode2)).isTrue();

        utbetalingsperiode1 = Utbetalingsperiode.ny().medNæring(Næring.ny().build()).build();
        assertThat(likeAktiviteter(utbetalingsperiode1, utbetalingsperiode2)).isFalse();

        utbetalingsperiode2 = Utbetalingsperiode.ny().medNæring(Næring.ny().medSistLignedeÅr(2020).build()).build();
        assertThat(likeAktiviteter(utbetalingsperiode1, utbetalingsperiode2)).isFalse();

        utbetalingsperiode1 = Utbetalingsperiode.ny().medNæring(Næring.ny().medSistLignedeÅr(2020).build()).build();
        assertThat(likeAktiviteter(utbetalingsperiode1, utbetalingsperiode2)).isTrue();

        utbetalingsperiode1 = Utbetalingsperiode.ny().medNæring(Næring.ny().medSistLignedeÅr(2020).medUtbetalingsgrad(Prosent.of(BigDecimal.ONE)).build()).build();
        assertThat(likeAktiviteter(utbetalingsperiode1, utbetalingsperiode2)).isTrue();

        utbetalingsperiode1 = Utbetalingsperiode.ny().medNæring(Næring.ny().medSistLignedeÅr(2019).medAktivitetDagsats(100).build()).build();
        assertThat(likeAktiviteter(utbetalingsperiode1, utbetalingsperiode2)).isFalse();

        utbetalingsperiode2 = Utbetalingsperiode.ny().medNæring(Næring.ny().medSistLignedeÅr(2019).medAktivitetDagsats(100).build()).build();
        assertThat(likeAktiviteter(utbetalingsperiode1, utbetalingsperiode2)).isTrue();

        utbetalingsperiode2 = Utbetalingsperiode.ny().medNæring(Næring.ny().medSistLignedeÅr(2019).medAktivitetDagsats(200).build()).build();
        assertThat(likeAktiviteter(utbetalingsperiode1, utbetalingsperiode2)).isFalse();
    }

    @Test
    public void skal_finne_like_aktiviteter_kompleks_annen_aktivitet() {
        Utbetalingsperiode utbetalingsperiode1 = Utbetalingsperiode.ny().medAnnenAktivitet(of()).build();
        Utbetalingsperiode utbetalingsperiode2 = Utbetalingsperiode.ny().medAnnenAktivitet(of()).build();

        assertThat(likeAktiviteter(utbetalingsperiode1, utbetalingsperiode2)).isTrue();

        utbetalingsperiode1 = Utbetalingsperiode.ny().medAnnenAktivitet(of(AnnenAktivitet.ny().medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER.name()).medAktivitetDagsats(200).build())).build();
        assertThat(likeAktiviteter(utbetalingsperiode1, utbetalingsperiode2)).isFalse();

        utbetalingsperiode2 = Utbetalingsperiode.ny().medAnnenAktivitet(of(AnnenAktivitet.ny().medAktivitetStatus(AktivitetStatus.FRILANSER.name()).medAktivitetDagsats(100).build())).build();
        assertThat(likeAktiviteter(utbetalingsperiode1, utbetalingsperiode2)).isFalse();

        utbetalingsperiode2 = Utbetalingsperiode.ny().medAnnenAktivitet(of(AnnenAktivitet.ny().medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER.name()).medAktivitetDagsats(100).build())).build();
        assertThat(likeAktiviteter(utbetalingsperiode1, utbetalingsperiode2)).isFalse();

        utbetalingsperiode2 = Utbetalingsperiode.ny().medAnnenAktivitet(of(AnnenAktivitet.ny().medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER.name()).medAktivitetDagsats(200).build())).build();
        assertThat(likeAktiviteter(utbetalingsperiode1, utbetalingsperiode2)).isTrue();
    }

    @Test
    public void skal_finne_like_aktiviteter_kompleks_arbeidsforhold() {
        Utbetalingsperiode utbetalingsperiode1 = Utbetalingsperiode.ny().medArbeidsforhold(of()).build();
        Utbetalingsperiode utbetalingsperiode2 = Utbetalingsperiode.ny().medArbeidsforhold(of()).build();
        assertThat(likeAktiviteter(utbetalingsperiode1, utbetalingsperiode2)).isTrue();

        utbetalingsperiode1 = Utbetalingsperiode.ny().medArbeidsforhold(of(Arbeidsforhold.ny().medGradering(false).medNaturalytelseEndringDato("1").build())).build();
        assertThat(likeAktiviteter(utbetalingsperiode1, utbetalingsperiode2)).isFalse();

        utbetalingsperiode2 = Utbetalingsperiode.ny().medArbeidsforhold(of(Arbeidsforhold.ny().medGradering(true).medNaturalytelseEndringDato("1").build())).build();
        assertThat(likeAktiviteter(utbetalingsperiode1, utbetalingsperiode2)).isTrue();

        utbetalingsperiode1 = Utbetalingsperiode.ny().medArbeidsforhold(of(Arbeidsforhold.ny().medGradering(false).medNaturalytelseEndringDato("2").medNaturalytelseNyDagsats(200L).build())).build();
        utbetalingsperiode2 = Utbetalingsperiode.ny().medArbeidsforhold(of(Arbeidsforhold.ny().medGradering(false).medNaturalytelseEndringDato("2").build())).build();
        assertThat(likeAktiviteter(utbetalingsperiode1, utbetalingsperiode2)).isFalse();

        utbetalingsperiode1 = Utbetalingsperiode.ny().medArbeidsforhold(of(Arbeidsforhold.ny().medArbeidsgiverNavn("navn1").build())).build();
        utbetalingsperiode2 = Utbetalingsperiode.ny().medArbeidsforhold(of(Arbeidsforhold.ny().medArbeidsgiverNavn("navn1").build())).build();
        assertThat(likeAktiviteter(utbetalingsperiode1, utbetalingsperiode2)).isTrue();

        utbetalingsperiode1 = Utbetalingsperiode.ny().medArbeidsforhold(of(Arbeidsforhold.ny().medArbeidsgiverNavn("navn1").build())).build();
        utbetalingsperiode2 = Utbetalingsperiode.ny().medArbeidsforhold(of(Arbeidsforhold.ny().medArbeidsgiverNavn("navn2").build())).build();
        assertThat(likeAktiviteter(utbetalingsperiode1, utbetalingsperiode2)).isFalse();

        utbetalingsperiode1 = Utbetalingsperiode.ny().medArbeidsforhold(of(Arbeidsforhold.ny().medAktivitetDagsats(1).build())).build();
        utbetalingsperiode2 = Utbetalingsperiode.ny().medArbeidsforhold(of(Arbeidsforhold.ny().medAktivitetDagsats(1).build())).build();
        assertThat(likeAktiviteter(utbetalingsperiode1, utbetalingsperiode2)).isTrue();

        utbetalingsperiode1 = Utbetalingsperiode.ny().medArbeidsforhold(of(Arbeidsforhold.ny().medAktivitetDagsats(1).build())).build();
        utbetalingsperiode2 = Utbetalingsperiode.ny().medArbeidsforhold(of(Arbeidsforhold.ny().medAktivitetDagsats(2).build())).build();
        assertThat(likeAktiviteter(utbetalingsperiode1, utbetalingsperiode2)).isFalse();
    }

    @Test
    public void skal_slå_sammen_perioder_med_like_årsaker() {
        // Arrange
        Utbetalingsperiode utbetalingsperiode1 = Utbetalingsperiode.ny().medÅrsak("2001").medPeriodeFom(PERIODE1_FOM).medPeriodeTom(PERIODE1_TOM).medAntallTapteDager(4).build();
        Utbetalingsperiode utbetalingsperiode2 = Utbetalingsperiode.ny().medÅrsak("2001").medPeriodeFom(PERIODE2_FOM).medPeriodeTom(PERIODE2_TOM).medAntallTapteDager(5).build();

        // Act
        List<Utbetalingsperiode> resultat = UtbetalingsperiodeMerger.mergePerioder(asList(utbetalingsperiode1, utbetalingsperiode2));

        // Assert
        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getPeriodeFom()).isEqualTo(PERIODE1_FOM);
        assertThat(resultat.get(0).getPeriodeTom()).isEqualTo(PERIODE2_TOM);
        assertThat(resultat.get(0).getAntallTapteDager()).isEqualTo(9);
    }

    @Test
    public void skal_slå_sammen_perioder_med_forskjellige_årsaker_som_er_regnet_som_like() {
        // Arrange
        Utbetalingsperiode utbetalingsperiode1 = Utbetalingsperiode.ny().medÅrsak("4040").medPeriodeFom(PERIODE1_FOM).medPeriodeTom(PERIODE1_TOM).medAntallTapteDager(4).build();
        Utbetalingsperiode utbetalingsperiode2 = Utbetalingsperiode.ny().medÅrsak("4112").medPeriodeFom(PERIODE2_FOM).medPeriodeTom(PERIODE2_TOM).medAntallTapteDager(5).build();
        Utbetalingsperiode utbetalingsperiode3 = Utbetalingsperiode.ny().medÅrsak("2003").medPeriodeFom(PERIODE2_TOM.plusDays(1)).medPeriodeTom(PERIODE2_TOM.plusDays(2)).medAntallTapteDager(1).build();
        Utbetalingsperiode utbetalingsperiode4 = Utbetalingsperiode.ny().medÅrsak("2007").medPeriodeFom(PERIODE2_TOM.plusDays(3)).medPeriodeTom(PERIODE2_TOM.plusDays(4)).medAntallTapteDager(2).build();

        // Act
        List<Utbetalingsperiode> resultat = UtbetalingsperiodeMerger.mergePerioder(asList(utbetalingsperiode1, utbetalingsperiode2, utbetalingsperiode3, utbetalingsperiode4));

        // Assert
        assertThat(resultat).hasSize(2);
        assertThat(resultat.get(0).getPeriodeFom()).isEqualTo(PERIODE1_FOM);
        assertThat(resultat.get(0).getPeriodeTom()).isEqualTo(PERIODE2_TOM);
        assertThat(resultat.get(0).getAntallTapteDager()).isEqualTo(9);
        assertThat(resultat.get(1).getPeriodeFom()).isEqualTo(PERIODE2_TOM.plusDays(1));
        assertThat(resultat.get(1).getPeriodeTom()).isEqualTo(PERIODE2_TOM.plusDays(4));
        assertThat(resultat.get(1).getAntallTapteDager()).isEqualTo(3);
    }

    @Test
    public void skal_ikke_slå_sammen_perioder_med_forskjellige_årsaker_som_ikke_er_regnet_som_like() {
        // Arrange
        Utbetalingsperiode utbetalingsperiode1 = Utbetalingsperiode.ny().medÅrsak("4030").medPeriodeFom(PERIODE1_FOM).medPeriodeTom(PERIODE1_TOM).medAntallTapteDager(4).build();
        Utbetalingsperiode utbetalingsperiode2 = Utbetalingsperiode.ny().medÅrsak("4502").medPeriodeFom(PERIODE2_FOM).medPeriodeTom(PERIODE2_TOM).medAntallTapteDager(5).build();

        // Act
        List<Utbetalingsperiode> resultat = UtbetalingsperiodeMerger.mergePerioder(asList(utbetalingsperiode1, utbetalingsperiode2));

        // Assert
        assertThat(resultat).hasSize(2);
        assertThat(resultat.get(0).getPeriodeFom()).isEqualTo(PERIODE1_FOM);
        assertThat(resultat.get(0).getPeriodeTom()).isEqualTo(PERIODE1_TOM);
        assertThat(resultat.get(0).getAntallTapteDager()).isEqualTo(4);
        assertThat(resultat.get(1).getPeriodeFom()).isEqualTo(PERIODE2_FOM);
        assertThat(resultat.get(1).getPeriodeTom()).isEqualTo(PERIODE2_TOM);
        assertThat(resultat.get(1).getAntallTapteDager()).isEqualTo(5);
    }

    @Test
    public void skal_slå_sammen_innvilgede_perioder() {
        // Arrange
        Utbetalingsperiode utbetalingsperiode1 = Utbetalingsperiode.ny().medInnvilget(true).medPeriodeFom(PERIODE1_FOM).medPeriodeTom(PERIODE1_TOM).medAntallTapteDager(4).build();
        Utbetalingsperiode utbetalingsperiode2 = Utbetalingsperiode.ny().medInnvilget(true).medPeriodeFom(PERIODE2_FOM).medPeriodeTom(PERIODE2_TOM).medAntallTapteDager(5).build();

        // Act
        List<Utbetalingsperiode> resultat = UtbetalingsperiodeMerger.mergePerioder(asList(utbetalingsperiode1, utbetalingsperiode2));

        // Assert
        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getPeriodeFom()).isEqualTo(PERIODE1_FOM);
        assertThat(resultat.get(0).getPeriodeTom()).isEqualTo(PERIODE2_TOM);
        assertThat(resultat.get(0).getAntallTapteDager()).isEqualTo(9);
    }

    @Test
    public void skal_slå_sammen_avslåtte_perioder() {
        // Arrange
        Utbetalingsperiode utbetalingsperiode1 = Utbetalingsperiode.ny().medInnvilget(false).medPeriodeFom(PERIODE1_FOM).medPeriodeTom(PERIODE1_TOM).medAntallTapteDager(4).build();
        Utbetalingsperiode utbetalingsperiode2 = Utbetalingsperiode.ny().medInnvilget(false).medPeriodeFom(PERIODE2_FOM).medPeriodeTom(PERIODE2_TOM).medAntallTapteDager(5).build();

        // Act
        List<Utbetalingsperiode> resultat = UtbetalingsperiodeMerger.mergePerioder(asList(utbetalingsperiode1, utbetalingsperiode2));

        // Assert
        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getPeriodeFom()).isEqualTo(PERIODE1_FOM);
        assertThat(resultat.get(0).getPeriodeTom()).isEqualTo(PERIODE2_TOM);
        assertThat(resultat.get(0).getAntallTapteDager()).isEqualTo(9);
    }

    @Test
    public void skal_ikke_slå_sammen_innvilgede_perioder_med_avslåtte_perioder() {
        // Arrange
        Utbetalingsperiode utbetalingsperiode1 = Utbetalingsperiode.ny().medInnvilget(false).medPeriodeFom(PERIODE1_FOM).medPeriodeTom(PERIODE1_TOM).medAntallTapteDager(4).build();
        Utbetalingsperiode utbetalingsperiode2 = Utbetalingsperiode.ny().medInnvilget(true).medPeriodeFom(PERIODE2_FOM).medPeriodeTom(PERIODE2_TOM).medAntallTapteDager(5).build();

        // Act
        List<Utbetalingsperiode> resultat = UtbetalingsperiodeMerger.mergePerioder(asList(utbetalingsperiode1, utbetalingsperiode2));

        // Assert
        assertThat(resultat).hasSize(2);
        assertThat(resultat.get(0).getPeriodeFom()).isEqualTo(PERIODE1_FOM);
        assertThat(resultat.get(0).getPeriodeTom()).isEqualTo(PERIODE1_TOM);
        assertThat(resultat.get(0).getAntallTapteDager()).isEqualTo(4);
        assertThat(resultat.get(1).getPeriodeFom()).isEqualTo(PERIODE2_FOM);
        assertThat(resultat.get(1).getPeriodeTom()).isEqualTo(PERIODE2_TOM);
        assertThat(resultat.get(1).getAntallTapteDager()).isEqualTo(5);
    }
}