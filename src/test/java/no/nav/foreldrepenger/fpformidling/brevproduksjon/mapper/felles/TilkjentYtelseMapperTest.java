package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import static java.util.List.of;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.TilkjentYtelseMapper.finnAntallArbeidsgivere;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.TilkjentYtelseMapper.finnDagsats;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.TilkjentYtelseMapper.finnMånedsbeløp;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.TilkjentYtelseMapper.harDelvisRefusjon;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.TilkjentYtelseMapper.harFullRefusjon;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.TilkjentYtelseMapper.harIngenRefusjon;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.TilkjentYtelseMapper.harUtbetaling;
import static no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseDagytelseDto.Aktivitetstatus;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseDagytelseDto;
import no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseDagytelseDto.TilkjentYtelseAndelDto;
import no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseDagytelseDto.TilkjentYtelsePeriodeDto;

class TilkjentYtelseMapperTest {

    private static final LocalDate START_DATE = LocalDate.now();
    private static final LocalDate END_DATE = LocalDate.now().plusDays(1);

    @Test
    void skal_finne_månedsbeløp() {
        // Arrange
        var tilkjentYtelse = new TilkjentYtelseDagytelseDto(
            of(new TilkjentYtelsePeriodeDto(START_DATE, END_DATE, 100, of()), new TilkjentYtelsePeriodeDto(START_DATE, END_DATE, 200, of())));

        // Act + Assert
        assertThat(finnMånedsbeløp(tilkjentYtelse)).isEqualTo(2166);
    }

    @Test
    void skal_finne_dagsats() {
        // Arrange
        var tilkjentYtelse = new TilkjentYtelseDagytelseDto(
            of(new TilkjentYtelsePeriodeDto(START_DATE, END_DATE, 100, of()), new TilkjentYtelsePeriodeDto(START_DATE, END_DATE, 200, of())));

        // Act + Assert
        assertThat(finnDagsats(tilkjentYtelse)).isEqualTo(100L);
    }

    @Test
    void skal_finne_antall_arbeidsgivere() {
        // Arrange
        var andel1 = new TilkjentYtelseAndelDto("Referanse1", 100, 100, Aktivitetstatus.ARBEIDSTAKER, null, BigDecimal.ZERO, BigDecimal.ZERO);
        var andel2 = new TilkjentYtelseAndelDto("Referanse2", 100, 100, Aktivitetstatus.FRILANSER, null, BigDecimal.ZERO,
            BigDecimal.ZERO); // Frilanser
        var andel3 = new TilkjentYtelseAndelDto("Referanse1", 100, 100, Aktivitetstatus.ARBEIDSTAKER, null, BigDecimal.ZERO,
            BigDecimal.ZERO); // Duplikat
        var andel4 = new TilkjentYtelseAndelDto("Referanse3", 100, 100, Aktivitetstatus.ARBEIDSTAKER, null, BigDecimal.ZERO, BigDecimal.ZERO); // #2
        var andel5 = new TilkjentYtelseAndelDto("Referanse4", 100, 100, Aktivitetstatus.ARBEIDSTAKER, null, BigDecimal.ZERO, BigDecimal.ZERO); // #3

        var tilkjentYtelse = new TilkjentYtelseDagytelseDto(of(new TilkjentYtelsePeriodeDto(START_DATE, END_DATE, 100, of(andel1, andel2)),
            new TilkjentYtelsePeriodeDto(START_DATE, END_DATE, 100, of(andel3, andel4, andel5))));

        // Act + Assert
        assertThat(finnAntallArbeidsgivere(tilkjentYtelse)).isEqualTo(3);
    }

    @Test
    void skal_finne_at_bruker_har_full_refusjon() {
        // Arrange
        var andel1 = new TilkjentYtelseAndelDto("Referanse1", 100, 0, Aktivitetstatus.ARBEIDSTAKER, null, BigDecimal.valueOf(100), BigDecimal.ZERO);
        var andel2 = new TilkjentYtelseAndelDto("Referanse2", 100, 0, Aktivitetstatus.ARBEIDSTAKER, null, BigDecimal.valueOf(100), BigDecimal.ZERO);

        var tilkjentYtelse = new TilkjentYtelseDagytelseDto(of(new TilkjentYtelsePeriodeDto(START_DATE, END_DATE, 100, of(andel1)),
            new TilkjentYtelsePeriodeDto(START_DATE, END_DATE, 100, of(andel2))));

        // Act + Assert
        assertThat(harFullRefusjon(tilkjentYtelse)).isTrue();
    }

