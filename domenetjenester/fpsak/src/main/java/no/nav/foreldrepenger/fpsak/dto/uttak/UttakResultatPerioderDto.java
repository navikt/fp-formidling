package no.nav.foreldrepenger.fpsak.dto.uttak;

import java.util.List;

public class UttakResultatPerioderDto {


    private List<UttakResultatPeriodeDto> perioderSøker;
    private List<UttakResultatPeriodeDto> perioderAnnenpart;
    private boolean annenForelderHarRett;
    private boolean aleneomsorg;

    public List<UttakResultatPeriodeDto> getPerioderSøker() {
        return perioderSøker;
    }

    public List<UttakResultatPeriodeDto> getPerioderAnnenpart() {
        return perioderAnnenpart;
    }

    public boolean isAnnenForelderHarRett() {
        return annenForelderHarRett;
    }

    public boolean isAleneomsorg() {
        return aleneomsorg;
    }

}
