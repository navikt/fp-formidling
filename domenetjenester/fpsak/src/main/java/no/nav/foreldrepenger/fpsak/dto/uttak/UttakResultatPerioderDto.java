package no.nav.foreldrepenger.fpsak.dto.uttak;

import java.util.List;

public class UttakResultatPerioderDto {

    private List<UttakResultatPeriodeDto> perioderSøker;
    private List<UttakResultatPeriodeDto> perioderAnnenpart;

    public List<UttakResultatPeriodeDto> getPerioderSøker() {
        return perioderSøker;
    }

    public List<UttakResultatPeriodeDto> getPerioderAnnenpart() {
        return perioderAnnenpart;
    }

}
