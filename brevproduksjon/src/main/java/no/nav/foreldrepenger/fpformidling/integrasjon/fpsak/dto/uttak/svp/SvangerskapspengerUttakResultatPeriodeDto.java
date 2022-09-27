package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.uttak.svp;

import java.math.BigDecimal;
import java.time.LocalDate;

import no.nav.foreldrepenger.fpformidling.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.fpformidling.uttak.svp.PeriodeIkkeOppfyltÅrsak;

public record SvangerskapspengerUttakResultatPeriodeDto(BigDecimal utbetalingsgrad, PeriodeResultatType periodeResultatType,
                                                        PeriodeIkkeOppfyltÅrsak periodeIkkeOppfyltÅrsak, LocalDate fom, LocalDate tom) {

}
