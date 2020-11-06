package no.nav.foreldrepenger.melding.datamapper.domene;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.KonsekvensForYtelseKode;

public class FellesMapperTest {


    @Test
    public void formaterLovhjemlerBeregningEnkelTest() throws IOException {
        String lovhjemmelFraBeregning = "folketrygdloven § 14-7";
        assertLovformateringBeregning(lovhjemmelFraBeregning, "", false, "§ 14-7");
    }

    @Test
    public void formaterLovhjemlerInnvilgetRevurderingBeregningTest() throws IOException {
        String lovhjemmelFraBeregning = "folketrygdloven § 14-7";
        assertLovformateringBeregning(lovhjemmelFraBeregning, "", true, "§ 14-7 og forvaltningsloven § 35");
    }

    @Test
    public void formaterLovhjemlerRevurderingEndringBeregningTest() throws IOException {
        String lovhjemmelFraBeregning = "folketrygdloven § 14-7";
        assertLovformateringBeregning(lovhjemmelFraBeregning, KonsekvensForYtelseKode.ENDRING_I_BEREGNING.value(), false, "§ 14-7 og forvaltningsloven § 35");
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
        String lovhjemler = FellesMapper.formaterLovhjemlerForBeregning(input, konsekvensForYtelse, innvilgetRevurdering);
        assertThat(lovhjemler).isEqualTo(forventetOutput);
    }


}
