package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsesvp;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatoVerktøy.erFomRettEtterTomDato;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsesvp.AktivitetsbeskrivelseUtleder.utledAktivitetsbeskrivelse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.PeriodeBeregner;
import no.nav.foreldrepenger.fpformidling.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Prosent;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp.Uttaksaktivitet;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp.Uttaksperiode;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelseForeldrepenger;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelsePeriode;
import no.nav.foreldrepenger.fpformidling.uttak.svp.SvangerskapspengerUttak;
import no.nav.foreldrepenger.fpformidling.uttak.svp.SvpUttakResultatPeriode;

public final class UttaksperiodeMapper {

    private UttaksperiodeMapper() {
    }

    public static List<Uttaksaktivitet> mapUttaksaktivteterMedPerioder(SvangerskapspengerUttak uttaksresultat,
                                                                       TilkjentYtelseForeldrepenger tilkjentYtelse,
                                                                       Språkkode språkkode) {
        Map<String, List<Uttaksperiode>> resultat = new HashMap<>();

        var uttakPerioder = uttaksresultat.getUttakResultatArbeidsforhold().stream().flatMap(ur -> ur.getPerioder().stream()).toList();

        for (var tilkjentPeriode : tilkjentYtelse.getPerioder()) {
            var uttakPeriodeKandidater = PeriodeBeregner.finnUttakPeriodeKandidater(tilkjentPeriode, uttakPerioder);
            for (var tilkjentAndel : tilkjentPeriode.getAndeler()) {
                var aktivitetStatus = tilkjentAndel.getAktivitetStatus();
                var aktivitetsbeskrivelse = utledAktivitetsbeskrivelse(tilkjentAndel, aktivitetStatus);

                var matchetUttaksperiode = finnUttakPeriode(uttakPeriodeKandidater, aktivitetsbeskrivelse);
                matchetUttaksperiode.ifPresent(
                    svpUttakResultatPeriode -> mapAktivitet(resultat, svpUttakResultatPeriode, tilkjentPeriode, aktivitetsbeskrivelse, språkkode));
            }
        }

        return resultat.entrySet()
            .stream()
            .map(entry -> Uttaksaktivitet.ny().medAktivitetsbeskrivelse(entry.getKey()).medUttaksperioder(entry.getValue()).build())
            .toList();
    }

    private static Optional<SvpUttakResultatPeriode> finnUttakPeriode(List<SvpUttakResultatPeriode> matchendeUttaksperioder,
                                                                      String aktivitetsbeskrivelse) {
        return matchendeUttaksperioder.stream()
            .filter(uttakPeriode -> !uttakPeriode.getArbeidsgiverNavn().isEmpty())
            .filter(uttakPeriode -> aktivitetsbeskrivelse.contains(uttakPeriode.getArbeidsgiverNavn()))
            .findFirst();
    }

    private static void mapAktivitet(Map<String, List<Uttaksperiode>> map,
                                     SvpUttakResultatPeriode matchetUttaksperiode,
                                     TilkjentYtelsePeriode tilkjentYtelsePeriode,
                                     String aktivitetsbeskrivelse,
                                     Språkkode språkkode) {
        var uttaksperiode = Uttaksperiode.ny()
            .medPeriodeFom(tilkjentYtelsePeriode.getPeriode().getFomDato(), språkkode)
            .medPeriodeTom(tilkjentYtelsePeriode.getPeriode().getTomDato(), språkkode)
            .medUtbetalingsgrad(Prosent.of(matchetUttaksperiode.getUtbetalingsgrad()))
            .build();

        var uttaksperioder = new ArrayList<Uttaksperiode>();
        uttaksperioder.add(uttaksperiode);

        if (map.putIfAbsent(aktivitetsbeskrivelse, uttaksperioder) != null) {
            map.merge(aktivitetsbeskrivelse, uttaksperioder, leggTilEllerMergeHvisSammenhengende());
        }
    }

    private static BiFunction<List<Uttaksperiode>, List<Uttaksperiode>, List<Uttaksperiode>> leggTilEllerMergeHvisSammenhengende() {
        return (eksisterendePerioder, nyPeriode) -> {
            eksisterendePerioder.add(nyPeriode.get(0));
            var perioderEtterSammenslåing = new ArrayList<Uttaksperiode>();
            return eksisterendePerioder.stream().reduce(perioderEtterSammenslåing, slåSammenSammenhengendePerioder(), dummyCombiner());
        };
    }

    private static BinaryOperator<List<Uttaksperiode>> dummyCombiner() { // trengs ikke for sekvensielle streams
        return (result1, result2) -> {
            result1.addAll(result2);
            return result1;
        };
    }

    private static BiFunction<List<Uttaksperiode>, Uttaksperiode, List<Uttaksperiode>> slåSammenSammenhengendePerioder() {
        return (resultat, neste) -> {
            if (resultat.isEmpty() || !erPerioderSammenhengendeEllerLikeOgSkalSlåSammen(hentSisteUttaksperiode(resultat), neste)) {
                resultat.add(neste);
            } else {
                var forrige = hentOgFjernSisteUttaksperiode(resultat);
                resultat.add(Uttaksperiode.ny(forrige).medPeriodeTom(neste.getPeriodeTom(), neste.getSpråkkode()).build());
            }
            return resultat;
        };
    }

    private static boolean erPerioderSammenhengendeEllerLikeOgSkalSlåSammen(Uttaksperiode periodeEn, Uttaksperiode periodeTo) {
        var sammeUtbetalingsgrad = periodeEn.getUtbetalingsgrad().equals(periodeTo.getUtbetalingsgrad());
        return sammeUtbetalingsgrad && (erFomRettEtterTomDato(periodeEn.getPeriodeTom(), periodeTo.getPeriodeFom()) || erFomOgTomLike(periodeEn,
            periodeTo));
    }

    private static boolean erFomOgTomLike(Uttaksperiode periodeEn, Uttaksperiode periodeTo) {
        return periodeEn.getPeriodeFom().equals(periodeTo.getPeriodeFom()) && periodeEn.getPeriodeTom().equals(periodeTo.getPeriodeTom());
    }

    private static Uttaksperiode hentSisteUttaksperiode(List<Uttaksperiode> uttaksperioder) {
        return uttaksperioder.get(uttaksperioder.size() - 1);
    }

    private static Uttaksperiode hentOgFjernSisteUttaksperiode(List<Uttaksperiode> uttaksperioder) {
        return uttaksperioder.remove(uttaksperioder.size() - 1);
    }
}
