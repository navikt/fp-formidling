package no.nav.foreldrepenger.melding.brevmapper.brev.opphørfp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.enterprise.context.ApplicationScoped;

import no.nav.foreldrepenger.melding.Tuple;
import no.nav.foreldrepenger.melding.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.melding.behandling.ÅrsakMedLovReferanse;
import no.nav.foreldrepenger.melding.datamapper.domene.FellesMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.LovhjemmelUtil;
import no.nav.foreldrepenger.melding.datamapper.domene.sortering.LovhjemmelComparator;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPerioder;
import no.nav.foreldrepenger.melding.uttak.kodeliste.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.melding.vilkår.Avslagsårsak;

@ApplicationScoped
public class ÅrsakMapperOpphør {

    private static Set<String> lovReferanser;

    public static Tuple<List<String>, String> mapÅrsakslisteOgLovhjemmelFra(Behandlingsresultat behandlingsresultat,
                                                                      UttakResultatPerioder uttakResultatPerioder) {
        lovReferanser = new TreeSet<>(new LovhjemmelComparator());
        List<String> årsaksliste = new ArrayList<>();
        årsaksliste.addAll(årsakerFra(behandlingsresultat, uttakResultatPerioder));

        return new Tuple<>(årsaksliste, FellesMapper.formaterLovhjemlerUttak(lovReferanser));
    }

    private static Collection<String> årsakerFra(Behandlingsresultat behandlingsresultat,
                                                 UttakResultatPerioder uttakResultatPerioder) {
        Map<String, String> avslagårsaker = new HashMap<>();

        Avslagsårsak avslagsårsak = behandlingsresultat.getAvslagsårsak();
        if (avslagsårsak != null) {
            String avslagKode = avslagsårsak.getKode();
            avslagårsaker.put(avslagKode, årsaktypeFra(avslagsårsak));
        }
        for (UttakResultatPeriode periode : uttakResultatPerioder.getPerioder()) {
            PeriodeResultatÅrsak periodeResultatÅrsak = periode.getPeriodeResultatÅrsak();
            if (PeriodeResultatType.AVSLÅTT.equals(periode.getPeriodeResultatType()) && periodeResultatÅrsak != null) {
                String avslagKode = periodeResultatÅrsak.getKode();
                avslagårsaker.put(avslagKode, årsaktypeFra(periodeResultatÅrsak));
            }
        }
        return avslagårsaker.values();
    }

    private static String årsaktypeFra(PeriodeResultatÅrsak periodeResultatÅrsak) {
        lovReferanser.addAll(LovhjemmelUtil.hentLovhjemlerFraJson(periodeResultatÅrsak, "FP"));
        return periodeResultatÅrsak.getKode();
    }

    private static String årsaktypeFra(ÅrsakMedLovReferanse årsakKode) { //NOSONAR - denne er i bruk...
        lovReferanser.addAll(LovhjemmelUtil.hentLovhjemlerFraJson(årsakKode, "FP"));
        return årsakKode.getKode();
    }

}
