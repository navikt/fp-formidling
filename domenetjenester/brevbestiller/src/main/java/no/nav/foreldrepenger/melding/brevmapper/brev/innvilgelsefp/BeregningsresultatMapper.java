package no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp;

import no.nav.foreldrepenger.melding.beregning.BeregningsresultatAndel;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatFP;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public final class BeregningsresultatMapper {

    public static long finnMånedsbeløp(BeregningsresultatFP beregningsresultat) {
        return finnFørsteInnvilgedePeriode(beregningsresultat).map(BeregningsresultatMapper::getMånedsbeløp).orElse(0L);
    }

    public static long finnDagsats(BeregningsresultatFP beregningsresultat) {
        return finnFørsteInnvilgedePeriode(beregningsresultat).map(BeregningsresultatPeriode::getDagsats).orElse(0L);
    }

    public static int finnAntallArbeidsgivere(BeregningsresultatFP beregningsresultat) {
        return (int)beregningsresultat.getBeregningsresultatPerioder().stream()
                .map(BeregningsresultatPeriode::getBeregningsresultatAndelList)
                .flatMap(Collection::stream)
                .filter(andel -> AktivitetStatus.ARBEIDSTAKER.equals(andel.getAktivitetStatus()))
                .map(BeregningsresultatAndel::getArbeidsgiver)
                .flatMap(Optional::stream)
                .map(Arbeidsgiver::getArbeidsgiverReferanse)
                .distinct()
                .count();
    }

    public static boolean harIngenRefusjon(BeregningsresultatFP beregningsresultatFP) {
        return harBrukerAndel(beregningsresultatFP) && !harArbeidsgiverAndel(beregningsresultatFP);
    }

    public static boolean harDelvisRefusjon(BeregningsresultatFP beregningsresultatFP) {
        return harBrukerAndel(beregningsresultatFP) && harArbeidsgiverAndel(beregningsresultatFP);
    }
    public static boolean harFullRefusjon(BeregningsresultatFP beregningsresultatFP) {
        return !harBrukerAndel(beregningsresultatFP) && harArbeidsgiverAndel(beregningsresultatFP);
    }

    public static boolean harBrukerAndel(BeregningsresultatFP beregningsresultatFP) {
        return beregningsresultatFP.getBeregningsresultatPerioder().stream()
                .map(BeregningsresultatPeriode::getBeregningsresultatAndelList)
                .flatMap(List::stream)
                .anyMatch(BeregningsresultatAndel::erBrukerMottaker);
    }

    public static boolean harArbeidsgiverAndel(BeregningsresultatFP beregningsresultatFP) {
        return beregningsresultatFP.getBeregningsresultatPerioder().stream()
                .map(BeregningsresultatPeriode::getBeregningsresultatAndelList)
                .flatMap(List::stream)
                .anyMatch(BeregningsresultatAndel::erArbeidsgiverMottaker);
    }

    private static Optional<BeregningsresultatPeriode> finnFørsteInnvilgedePeriode(BeregningsresultatFP beregningsresultat) {
        return beregningsresultat.getBeregningsresultatPerioder()
                .stream()
                .filter(harDagsatsOverNull())
                .min(Comparator.comparing(BeregningsresultatPeriode::getBeregningsresultatPeriodeFom));
    }

    private static Predicate<BeregningsresultatPeriode> harDagsatsOverNull() {
        return beregningsresultatPeriode -> beregningsresultatPeriode.getDagsats() != null && beregningsresultatPeriode.getDagsats() > 0;
    }

    private static long getMånedsbeløp(BeregningsresultatPeriode førstePeriode) {
        return førstePeriode.getDagsats() * 260 / 12;
    }
}
