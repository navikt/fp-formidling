package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.domene.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.BeregningHjemmel;

class FellesMapperTest {

    @Test
    void formaterLovhjemlerBeregningEnkel() {
        var lovhjemmelFraBeregning = "folketrygdloven § 14-7";
        assertLovformateringBeregning(lovhjemmelFraBeregning, null, false, "§ 14-7");
    }

    @Test
    void formaterLovhjemlerInnvilgetRevurderingBeregning() {
        var lovhjemmelFraBeregning = "folketrygdloven § 14-7";
        assertLovformateringBeregning(lovhjemmelFraBeregning, null, true, "§ 14-7 og forvaltningsloven § 35");
    }

    @Test
    void formaterLovhjemlerRevurderingEndringBeregning() {
        var lovhjemmelFraBeregning = "folketrygdloven § 14-7";
        assertLovformateringBeregning(lovhjemmelFraBeregning, KonsekvensForYtelsen.ENDRING_I_BEREGNING, false,
            "§ 14-7 og forvaltningsloven § 35");
    }

    @Test
    void formaterLovhjemlerUdefinert() {
        var lovhjemmelFraBeregning = BeregningHjemmel.UDEFINERT.getLovRef();
        assertLovformateringBeregning(lovhjemmelFraBeregning, null, false, "");
    }

    @Test
    void formaterLovhjemlerNull() {
        String lovhjemmelFraBeregning = null;
        assertLovformateringBeregning(lovhjemmelFraBeregning, null, false, "");
    }

    @Test
    void formaterLovhjemlerRevurderingEndringBeregningOgLovhjemmelUdefinertFraBeregning() {
        var lovhjemmelFraBeregning = BeregningHjemmel.UDEFINERT.getLovRef();
        assertLovformateringBeregning(lovhjemmelFraBeregning, KonsekvensForYtelsen.ENDRING_I_BEREGNING, false,
            " og forvaltningsloven § 35");
    }

    @Test
    void formaterLovhjemlerRevurderingEndringBeregningOgLovhjemmelNullFraBeregning() {
        String lovhjemmelFraBeregning = null;
        assertLovformateringBeregning(lovhjemmelFraBeregning, KonsekvensForYtelsen.ENDRING_I_BEREGNING, false,
            " og forvaltningsloven § 35");
    }

    @Test
    void skal_formatere_lovhjemmel_uttak_med_forvaltningsloven() {
        Set<String> hjemmelSet = new TreeSet<>();
        hjemmelSet.add("14-16");
        hjemmelSet.add("14-18");
        var resultat = FellesMapper.formaterLovhjemlerUttak(hjemmelSet, null, true);
        assertThat(resultat).isEqualTo("§§ 14-16, 14-18 og forvaltningsloven § 35");
    }

    @Test
    void skal_formatere_lovhjemmel_uttak() {
        Set<String> hjemmelSet = new TreeSet<>();
        hjemmelSet.add("14-16");
        hjemmelSet.add("14-18");
        hjemmelSet.add("14-17");
        var resultat = FellesMapper.formaterLovhjemlerUttak(hjemmelSet, null, false);
        assertThat(resultat).isEqualTo("§§ 14-16, 14-17 og 14-18");
    }

    @Test
    void skal_formatere_enkel_lovhjemmel() {
        Set<String> hjemmelSet = new TreeSet<>();
        hjemmelSet.add("14-16");
        var resultat = FellesMapper.formaterLovhjemlerUttak(hjemmelSet, null, false);
        assertThat(resultat).isEqualTo("§ 14-16");
    }

    private void assertLovformateringBeregning(String input, KonsekvensForYtelsen konsekvensForYtelse, boolean innvilgetRevurdering, String forventetOutput) {
        var lovhjemler = FellesMapper.formaterLovhjemlerForBeregning(input, konsekvensForYtelse, innvilgetRevurdering,
                UUID.randomUUID());
        assertThat(lovhjemler).isEqualTo(forventetOutput);
    }
}
