package no.nav.foreldrepenger.melding.kodeverk;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Rule;
import org.junit.Test;

import no.nav.foreldrepenger.melding.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;

public class KodeverkTabellRepositoryImplTest {

    @Rule
    public UnittestRepositoryRule repositoryRule = new UnittestRepositoryRule();

    @Test
    public void skal_hente_en_dokumentmal() {
        DokumentMalType resultat = DokumentMalType.AVSLAGSVEDTAK_DOK;
        assertThat(resultat.getKode()).isEqualTo(DokumentMalType.AVSLAGSVEDTAK_DOK.getKode());
    }

}