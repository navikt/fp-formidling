package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.tilkjentytelse;

import java.math.BigDecimal;

import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.AktivitetStatus;

public record TilkjentYtelseAndelDto(String arbeidsgiverReferanse,
                                     Integer refusjon,
                                     Integer tilSoker,
                                     AktivitetStatus aktivitetStatus,
                                     String arbeidsforholdId,
                                     BigDecimal stillingsprosent) {
}
