package no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsesvp;

import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsesvp.AktivitetsbeskrivelseUtleder.utledAktivitetsbeskrivelse;
import static no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeMergerVerktøy.erFomRettEtterTomDato;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.melding.beregning.BeregningsresultatAndel;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatFP;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeBeregner;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Prosent;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsesvp.Uttaksaktivitet;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsesvp.Uttaksperiode;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttaksresultat;

public final class UttaksperiodeMapper {

    public static List<Uttaksaktivitet> mapUttaksaktivteterMedPerioder(SvpUttaksresultat uttaksresultat,
                                                                       BeregningsresultatFP beregningsresultat,
                                                                       Språkkode språkkode) {
        Map<String, List<Uttaksperiode>> resultat = new HashMap<>();

        List<SvpUttakResultatPeriode> uttakPerioder = uttaksresultat.getUttakResultatArbeidsforhold().stream()
                .flatMap(ur -> ur.getPerioder().stream())
                .collect(Collectors.toList());

        for (BeregningsresultatPeriode periode : beregningsresultat.getBeregningsresultatPerioder()) {
            var uttakPeriodeKandidater = PeriodeBeregner.finnUttakPeriodeKandidater(periode, uttakPerioder);
            for (BeregningsresultatAndel andel : periode.getBeregningsresultatAndelList()) {
                AktivitetStatus aktivitetStatus = andel.getAktivitetStatus();
                String aktivitetsbeskrivelse = utledAktivitetsbeskrivelse(andel, aktivitetStatus);

                Optional<SvpUttakResultatPeriode> matchetUttaksperiode = finnUttakPeriode(uttakPeriodeKandidater, aktivitetsbeskrivelse);
                matchetUttaksperiode.ifPresent(svpUttakResultatPeriode ->
                        mapAktivitet(resultat, svpUttakResultatPeriode, periode, aktivitetsbeskrivelse, språkkode));
            }
        }

        return resultat.entrySet().stream()
                .map(entry -> Uttaksaktivitet.ny()
                    .medAktivitetsbeskrivelse(entry.getKey())
                    .medUttaksperioder(entry.getValue())
                    .build())
                .collect(Collectors.toList());
    }

    private static Optional<SvpUttakResultatPeriode> finnUttakPeriode(List<SvpUttakResultatPeriode> matchendeUttaksperioder,
                                                                      String aktivitetsbeskrivelse) {
        return matchendeUttaksperioder.stream().filter(uttakPeriode -> !uttakPeriode.getArbeidsgiverNavn().isEmpty())
                .filter(uttakPeriode -> aktivitetsbeskrivelse.contains(uttakPeriode.getArbeidsgiverNavn()))
                .findFirst();
    }

    private static void mapAktivitet(Map<String, List<Uttaksperiode>> map, SvpUttakResultatPeriode matchetUttaksperiode,
                                     BeregningsresultatPeriode beregningsresultatPeriode,
                                     String aktivitetsbeskrivelse, Språkkode språkkode) {
        Uttaksperiode uttaksperiode = Uttaksperiode.ny()
                .medPeriodeFom(beregningsresultatPeriode.getPeriode().getFomDato(), språkkode)
                .medPeriodeTom(beregningsresultatPeriode.getPeriode().getTomDato(), språkkode)
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
            return eksisterendePerioder.stream()
                    .reduce(perioderEtterSammenslåing, slåSammenSammenhengendePerioder(), dummyCombiner());
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
            if (resultat.isEmpty() || !erPerioderSammenhengendeOgSkalSlåSammen(hentSisteUttaksperiode(resultat), neste)) {
                resultat.add(neste);
            } else {
                var forrige = hentOgFjernSisteUttaksperiode(resultat);
                resultat.add(Uttaksperiode.ny(forrige).medPeriodeTom(neste.getPeriodeTom(), neste.getSpråkkode()).build());
            }
            return resultat;
        };
    }

    private static boolean erPerioderSammenhengendeOgSkalSlåSammen(Uttaksperiode periodeEn, Uttaksperiode periodeTo) {
        boolean sammeUtbetalingsgrad = periodeEn.getUtbetalingsgrad().equals(periodeTo.getUtbetalingsgrad());
        return sammeUtbetalingsgrad && erFomRettEtterTomDato(periodeEn.getPeriodeTom(), periodeTo.getPeriodeFom());
    }

    private static Uttaksperiode hentSisteUttaksperiode(List<Uttaksperiode> uttaksperioder) {
        return uttaksperioder.get(uttaksperioder.size() - 1);
    }

    private static Uttaksperiode hentOgFjernSisteUttaksperiode(List<Uttaksperiode> uttaksperioder) {
        return uttaksperioder.remove(uttaksperioder.size() - 1);
    }
}
