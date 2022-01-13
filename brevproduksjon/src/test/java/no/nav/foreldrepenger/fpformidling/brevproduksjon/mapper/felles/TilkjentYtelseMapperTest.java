package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import static java.util.List.of;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.TilkjentYtelseMapper.finnAntallArbeidsgivere;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.TilkjentYtelseMapper.finnDagsats;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.TilkjentYtelseMapper.finnMånedsbeløp;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.TilkjentYtelseMapper.harDelvisRefusjon;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.TilkjentYtelseMapper.harFullRefusjon;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.TilkjentYtelseMapper.harIngenRefusjon;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.TilkjentYtelseMapper.harUtbetaling;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelseAndel;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelseForeldrepenger;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelsePeriode;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.typer.DatoIntervall;
import no.nav.foreldrepenger.fpformidling.virksomhet.Arbeidsgiver;

public class TilkjentYtelseMapperTest {

    private static final DatoIntervall UBETYDELIG_PERIODE = DatoIntervall.fraOgMedTilOgMed(LocalDate.now(), LocalDate.now().plusDays(1));

    @Test
    public void skal_finne_månedsbeløp() {
        // Arrange
        TilkjentYtelseForeldrepenger tilkjentYtelse = TilkjentYtelseForeldrepenger.ny()
                .leggTilPerioder(of(
                        TilkjentYtelsePeriode.ny()
                                .medDagsats(100L)
                                .medPeriode(UBETYDELIG_PERIODE)
                                .build(),
                        TilkjentYtelsePeriode.ny()
                                .medDagsats(100L * 2)
                                .medPeriode(UBETYDELIG_PERIODE)
                                .build()))
                .build();

        // Act + Assert
        assertThat(finnMånedsbeløp(tilkjentYtelse)).isEqualTo(2166);
    }

    @Test
    public void skal_finne_dagsats() {
        // Arrange
        TilkjentYtelseForeldrepenger tilkjentYtelse = TilkjentYtelseForeldrepenger.ny()
                .leggTilPerioder(of(
                        TilkjentYtelsePeriode.ny()
                                .medDagsats(100L)
                                .medPeriode(UBETYDELIG_PERIODE)
                                .build(),
                        TilkjentYtelsePeriode.ny()
                                .medDagsats(100L * 2)
                                .medPeriode(UBETYDELIG_PERIODE)
                                .build()))
                .build();

        // Act + Assert
        assertThat(finnDagsats(tilkjentYtelse)).isEqualTo(100L);
    }

    @Test
    public void skal_finne_antall_arbeidsgivere() {
        // Arrange
        TilkjentYtelseForeldrepenger tilkjentYtelse = TilkjentYtelseForeldrepenger.ny()
                .leggTilPerioder(of(
                        TilkjentYtelsePeriode.ny()
                                .medPeriode(UBETYDELIG_PERIODE)
                                .medAndeler(of(
                                        TilkjentYtelseAndel.ny()
                                                .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                                                .medArbeidsgiver(new Arbeidsgiver("Navn1", "Referanse1")) // #1
                                                .build(),
                                        TilkjentYtelseAndel.ny()
                                                .medAktivitetStatus(AktivitetStatus.FRILANSER)
                                                .medArbeidsgiver(new Arbeidsgiver("Navn2", "Referanse2")) // Frilanser
                                                .build()
                                        ))
                                .build(),
                        TilkjentYtelsePeriode.ny()
                                .medPeriode(UBETYDELIG_PERIODE)
                                .medAndeler(of(
                                        TilkjentYtelseAndel.ny()
                                                .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                                                .medArbeidsgiver(new Arbeidsgiver("Navn1", "Referanse1")) // Duplikat
                                                .build(),
                                        TilkjentYtelseAndel.ny()
                                                .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                                                .medArbeidsgiver(new Arbeidsgiver("Navn3", "Referanse3")) // #2
                                                .build(),
                                        TilkjentYtelseAndel.ny()
                                                .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                                                .medArbeidsgiver(new Arbeidsgiver("Navn4", "Referanse4")) // #3
                                                .build()
                                ))
                                .build()))
                .build();

        // Act + Assert
        assertThat(finnAntallArbeidsgivere(tilkjentYtelse)).isEqualTo(3);
    }

    @Test
    public void skal_finne_at_bruker_har_full_refusjon() {
        // Arrange
        TilkjentYtelseForeldrepenger tilkjentYtelse = TilkjentYtelseForeldrepenger.ny()
                .leggTilPerioder(of(
                        TilkjentYtelsePeriode.ny()
                                .medPeriode(UBETYDELIG_PERIODE)
                                .medAndeler(of(
                                        TilkjentYtelseAndel.ny()
                                                .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                                                .medArbeidsgiver(new Arbeidsgiver("Navn1", "Referanse1"))
                                                .medErBrukerMottaker(false)
                                                .build()
                                ))
                                .build(),
                        TilkjentYtelsePeriode.ny()
                                .medPeriode(UBETYDELIG_PERIODE)
                                .medAndeler(of(
                                        TilkjentYtelseAndel.ny()
                                                .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                                                .medArbeidsgiver(new Arbeidsgiver("Navn2", "Referanse2"))
                                                .medErArbeidsgiverMottaker(true)
                                                .build()
                                ))
                                .build()))
                .build();

        // Act + Assert
        assertThat(harFullRefusjon(tilkjentYtelse)).isTrue();
    }

