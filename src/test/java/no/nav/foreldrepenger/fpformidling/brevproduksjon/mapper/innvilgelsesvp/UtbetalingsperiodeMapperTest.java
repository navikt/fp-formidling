package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsesvp;

import static java.util.List.of;
import static no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseDagytelseDto.Aktivitetstatus;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.function.UnaryOperator;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Prosent;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp.AktiviteterOgUtbetalingsperioder;
import no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseDagytelseDto.TilkjentYtelseAndelDto;
import no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseDagytelseDto.TilkjentYtelsePeriodeDto;

class UtbetalingsperiodeMapperTest {

    private static final String ARBEIDSGIVER_1 = "Arbeidsgiver1";
    private static final String ARBEIDSGIVER_2 = "Arbeidsgiver2";
    private static final String ORG_NR_1 = "1111";
    private static final String ORG_NR_2 = "2222";

    private static final LocalDate START = LocalDate.now();

    private static final UnaryOperator<String> HENT_NAVN = ref -> {
        if (ORG_NR_1.equals(ref)) {
            return ARBEIDSGIVER_1;
        } else if (ORG_NR_2.equals(ref)) {
            return ARBEIDSGIVER_2;
        }
        return "Ukjent arbeidsgiver";
    };

    @Test
    void skal_mappe_2_arbeidsforhold_og_3_Perioder() {
        // Arrange
        var andel1 = new TilkjentYtelseAndelDto(ORG_NR_1, 584, 0, Aktivitetstatus.ARBEIDSTAKER, null, BigDecimal.valueOf(80), BigDecimal.valueOf(100));
        var andel2 = new TilkjentYtelseAndelDto(ORG_NR_1, 0, 0, Aktivitetstatus.ARBEIDSTAKER, null, BigDecimal.ZERO, BigDecimal.ZERO);
        var andel3 = new TilkjentYtelseAndelDto(ORG_NR_2, 1012, 0, Aktivitetstatus.ARBEIDSTAKER, null, BigDecimal.valueOf(80), BigDecimal.valueOf(20));
        var andel4 = new TilkjentYtelseAndelDto(ORG_NR_1, 0, 584, Aktivitetstatus.ARBEIDSTAKER, null, BigDecimal.valueOf(20), BigDecimal.valueOf(100));

        var tilkjentPerioder = of(new TilkjentYtelsePeriodeDto(START, START.plusDays(3), 584, of(andel1, andel2)),
            new TilkjentYtelsePeriodeDto(START.plusDays(4), START.plusDays(7), 1596, of(andel3, andel4)));

        // Act
        var resultat = UtbetalingsperiodeMapper.mapUtbetalingsperioderPerAktivitet(tilkjentPerioder, Språkkode.NB, HENT_NAVN);

        resultat.sort(Comparator.comparing(AktiviteterOgUtbetalingsperioder::beskrivelse));
        // Assert
        assertThat(resultat).hasSize(2);
        assertThat(resultat.get(0).beskrivelse()).isEqualTo(ARBEIDSGIVER_1);
        assertThat(resultat.get(0).utbetalingsperioder()).hasSize(2);
        //Utbet periode 1
        assertThat(resultat.get(0).utbetalingsperioder().get(0).getPeriodeFom()).isEqualTo(START);
        assertThat(resultat.get(0).utbetalingsperioder().get(0).getPeriodeTom()).isEqualTo(START.plusDays(3));
        assertThat(resultat.get(0).utbetalingsperioder().get(0).getDagsats()).isEqualTo(584);
        assertThat(resultat.get(0).utbetalingsperioder().get(0).getUtbetaltTilSøker()).isZero();
        assertThat(resultat.get(0).utbetalingsperioder().get(0).getUtbetalingsgrad()).isEqualTo(Prosent.of(100));
        //Utbet periode 2
        assertThat(resultat.get(0).utbetalingsperioder().get(1).getPeriodeFom()).isEqualTo(START.plusDays(4));
        assertThat(resultat.get(0).utbetalingsperioder().get(1).getPeriodeTom()).isEqualTo(START.plusDays(7));
        assertThat(resultat.get(0).utbetalingsperioder().get(1).getDagsats()).isEqualTo(584);
        assertThat(resultat.get(0).utbetalingsperioder().get(1).getUtbetaltTilSøker()).isEqualTo(584);
        assertThat(resultat.get(0).utbetalingsperioder().get(1).getUtbetalingsgrad()).isEqualTo(Prosent.of(100));

        assertThat(resultat.get(1).beskrivelse()).isEqualTo(ARBEIDSGIVER_2);
        assertThat(resultat.get(1).utbetalingsperioder()).hasSize(1);
        assertThat(resultat.get(1).utbetalingsperioder().get(0).getPeriodeFom()).isEqualTo(START.plusDays(4));
        assertThat(resultat.get(1).utbetalingsperioder().get(0).getPeriodeTom()).isEqualTo(START.plusDays(7));
        assertThat(resultat.get(1).utbetalingsperioder().get(0).getDagsats()).isEqualTo(1012);
        assertThat(resultat.get(1).utbetalingsperioder().get(0).getUtbetaltTilSøker()).isZero();
        assertThat(resultat.get(1).utbetalingsperioder().get(0).getUtbetalingsgrad()).isEqualTo(Prosent.of(20));
    }

