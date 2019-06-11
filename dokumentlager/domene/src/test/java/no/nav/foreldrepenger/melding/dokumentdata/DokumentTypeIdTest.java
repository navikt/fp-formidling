package no.nav.foreldrepenger.melding.dokumentdata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class DokumentTypeIdTest {

    @Test
    public void skal_mappe_enkelt_objekt() {
        assertThat(new DokumentTypeId(DokumentTypeId.FORELDREPENGER_ENDRING_SØKNAD.getKode()).erEndringsøknadType()).isTrue();
        assertThat(new DokumentTypeId(DokumentTypeId.SØKNAD_ENGANGSSTØNAD_FØDSEL.getKode()).erSøknadType()).isTrue();
    }

}
