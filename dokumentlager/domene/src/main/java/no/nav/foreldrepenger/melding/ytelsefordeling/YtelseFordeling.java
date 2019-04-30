package no.nav.foreldrepenger.melding.ytelsefordeling;

import no.nav.foreldrepenger.melding.fagsak.Dekningsgrad;

public class YtelseFordeling {
    Dekningsgrad dekningsgrad;
    private boolean annenForelderHarRett;
    private boolean harPerioderMedAleneomsorg;


    public YtelseFordeling(Dekningsgrad dekningsgrad, boolean annenForelderHarRett, boolean harPerioderMedAleneomsorg) {
        this.dekningsgrad = dekningsgrad;
        this.annenForelderHarRett = annenForelderHarRett;
        this.harPerioderMedAleneomsorg = harPerioderMedAleneomsorg;
    }

    public Dekningsgrad getDekningsgrad() {
        return dekningsgrad;
    }

    public boolean isAnnenForelderHarRett() {
        return annenForelderHarRett;
    }

    public boolean isHarPerioderMedAleneomsorg() {
        return harPerioderMedAleneomsorg;
    }
}
