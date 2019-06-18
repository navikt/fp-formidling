package no.nav.vedtak.felles.prosesstask.impl.feilhåndtering;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.Assert;
import org.junit.Test;

public class ÅpningstidForsinkelseStrategiTest {

    final static int klokkeslettÅpning = 7;
    final static int klokkeslettStenging = 18;

    ÅpningstidForsinkelseStrategi strategi = new ÅpningstidForsinkelseStrategi();

    @Test
    public void utenforÅpningsTidTest() {
        LocalDateTime localDateTime = LocalDateTime.of(2017, 6, 14, 5, 5, 5);
        LocalDateTime expected = LocalDateTime.of(2017, 6, 14, klokkeslettÅpning, 5, 5);
        int i = strategi.sekunderTilNesteForsøk(localDateTime, klokkeslettÅpning, klokkeslettStenging);
        long diff = expected.toEpochSecond(ZoneOffset.UTC) - localDateTime.toEpochSecond(ZoneOffset.UTC) + 120;
        Assert.assertEquals(i, diff);
    }

    @Test
    public void innenforÅpningsTidTest() {
        LocalDateTime localDateTime = LocalDateTime.of(2017, 6, 14, 10, 5, 5);
        int i = strategi.sekunderTilNesteForsøk(localDateTime, klokkeslettÅpning, klokkeslettStenging);
        Assert.assertTrue(i == 120);
    }

    @Test
    public void helgenÅpningsTidTest() {
        LocalDateTime localDateTime = LocalDateTime.of(2017, 6, 17, 10, 5, 5);
        LocalDateTime expected = LocalDateTime.of(2017, 6, 19, klokkeslettÅpning, 5, 5);
        int i = strategi.sekunderTilNesteForsøk(localDateTime, klokkeslettÅpning, klokkeslettStenging);
        long diff = expected.toEpochSecond(ZoneOffset.UTC) - localDateTime.toEpochSecond(ZoneOffset.UTC) + 120;
        Assert.assertEquals(i, diff);
    }
}
