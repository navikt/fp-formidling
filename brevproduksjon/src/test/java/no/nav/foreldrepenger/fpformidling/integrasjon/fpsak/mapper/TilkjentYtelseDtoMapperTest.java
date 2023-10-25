package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper;

import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseDagytelseDto;

import no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseEngangsstønadDto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class TilkjentYtelseDtoMapperTest {
    private static final String AG_NAVN = "NAV";
    private static String hentAGNavn(String ref) {
        return AG_NAVN;
    }
    @Test
    void skal_teste_tilkjent_ytelse_dagytelse_mapper() {
        // Arrange
        var andelDto = new TilkjentYtelseDagytelseDto.TilkjentYtelseAndelDto("999999999", 100, 200, TilkjentYtelseDagytelseDto.Aktivitetstatus.ARBEIDSTAKER, "arb1", BigDecimal.valueOf(100));
        var periodeDto = new TilkjentYtelseDagytelseDto.TilkjentYtelsePeriodeDto(LocalDate.of(2021, 1, 1), LocalDate.of(2021,12,31), 200, Collections.singletonList(andelDto));
        var resultatDto = new TilkjentYtelseDagytelseDto(Collections.singletonList(periodeDto));

        // Act
        var resultatDomene = TilkjentYtelseDtoMapper.mapTilkjentYtelseDagytelseFraDto(resultatDto,
            TilkjentYtelseDtoMapperTest::hentAGNavn);
        assertThat(resultatDomene).isNotNull();
        assertThat(resultatDomene.getPerioder()).hasSameSizeAs(resultatDto.perioder());

        var periodeDomene = resultatDomene.getPerioder().get(0);
        assertThat(periodeDomene.getPeriodeFom()).isEqualTo(periodeDto.fom());
        assertThat(periodeDomene.getPeriodeTom()).isEqualTo(periodeDto.tom());
        assertThat(periodeDomene.getDagsats()).isEqualTo(periodeDto.dagsats().longValue());
        assertThat(periodeDomene.getUtbetaltTilSøker()).isEqualTo(andelDto.tilSoker().longValue());
        assertThat(periodeDomene.getAndeler()).hasSameSizeAs(periodeDto.andeler());

        var andelDomene = periodeDomene.getAndeler().get(0);
        assertThat(andelDomene.getDagsats()).isEqualTo(andelDto.tilSoker() + andelDto.refusjon());
        assertThat(andelDomene.getStillingsprosent()).isEqualByComparingTo(andelDto.stillingsprosent());
        assertThat(andelDomene.getUtbetalesTilBruker()).isEqualTo(andelDto.tilSoker());
        assertThat(andelDomene.getAktivitetStatus()).isEqualTo(AktivitetStatus.ARBEIDSTAKER);
        assertThat(andelDomene.getArbeidsgiver()).isPresent();
        assertThat(andelDomene.getArbeidsgiver().get().arbeidsgiverReferanse()).isEqualTo(andelDto.arbeidsgiverReferanse());
        assertThat(andelDomene.getArbeidsgiver().get().navn()).isEqualTo(AG_NAVN);
        assertThat(andelDomene.getArbeidsforholdRef().getReferanse()).isEqualTo(andelDto.arbeidsforholdId());
    }

    @Test
    void skal_teste_tilkjent_ytelse_engangsstønad_mapper() {
        // Arrange
        var resultatDto = new TilkjentYtelseEngangsstønadDto(90000L);

        // Act
        var resultatDomene = TilkjentYtelseDtoMapper.mapTilkjentYtelseESFraDto(resultatDto);

        // Assert
        assertThat(resultatDomene).isNotNull();
        assertThat(resultatDomene.beløp()).isEqualTo(resultatDto.beregnetTilkjentYtelse());
    }

}
