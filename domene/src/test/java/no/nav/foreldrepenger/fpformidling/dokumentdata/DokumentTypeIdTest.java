package no.nav.foreldrepenger.fpformidling.dokumentdata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class DokumentTypeIdTest {

    @Test
    public void skal_mappe_enkelt_objekt() {
        assertThat(new DokumentTypeId("I000050").erEndringsøknadType()).isTrue();
        assertThat(new DokumentTypeId("SØKNAD_ENGANGSSTØNAD_FØDSEL").erSøknadType()).isTrue();
    }

}
