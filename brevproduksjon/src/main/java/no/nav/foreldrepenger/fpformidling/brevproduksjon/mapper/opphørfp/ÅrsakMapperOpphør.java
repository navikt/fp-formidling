package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.opphørfp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import no.nav.foreldrepenger.fpformidling.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.fpformidling.behandling.ÅrsakMedLovReferanse;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.FellesMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.LovhjemmelComparator;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.LovhjemmelUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.Tuple;
import no.nav.foreldrepenger.fpformidling.uttak.ForeldrepengerUttak;
import no.nav.foreldrepenger.fpformidling.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.fpformidling.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.fpformidling.uttak.kodeliste.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.fpformidling.vilkår.Avslagsårsak;

public final class ÅrsakMapperOpphør {

    private ÅrsakMapperOpphør() {
    }

    public static Tuple<List<String>, String> mapÅrsakslisteOgLovhjemmelFra(Behandlingsresultat behandlingsresultat,
                                                                            ForeldrepengerUttak foreldrepengerUttak) {
        var lovReferanser = new TreeSet<>(new LovhjemmelComparator());
        List<String> årsaksliste = new ArrayList<>();
        årsaksliste.addAll(årsakerFra(behandlingsresultat, foreldrepengerUttak, lovReferanser));

        return new Tuple<>(årsaksliste, FellesMapper.formaterLovhjemlerUttak(lovReferanser));
    }

    private static Collection<String> årsakerFra(Behandlingsresultat behandlingsresultat,
                                                 ForeldrepengerUttak foreldrepengerUttak,
                                                 TreeSet<String> lovReferanser) {
        Map<String, String> avslagårsaker = new HashMap<>();

        Avslagsårsak avslagsårsak = behandlingsresultat.getAvslagsårsak();
        if (avslagsårsak != null) {
            String avslagKode = avslagsårsak.getKode();
            avslagårsaker.put(avslagKode, årsaktypeFra(avslagsårsak, lovReferanser));
        }
        for (UttakResultatPeriode periode : foreldrepengerUttak.perioder()) {
            PeriodeResultatÅrsak periodeResultatÅrsak = periode.getPeriodeResultatÅrsak();
            if (PeriodeResultatType.AVSLÅTT.equals(periode.getPeriodeResultatType()) && periodeResultatÅrsak != null) {
                String avslagKode = periodeResultatÅrsak.getKode();
                avslagårsaker.put(avslagKode, årsaktypeFra(periodeResultatÅrsak, lovReferanser));
            }
        }
        return avslagårsaker.values();
    }

    private static String årsaktypeFra(PeriodeResultatÅrsak periodeResultatÅrsak, TreeSet<String> lovReferanser) {
        lovReferanser.addAll(LovhjemmelUtil.hentLovhjemlerFraJson(periodeResultatÅrsak, "FP"));
        return periodeResultatÅrsak.getKode();
    }

    private static String årsaktypeFra(ÅrsakMedLovReferanse årsakKode, TreeSet<String> lovReferanser) {
        lovReferanser.addAll(LovhjemmelUtil.hentLovhjemlerFraJson(årsakKode, "FP"));
        return årsakKode.getKode();
    }

}
