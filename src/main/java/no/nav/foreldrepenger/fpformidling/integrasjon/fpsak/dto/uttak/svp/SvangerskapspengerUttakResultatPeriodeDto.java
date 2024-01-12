package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.uttak.svp;

import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.PeriodeResultatType;
import no.nav.foreldrepenger.fpformidling.domene.uttak.svp.PeriodeIkkeOppfyltÅrsak;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SvangerskapspengerUttakResultatPeriodeDto(BigDecimal utbetalingsgrad, PeriodeResultatType periodeResultatType,
                                                        PeriodeIkkeOppfyltÅrsak periodeIkkeOppfyltÅrsak, LocalDate fom, LocalDate tom) {

}
