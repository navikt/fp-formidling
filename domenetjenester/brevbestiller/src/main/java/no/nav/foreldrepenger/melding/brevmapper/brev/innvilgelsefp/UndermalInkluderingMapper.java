package no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp;

import java.util.List;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.AnnenAktivitet;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.Arbeidsforhold;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.KonsekvensForInnvilgetYtelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.Utbetalingsperiode;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BehandlingResultatType;

/**
 * Klassen utleder hvorvidt for forskjellige blokker / undermaler i innvilgelse foreldrepenger brevet skal inkluderes:
 */
public final class UndermalInkluderingMapper {
    private static final List<String> UTBETALING_ÅRSAKER = List.of("2010" , "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022", "2023", "2030", "2031", "2032", "2033", "2034", "2038");

    public static boolean skalInkludereInfoOmUtbetaling(Behandling behandling, List<Utbetalingsperiode> utbetalingsperioder) {
        return behandling.getBehandlingsresultat().erInnvilget() && (utbetalingsperioder.size() > 1 || harKunEnPeriodeUtenGraderingOgUtenGitteÅrsaker(utbetalingsperioder));
    }

    public static boolean skalInkludereUtbetaling(Behandling behandling, List<Utbetalingsperiode> utbetalingsperioder) {
        return behandling.getBehandlingsresultat().erInnvilget() && utbetalingsperioder.size() == 1 && periodeHarGitteÅrsakerEllerGradering(utbetalingsperioder.get(0));
    }

    public static boolean skalInkludereInnvilget(Behandling behandling, List<Utbetalingsperiode> utbetalingsperioder, KonsekvensForInnvilgetYtelse konsekvens) {
        return !KonsekvensForInnvilgetYtelse.ENDRING_I_BEREGNING.equals(konsekvens)
                && (harMerEnnEnPeriodeOgMinstEnInnvilget(utbetalingsperioder)
                || erRevurderingMedEndring(konsekvens, behandling)
                || utbetalingsperioder.stream().anyMatch(UndermalInkluderingMapper::periodeHarGitteÅrsakerEllerGradering)); //TODO: Dobbeltsjekke med CCM at dette ble riktig
    }

    public static boolean skalInkludereAvslag(List<Utbetalingsperiode> utbetalingsperioder, KonsekvensForInnvilgetYtelse konsekvens) {
        return utbetalingsperioder.stream().anyMatch(periode -> !periode.isInnvilget()) && !KonsekvensForInnvilgetYtelse.ENDRING_I_BEREGNING.equals(konsekvens);
    }

    private static boolean harKunEnPeriodeUtenGraderingOgUtenGitteÅrsaker(List<Utbetalingsperiode> utbetalingsperioder) {
        return utbetalingsperioder.size() == 1 && !UTBETALING_ÅRSAKER.contains(utbetalingsperioder.get(0).getÅrsak()) && ikkeHarGradering(utbetalingsperioder.get(0));
    }

    private static boolean ikkeHarGradering(Utbetalingsperiode utbetalingsperiode) {
        return (utbetalingsperiode.getArbeidsforholdsliste().size() == 0 || utbetalingsperiode.getArbeidsforholdsliste().stream().noneMatch(Arbeidsforhold::isGradering))
                && (utbetalingsperiode.getNæring() == null || !utbetalingsperiode.getNæring().isGradering())
                && (utbetalingsperiode.getAnnenAktivitetsliste().size() == 0 || utbetalingsperiode.getAnnenAktivitetsliste().stream().noneMatch(AnnenAktivitet::isGradering));
    }

    private static boolean harMerEnnEnPeriodeOgMinstEnInnvilget(List<Utbetalingsperiode> utbetalingsperioder) {
        return utbetalingsperioder.stream().anyMatch(Utbetalingsperiode::isInnvilget) && utbetalingsperioder.size() > 1;
    }

    private static boolean erRevurderingMedEndring(KonsekvensForInnvilgetYtelse konsekvens, Behandling behandling) {
        return !BehandlingType.FØRSTEGANGSSØKNAD.equals(behandling.getBehandlingType())
                && BehandlingResultatType.FORELDREPENGER_ENDRET.equals(behandling.getBehandlingsresultat().getBehandlingResultatType())
                && erEndringIUttak(konsekvens);
    }

    private static boolean erEndringIUttak(KonsekvensForInnvilgetYtelse konsekvens) {
        return KonsekvensForInnvilgetYtelse.ENDRING_I_UTTAK.equals(konsekvens) || KonsekvensForInnvilgetYtelse.ENDRING_I_BEREGNING_OG_UTTAK.equals(konsekvens);
    }

    private static boolean periodeHarGitteÅrsakerEllerGradering(Utbetalingsperiode utbetalingsperiode) {
        return UTBETALING_ÅRSAKER.contains(utbetalingsperiode.getÅrsak())
                || (utbetalingsperiode.getArbeidsforholdsliste().size() > 0 && utbetalingsperiode.getArbeidsforholdsliste().stream().anyMatch(Arbeidsforhold::isGradering))
                || (utbetalingsperiode.getNæring() != null && utbetalingsperiode.getNæring().isGradering())
                || (utbetalingsperiode.getAnnenAktivitetsliste().size() > 0 && utbetalingsperiode.getAnnenAktivitetsliste().stream().anyMatch(AnnenAktivitet::isGradering));
    }
}