    @Test
    void skal_mappe_1_arbeidsforhold_og_SN_og_3_Perioder() {
        // Arrange
        var andel1 = new TilkjentYtelseAndelDto(null, 584, 0, Aktivitetstatus.KOMBINERT_AT_SN, null, BigDecimal.valueOf(80), BigDecimal.valueOf(100));
        var andel2 = new TilkjentYtelseAndelDto(ORG_NR_2, 1012, 0, Aktivitetstatus.ARBEIDSTAKER, null, BigDecimal.valueOf(80), BigDecimal.valueOf(20));
        var andel3 = new TilkjentYtelseAndelDto(null, 0, 584, Aktivitetstatus.KOMBINERT_AT_SN, null, BigDecimal.valueOf(20), BigDecimal.valueOf(100));

        var tilkjentPerioder = of(new TilkjentYtelsePeriodeDto(START, START.plusDays(3), 584, of(andel1)),
            new TilkjentYtelsePeriodeDto(START.plusDays(4), START.plusDays(7), 1596, of(andel2, andel3)));

        // Act
        var resultat = UtbetalingsperiodeMapper.mapUtbetalingsperioderPerAktivitet(tilkjentPerioder, Språkkode.NB, HENT_NAVN);

        resultat.sort(Comparator.comparing(AktiviteterOgUtbetalingsperioder::beskrivelse));
        // Assert
        assertThat(resultat).hasSize(2);

        assertThat(resultat.get(0).beskrivelse()).isEqualTo(ARBEIDSGIVER_2);
        assertThat(resultat.get(0).utbetalingsperioder()).hasSize(1);
        assertThat(resultat.get(0).utbetalingsperioder().get(0).getPeriodeFom()).isEqualTo(START.plusDays(4));
        assertThat(resultat.get(0).utbetalingsperioder().get(0).getPeriodeTom()).isEqualTo(START.plusDays(7));
        assertThat(resultat.get(0).utbetalingsperioder().get(0).getDagsats()).isEqualTo(1012);
        assertThat(resultat.get(0).utbetalingsperioder().get(0).getUtbetaltTilSøker()).isZero();
        assertThat(resultat.get(0).utbetalingsperioder().get(0).getUtbetalingsgrad()).isEqualTo(Prosent.of(20));

        assertThat(resultat.get(1).beskrivelse()).isEqualTo("Som næringsdrivende");
        assertThat(resultat.get(1).utbetalingsperioder()).hasSize(2);
        //Utbet periode 1
        assertThat(resultat.get(1).utbetalingsperioder().get(0).getPeriodeFom()).isEqualTo(START);
        assertThat(resultat.get(1).utbetalingsperioder().get(0).getPeriodeTom()).isEqualTo(START.plusDays(3));
        assertThat(resultat.get(1).utbetalingsperioder().get(0).getDagsats()).isEqualTo(584);
        assertThat(resultat.get(1).utbetalingsperioder().get(0).getUtbetaltTilSøker()).isZero();
        assertThat(resultat.get(1).utbetalingsperioder().get(0).getUtbetalingsgrad()).isEqualTo(Prosent.of(100));
        //Utbet periode 2
        assertThat(resultat.get(1).utbetalingsperioder().get(1).getPeriodeFom()).isEqualTo(START.plusDays(4));
        assertThat(resultat.get(1).utbetalingsperioder().get(1).getPeriodeTom()).isEqualTo(START.plusDays(7));
        assertThat(resultat.get(1).utbetalingsperioder().get(1).getDagsats()).isEqualTo(584);
        assertThat(resultat.get(1).utbetalingsperioder().get(1).getUtbetaltTilSøker()).isEqualTo(584);
        assertThat(resultat.get(1).utbetalingsperioder().get(1).getUtbetalingsgrad()).isEqualTo(Prosent.of(100));
    }

