package no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp;

import static java.util.List.of;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.UtbetalingsperiodeMergerFelles.erFomRettEtterTomDato;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.UtbetalingsperiodeMergerFelles.likeAktiviteter;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.UtbetalingsperiodeMergerFelles.slåSammenPerioder;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.AnnenAktivitet;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.Arbeidsforhold;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.Næring;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.Utbetalingsperiode;

public class UtbetalingsperiodeMergerFellesTest {

    @Test
    public void skal_slå_sammen_perioder() {
        // Arrange
        Utbetalingsperiode utbetalingsperiode1 = Utbetalingsperiode.ny().medPeriodeFom(LocalDate.now().minusDays(10)).medPeriodeTom(LocalDate.now().plusDays(5)).medAntallTapteDager(5).build();
        Utbetalingsperiode utbetalingsperiode2 = Utbetalingsperiode.ny().medPeriodeFom(LocalDate.now().plusDays(6)).medPeriodeTom(LocalDate.now().plusDays(10)).medAntallTapteDager(5).build();

        // Act
        Utbetalingsperiode resultat = slåSammenPerioder(utbetalingsperiode1, utbetalingsperiode2);

        // Assert
        assertThat(resultat.getPeriodeFom()).isEqualTo(LocalDate.now().minusDays(10));
        assertThat(resultat.getPeriodeTom()).isEqualTo(LocalDate.now().plusDays(10));
        assertThat(resultat.getAntallTapteDager()).isEqualTo(10);
    }

    @Test
    public void skal_finne_at_periode_2_er_rett_etter_periode_1() {
        // Arrange
        Utbetalingsperiode utbetalingsperiode1 = Utbetalingsperiode.ny().medPeriodeFom(LocalDate.now().minusDays(10)).medPeriodeTom(LocalDate.now().plusDays(5)).build();
        Utbetalingsperiode utbetalingsperiode2 = Utbetalingsperiode.ny().medPeriodeFom(LocalDate.now().plusDays(6)).medPeriodeTom(LocalDate.now().plusDays(10)).build();

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

        utbetalingsperiode1 = Utbetalingsperiode.ny().medNæring(Næring.ny().medSistLignedeÅr(2020).medUtbetalingsgrad(BigDecimal.ONE).build()).build();
        assertThat(likeAktiviteter(utbetalingsperiode1, utbetalingsperiode2)).isTrue();

        utbetalingsperiode1 = Utbetalingsperiode.ny().medNæring(Næring.ny().medSistLignedeÅr(2019).build()).build();
        assertThat(likeAktiviteter(utbetalingsperiode1, utbetalingsperiode2)).isFalse();

        utbetalingsperiode2 = Utbetalingsperiode.ny().medNæring(Næring.ny().medSistLignedeÅr(2019).build()).build();
        assertThat(likeAktiviteter(utbetalingsperiode1, utbetalingsperiode2)).isTrue();
    }

    @Test
    public void skal_finne_like_aktiviteter_kompleks_annen_aktivitet() {
        Utbetalingsperiode utbetalingsperiode1 = Utbetalingsperiode.ny().medAnnenAktivitet(of()).build();
        Utbetalingsperiode utbetalingsperiode2 = Utbetalingsperiode.ny().medAnnenAktivitet(of()).build();

        assertThat(likeAktiviteter(utbetalingsperiode1, utbetalingsperiode2)).isTrue();

        utbetalingsperiode1 = Utbetalingsperiode.ny().medAnnenAktivitet(of(AnnenAktivitet.ny().medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER.name()).build())).build();
        assertThat(likeAktiviteter(utbetalingsperiode1, utbetalingsperiode2)).isFalse();

        utbetalingsperiode2 = Utbetalingsperiode.ny().medAnnenAktivitet(of(AnnenAktivitet.ny().medAktivitetStatus(AktivitetStatus.FRILANSER.name()).build())).build();
        assertThat(likeAktiviteter(utbetalingsperiode1, utbetalingsperiode2)).isFalse();

        utbetalingsperiode2 = Utbetalingsperiode.ny().medAnnenAktivitet(of(AnnenAktivitet.ny().medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER.name()).build())).build();
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
    }
}