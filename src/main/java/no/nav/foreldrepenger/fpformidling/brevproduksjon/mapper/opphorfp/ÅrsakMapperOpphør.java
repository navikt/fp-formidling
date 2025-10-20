package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.opphorfp;

import static no.nav.foreldrepenger.fpformidling.domene.uttak.fp.PeriodeResultatÅrsak.PERIODE_ÅRSAK_DISCRIMINATOR;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.Behandlingsresultat;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.Foreldrepenger;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.PeriodeResultatType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.FellesMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.LovhjemmelComparator;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.Tuple;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.fpformidling.domene.vilkår.Avslagsårsak;
import no.nav.foreldrepenger.fpformidling.domene.vilkår.VilkårType;

public final class ÅrsakMapperOpphør {

    private ÅrsakMapperOpphør() {
    }

    public static Tuple<List<String>, String> mapÅrsakslisteOgLovhjemmelFra(Collection<VilkårType> vilkårTyper,
                                                                            Behandlingsresultat behandlingsresultat,
                                                                            List<Foreldrepenger.Uttaksperiode> perioder) {
        var lovReferanser = new TreeSet<>(new LovhjemmelComparator());
        var årsaksliste = new ArrayList<>(årsakerFra(vilkårTyper, behandlingsresultat, lovReferanser, perioder));

        return new Tuple<>(årsaksliste, FellesMapper.formaterLovhjemlerUttak(lovReferanser));
    }

    private static Collection<String> årsakerFra(Collection<VilkårType> vilkårTyper,
                                                 Behandlingsresultat behandlingsresultat,
                                                 TreeSet<String> lovReferanser,
                                                 List<Foreldrepenger.Uttaksperiode> perioder) {
        var avslagårsaker = new HashSet<String>();

        if (behandlingsresultat.avslagsårsak() != null) {
            avslagårsaker.add(behandlingsresultat.avslagsårsak());
            var avslagsårsak = Avslagsårsak.fraKode(behandlingsresultat.avslagsårsak());
            var referanser = FellesMapper.lovhjemmelFraAvslagsårsak(FagsakYtelseType.FORELDREPENGER, vilkårTyper, avslagsårsak);
            lovReferanser.addAll(referanser);
        }
        for (var periode : perioder) {
            if (PeriodeResultatType.AVSLÅTT.equals(periode.periodeResultatType())) {
                var periodeResultatÅrsak = new PeriodeResultatÅrsak(periode.periodeResultatÅrsak(), PERIODE_ÅRSAK_DISCRIMINATOR, periode.periodeResultatÅrsakLovhjemmel());
                avslagårsaker.add(periodeResultatÅrsak.getKode());
                lovReferanser.addAll(periodeResultatÅrsak.hentLovhjemlerFraJson());
            }
        }
        return avslagårsaker;
    }

}
