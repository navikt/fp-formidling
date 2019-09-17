package no.nav.foreldrepenger.melding.datamapper.domene.svp;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.melding.uttak.svp.PeriodeIkkeOppfyltÅrsak;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatArbeidsforhold;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatPeriode;

final class SvpUtledAvslagPerioder {

    static final List<PeriodeIkkeOppfyltÅrsak> RELEVANTE_PERIODE_ÅRSAKER = List.of(
            PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT,
            PeriodeIkkeOppfyltÅrsak.PERIODE_SAMTIDIG_SOM_FERIE
    );

    private SvpUtledAvslagPerioder() {
        // Skjul default constructor
    }

    static Set<SvpAvslagPeriode> utled(List<SvpUttakResultatArbeidsforhold> uttakResultatArbeidsforhold) {
        Set<SvpAvslagPeriode> filtrertePerioder = uttakResultatArbeidsforhold.stream()
                .flatMap(ura -> ura.getPerioder().stream())
                .filter(Predicate.not(SvpUttakResultatPeriode::isInnvilget))
                .filter(p -> RELEVANTE_PERIODE_ÅRSAKER.contains(p.getPeriodeIkkeOppfyltÅrsak()))
                .map(SvpUtledAvslagPerioder::opprettSvpAvslagPeriode)
                .collect(Collectors.toSet());
        return SvpSlåSammenAvslagPerioder.slåSammen(filtrertePerioder);
    }

    private static SvpAvslagPeriode opprettSvpAvslagPeriode(SvpUttakResultatPeriode p) {
        return SvpAvslagPeriode.Builder.ny()
                .medPeriodeÅrsak(p.getPeriodeIkkeOppfyltÅrsak())
                .medFom(p.getFom())
                .medTom(p.getTom())
                .build();
    }

}
