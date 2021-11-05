package no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsesvp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeMergerVerktøy;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Årsak;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsesvp.Avslagsperiode;
import no.nav.foreldrepenger.melding.jpa.DatoIntervallEntitet;
import no.nav.foreldrepenger.melding.uttak.svp.PeriodeIkkeOppfyltÅrsak;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatArbeidsforhold;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatPeriode;

public final class AvslagsperiodeMapper {

    private static final List<PeriodeIkkeOppfyltÅrsak> RELEVANTE_PERIODE_ÅRSAKER = List.of(
            PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT,
            PeriodeIkkeOppfyltÅrsak.PERIODE_SAMTIDIG_SOM_FERIE
    );

    public static List<Avslagsperiode> mapAvslagsperioder(List<SvpUttakResultatArbeidsforhold> uttakResultatArbeidsforhold,
                                                          Språkkode språkkode) {
        List<Avslagsperiode> filtrertePerioder = uttakResultatArbeidsforhold.stream()
                .flatMap(ura -> ura.getPerioder().stream())
                .filter(Predicate.not(SvpUttakResultatPeriode::isInnvilget))
                .filter(p -> RELEVANTE_PERIODE_ÅRSAKER.contains(p.getPeriodeIkkeOppfyltÅrsak()))
                .map(p -> opprettAvslagsperiode(p, språkkode))
                .collect(Collectors.toList());
        return slåSammenPerioder(filtrertePerioder);
    }

    private static Avslagsperiode opprettAvslagsperiode(SvpUttakResultatPeriode p, Språkkode språkkode) {
        return Avslagsperiode.ny()
                .medÅrsak(Årsak.of(p.getPeriodeIkkeOppfyltÅrsak().getKode()))
                .medPeriodeFom(p.getFom().toLocalDate(), språkkode)
                .medPeriodeTom(p.getTom().toLocalDate(), språkkode)
                .build();
    }

    private static List<Avslagsperiode> slåSammenPerioder(List<Avslagsperiode> filtrertePerioder) {
        if (filtrertePerioder.isEmpty()) {
            return filtrertePerioder;
        }

        List<Avslagsperiode> sammenslåttePerioder = new ArrayList<>();

        for (PeriodeIkkeOppfyltÅrsak årsak : RELEVANTE_PERIODE_ÅRSAKER) {
            List<Avslagsperiode> sortertePerioder = filtrertePerioder.stream()
                    .filter(p -> årsak.getKode().equals(String.valueOf(p.getÅrsak().getKode())))
                    .sorted(Comparator.comparing(Avslagsperiode::getPeriodeFom))
                    .collect(Collectors.toList());

            ArrayList<Avslagsperiode> nyePerioder = new ArrayList<>();

            for (Avslagsperiode avslagsperiode : sortertePerioder) {
                if (!nyePerioder.isEmpty()) {
                    int sisteIndex = nyePerioder.size() - 1;
                    Avslagsperiode forrigePeriode = nyePerioder.get(sisteIndex);

                    if (skalSlåSammenPerioder(forrigePeriode, avslagsperiode.getPeriodeFom())) {
                        LocalDate nyTom = Stream.of(forrigePeriode.getPeriodeTom(), avslagsperiode.getPeriodeTom())
                                .max(Comparator.naturalOrder())
                                .orElseThrow();
                        nyePerioder.remove(sisteIndex);
                        nyePerioder.add(byggSammenslåttAvslagsperiode(forrigePeriode, nyTom));
                        continue;
                    }
                }
                nyePerioder.add(avslagsperiode);
            }

            sammenslåttePerioder.addAll(nyePerioder);
        }

        return sammenslåttePerioder;
    }

    private static boolean skalSlåSammenPerioder(Avslagsperiode forrigePeriode, LocalDate nestePeriodeFom) {
        DatoIntervallEntitet forrigePeriodeIntervall = DatoIntervallEntitet.fraOgMedTilOgMed(forrigePeriode.getPeriodeFom(),
                forrigePeriode.getPeriodeTom());
        return forrigePeriodeIntervall.inkluderer(nestePeriodeFom)
                || PeriodeMergerVerktøy.erFomRettEtterTomDato(forrigePeriode.getPeriodeTom(), nestePeriodeFom);
    }

    private static Avslagsperiode byggSammenslåttAvslagsperiode(Avslagsperiode forrigePeriode, LocalDate nyTom) {
        return Avslagsperiode.ny(forrigePeriode)
                .medPeriodeTom(nyTom, forrigePeriode.getSpråkkode())
                .build();
    }
}
