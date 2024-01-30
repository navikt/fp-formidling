package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp;

import static java.util.List.of;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.UndermalInkluderingMapper.skalInkludereAvslag;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.UndermalInkluderingMapper.skalInkludereInnvilget;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import no.nav.foreldrepenger.fpformidling.domene.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Årsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.Vedtaksperiode;


class UndermalInkluderingMapperTest {


    @ParameterizedTest
    @MethodSource("inkludereInnvilget")
    void undermalInnvilgetSkalInkluderes(List<Vedtaksperiode> vedtaksperioder, String konsekvens) {
        var resultat = skalInkludereInnvilget(vedtaksperioder, konsekvens);

        assertThat(resultat).isTrue();
    }

    static Stream<Arguments> inkludereInnvilget() {
        return Stream.of(
            Arguments.of(of(lagPeriode("2010", true), lagPeriode("2011", false)), KonsekvensForYtelsen.ENDRING_I_UTTAK.getKode()),
            Arguments.of(of(lagPeriode("1234", true)), KonsekvensForYtelsen.ENDRING_I_UTTAK.getKode()),
            Arguments.of(of(lagPeriode("2010", true)), KonsekvensForYtelsen.ENDRING_I_UTTAK.getKode()),
            Arguments.of(of(lagPeriode("2010", false), lagPeriode("2010", true)), KonsekvensForYtelsen.ENDRING_I_UTTAK.getKode()),
            Arguments.of(of(lagPeriode("2030", true)), KonsekvensForYtelsen.ENDRING_I_UTTAK.getKode()),
            Arguments.of(of(lagPeriode("2030", true), lagPeriode("2030", true)), KonsekvensForYtelsen.ENDRING_I_UTTAK.getKode()),
            Arguments.of(of(lagPeriode("2030", true), lagPeriode("4022", false)), KonsekvensForYtelsen.ENDRING_I_UTTAK.getKode()),
            Arguments.of(of(lagPeriode("2030", true), lagPeriode("4022", false), lagPeriode("4022", false)), KonsekvensForYtelsen.ENDRING_I_UTTAK.getKode()));
    }

    @ParameterizedTest
    @MethodSource("ikkeInkludereInnvilget")
    void undermalInnvilgetSkalIkkeInkluderes(List<Vedtaksperiode> vedtaksperioder, String konsekvens) {
        var resultat = skalInkludereInnvilget(vedtaksperioder, konsekvens);

        assertThat(resultat).isFalse();
    }

    static Stream<Arguments> ikkeInkludereInnvilget() {
        return Stream.of(
            Arguments.of(of(lagPeriode("2010", true), lagPeriode("2011", false)), KonsekvensForYtelsen.ENDRING_I_BEREGNING.getKode()),
            Arguments.of(of(lagPeriode("2010", false), lagPeriode("2011", false)), KonsekvensForYtelsen.ENDRING_I_BEREGNING.getKode()));
    }


    @ParameterizedTest
    @MethodSource("testScenarioerSkalIkkeInklAvslag")
    void undermalAvslagSkalIkkeInkluderes(List<Vedtaksperiode> vedtaksperioder, String konsekvens) {
        var resultat = skalInkludereAvslag(vedtaksperioder, konsekvens);
        assertThat(resultat).isFalse();
    }

    static Stream<Arguments> testScenarioerSkalIkkeInklAvslag() {
        return Stream.of(
            Arguments.of(of(lagPeriode("2010", true),lagPeriode("2010", true)), KonsekvensForYtelsen.ENDRING_I_UTTAK.getKode()),
            Arguments.of(of(lagPeriode("2010", false),lagPeriode("2010", true)), KonsekvensForYtelsen.ENDRING_I_BEREGNING.getKode()));
    }

    @ParameterizedTest
    @MethodSource("testScenarioerInklAvslag")
    void undermalAvslagSkalInkluderes(List<Vedtaksperiode> vedtaksperioder, String konsekvens) {
        var resultat = skalInkludereAvslag(vedtaksperioder, konsekvens);
        assertThat(resultat).isTrue();
    }

    static Stream<Arguments> testScenarioerInklAvslag() {
        return Stream.of(
            Arguments.of(of(lagPeriode("2010",false),lagPeriode("2010", true)), KonsekvensForYtelsen.ENDRING_I_UTTAK.getKode()),
            Arguments.of(of(lagPeriode("2010", false), lagPeriode("2010", true)), null)
        );
    }

    private static Vedtaksperiode lagPeriode(String årsak, boolean erInnvilget) {
        return Vedtaksperiode.ny().medÅrsak(Årsak.of(årsak)).medInnvilget(erInnvilget).build();
    }
}
