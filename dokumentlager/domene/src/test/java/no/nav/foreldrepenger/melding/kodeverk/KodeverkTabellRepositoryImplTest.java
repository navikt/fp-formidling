package no.nav.foreldrepenger.melding.kodeverk;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;

public class KodeverkTabellRepositoryImplTest {

    @Test
    public void skal_hente_en_dokumentmal() {
        DokumentMalType resultat = DokumentMalType.AVSLAGSVEDTAK_DOK;
        assertThat(resultat.getKode()).isEqualTo(DokumentMalType.AVSLAGSVEDTAK_DOK.getKode());
    }

}
