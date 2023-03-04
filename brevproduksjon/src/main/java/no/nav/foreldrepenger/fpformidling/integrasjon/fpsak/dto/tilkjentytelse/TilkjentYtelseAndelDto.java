package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.tilkjentytelse;

import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.AktivitetStatus;

import java.math.BigDecimal;

public record TilkjentYtelseAndelDto(String arbeidsgiverReferanse, Integer refusjon, Integer tilSoker, AktivitetStatus aktivitetStatus,
                                     String arbeidsforholdId, BigDecimal stillingsprosent) {
}
