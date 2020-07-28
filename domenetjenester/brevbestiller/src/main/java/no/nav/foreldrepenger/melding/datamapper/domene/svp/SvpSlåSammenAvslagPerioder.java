package no.nav.foreldrepenger.melding.datamapper.domene.svp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeMergerVerktøy;
import no.nav.foreldrepenger.melding.jpa.DatoIntervallEntitet;
import no.nav.foreldrepenger.melding.uttak.svp.PeriodeIkkeOppfyltÅrsak;


final class SvpSlåSammenAvslagPerioder {

    private SvpSlåSammenAvslagPerioder() {
        // Skjul default constructor
    }

    static Set<SvpAvslagPeriode> slåSammen(Set<SvpAvslagPeriode> filtrertePerioder){

        if (filtrertePerioder.isEmpty()) {
            return filtrertePerioder;
        }

        Set<SvpAvslagPeriode> sammenslåttePerioder = new HashSet<>();

        for (PeriodeIkkeOppfyltÅrsak årsak : SvpUtledAvslagPerioder.RELEVANTE_PERIODE_ÅRSAKER) {

            List<SvpAvslagPeriode> sortertePerioder = filtrertePerioder.stream()
                    .filter(p -> årsak.getKode().equals(String.valueOf(p.getAarsakskode())))
                    .sorted(Comparator.comparing(SvpAvslagPeriode::getFom))
                    .collect(Collectors.toList());

            if ( sortertePerioder.isEmpty() ) {
                continue;
            }

            ArrayList<DatoIntervallEntitet> nyePerioder = new ArrayList<>();

            Iterator<SvpAvslagPeriode> iterator = sortertePerioder.iterator();

            SvpAvslagPeriode førsteAvslagPeriode = iterator.next();
            LocalDate førsteAvslagPeriodeFom = førsteAvslagPeriode.getFom().toLocalDate();
            LocalDate førsteAvslagPeriodeTom = førsteAvslagPeriode.getTom().toLocalDate();
            nyePerioder.add(DatoIntervallEntitet.fraOgMedTilOgMed(førsteAvslagPeriodeFom, førsteAvslagPeriodeTom));

            while(iterator.hasNext()) {

                int sisteIndex = nyePerioder.size() - 1;
                DatoIntervallEntitet forrigePeriode = nyePerioder.get(sisteIndex);
                LocalDate forrigePeriodeTom = forrigePeriode.getTomDato();
                LocalDate forrigePeriodeFom = forrigePeriode.getFomDato();

                SvpAvslagPeriode nesteAvslagPeriode = iterator.next();
                LocalDate nesteAvslagPeriodeFom = nesteAvslagPeriode.getFom().toLocalDate();
                LocalDate nesteAvslagPeriodeTom = nesteAvslagPeriode.getTom().toLocalDate();

                if (skalSlåSammenPerioder(forrigePeriode, nesteAvslagPeriodeFom)) {
                    LocalDate nyTom = List.of(forrigePeriodeTom, nesteAvslagPeriodeTom).stream()
                            .max(Comparator.naturalOrder())
                            .orElseThrow();
                    nyePerioder.remove(sisteIndex);
                    nyePerioder.add(DatoIntervallEntitet.fraOgMedTilOgMed(forrigePeriodeFom, nyTom));
                } else {
                    nyePerioder.add(DatoIntervallEntitet.fraOgMedTilOgMed(nesteAvslagPeriodeFom, nesteAvslagPeriodeTom));
                }

            }

            Set<SvpAvslagPeriode> sammenslåtteAvslagPerioder = nyePerioder.stream()
                    .map(p -> byggMergetAvslagPeriode(årsak, p))
                    .collect(Collectors.toSet());
            sammenslåttePerioder.addAll(sammenslåtteAvslagPerioder);

        }

        return sammenslåttePerioder;

    }

    private static boolean skalSlåSammenPerioder(DatoIntervallEntitet forrigePeriode, LocalDate nesteAvslagPeriodeFom) {
        LocalDate forrigePeriodeTom = forrigePeriode.getTomDato();
        return forrigePeriode.inkluderer(nesteAvslagPeriodeFom) ||
                PeriodeMergerVerktøy.erFomRettEtterTomDato(forrigePeriodeTom, nesteAvslagPeriodeFom);
    }

    private static SvpAvslagPeriode byggMergetAvslagPeriode(PeriodeIkkeOppfyltÅrsak årsak, DatoIntervallEntitet p) {
        return SvpAvslagPeriode.Builder.ny()
                .medPeriodeÅrsak(årsak)
                .medFom(p.getFomDato())
                .medTom(p.getTomDato())
                .build();
    }

}
