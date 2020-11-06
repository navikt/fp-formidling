package no.nav.foreldrepenger.melding.datamapper;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.SpraakkodeType;

public class DokumentBestillerTjenesteUtilTest {

    @Test
    public void mapSpråkkode() {
        SpraakkodeType spraakkode = DokumentBestillerTjenesteUtil.mapSpråkkode(Språkkode.nb);
        assertThat(spraakkode).isEqualTo(SpraakkodeType.NB);

        spraakkode = DokumentBestillerTjenesteUtil.mapSpråkkode(Språkkode.nn);
        assertThat(spraakkode).isEqualTo(SpraakkodeType.NN);

        spraakkode = DokumentBestillerTjenesteUtil.mapSpråkkode(Språkkode.en);
        assertThat(spraakkode).isEqualTo(SpraakkodeType.EN);

        spraakkode = DokumentBestillerTjenesteUtil.mapSpråkkode(Språkkode.UDEFINERT);
        assertThat(spraakkode).isEqualTo(SpraakkodeType.NB);
    }
}
