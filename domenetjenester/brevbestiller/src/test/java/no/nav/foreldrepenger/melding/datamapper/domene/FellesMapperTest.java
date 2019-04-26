package no.nav.foreldrepenger.melding.datamapper.domene;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;

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

    private void assertLovformateringBeregning(String input, String konsekvensForYtelse, boolean innvilgetRevurdering, String forventetOutput) {
        String lovhjemler = FellesMapper.formaterLovhjemlerForBeregning(input, konsekvensForYtelse, innvilgetRevurdering);
        assertThat(lovhjemler).isEqualTo(forventetOutput);
    }


}
