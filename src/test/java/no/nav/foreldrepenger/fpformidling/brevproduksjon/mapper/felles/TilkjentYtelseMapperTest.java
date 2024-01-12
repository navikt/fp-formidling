package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.domene.tilkjentytelse.TilkjentYtelseAndel;
import no.nav.foreldrepenger.fpformidling.domene.tilkjentytelse.TilkjentYtelseForeldrepenger;
import no.nav.foreldrepenger.fpformidling.domene.tilkjentytelse.TilkjentYtelsePeriode;
import no.nav.foreldrepenger.fpformidling.domene.typer.DatoIntervall;
import no.nav.foreldrepenger.fpformidling.domene.virksomhet.Arbeidsgiver;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static java.util.List.of;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.TilkjentYtelseMapper.*;
import static org.assertj.core.api.Assertions.assertThat;

class TilkjentYtelseMapperTest {

    private static final DatoIntervall UBETYDELIG_PERIODE = DatoIntervall.fraOgMedTilOgMed(LocalDate.now(), LocalDate.now().plusDays(1));

    @Test
    void skal_finne_månedsbeløp() {
        // Arrange
        var tilkjentYtelse = TilkjentYtelseForeldrepenger.ny()
            .leggTilPerioder(of(TilkjentYtelsePeriode.ny().medDagsats(100L).medPeriode(UBETYDELIG_PERIODE).build(),
                TilkjentYtelsePeriode.ny().medDagsats(100L * 2).medPeriode(UBETYDELIG_PERIODE).build()))
            .build();

        // Act + Assert
        assertThat(finnMånedsbeløp(tilkjentYtelse)).isEqualTo(2166);
    }

    @Test
    void skal_finne_dagsats() {
        // Arrange
        var tilkjentYtelse = TilkjentYtelseForeldrepenger.ny()
            .leggTilPerioder(of(TilkjentYtelsePeriode.ny().medDagsats(100L).medPeriode(UBETYDELIG_PERIODE).build(),
                TilkjentYtelsePeriode.ny().medDagsats(100L * 2).medPeriode(UBETYDELIG_PERIODE).build()))
            .build();

        // Act + Assert
        assertThat(finnDagsats(tilkjentYtelse)).isEqualTo(100L);
    }

    @Test
    void skal_finne_antall_arbeidsgivere() {
        // Arrange
        var tilkjentYtelse = TilkjentYtelseForeldrepenger.ny()
            .leggTilPerioder(of(TilkjentYtelsePeriode.ny()
                .medPeriode(UBETYDELIG_PERIODE)
                .medAndeler(of(TilkjentYtelseAndel.ny()
                    .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                    .medArbeidsgiver(new Arbeidsgiver("Navn1", "Referanse1")) // #1
                    .build(), TilkjentYtelseAndel.ny()
                    .medAktivitetStatus(AktivitetStatus.FRILANSER)
                    .medArbeidsgiver(new Arbeidsgiver("Navn2", "Referanse2")) // Frilanser
                    .build()))
                .build(), TilkjentYtelsePeriode.ny()
                .medPeriode(UBETYDELIG_PERIODE)
                .medAndeler(of(TilkjentYtelseAndel.ny()
                    .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                    .medArbeidsgiver(new Arbeidsgiver("Navn1", "Referanse1")) // Duplikat
                    .build(), TilkjentYtelseAndel.ny()
                    .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                    .medArbeidsgiver(new Arbeidsgiver("Navn3", "Referanse3")) // #2
                    .build(), TilkjentYtelseAndel.ny()
                    .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                    .medArbeidsgiver(new Arbeidsgiver("Navn4", "Referanse4")) // #3
                    .build()))
                .build()))
            .build();

        // Act + Assert
        assertThat(finnAntallArbeidsgivere(tilkjentYtelse)).isEqualTo(3);
    }

    @Test
    void skal_finne_at_bruker_har_full_refusjon() {
        // Arrange
        var tilkjentYtelse = TilkjentYtelseForeldrepenger.ny()
            .leggTilPerioder(of(TilkjentYtelsePeriode.ny()
                .medPeriode(UBETYDELIG_PERIODE)
                .medAndeler(of(TilkjentYtelseAndel.ny()
                    .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                    .medArbeidsgiver(new Arbeidsgiver("Navn1", "Referanse1"))
                    .medErBrukerMottaker(false)
                    .build()))
                .build(), TilkjentYtelsePeriode.ny()
                .medPeriode(UBETYDELIG_PERIODE)
                .medAndeler(of(TilkjentYtelseAndel.ny()
                    .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                    .medArbeidsgiver(new Arbeidsgiver("Navn2", "Referanse2"))
                    .medErArbeidsgiverMottaker(true)
                    .build()))
                .build()))
            .build();

        // Act + Assert
        assertThat(harFullRefusjon(tilkjentYtelse)).isTrue();
    }

