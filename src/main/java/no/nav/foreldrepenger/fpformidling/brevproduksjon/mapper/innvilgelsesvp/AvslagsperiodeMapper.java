package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsesvp;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatoVerktøy.erFomRettEtterTomDato;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Årsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp.Avslagsperiode;
import no.nav.foreldrepenger.fpformidling.felles.DatoIntervallEntitet;
import no.nav.foreldrepenger.fpformidling.domene.uttak.svp.PeriodeIkkeOppfyltÅrsak;
import no.nav.foreldrepenger.fpformidling.domene.uttak.svp.SvpUttakResultatArbeidsforhold;
import no.nav.foreldrepenger.fpformidling.domene.uttak.svp.SvpUttakResultatPeriode;

public final class AvslagsperiodeMapper {

    private static final List<PeriodeIkkeOppfyltÅrsak> RELEVANTE_PERIODE_ÅRSAKER = List.of(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT,
        PeriodeIkkeOppfyltÅrsak.PERIODE_SAMTIDIG_SOM_FERIE, PeriodeIkkeOppfyltÅrsak.PERIODEN_ER_SAMTIDIG_SOM_SYKEPENGER);

    private AvslagsperiodeMapper() {
    }

    public static List<Avslagsperiode> mapAvslagsperioder(List<SvpUttakResultatArbeidsforhold> uttakResultatArbeidsforhold, Språkkode språkkode) {
        List<Avslagsperiode> avslagPerioderMedArbinformasjon = new ArrayList<>();
        uttakResultatArbeidsforhold.forEach(ura -> {
            var arbeidsforholdInformasjon = mapArbeidsforholdInformasjon(ura);
            avslagPerioderMedArbinformasjon.addAll(ura.getPerioder().stream()
                .filter(Predicate.not(SvpUttakResultatPeriode::isInnvilget))
                .filter(p -> RELEVANTE_PERIODE_ÅRSAKER.contains(p.getPeriodeIkkeOppfyltÅrsak()))
                .map(p -> opprettAvslagsperiode(p, språkkode, arbeidsforholdInformasjon))
                .toList());
        });
        return slåSammenPerioder(avslagPerioderMedArbinformasjon);
    }

    private static Avslagsperiode.ArbeidsforholdInformasjon mapArbeidsforholdInformasjon(SvpUttakResultatArbeidsforhold urArbeidsforhold) {
        if (urArbeidsforhold.getArbeidsgiver() != null && urArbeidsforhold.getArbeidsgiver().arbeidsgiverReferanse() != null) {
            return  new Avslagsperiode.ArbeidsforholdInformasjon(urArbeidsforhold.getArbeidsgiver().navn(),
                urArbeidsforhold.getUttakArbeidType().getKode());
        } else if (urArbeidsforhold.getArbeidsgiver() != null && urArbeidsforhold.getArbeidsgiver().arbeidsgiverReferanse() == null) {
            return new Avslagsperiode.ArbeidsforholdInformasjon(urArbeidsforhold.getArbeidsgiver().navn(),
                urArbeidsforhold.getUttakArbeidType().getKode());
        } else {
            return  new Avslagsperiode.ArbeidsforholdInformasjon(null, urArbeidsforhold.getUttakArbeidType().getKode());
        }
    }

    private static Avslagsperiode opprettAvslagsperiode(SvpUttakResultatPeriode p, Språkkode språkkode, Avslagsperiode.ArbeidsforholdInformasjon arbeidsforholdInformasjon) {
        return  Avslagsperiode.ny()
            .medÅrsak(Årsak.of(p.getPeriodeIkkeOppfyltÅrsak().getKode()))
            .medPeriodeFom(p.getFom().toLocalDate(), språkkode)
            .medPeriodeTom(p.getTom().toLocalDate(), språkkode)
            .medArbeidsforholdInformasjon(arbeidsforholdInformasjon, språkkode)
            .build();
    }

    private static List<Avslagsperiode> slåSammenPerioder(List<Avslagsperiode> filtrertePerioder) {
        if (filtrertePerioder.isEmpty()) {
            return filtrertePerioder;
        }

        List<Avslagsperiode> sammenslåttePerioder = new ArrayList<>();

        for (var årsak : RELEVANTE_PERIODE_ÅRSAKER) {
            var sortertePerioder = filtrertePerioder.stream()
                .filter(p -> årsak.getKode().equals(String.valueOf(p.getÅrsak().getKode())))
                .sorted(Comparator.comparing(Avslagsperiode::getPeriodeFom))
                .toList();

            var nyePerioder = new ArrayList<Avslagsperiode>();

            for (var avslagsperiode : sortertePerioder) {
                if (!nyePerioder.isEmpty()) {
                    var sisteIndex = nyePerioder.size() - 1;
                    var forrigePeriode = nyePerioder.get(sisteIndex);

                    if (skalSlåSammenPerioder(forrigePeriode, avslagsperiode, årsak)) {
                        var nyTom = Stream.of(forrigePeriode.getPeriodeTom(), avslagsperiode.getPeriodeTom())
                            .max(Comparator.naturalOrder())
                            .orElseThrow();
                        nyePerioder.remove(sisteIndex);
                        nyePerioder.add(byggSammenslåttAvslagsperiode(forrigePeriode, nyTom, avslagsperiode.getArbeidsforholdInformasjon()));
                        continue;
                    }
                }
                nyePerioder.add(avslagsperiode);
            }

            sammenslåttePerioder.addAll(nyePerioder);
        }

        return sammenslåttePerioder;
    }

    private static boolean skalSlåSammenPerioder(Avslagsperiode forrigePeriode, Avslagsperiode nestePeriode, PeriodeIkkeOppfyltÅrsak årsak) {
        var forrigePeriodeIntervall = DatoIntervallEntitet.fraOgMedTilOgMed(forrigePeriode.getPeriodeFom(), forrigePeriode.getPeriodeTom());
        var forrigePeriodeArbeidsforholdInformasjon = forrigePeriode.getArbeidsforholdInformasjon();
        var nestePeriodeArbeidsforholdInformasjo = nestePeriode.getArbeidsforholdInformasjon();

        if (PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT.equals(årsak)) { //alle perioder gjelder for alle arbeidsforhold
            return forrigePeriodeIntervall.inkluderer(nestePeriode.getPeriodeFom()) ||
                erFomRettEtterTomDato(forrigePeriode.getPeriodeTom(), nestePeriode.getPeriodeFom());
        } else { //dersom avslått pga ferie eller sykepenger skal de ikke slås sammen om ulike arbeidsforhold
            return (forrigePeriodeIntervall.inkluderer(nestePeriode.getPeriodeFom()) ||
                erFomRettEtterTomDato(forrigePeriode.getPeriodeTom(), nestePeriode.getPeriodeFom())) &&
                    forrigePeriodeArbeidsforholdInformasjon.equals(nestePeriodeArbeidsforholdInformasjo);
        }


    }

    private static Avslagsperiode byggSammenslåttAvslagsperiode(Avslagsperiode forrigePeriode, LocalDate nyTom, Avslagsperiode.ArbeidsforholdInformasjon nyPeriodeArbeidsforholdInformasjon) {
        return  Avslagsperiode.ny(forrigePeriode, nyPeriodeArbeidsforholdInformasjon).medPeriodeTom(nyTom, forrigePeriode.getSpråkkode()).build();
    }
}
