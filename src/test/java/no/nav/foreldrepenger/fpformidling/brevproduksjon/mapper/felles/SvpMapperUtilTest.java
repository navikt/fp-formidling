package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import static java.util.List.of;
import static no.nav.foreldrepenger.fpformidling.domene.typer.DatoIntervall.fraOgMedTilOgMed;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.SvpMapperUtil;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.domene.tilkjentytelse.TilkjentYtelseAndel;
import no.nav.foreldrepenger.fpformidling.domene.tilkjentytelse.TilkjentYtelsePeriode;
import no.nav.foreldrepenger.fpformidling.domene.virksomhet.Arbeidsgiver;

class SvpMapperUtilTest {

    private final LocalDate tilkjentYtelseFraDato = LocalDate.of(2023, 5, 15);
    private final LocalDate tilkjentYtelseTilDato = tilkjentYtelseFraDato.plusDays(2);
    private final LocalDate tilkjentYtelsePeriode2FraDato = tilkjentYtelseFraDato.plusDays(3);

    @Test
    void finnRiktigOpphørsdatoMedDagsats() {
        var forventetDato = LocalDate.of(2023, 5, 18);

        List<TilkjentYtelsePeriode> tilkjentYtelseperiodeListe = List.of(opprettTilkjentYtelse(tilkjentYtelseFraDato, tilkjentYtelseTilDato, 500L),
            opprettTilkjentYtelse(tilkjentYtelsePeriode2FraDato, tilkjentYtelseFraDato.plusDays(6), 0L));

        var opphørsdato = SvpMapperUtil.finnOpphørsdato(tilkjentYtelseperiodeListe);

        assertThat(opphørsdato).isEqualTo(Optional.of(forventetDato));
    }

    @Test
    void finnRiktigOpphørsdatoJustertForHelgLørdagOgSøndag() {
        var forventetDato = LocalDate.of(2023, 5, 22);

        List<TilkjentYtelsePeriode> tilkjentYtelseperiodeListe = List.of(opprettTilkjentYtelse(tilkjentYtelseFraDato, tilkjentYtelseTilDato, 500L),
            opprettTilkjentYtelse(tilkjentYtelsePeriode2FraDato, tilkjentYtelseFraDato.plusDays(4), 200L));

        var opphørsdato = SvpMapperUtil.finnOpphørsdato(tilkjentYtelseperiodeListe);

        assertThat(opphørsdato).isEqualTo(Optional.of(forventetDato));
    }

    @Test
    void finnRiktigOpphørsdatoJustertForHelgSøndag() {
        var forventetDato = LocalDate.of(2023, 5, 22);

        List<TilkjentYtelsePeriode> tilkjentYtelseperiodeListe = List.of(opprettTilkjentYtelse(tilkjentYtelseFraDato, tilkjentYtelseTilDato, 500L),
            opprettTilkjentYtelse(tilkjentYtelsePeriode2FraDato, tilkjentYtelseFraDato.plusDays(5), 200L));

        var opphørsdato = SvpMapperUtil.finnOpphørsdato(tilkjentYtelseperiodeListe);

        assertThat(opphørsdato).isEqualTo(Optional.of(forventetDato));
    }


    private TilkjentYtelsePeriode opprettTilkjentYtelse(LocalDate fraDato, LocalDate tilDato, Long dagsats) {
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
