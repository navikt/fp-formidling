package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.uttak;

import java.util.List;

public record UttakResultatPerioderDto(List<UttakResultatPeriodeDto> perioderSøker,
                                       List<UttakResultatPeriodeDto> perioderAnnenpart,
                                       boolean annenForelderHarRett,
                                       boolean aleneomsorg,
                                       boolean annenForelderRettEØS,
                                       boolean oppgittAnnenForelderRettEØS
                                       ) {
}
