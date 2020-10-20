package no.nav.foreldrepenger.melding.brevbestiller;

import static no.nav.foreldrepenger.melding.brevbestiller.LandkodeOversetter.LANDKODER_ISO2_UOPPGITT;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.melding.geografisk.Landkoder;

public class LandkodeOversetterTest {

    @Test
    public void iso3ErNull() {
        String land = LandkodeOversetter.tilLandkoderToBokstav(null);
        assertThat(land, is(LANDKODER_ISO2_UOPPGITT));
    }

    @Test
    public void landFinnesIkke() {
        String land = LandkodeOversetter.tilLandkoderToBokstav("XYZ");
        assertThat(land, is(LANDKODER_ISO2_UOPPGITT));
    }

    @Test
    public void landFinnes2() {
        String land = LandkodeOversetter.tilLandkoderToBokstav(Landkoder.SWE.getKode());
        assertThat(land, is("SE"));
    }

    @Test
    public void landFinnes3() {
        String land = LandkodeOversetter.tilLandkoderToBokstav("SE");
        assertThat(land, is("SE"));
    }

    @Test
    public void landFinnesIkke2() {
        String land = LandkodeOversetter.tilLandkoderToBokstav("NN");
        assertThat(land, is(LANDKODER_ISO2_UOPPGITT));
    }

}