    @Test
    void skal_finne_at_bruker_har_ingen_refusjon() {
        // Arrange
        var andel1 = new TilkjentYtelseAndelDto("Referanse1", 0, 100, Aktivitetstatus.ARBEIDSTAKER, null, BigDecimal.ZERO, BigDecimal.valueOf(100));
        var andel2 = new TilkjentYtelseAndelDto("Referanse2", 0, 100, Aktivitetstatus.ARBEIDSTAKER, null, BigDecimal.ZERO, BigDecimal.valueOf(100));

        var tilkjentYtelse = new TilkjentYtelseDagytelseDto(of(new TilkjentYtelsePeriodeDto(START_DATE, END_DATE, 100, of(andel1)),
            new TilkjentYtelsePeriodeDto(START_DATE, END_DATE, 100, of(andel2))));

        // Act + Assert
        assertThat(harIngenRefusjon(tilkjentYtelse)).isTrue();
    }

    @Test
    void skal_finne_at_bruker_har_ingen_refusjon_når_ingen_er_mottakere() {
        // Arrange
        var andel1 = new TilkjentYtelseAndelDto("Referanse1", 0, 0, Aktivitetstatus.ARBEIDSTAKER, null, BigDecimal.ZERO, BigDecimal.ZERO);
        var andel2 = new TilkjentYtelseAndelDto("Referanse2", 0, 0, Aktivitetstatus.ARBEIDSTAKER, null, BigDecimal.ZERO, BigDecimal.ZERO);

        var tilkjentYtelse = new TilkjentYtelseDagytelseDto(of(new TilkjentYtelsePeriodeDto(START_DATE, END_DATE, 100, of(andel1)),
            new TilkjentYtelsePeriodeDto(START_DATE, END_DATE, 100, of(andel2))));

        // Act + Assert
        assertThat(harIngenRefusjon(tilkjentYtelse)).isTrue();
    }

    @Test
    void skal_finne_at_bruker_har_delvis_refusjon() {
        // Arrange
        var andel1 = new TilkjentYtelseAndelDto("Referanse1", 100, 100, Aktivitetstatus.ARBEIDSTAKER, null, BigDecimal.ZERO, BigDecimal.valueOf(100));
        var andel2 = new TilkjentYtelseAndelDto("Referanse2", 100, 100, Aktivitetstatus.ARBEIDSTAKER, null, BigDecimal.valueOf(100), BigDecimal.ZERO);

        var tilkjentYtelse = new TilkjentYtelseDagytelseDto(of(new TilkjentYtelsePeriodeDto(START_DATE, END_DATE, 100, of(andel1)),
            new TilkjentYtelsePeriodeDto(START_DATE, END_DATE, 100, of(andel2))));

        // Act + Assert
        assertThat(harDelvisRefusjon(tilkjentYtelse)).isTrue();
    }

    @Test
    void skal_finne_at_bruker_har_utbetaling() {
        // Arrange
        var andel1 = new TilkjentYtelseAndelDto("Referanse1", 0, 0, Aktivitetstatus.ARBEIDSTAKER, null, BigDecimal.ZERO, BigDecimal.ZERO);
        var andel2 = new TilkjentYtelseAndelDto("Referanse2", 0, 0, Aktivitetstatus.ARBEIDSTAKER, null, BigDecimal.ZERO, BigDecimal.ZERO);
        var andel3 = new TilkjentYtelseAndelDto("Referanse3", 100, 100, Aktivitetstatus.FRILANSER, null, BigDecimal.ZERO, BigDecimal.valueOf(100));

        var tilkjentYtelse = new TilkjentYtelseDagytelseDto(of(new TilkjentYtelsePeriodeDto(START_DATE, END_DATE, 0, of(andel1, andel2)),
            new TilkjentYtelsePeriodeDto(START_DATE, END_DATE, 100, of(andel3))));

        // Act + Assert
        assertThat(harUtbetaling(tilkjentYtelse)).isTrue();
    }

    @Test
    void skal_finne_at_bruker_ikke_har_utbetaling() {
        // Arrange
        var andel1 = new TilkjentYtelseAndelDto("Referanse1", 0, 0, Aktivitetstatus.ARBEIDSTAKER, null, BigDecimal.ZERO, BigDecimal.ZERO);

        var tilkjentYtelse = new TilkjentYtelseDagytelseDto(of(new TilkjentYtelsePeriodeDto(START_DATE, END_DATE, 0, of(andel1))));

        // Act + Assert
        assertThat(harUtbetaling(tilkjentYtelse)).isFalse();
    }
}
