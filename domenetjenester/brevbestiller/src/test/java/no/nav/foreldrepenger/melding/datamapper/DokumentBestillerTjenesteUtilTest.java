package no.nav.foreldrepenger.melding.datamapper;

import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.SpraakkodeType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DokumentBestillerTjenesteUtilTest {

    @Test
    public void mapSpråkkode() {
        SpraakkodeType spraakkode = DokumentBestillerTjenesteUtil.mapSpråkkode(Språkkode.nb);
        assertEquals(SpraakkodeType.NB, spraakkode);

        spraakkode = DokumentBestillerTjenesteUtil.mapSpråkkode(Språkkode.nn);
        assertEquals(SpraakkodeType.NN, spraakkode);

        spraakkode = DokumentBestillerTjenesteUtil.mapSpråkkode(Språkkode.en);
        assertEquals(SpraakkodeType.EN, spraakkode);

        spraakkode = DokumentBestillerTjenesteUtil.mapSpråkkode(Språkkode.UDEFINERT);
        assertEquals(SpraakkodeType.NB, spraakkode);
    }
}
