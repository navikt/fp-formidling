package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.inntektarbeidytelse.Inntektsmelding;
import no.nav.foreldrepenger.fpformidling.domene.inntektarbeidytelse.Inntektsmeldinger;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.PeriodeResultatType;
import no.nav.foreldrepenger.fpformidling.domene.uttak.svp.SvangerskapspengerUttak;
import no.nav.foreldrepenger.fpformidling.domene.uttak.svp.SvpUttakResultatArbeidsforhold;
import no.nav.foreldrepenger.fpformidling.domene.uttak.svp.SvpUttakResultatPeriode;
import no.nav.foreldrepenger.fpformidling.domene.vilkår.Avslagsårsak;
import no.nav.foreldrepenger.fpformidling.domene.vilkår.VilkårType;

public final class SvpMapperUtil {

    private SvpMapperUtil() {
    }

    public static List<SvpUttakResultatPeriode> hentUttaksperioder(Optional<SvangerskapspengerUttak> svpUttaksresultat) {
        return svpUttaksresultat.map(SvangerskapspengerUttak::getUttakResultatArbeidsforhold)
            .orElse(Collections.emptyList())
            .stream()
            .flatMap(ura -> ura.getPerioder().stream())
            .toList();
    }

    public static String leggTilLovreferanse(Avslagsårsak avslagsårsak) {
        var vilkårTyper = VilkårType.getVilkårTyper(avslagsårsak);
        var lovReferanse = vilkårTyper.stream().map(vt -> vt.getLovReferanse(FagsakYtelseType.SVANGERSKAPSPENGER)).filter(Objects::nonNull).findFirst();
        return lovReferanse.orElse("");
    }

    public static Optional<LocalDate> finnFørsteAvslåtteUttakDato(List<SvpUttakResultatPeriode> uttaksperioder, Behandlingsresultat behandlingsresultat) {
        return finnMinsteDatoFraAvslåttUttak(uttaksperioder).or(behandlingsresultat::getSkjæringstidspunkt);

    }

    public static Optional<LocalDate> finnMinsteDatoFraAvslåttUttak(List<SvpUttakResultatPeriode> perioder) {
        return finnMinsteFraDatoAvslåttUttak(perioder).or(() ->finnMinsteFraDatoFraInnvilgetUttak(perioder));
    }

    private static Optional<LocalDate> finnMinsteFraDatoAvslåttUttak(List<SvpUttakResultatPeriode> uttaksperioder) {
        return uttaksperioder.stream()
            .filter(p -> PeriodeResultatType.AVSLÅTT.equals(p.getPeriodeResultatType()))
            .map(p -> p.getTidsperiode().getFomDato())
            .min(LocalDate::compareTo);
    }

    private static Optional<LocalDate> finnMinsteFraDatoFraInnvilgetUttak(List<SvpUttakResultatPeriode> uttaksperioder) {
        return uttaksperioder.stream()
            .filter(p -> PeriodeResultatType.INNVILGET.equals(p.getPeriodeResultatType()))
            .map(p -> p.getTidsperiode().getFomDato())
            .min(LocalDate::compareTo);
    }

    public static int finnAntallArbeidsgivere(List<SvpUttakResultatArbeidsforhold> uttakResultatArbeidsforhold, Inntektsmeldinger iay) {
        var antallArbeidsgivere = (int) uttakResultatArbeidsforhold.stream()
            .flatMap(ura -> ura.getPerioder().stream())
            .map(SvpUttakResultatPeriode::getArbeidsgiverNavn)
            .distinct()
            .count();
        if (antallArbeidsgivere == 0) {
            antallArbeidsgivere = (int) uttakResultatArbeidsforhold.stream().map(SvpUttakResultatArbeidsforhold::getArbeidsgiver).distinct().count();
        }
        if (antallArbeidsgivere == 0) {
            antallArbeidsgivere = (int) iay.getInntektsmeldinger().stream().map(Inntektsmelding::arbeidsgiverReferanse).distinct().count();
        }
        return antallArbeidsgivere;
    }
}
