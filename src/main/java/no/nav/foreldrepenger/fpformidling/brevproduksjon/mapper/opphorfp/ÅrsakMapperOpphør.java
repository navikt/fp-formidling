package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.opphorfp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.FellesMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.LovhjemmelComparator;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.Tuple;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.ForeldrepengerUttak;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.PeriodeResultatType;
import no.nav.foreldrepenger.fpformidling.domene.vilkår.VilkårType;

public final class ÅrsakMapperOpphør {

    private ÅrsakMapperOpphør() {
    }

    public static Tuple<List<String>, String> mapÅrsakslisteOgLovhjemmelFra(Collection<VilkårType> vilkårTyper,
                                                                            Behandlingsresultat behandlingsresultat,
                                                                            ForeldrepengerUttak foreldrepengerUttak) {
        var lovReferanser = new TreeSet<>(new LovhjemmelComparator());
        var årsaksliste = new ArrayList<>(årsakerFra(vilkårTyper, behandlingsresultat, foreldrepengerUttak, lovReferanser));

        return new Tuple<>(årsaksliste, FellesMapper.formaterLovhjemlerUttak(lovReferanser));
    }

    private static Collection<String> årsakerFra(Collection<VilkårType> vilkårTyper,
                                                 Behandlingsresultat behandlingsresultat,
                                                 ForeldrepengerUttak foreldrepengerUttak,
                                                 TreeSet<String> lovReferanser) {
        var avslagårsaker = new HashSet<String>();

        var avslagsårsak = behandlingsresultat.getAvslagsårsak();
        if (avslagsårsak != null) {
            avslagårsaker.add(avslagsårsak.getKode());
            var referanser = FellesMapper.lovhjemmelFraAvslagsårsak(FagsakYtelseType.FORELDREPENGER, vilkårTyper, avslagsårsak);
            lovReferanser.addAll(referanser);
        }
        for (var periode : foreldrepengerUttak.perioder()) {
            var periodeResultatÅrsak = periode.getPeriodeResultatÅrsak();
            if (PeriodeResultatType.AVSLÅTT.equals(periode.getPeriodeResultatType()) && periodeResultatÅrsak != null) {
                avslagårsaker.add(periodeResultatÅrsak.getKode());
                lovReferanser.addAll(periodeResultatÅrsak.hentLovhjemlerFraJson());
            }
        }
        return avslagårsaker;
    }

}
