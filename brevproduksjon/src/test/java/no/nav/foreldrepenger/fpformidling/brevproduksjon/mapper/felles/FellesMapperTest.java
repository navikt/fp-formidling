package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.Hjemmel;

class FellesMapperTest {

    @Test
    void formaterLovhjemlerBeregningEnkel() {
        var lovhjemmelFraBeregning = "folketrygdloven § 14-7";
        assertLovformateringBeregning(lovhjemmelFraBeregning, "", false, "§ 14-7");
    }

    @Test
    void formaterLovhjemlerInnvilgetRevurderingBeregning() {
        var lovhjemmelFraBeregning = "folketrygdloven § 14-7";
        assertLovformateringBeregning(lovhjemmelFraBeregning, "", true, "§ 14-7 og forvaltningsloven § 35");
    }

    @Test
    void formaterLovhjemlerRevurderingEndringBeregning() {
        var lovhjemmelFraBeregning = "folketrygdloven § 14-7";
        assertLovformateringBeregning(lovhjemmelFraBeregning, KonsekvensForYtelsen.ENDRING_I_BEREGNING.getKode(), false,
            "§ 14-7 og forvaltningsloven § 35");
    }

    @Test
    void formaterLovhjemlerUdefinert() {
        var lovhjemmelFraBeregning = Hjemmel.UDEFINERT.getNavn();
        assertLovformateringBeregning(lovhjemmelFraBeregning, "", false, "");
    }

    @Test
    void formaterLovhjemlerNull() {
        String lovhjemmelFraBeregning = null;
        assertLovformateringBeregning(lovhjemmelFraBeregning, "", false, "");
    }

    @Test
    void formaterLovhjemlerRevurderingEndringBeregningOgLovhjemmelUdefinertFraBeregning() {
        var lovhjemmelFraBeregning = Hjemmel.UDEFINERT.getNavn();
        assertLovformateringBeregning(lovhjemmelFraBeregning, KonsekvensForYtelsen.ENDRING_I_BEREGNING.getKode(), false,
            " og forvaltningsloven § 35");
    }

    @Test
    void formaterLovhjemlerRevurderingEndringBeregningOgLovhjemmelNullFraBeregning() {
        String lovhjemmelFraBeregning = null;
        assertLovformateringBeregning(lovhjemmelFraBeregning, KonsekvensForYtelsen.ENDRING_I_BEREGNING.getKode(), false,
            " og forvaltningsloven § 35");
    }

    @Test
    void skal_formatere_lovhjemmel_uttak_med_forvaltningsloven() {
        Set<String> hjemmelSet = new TreeSet<>();
        hjemmelSet.add("14-16");
        hjemmelSet.add("14-18");
        var resultat = FellesMapper.formaterLovhjemlerUttak(hjemmelSet, "", true);
        assertThat(resultat).isEqualTo("§§ 14-16, 14-18 og forvaltningsloven § 35");
    }

    @Test
    void skal_formatere_lovhjemmel_uttak() {
        Set<String> hjemmelSet = new TreeSet<>();
        hjemmelSet.add("14-16");
        hjemmelSet.add("14-18");
        hjemmelSet.add("14-17");
        var resultat = FellesMapper.formaterLovhjemlerUttak(hjemmelSet, "", false);
        assertThat(resultat).isEqualTo("§§ 14-16, 14-17 og 14-18");
    }

    @Test
    void skal_formatere_enkel_lovhjemmel() {
        Set<String> hjemmelSet = new TreeSet<>();
        hjemmelSet.add("14-16");
        var resultat = FellesMapper.formaterLovhjemlerUttak(hjemmelSet, "", false);
        assertThat(resultat).isEqualTo("§ 14-16");
    }

    private void assertLovformateringBeregning(String input, String konsekvensForYtelse, boolean innvilgetRevurdering, String forventetOutput) {
        var lovhjemler = FellesMapper.formaterLovhjemlerForBeregning(input, konsekvensForYtelse, innvilgetRevurdering,
            Behandling.builder().medUuid(UUID.randomUUID()).build());
        assertThat(lovhjemler).isEqualTo(forventetOutput);
    }
}
