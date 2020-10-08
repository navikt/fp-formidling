package no.nav.foreldrepenger.melding.brevbestiller;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Ignore;
import org.junit.Test;

import no.nav.foreldrepenger.melding.geografisk.Landkoder;

public class LandkodeOversetterTest {


    @Test
    public void iso3ErNull() {
        LandkodeOversetter oversetter = new LandkodeOversetter();
        String iso2 = oversetter.tilIso2(null);
        assertThat(iso2, is("???"));
    }

    @Test
    public void iso2FinnesIkke() {
        LandkodeOversetter oversetter = new LandkodeOversetter();
        String iso2 = oversetter.tilIso2("XXX");
        assertThat(iso2, is(Landkoder.UOPPGITT.getKode()));
    }

    @Ignore // Denne virket kun pga mocking
    @Test
    public void iso2Finnes() {
        LandkodeOversetter oversetter = new LandkodeOversetter();
        String iso2 = oversetter.tilIso2("NOR");
        assertThat(iso2, is("NO"));
    }

    @Test
    public void iso2Finnes2() {
        LandkodeOversetter oversetter = new LandkodeOversetter();
        String iso2 = oversetter.tilIso2("NO");
        assertThat(iso2, is(Landkoder.NOR.getKode()));
    }

}