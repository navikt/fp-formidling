package no.nav.foreldrepenger.melding.datamapper.domene;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.enterprise.context.ApplicationScoped;

import no.nav.foreldrepenger.melding.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.melding.behandling.ÅrsakskodeMedLovreferanse;
import no.nav.foreldrepenger.melding.datamapper.domene.sortering.LovhjemmelComparator;
import no.nav.foreldrepenger.melding.integrasjon.dokument.opphor.AarsakListeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.opphor.AvslagsAarsakType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.opphor.ObjectFactory;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPerioder;
import no.nav.foreldrepenger.melding.vilkår.Avslagsårsak;
import no.nav.vedtak.util.Tuple;

@ApplicationScoped
public class ÅrsakMapperOpphør {

    private static ObjectFactory objectFactory = new ObjectFactory();
    private static Set<String> lovReferanser;

    public static Tuple<AarsakListeType, String> mapAarsakListeOgLovhjemmelFra(Behandlingsresultat behandlingsresultat,
                                                                               UttakResultatPerioder uttakResultatPerioder) {
        lovReferanser = new TreeSet<>(new LovhjemmelComparator());
        AarsakListeType aarsakListeType = objectFactory.createAarsakListeType();
        aarsakListeType.getAvslagsAarsak().addAll(årsakerFra(behandlingsresultat, uttakResultatPerioder));

        return new Tuple<>(aarsakListeType, FellesMapper.formaterLovhjemlerUttak(lovReferanser));
    }

    private static Set<AvslagsAarsakType> årsakerFra(Behandlingsresultat behandlingsresultat,
                                                     UttakResultatPerioder uttakResultatPerioder) {
        Set<AvslagsAarsakType> avslagsAarsaker = new HashSet<>();

        Avslagsårsak avslagsårsak = behandlingsresultat.getAvslagsårsak();
        if (avslagsårsak != null) {
            avslagsAarsaker.add(årsaktypeFra(avslagsårsak));
        }
        for (UttakResultatPeriode periode : uttakResultatPerioder.getPerioder()) {
            PeriodeResultatÅrsak periodeResultatÅrsak = periode.getPeriodeResultatÅrsak();
            if (PeriodeResultatType.AVSLÅTT.equals(periode.getPeriodeResultatType()) && periodeResultatÅrsak != null) {
                avslagsAarsaker.add(årsaktypeFra(periodeResultatÅrsak));
            }
        }
        return avslagsAarsaker;
    }

    private static AvslagsAarsakType årsaktypeFra(ÅrsakskodeMedLovreferanse årsakKode) {
        AvslagsAarsakType avslagsAarsak = objectFactory.createAvslagsAarsakType();
        avslagsAarsak.setAvslagsAarsakKode(årsakKode.getKode());
        lovReferanser.addAll(LovhjemmelUtil.hentLovhjemlerFraJson(årsakKode, "FP"));
        return avslagsAarsak;
    }

}
