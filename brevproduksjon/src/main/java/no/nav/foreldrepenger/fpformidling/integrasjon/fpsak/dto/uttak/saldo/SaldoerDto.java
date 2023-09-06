package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.uttak.saldo;

import java.util.Map;

import no.nav.foreldrepenger.fpformidling.uttak.fp.SaldoVisningStønadskontoType;

public record SaldoerDto(Map<SaldoVisningStønadskontoType, StønadskontoDto> stonadskontoer, int tapteDagerFpff) {

}
