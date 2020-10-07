package no.nav.foreldrepenger.melding.dokumentdata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class DokumentTypeIdTest {

    @Test
    public void skal_mappe_enkelt_objekt() {
        assertThat(new DokumentTypeId("I000050").erEndringsøknadType()).isTrue();
        assertThat(new DokumentTypeId("SØKNAD_ENGANGSSTØNAD_FØDSEL").erSøknadType()).isTrue();
    }

}
