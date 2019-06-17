package no.nav.foreldrepenger.melding.datamapper.domene;

import static no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeBeregner.alleAktiviteterHarNullUtbetaling;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import javax.enterprise.context.ApplicationScoped;

import no.nav.foreldrepenger.melding.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.melding.behandling.ÅrsakMedLovReferanse;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.melding.brevbestiller.XmlUtil;
import no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeBeregner;
import no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeMergerAvslag;
import no.nav.foreldrepenger.melding.datamapper.domene.sortering.LovhjemmelComparator;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avslag.foreldrepenger.AarsakListeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avslag.foreldrepenger.AvslagsAarsakType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avslag.foreldrepenger.ObjectFactory;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPerioder;
import no.nav.foreldrepenger.melding.uttak.kodeliste.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.melding.vilkår.Avslagsårsak;
import no.nav.vedtak.util.Tuple;

@ApplicationScoped
public class ÅrsakMapperAvslag {

    private static ObjectFactory objectFactory = new ObjectFactory();
    private static Set<String> lovReferanser;

    public static Tuple<AarsakListeType, String> mapAarsakListeOgLovhjemmelFra(Behandlingsresultat behandlingsresultat,
                                                                               List<BeregningsresultatPeriode> beregningsresultatPerioder,
                                                                               Optional<UttakResultatPerioder> uttakResultatPerioder) {
        AarsakListeType aarsakListeType = objectFactory.createAarsakListeType();
        lovReferanser = new TreeSet<>(new LovhjemmelComparator());

        List<AvslagsAarsakType> avslagsAarsaker = årsakerFra(beregningsresultatPerioder, uttakResultatPerioder);
        String lovhjemmelForAvslag = FellesMapper.formaterLovhjemlerUttak(lovReferanser,
                BehandlingMapper.kodeFra(behandlingsresultat.getKonsekvenserForYtelsen()),
                false);
        if (avslagsAarsaker.isEmpty()) {
            avslagsAarsaker = årsakerFra(behandlingsresultat, uttakResultatPerioder);
            lovhjemmelForAvslag = FellesMapper.formaterLovhjemlerUttak(lovReferanser);
        }
        avslagsAarsaker = PeriodeMergerAvslag.mergePerioder(avslagsAarsaker);
        aarsakListeType.getAvslagsAarsak().addAll(avslagsAarsaker);

        return new Tuple<>(aarsakListeType, lovhjemmelForAvslag);
    }

    private static List<AvslagsAarsakType> årsakerFra(List<BeregningsresultatPeriode> beregningsresultatPerioder,
                                                      Optional<UttakResultatPerioder> uttakResultatPerioder) {
        List<AvslagsAarsakType> avslagsAarsaker = new ArrayList<>();
        for (BeregningsresultatPeriode beregningsresultatPeriode : beregningsresultatPerioder) {
            AvslagsAarsakType aarsakType = årsaktypeFra(beregningsresultatPeriode,
                    PeriodeBeregner.finnUttaksPeriode(beregningsresultatPeriode, uttakResultatPerioder
                            .map(UttakResultatPerioder::getPerioder).orElse(Collections.emptyList())));
            avslagsAarsaker.add(aarsakType);
        }
        return avslagsAarsaker;
    }

    private static List<AvslagsAarsakType> årsakerFra(Behandlingsresultat behandlingsresultat,
                                                      Optional<UttakResultatPerioder> uttakResultatPerioder) {
        List<AvslagsAarsakType> avslagsAarsaker = new ArrayList<>();

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

    private static AvslagsAarsakType årsaktypeFra(ÅrsakMedLovReferanse årsakKode) {
        AvslagsAarsakType avslagsAarsak = objectFactory.createAvslagsAarsakType();
        avslagsAarsak.setAvslagsAarsakKode(årsakKode.getKode());
        lovReferanser.addAll(LovhjemmelUtil.hentLovhjemlerFraJson(årsakKode, "FP"));
        return avslagsAarsak;
    }

    private static AvslagsAarsakType årsaktypeFra(BeregningsresultatPeriode beregningsresultatPeriode,
                                                  UttakResultatPeriode uttakResultatPeriode) {
        AvslagsAarsakType periode = årsaktypeFra(uttakResultatPeriode.getPeriodeResultatÅrsak());
        periode.setAntallTapteDager(BigInteger.valueOf(mapAntallTapteDagerFra(uttakResultatPeriode.getAktiviteter()).longValue()));
        periode.setPeriodeFom(XmlUtil.finnDatoVerdiAvUtenTidSone(beregningsresultatPeriode.getBeregningsresultatPeriodeFom()));
        periode.setPeriodeTom(XmlUtil.finnDatoVerdiAvUtenTidSone(beregningsresultatPeriode.getBeregningsresultatPeriodeTom()));

        return periode;
    }

    private static BigDecimal mapAntallTapteDagerFra(List<UttakResultatPeriodeAktivitet> uttakAktiviteter) {
        return alleAktiviteterHarNullUtbetaling(uttakAktiviteter) ?
                uttakAktiviteter.stream()
                        .map(UttakResultatPeriodeAktivitet::getTrekkdager)
                        .filter(Objects::nonNull)
                        .max(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO) : BigDecimal.ZERO;
    }
}
