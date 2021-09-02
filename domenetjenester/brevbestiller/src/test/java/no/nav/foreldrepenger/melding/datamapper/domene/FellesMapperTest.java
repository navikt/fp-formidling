package no.nav.foreldrepenger.melding.datamapper.domene;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.melding.beregningsgrunnlag.Hjemmel;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.KonsekvensForYtelseKode;
import no.nav.foreldrepenger.melding.typer.Saksnummer;

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
        assertLovformateringBeregning(lovhjemmelFraBeregning, KonsekvensForYtelseKode.ENDRING_I_BEREGNING.value(), false, "§ 14-7 og forvaltningsloven § 35");
    }

    @Test
    public void formaterLovhjemlerUdefinert() {
        String lovhjemmelFraBeregning = Hjemmel.UDEFINERT.getNavn();
        assertLovformateringBeregning(lovhjemmelFraBeregning, "", false, FellesMapper.UDEFINERT);
    }

    @Test
    public void formaterLovhjemlerNull() {
        String lovhjemmelFraBeregning = null;
        assertLovformateringBeregning(lovhjemmelFraBeregning, "", false, FellesMapper.UDEFINERT);
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
        String lovhjemler = FellesMapper.formaterLovhjemlerForBeregning(input, konsekvensForYtelse, innvilgetRevurdering, new Saksnummer("123"));
        assertThat(lovhjemler).isEqualTo(forventetOutput);
    }
}
