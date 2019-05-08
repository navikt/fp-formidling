package no.nav.foreldrepenger.melding.datamapper.domene;

import static no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeBeregner.alleAktiviteterHarNullUtbetaling;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import javax.enterprise.context.ApplicationScoped;

import no.nav.foreldrepenger.melding.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.melding.behandling.ÅrsakskodeMedLovreferanse;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.melding.brevbestiller.XmlUtil;
import no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeBeregner;
import no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeMergerAvslag;
import no.nav.foreldrepenger.melding.datamapper.domene.sortering.LovhjemmelComparator;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avslag.foreldrepenger.AarsakListeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avslag.foreldrepenger.AvslagsAarsakType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avslag.foreldrepenger.ObjectFactory;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPerioder;
import no.nav.foreldrepenger.melding.vilkår.Avslagsårsak;
import no.nav.vedtak.util.Tuple;

@ApplicationScoped
public class ÅrsakMapperAvslag {

    private static ObjectFactory objectFactory = new ObjectFactory();
    private static Set<AvslagsAarsakType> avslagsAarsaker;
    private static Set<String> lovReferanser;

    public static Tuple<AarsakListeType, String> mapAarsakListeOgLovhjemmelFra(Behandlingsresultat behandlingsresultat,
                                                                               List<BeregningsresultatPeriode> beregningsresultatPerioder,
                                                                               Optional<UttakResultatPerioder> uttakResultatPerioder) {
        AarsakListeType aarsakListeType = objectFactory.createAarsakListeType();
        avslagsAarsaker = new TreeSet<>(medSorteringPåPeriodeFom());
        lovReferanser = new TreeSet<>(new LovhjemmelComparator());

        List<AvslagsAarsakType> avslag = new ArrayList<>(medÅrsakerFra(beregningsresultatPerioder, uttakResultatPerioder));
        String lovhjemmelForAvslag = FellesMapper.formaterLovhjemlerUttak(lovReferanser,
                BehandlingMapper.kodeFra(behandlingsresultat.getKonsekvenserForYtelsen()),
                false);
        if (avslag.isEmpty()) {
            avslag.addAll(årsakerFra(behandlingsresultat, uttakResultatPerioder));
            lovhjemmelForAvslag = FellesMapper.formaterLovhjemlerUttak(lovReferanser);
        }
        avslag = PeriodeMergerAvslag.mergePerioder(avslag);
        aarsakListeType.getAvslagsAarsak().addAll(avslag);

        return new Tuple<>(aarsakListeType, lovhjemmelForAvslag);
    }

    private static Set<AvslagsAarsakType> medÅrsakerFra(List<BeregningsresultatPeriode> beregningsresultatPerioder,
                                                        Optional<UttakResultatPerioder> uttakResultatPerioder) {
        for (BeregningsresultatPeriode beregningsresultatPeriode : beregningsresultatPerioder) {
            AvslagsAarsakType aarsakType = årsaktypeFra(beregningsresultatPeriode,
                    PeriodeBeregner.finnUttaksPeriode(beregningsresultatPeriode, uttakResultatPerioder
                            .map(UttakResultatPerioder::getPerioder).orElse(Collections.emptyList())));
            avslagsAarsaker.add(aarsakType);
        }
        return avslagsAarsaker;
    }

    private static Set<AvslagsAarsakType> årsakerFra(Behandlingsresultat behandlingsresultat,
                                                     Optional<UttakResultatPerioder> uttakResultatPerioder) {
        Avslagsårsak avslagsårsak = behandlingsresultat.getAvslagsårsak();
        if (avslagsårsak != null) {
            avslagsAarsaker.add(årsaktypeFra(avslagsårsak));
        }
        for (UttakResultatPeriode periode : uttakResultatPerioder.map(UttakResultatPerioder::getPerioder).orElse(Collections.emptyList())) {
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

    private static AvslagsAarsakType årsaktypeFra(BeregningsresultatPeriode beregningsresultatPeriode,
                                                  UttakResultatPeriode uttakResultatPeriode) {
        AvslagsAarsakType periode = årsaktypeFra(uttakResultatPeriode.getPeriodeResultatÅrsak());
        periode.setAntallTapteDager(BigInteger.valueOf(mapAntallTapteDagerFra(uttakResultatPeriode.getAktiviteter())));
        periode.setPeriodeFom(XmlUtil.finnDatoVerdiAvUtenTidSone(beregningsresultatPeriode.getBeregningsresultatPeriodeFom()));
        periode.setPeriodeTom(XmlUtil.finnDatoVerdiAvUtenTidSone(beregningsresultatPeriode.getBeregningsresultatPeriodeTom()));

        return periode;
    }

    private static int mapAntallTapteDagerFra(List<UttakResultatPeriodeAktivitet> uttakAktiviteter) {
        return alleAktiviteterHarNullUtbetaling(uttakAktiviteter) ?
                uttakAktiviteter.stream()
                        .mapToInt(UttakResultatPeriodeAktivitet::getTrekkdager)
                        .max()
                        .orElse(0) : 0;
    }

    private static Comparator<AvslagsAarsakType> medSorteringPåPeriodeFom() {
        return (a1, a2) -> a1.getPeriodeFom().compare(a2.getPeriodeFom());
    }
}
