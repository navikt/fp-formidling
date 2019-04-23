package no.nav.foreldrepenger.fpsak.dto.uttak;

import java.util.List;

public class UttakResultatPerioderDto {

    private List<UttakResultatPeriodeDto> perioderSøker;
    private List<UttakResultatPeriodeDto> perioderAnnenpart;

    public UttakResultatPerioderDto(List<UttakResultatPeriodeDto> perioderSøker, List<UttakResultatPeriodeDto> perioderAnnenpart) {
        this.perioderSøker = perioderSøker;
        this.perioderAnnenpart = perioderAnnenpart;
    }

    public List<UttakResultatPeriodeDto> getPerioderSøker() {
        return perioderSøker;
    }

    public List<UttakResultatPeriodeDto> getPerioderAnnenpart() {
        return perioderAnnenpart;
    }

}