    @Test
    void skal_finne_at_bruker_har_ingen_refusjon() {
        // Arrange
        var tilkjentYtelse = TilkjentYtelseForeldrepenger.ny()
            .leggTilPerioder(of(TilkjentYtelsePeriode.ny()
                .medPeriode(UBETYDELIG_PERIODE)
                .medAndeler(of(TilkjentYtelseAndel.ny()
                    .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                    .medArbeidsgiver(new Arbeidsgiver("Navn1", "Referanse1"))
                    .medErBrukerMottaker(true)
                    .build()))
                .build(), TilkjentYtelsePeriode.ny()
                .medPeriode(UBETYDELIG_PERIODE)
                .medAndeler(of(TilkjentYtelseAndel.ny()
                    .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                    .medArbeidsgiver(new Arbeidsgiver("Navn2", "Referanse2"))
                    .medErArbeidsgiverMottaker(false)
                    .build()))
                .build()))
            .build();

        // Act + Assert
        assertThat(harIngenRefusjon(tilkjentYtelse)).isTrue();
    }

    @Test
    void skal_finne_at_bruker_har_ingen_refusjon_når_ingen_er_mottakere() {
        // Arrange
        var tilkjentYtelse = TilkjentYtelseForeldrepenger.ny()
            .leggTilPerioder(of(TilkjentYtelsePeriode.ny()
                .medPeriode(UBETYDELIG_PERIODE)
                .medAndeler(of(TilkjentYtelseAndel.ny()
                    .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                    .medArbeidsgiver(new Arbeidsgiver("Navn1", "Referanse1"))
                    .medErBrukerMottaker(false)
                    .build()))
                .build(), TilkjentYtelsePeriode.ny()
                .medPeriode(UBETYDELIG_PERIODE)
                .medAndeler(of(TilkjentYtelseAndel.ny()
                    .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                    .medArbeidsgiver(new Arbeidsgiver("Navn2", "Referanse2"))
                    .medErArbeidsgiverMottaker(false)
                    .build()))
                .build()))
            .build();

        // Act + Assert
        assertThat(harIngenRefusjon(tilkjentYtelse)).isTrue();
    }

    @Test
    void skal_finne_at_bruker_har_delvis_refusjon() {
        // Arrange
        var tilkjentYtelse = TilkjentYtelseForeldrepenger.ny()
            .leggTilPerioder(of(TilkjentYtelsePeriode.ny()
                .medPeriode(UBETYDELIG_PERIODE)
                .medAndeler(of(TilkjentYtelseAndel.ny()
                    .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                    .medArbeidsgiver(new Arbeidsgiver("Navn1", "Referanse1"))
                    .medErBrukerMottaker(true)
                    .build()))
                .build(), TilkjentYtelsePeriode.ny()
                .medPeriode(UBETYDELIG_PERIODE)
                .medAndeler(of(TilkjentYtelseAndel.ny()
                    .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                    .medArbeidsgiver(new Arbeidsgiver("Navn2", "Referanse2"))
                    .medErArbeidsgiverMottaker(true)
                    .build()))
                .build()))
            .build();

        // Act + Assert
        assertThat(harDelvisRefusjon(tilkjentYtelse)).isTrue();
    }

    @Test
    void skal_finne_at_bruker_har_utbetaling() {
        // Arrange
        var tilkjentYtelse = TilkjentYtelseForeldrepenger.ny()
            .leggTilPerioder(of(TilkjentYtelsePeriode.ny()
                .medPeriode(UBETYDELIG_PERIODE)
                .medAndeler(of(TilkjentYtelseAndel.ny().medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER).medDagsats(0).build(),
                    TilkjentYtelseAndel.ny().medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER).medDagsats(0).build()))
                .build(), TilkjentYtelsePeriode.ny()
                .medPeriode(UBETYDELIG_PERIODE)
                .medAndeler(of(TilkjentYtelseAndel.ny().medAktivitetStatus(AktivitetStatus.FRILANSER).medDagsats(100).build()))
                .build()))
            .build();

        // Act + Assert
        assertThat(harUtbetaling(tilkjentYtelse)).isTrue();
    }

    @Test
    void skal_finne_at_bruker_ikke_har_utbetaling() {
        // Arrange
        var tilkjentYtelse = TilkjentYtelseForeldrepenger.ny()
            .leggTilPerioder(of(TilkjentYtelsePeriode.ny()
                .medPeriode(UBETYDELIG_PERIODE)
                .medAndeler(of(TilkjentYtelseAndel.ny().medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER).medDagsats(0).build()))
                .build()))
            .build();

        // Act + Assert
        assertThat(harUtbetaling(tilkjentYtelse)).isFalse();
    }
}
