package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.opphorfp;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.FellesMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.LovhjemmelComparator;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.LovhjemmelUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.Tuple;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.fpformidling.domene.behandling.ÅrsakMedLovReferanse;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.ForeldrepengerUttak;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.PeriodeResultatType;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.PeriodeResultatÅrsak;

import java.util.*;

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

        var avslagsårsak = behandlingsresultat.getAvslagsårsak();
        if (avslagsårsak != null) {
            var avslagKode = avslagsårsak.getKode();
            avslagårsaker.put(avslagKode, årsaktypeFra(avslagsårsak, lovReferanser));
        }
        for (var periode : foreldrepengerUttak.perioder()) {
            var periodeResultatÅrsak = periode.getPeriodeResultatÅrsak();
            if (PeriodeResultatType.AVSLÅTT.equals(periode.getPeriodeResultatType()) && periodeResultatÅrsak != null) {
                var avslagKode = periodeResultatÅrsak.getKode();
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
