package no.nav.foreldrepenger.melding.datamapper.domene;

import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.VurderingsstatusKode;
import no.nav.foreldrepenger.melding.søknad.Søknad;
import no.nav.foreldrepenger.melding.ytelsefordeling.YtelseFordeling;

public class YtelsefordelingMapper {

    public static VurderingsstatusKode harSøkerAleneomsorg(Søknad søknad, YtelseFordeling ytelseFordeling) {
        if (!søknad.getOppgittRettighet().isHarAleneomsorgForBarnet()) {
            return VurderingsstatusKode.IKKE_VURDERT;
        }
        if (ytelseFordeling.isHarPerioderMedAleneomsorg()) {
            return VurderingsstatusKode.JA;
        }
        return VurderingsstatusKode.NEI;
    }
}
