package no.nav.foreldrepenger.melding.datamapper.domene;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Hjemmel;

public class FellesMapperTest {

    @Test
    public void formaterLovhjemlerBeregningEnkel() {
        String lovhjemmelFraBeregning = "folketrygdloven § 14-7";
        assertLovformateringBeregning(lovhjemmelFraBeregning, "", false, "§ 14-7");
    }

    @Test
    public void formaterLovhjemlerInnvilgetRevurderingBeregning() {
        String lovhjemmelFraBeregning = "folketrygdloven § 14-7";
        assertLovformateringBeregning(lovhjemmelFraBeregning, "", true, "§ 14-7 og forvaltningsloven § 35");
    }

    @Test
    public void formaterLovhjemlerRevurderingEndringBeregning() {
        String lovhjemmelFraBeregning = "folketrygdloven § 14-7";
        assertLovformateringBeregning(lovhjemmelFraBeregning, KonsekvensForYtelsen.ENDRING_I_BEREGNING.getKode(), false, "§ 14-7 og forvaltningsloven § 35");
    }

    @Test
    public void formaterLovhjemlerUdefinert() {
        String lovhjemmelFraBeregning = Hjemmel.UDEFINERT.getNavn();
        assertLovformateringBeregning(lovhjemmelFraBeregning, "", false, "");
    }

    @Test
    public void formaterLovhjemlerNull() {
        String lovhjemmelFraBeregning = null;
        assertLovformateringBeregning(lovhjemmelFraBeregning, "", false, "");
    }

    @Test
    public void formaterLovhjemlerRevurderingEndringBeregningOgLovhjemmelUdefinertFraBeregning() {
        String lovhjemmelFraBeregning = Hjemmel.UDEFINERT.getNavn();
        assertLovformateringBeregning(lovhjemmelFraBeregning, KonsekvensForYtelsen.ENDRING_I_BEREGNING.getKode(), false, " og forvaltningsloven § 35");
    }

    @Test
    public void formaterLovhjemlerRevurderingEndringBeregningOgLovhjemmelNullFraBeregning() {
        String lovhjemmelFraBeregning = null;
        assertLovformateringBeregning(lovhjemmelFraBeregning, KonsekvensForYtelsen.ENDRING_I_BEREGNING.getKode(), false, " og forvaltningsloven § 35");
    }

    @Test
    public void skal_formatere_lovhjemmel_uttak_med_forvaltningsloven() {
        Set<String> hjemmelSet = new TreeSet<>();
        hjemmelSet.add("14-16");
        hjemmelSet.add("14-18");
        String resultat = FellesMapper.formaterLovhjemlerUttak(hjemmelSet, "", true);
        assertThat(resultat).isEqualTo("§§ 14-16, 14-18 og forvaltningsloven § 35");
    }

    @Test
    public void skal_formatere_lovhjemmel_uttak() {
        Set<String> hjemmelSet = new TreeSet<>();
        hjemmelSet.add("14-16");
        hjemmelSet.add("14-18");
        hjemmelSet.add("14-17");
        String resultat = FellesMapper.formaterLovhjemlerUttak(hjemmelSet, "", false);
        assertThat(resultat).isEqualTo("§§ 14-16, 14-17 og 14-18");
    }

    @Test
    public void skal_formatere_enkel_lovhjemmel() {
        Set<String> hjemmelSet = new TreeSet<>();
        hjemmelSet.add("14-16");
        String resultat = FellesMapper.formaterLovhjemlerUttak(hjemmelSet, "", false);
        assertThat(resultat).isEqualTo("§ 14-16");
    }

    private void assertLovformateringBeregning(String input, String konsekvensForYtelse, boolean innvilgetRevurdering, String forventetOutput) {
        String lovhjemler = FellesMapper.formaterLovhjemlerForBeregning(input, konsekvensForYtelse, innvilgetRevurdering, Behandling.builder().medUuid(UUID.randomUUID()).build());
        assertThat(lovhjemler).isEqualTo(forventetOutput);
    }
}
