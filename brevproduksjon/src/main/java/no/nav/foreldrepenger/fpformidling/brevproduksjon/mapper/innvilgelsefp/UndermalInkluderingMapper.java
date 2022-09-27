package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp;

import java.util.List;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.behandling.BehandlingType;
import no.nav.foreldrepenger.fpformidling.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Årsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.AnnenAktivitet;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.Arbeidsforhold;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.Utbetalingsperiode;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.fpformidling.uttak.kodeliste.PeriodeResultatÅrsak;

/**
 * Klassen utleder hvorvidt for forskjellige blokker / undermaler i innvilgelse foreldrepenger brevet skal inkluderes:
 */
public final class UndermalInkluderingMapper {

    public static boolean skalInkludereUtbetaling(Behandling behandling, List<Utbetalingsperiode> utbetalingsperioder) {
        return behandling.getBehandlingsresultat().erInnvilget()
                && (utbetalingsperioder.size() > 1 || harKunEnPeriodeUtenGraderingOgUtenUtbetalingÅrsak(utbetalingsperioder));
    }

    public static boolean skalInkludereUtbetNårGradering(Behandling behandling, List<Utbetalingsperiode> utbetalingsperioder) {
        return behandling.getBehandlingsresultat().erInnvilget() && utbetalingsperioder.size() == 1 && periodeHarUtbetalingÅrsakEllerGradering(utbetalingsperioder.get(0));
    }

    public static boolean skalInkludereInnvilget(Behandling behandling, List<Utbetalingsperiode> utbetalingsperioder, String konsekvens) {
        return !KonsekvensForYtelsen.ENDRING_I_BEREGNING.getKode().equals(konsekvens)
                && (harMerEnnEnPeriodeOgMinstEnInnvilget(utbetalingsperioder)
                || erRevurderingMedEndring(konsekvens, behandling)
                || utbetalingsperioder.stream().anyMatch(UndermalInkluderingMapper::periodeHarUtbetalingÅrsakEllerGradering));
    }

    public static boolean skalInkludereAvslag(List<Utbetalingsperiode> utbetalingsperioder, String konsekvens) {
        return utbetalingsperioder.stream()
                .filter(periode -> !periodeMed4102UtenTapteDager(periode))
                .anyMatch(Utbetalingsperiode::isAvslått)
                && (!KonsekvensForYtelsen.ENDRING_I_BEREGNING.getKode().equals(konsekvens)) ;
    }

    private static boolean periodeMed4102UtenTapteDager(Utbetalingsperiode periode) {
        return Årsak.of(PeriodeResultatÅrsak.BARE_FAR_RETT_IKKE_SØKT.getKode()).equals(periode.getÅrsak()) && periode.getAntallTapteDager() == 0;
    }

    public static boolean skalInkludereNyeOpplysningerUtbet(Behandling behandling, List<Utbetalingsperiode> utbetalingsperioder, long dagsats) {
        return dagsats > 0 && behandling.getBehandlingsresultat().erInnvilget() && utbetalingsperioder.size() == 1 && periodeHarGyldigUtsettelseÅrsakEllerGradering(utbetalingsperioder.get(0));
    }

    private static boolean harKunEnPeriodeUtenGraderingOgUtenUtbetalingÅrsak(List<Utbetalingsperiode> utbetalingsperioder) {
        return utbetalingsperioder.size() == 1 && !utbetalingsperioder.get(0).getÅrsak().erUtbetalingÅrsak() && ikkeHarGradering(utbetalingsperioder.get(0));
    }

    private static boolean ikkeHarGradering(Utbetalingsperiode utbetalingsperiode) {
        return utbetalingsperiode.getArbeidsforholdsliste().stream().noneMatch(Arbeidsforhold::isGradering)
                && (utbetalingsperiode.getNæring() == null || !utbetalingsperiode.getNæring().isGradering())
                && utbetalingsperiode.getAnnenAktivitetsliste().stream().noneMatch(AnnenAktivitet::isGradering);
    }

    private static boolean harMerEnnEnPeriodeOgMinstEnInnvilget(List<Utbetalingsperiode> utbetalingsperioder) {
        return utbetalingsperioder.stream().anyMatch(Utbetalingsperiode::isInnvilget) && utbetalingsperioder.size() > 1;
    }

    private static boolean erRevurderingMedEndring(String konsekvens, Behandling behandling) {
        return !BehandlingType.FØRSTEGANGSSØKNAD.equals(behandling.getBehandlingType())
                && BehandlingResultatType.FORELDREPENGER_ENDRET.equals(behandling.getBehandlingsresultat().getBehandlingResultatType())
                && erEndringIUttak(konsekvens);
    }

    private static boolean erEndringIUttak(String konsekvens) {
        return KonsekvensForYtelsen.ENDRING_I_UTTAK.getKode().equals(konsekvens) || KonsekvensForYtelsen.ENDRING_I_BEREGNING_OG_UTTAK.getKode().equals(konsekvens);
    }

    private static boolean periodeHarUtbetalingÅrsakEllerGradering(Utbetalingsperiode utbetalingsperiode) {
        return utbetalingsperiode.getÅrsak().erUtbetalingÅrsak() || harGradering(utbetalingsperiode);
    }

    private static boolean periodeHarGyldigUtsettelseÅrsakEllerGradering(Utbetalingsperiode utbetalingsperiode) {
        return utbetalingsperiode.getÅrsak().erGyldigUtsettelseÅrsak() || harGradering(utbetalingsperiode);
    }

    private static boolean harGradering(Utbetalingsperiode utbetalingsperiode) {
        return utbetalingsperiode.getArbeidsforholdsliste().stream().anyMatch(Arbeidsforhold::isGradering)
                || (utbetalingsperiode.getNæring() != null && utbetalingsperiode.getNæring().isGradering())
                || utbetalingsperiode.getAnnenAktivitetsliste().stream().anyMatch(AnnenAktivitet::isGradering);
    }
}
