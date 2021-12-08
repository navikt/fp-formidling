package no.nav.foreldrepenger.melding.brevmapper.brev.felles;

import no.nav.foreldrepenger.melding.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.inntektarbeidytelse.InntektArbeidYtelse;
import no.nav.foreldrepenger.melding.inntektarbeidytelse.Inntektsmelding;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatArbeidsforhold;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttaksresultat;
import no.nav.foreldrepenger.melding.vilkår.Avslagsårsak;
import no.nav.foreldrepenger.melding.vilkår.VilkårType;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class SvpMapperUtil {

    public static List<SvpUttakResultatPeriode> hentUttaksperioder(Optional<SvpUttaksresultat> svpUttaksresultat) {
        return svpUttaksresultat.map(SvpUttaksresultat::getUttakResultatArbeidsforhold).orElse(Collections.emptyList()).stream().flatMap(ura->ura.getPerioder().stream()).collect(Collectors.toList());
    }

    public static String leggTilLovreferanse(Avslagsårsak avslagsårsak) {
        Set<VilkårType> vilkårTyper = VilkårType.getVilkårTyper(avslagsårsak);
        return vilkårTyper.stream().map(vt -> vt.getLovReferanse(FagsakYtelseType.SVANGERSKAPSPENGER)).findFirst().orElse("");
    }

    public static Optional<LocalDate> finnFørsteUttakssdato(List<SvpUttakResultatPeriode> uttaksperioder, Behandlingsresultat behandlingsresultat) {
        return finnMinsteDatoFraUttak(uttaksperioder).or(behandlingsresultat::getSkjæringstidspunkt);

    }

    public static Optional<LocalDate> finnMinsteDatoFraUttak(List<SvpUttakResultatPeriode> perioder) {
        Optional<LocalDate> minsteDatoFraÅvslåttUttak = finnMinsteFraDatoAvslåttUttak(perioder);
        return minsteDatoFraÅvslåttUttak.isPresent() ? minsteDatoFraÅvslåttUttak : finnMinsteFraDatoFraInnvilgetUttak(perioder);
    }

//Henter ut alle avslåtte perioder her fordi det kan være en ferie midt i perioden som tidligere har blitt avslått(8311), men som ikke er en opphørsavslagsgrunn.
// Siste opphørsdato vil da se merkelig ut siden bruker ikke får svp penger i den avslåtte perioden
    private static Optional<LocalDate> finnMinsteFraDatoAvslåttUttak(List<SvpUttakResultatPeriode> uttaksperioder) {
        return uttaksperioder.stream()
                .filter(p-> PeriodeResultatType.AVSLÅTT.equals(p.getPeriodeResultatType()))
                .map(p -> p.getTidsperiode().getFomDato())
                .min(LocalDate::compareTo);
    }

    private static Optional<LocalDate> finnMinsteFraDatoFraInnvilgetUttak(List<SvpUttakResultatPeriode> uttaksperioder) {
        return uttaksperioder.stream()
                .filter(p-> PeriodeResultatType.INNVILGET.equals(p.getPeriodeResultatType()))
                .map(p-> p.getTidsperiode().getFomDato())
                .min(LocalDate::compareTo);
    }

    public static int finnAntallArbeidsgivere(List<SvpUttakResultatArbeidsforhold> uttakResultatArbeidsforhold, InntektArbeidYtelse iay) {
        int antallArbeidsgivere = (int) uttakResultatArbeidsforhold.stream()
                .flatMap(ura -> ura.getPerioder().stream())
                .map(SvpUttakResultatPeriode::getArbeidsgiverNavn)
                .distinct()
                .count();
        if(antallArbeidsgivere==0) {
            antallArbeidsgivere = (int) uttakResultatArbeidsforhold.stream().map(SvpUttakResultatArbeidsforhold::getArbeidsgiver).distinct().count();
        }
        if(antallArbeidsgivere ==0) {
            antallArbeidsgivere = (int) iay.getInntektsmeldinger().stream().map(Inntektsmelding::arbeidsgiverReferanse).distinct().count();
        }
        return antallArbeidsgivere;
    }
}