    @Test
    void skal_slå_sammen_like_utbetalingsperioder() {
        // Arrange
        var andel1 = new TilkjentYtelseAndelDto(null, 584, 0, Aktivitetstatus.KOMBINERT_AT_SN, null, BigDecimal.valueOf(80), BigDecimal.valueOf(100));
        var andel2 = new TilkjentYtelseAndelDto(ORG_NR_2, 1012, 0, Aktivitetstatus.ARBEIDSTAKER, null, BigDecimal.valueOf(80), BigDecimal.valueOf(20));
        var andel3 = new TilkjentYtelseAndelDto(ORG_NR_2, 1012, 0, Aktivitetstatus.ARBEIDSTAKER, null, BigDecimal.valueOf(80), BigDecimal.valueOf(20));
        var andel4 = new TilkjentYtelseAndelDto(null, 0, 584, Aktivitetstatus.KOMBINERT_AT_SN, null, BigDecimal.valueOf(20), BigDecimal.valueOf(100));

        var tilkjentPerioder = of(new TilkjentYtelsePeriodeDto(START, START.plusDays(3), 1596, of(andel1, andel2)),
            new TilkjentYtelsePeriodeDto(START.plusDays(4), START.plusDays(7), 1596, of(andel3, andel4)));

        // Act
        var resultat = UtbetalingsperiodeMapper.mapUtbetalingsperioderPerAktivitet(tilkjentPerioder, Språkkode.NB, HENT_NAVN);

        resultat.sort(Comparator.comparing(AktiviteterOgUtbetalingsperioder::beskrivelse));
        // Assert
        assertThat(resultat).hasSize(2);

        assertThat(resultat.get(0).beskrivelse()).isEqualTo(ARBEIDSGIVER_2);
        assertThat(resultat.get(0).utbetalingsperioder()).hasSize(1);
        assertThat(resultat.get(0).utbetalingsperioder().get(0).getPeriodeFom()).isEqualTo(START);
        assertThat(resultat.get(0).utbetalingsperioder().get(0).getPeriodeTom()).isEqualTo(START.plusDays(7));
        assertThat(resultat.get(0).utbetalingsperioder().get(0).getDagsats()).isEqualTo(1012);
        assertThat(resultat.get(0).utbetalingsperioder().get(0).getUtbetaltTilSøker()).isZero();
        assertThat(resultat.get(0).utbetalingsperioder().get(0).getUtbetalingsgrad()).isEqualTo(Prosent.of(20));

        assertThat(resultat.get(1).beskrivelse()).isEqualTo("Som næringsdrivende");
        assertThat(resultat.get(1).utbetalingsperioder()).hasSize(2);
        //Utbet periode 1
        assertThat(resultat.get(1).utbetalingsperioder().get(0).getPeriodeFom()).isEqualTo(START);
        assertThat(resultat.get(1).utbetalingsperioder().get(0).getPeriodeTom()).isEqualTo(START.plusDays(3));
        assertThat(resultat.get(1).utbetalingsperioder().get(0).getDagsats()).isEqualTo(584);
        assertThat(resultat.get(1).utbetalingsperioder().get(0).getUtbetaltTilSøker()).isZero();
        assertThat(resultat.get(1).utbetalingsperioder().get(0).getUtbetalingsgrad()).isEqualTo(Prosent.of(100));
        //Utbet periode 2
        assertThat(resultat.get(1).utbetalingsperioder().get(1).getPeriodeFom()).isEqualTo(START.plusDays(4));
        assertThat(resultat.get(1).utbetalingsperioder().get(1).getPeriodeTom()).isEqualTo(START.plusDays(7));
        assertThat(resultat.get(1).utbetalingsperioder().get(1).getDagsats()).isEqualTo(584);
        assertThat(resultat.get(1).utbetalingsperioder().get(1).getUtbetaltTilSøker()).isEqualTo(584);
        assertThat(resultat.get(1).utbetalingsperioder().get(1).getUtbetalingsgrad()).isEqualTo(Prosent.of(100));
    }
}
