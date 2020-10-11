package no.nav.foreldrepenger.melding.brevbestiller;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.melding.geografisk.Landkoder;

public class LandkodeOversetterTest {

    @Test
    public void iso3ErNull() {
        String land = LandkodeOversetter.tilLandkoder(null);
        assertThat(land, is("???"));
    }

    @Test
    public void landFinnesIkke() {
        String land = LandkodeOversetter.tilLandkoder("XYZ");
        assertThat(land, is(Landkoder.UOPPGITT.getKode()));
    }

    @Test
    public void landFinnes2() {
        String land = LandkodeOversetter.tilLandkoder("SE");
        assertThat(land, is(Landkoder.SWE.getKode()));
    }

    @Test
    public void landFinnes3() {
        String land = LandkodeOversetter.tilLandkoder("NOR");
        assertThat(land, is(Landkoder.NOR.getKode()));
    }

}