    @Test
    public void skal_finne_at_bruker_har_ingen_refusjon() {
        // Arrange
        TilkjentYtelseForeldrepenger tilkjentYtelse = TilkjentYtelseForeldrepenger.ny()
                .leggTilPerioder(of(
                        TilkjentYtelsePeriode.ny()
                                .medPeriode(UBETYDELIG_PERIODE)
                                .medAndeler(of(
                                        TilkjentYtelseAndel.ny()
                                                .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                                                .medArbeidsgiver(new Arbeidsgiver("Navn1", "Referanse1"))
                                                .medErBrukerMottaker(true)
                                                .build()
                                ))
                                .build(),
                        TilkjentYtelsePeriode.ny()
                                .medPeriode(UBETYDELIG_PERIODE)
                                .medAndeler(of(
                                        TilkjentYtelseAndel.ny()
                                                .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                                                .medArbeidsgiver(new Arbeidsgiver("Navn2", "Referanse2"))
                                                .medErArbeidsgiverMottaker(false)
                                                .build()
                                ))
                                .build()))
                .build();

        // Act + Assert
        assertThat(harIngenRefusjon(tilkjentYtelse)).isTrue();
    }

    @Test
    public void skal_finne_at_bruker_har_ingen_refusjon_når_ingen_er_mottakere() {
        // Arrange
        TilkjentYtelseForeldrepenger tilkjentYtelse = TilkjentYtelseForeldrepenger.ny()
                .leggTilPerioder(of(
                        TilkjentYtelsePeriode.ny()
                                .medPeriode(UBETYDELIG_PERIODE)
                                .medAndeler(of(
                                        TilkjentYtelseAndel.ny()
                                                .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                                                .medArbeidsgiver(new Arbeidsgiver("Navn1", "Referanse1"))
                                                .medErBrukerMottaker(false)
                                                .build()
                                ))
                                .build(),
                        TilkjentYtelsePeriode.ny()
                                .medPeriode(UBETYDELIG_PERIODE)
                                .medAndeler(of(
                                        TilkjentYtelseAndel.ny()
                                                .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                                                .medArbeidsgiver(new Arbeidsgiver("Navn2", "Referanse2"))
                                                .medErArbeidsgiverMottaker(false)
                                                .build()
                                ))
                                .build()))
                .build();

        // Act + Assert
        assertThat(harIngenRefusjon(tilkjentYtelse)).isTrue();
    }

    @Test
    public void skal_finne_at_bruker_har_delvis_refusjon() {
        // Arrange
        TilkjentYtelseForeldrepenger tilkjentYtelse = TilkjentYtelseForeldrepenger.ny()
                .leggTilPerioder(of(
                        TilkjentYtelsePeriode.ny()
                                .medPeriode(UBETYDELIG_PERIODE)
                                .medAndeler(of(
                                        TilkjentYtelseAndel.ny()
                                                .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                                                .medArbeidsgiver(new Arbeidsgiver("Navn1", "Referanse1"))
                                                .medErBrukerMottaker(true)
                                                .build()
                                ))
                                .build(),
                        TilkjentYtelsePeriode.ny()
                                .medPeriode(UBETYDELIG_PERIODE)
                                .medAndeler(of(
                                        TilkjentYtelseAndel.ny()
                                                .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                                                .medArbeidsgiver(new Arbeidsgiver("Navn2", "Referanse2"))
                                                .medErArbeidsgiverMottaker(true)
                                                .build()
                                ))
                                .build()))
                .build();

        // Act + Assert
        assertThat(harDelvisRefusjon(tilkjentYtelse)).isTrue();
    }

    @Test
    public void skal_finne_at_bruker_har_utbetaling() {
        // Arrange
        TilkjentYtelseForeldrepenger tilkjentYtelse = TilkjentYtelseForeldrepenger.ny()
                .leggTilPerioder(of(
                        TilkjentYtelsePeriode.ny()
                                .medPeriode(UBETYDELIG_PERIODE)
                                .medAndeler(of(
                                        TilkjentYtelseAndel.ny()
                                                .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                                                .medDagsats(0)
                                                .build(),
                                        TilkjentYtelseAndel.ny()
                                                .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                                                .medDagsats(0)
                                                .build()
                                ))
                                .build(),
                        TilkjentYtelsePeriode.ny()
                                .medPeriode(UBETYDELIG_PERIODE)
                                .medAndeler(of(
                                        TilkjentYtelseAndel.ny()
                                                .medAktivitetStatus(AktivitetStatus.FRILANSER)
                                                .medDagsats(100)
                                                .build()
                                ))
                                .build()))
                .build();

        // Act + Assert
        assertThat(harUtbetaling(tilkjentYtelse)).isTrue();
    }

    @Test
    public void skal_finne_at_bruker_ikke_har_utbetaling() {
        // Arrange
        TilkjentYtelseForeldrepenger tilkjentYtelse = TilkjentYtelseForeldrepenger.ny()
                .leggTilPerioder(of(
                        TilkjentYtelsePeriode.ny()
                                .medPeriode(UBETYDELIG_PERIODE)
                                .medAndeler(of(
                                        TilkjentYtelseAndel.ny()
                                                .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                                                .medDagsats(0)
                                                .build()
                                ))
                                .build()))
                .build();

        // Act + Assert
        assertThat(harUtbetaling(tilkjentYtelse)).isFalse();
    }
}