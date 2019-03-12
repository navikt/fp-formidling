package no.nav.foreldrepenger.melding.brevbestiller;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.vedtak.felles.testutilities.cdi.CdiRunner;

@RunWith(CdiRunner.class)
public class LandkodeOversetterTest {

    private final Map<String, String> landkoder = new HashMap<String, String>() {{
        put("NOR", "NO");
    }};

    private KodeverkRepository kodeRepo;

    @Before
    public void setup() {
        kodeRepo = mock(KodeverkRepository.class);
        when(kodeRepo.hentLandkodeISO2TilLandkoderMap()).thenReturn(landkoder);
    }

    @Test
    public void iso3ErNull() {
        LandkodeOversetter oversetter = new LandkodeOversetter(kodeRepo);
        String iso2 = oversetter.tilIso2(null);
        assertThat(iso2, is("???"));
    }

    @Test
    public void iso2FinnesIkke() {
        LandkodeOversetter oversetter = new LandkodeOversetter(kodeRepo);
        String iso2 = oversetter.tilIso2("XXX");
        assertThat(iso2, is(LandkodeOversetter.UOPPGITT));
    }

    @Test
    public void iso2Finnes() {
        LandkodeOversetter oversetter = new LandkodeOversetter(kodeRepo);
        String iso2 = oversetter.tilIso2("NOR");
        assertThat(iso2, is("NO"));
    }

}