package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import static java.util.List.of;
import static no.nav.foreldrepenger.fpformidling.typer.DatoIntervall.fraOgMedTilOgMed;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.domene.tilkjentytelse.TilkjentYtelseAndel;
import no.nav.foreldrepenger.fpformidling.domene.tilkjentytelse.TilkjentYtelsePeriode;
import no.nav.foreldrepenger.fpformidling.domene.virksomhet.Arbeidsgiver;

class SvpMapperUtilTest {

    private static final LocalDate tilkjentYtelseFraDato = LocalDate.of(2023, 5, 15);
    private static final LocalDate tilkjentYtelseTilDato = tilkjentYtelseFraDato.plusDays(2);
    private static final LocalDate tilkjentYtelsePeriode2FraDato = tilkjentYtelseFraDato.plusDays(3);


    @ParameterizedTest
    @MethodSource("tilkjentListeMedForventetDato")
    void finnRiktigOpphørsdatoJustertForHelgSøndag(List<TilkjentYtelsePeriode> tilkjentYtelsePeriodeStream, LocalDate forventetDato) {

        var opphørsdato = SvpMapperUtil.finnOpphørsdato(tilkjentYtelsePeriodeStream);

        assertThat(opphørsdato).isEqualTo(Optional.of(forventetDato));
    }

    static Stream<Arguments> tilkjentListeMedForventetDato() {
        return Stream.of(Arguments.of(liste1(), LocalDate.of(2023, 5, 22)),
            Arguments.of(liste2(), LocalDate.of(2023, 5, 18)),
            Arguments.of(liste3(), LocalDate.of(2023, 5, 22)));
    }

    static List<TilkjentYtelsePeriode> liste1() {
        return List.of(opprettTilkjentYtelse(tilkjentYtelseFraDato, tilkjentYtelseTilDato, 500L),
            opprettTilkjentYtelse(tilkjentYtelsePeriode2FraDato, tilkjentYtelseFraDato.plusDays(5), 200L));
    }
    static List<TilkjentYtelsePeriode> liste2() {
        return List.of(opprettTilkjentYtelse(tilkjentYtelseFraDato, tilkjentYtelseTilDato, 500L),
            opprettTilkjentYtelse(tilkjentYtelsePeriode2FraDato, tilkjentYtelseFraDato.plusDays(6), 0L));
    }

    static List<TilkjentYtelsePeriode> liste3() {
        return List.of(opprettTilkjentYtelse(tilkjentYtelseFraDato, tilkjentYtelseTilDato, 500L),
            opprettTilkjentYtelse(tilkjentYtelsePeriode2FraDato, tilkjentYtelseFraDato.plusDays(4), 200L));
    }
    private static TilkjentYtelsePeriode opprettTilkjentYtelse(LocalDate fraDato, LocalDate tilDato, Long dagsats) {
        return TilkjentYtelsePeriode.ny()
                .medDagsats(dagsats)
                .medPeriode(fraOgMedTilOgMed(fraDato, tilDato))
                .medAndeler(of(TilkjentYtelseAndel.ny()
                    .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                    .medArbeidsgiver(new Arbeidsgiver("123456", "ARBEIDSGIVER_NAVN"))
                    .medStillingsprosent(BigDecimal.valueOf(100))
                    .build()))
                .build();
    }
}
