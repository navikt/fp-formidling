package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.uttak;

import java.util.List;

public record UttakResultatPerioderDto(List<UttakResultatPeriodeDto> perioderSøker, List<UttakResultatPeriodeDto> perioderAnnenpart) {
}
