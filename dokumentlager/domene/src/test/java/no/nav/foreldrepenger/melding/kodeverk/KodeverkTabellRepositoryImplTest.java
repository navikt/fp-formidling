package no.nav.foreldrepenger.melding.kodeverk;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Rule;
import org.junit.Test;

import no.nav.foreldrepenger.melding.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;

public class KodeverkTabellRepositoryImplTest {

    @Rule
    public UnittestRepositoryRule repositoryRule = new UnittestRepositoryRule();

    private KodeverkTabellRepository repository = new KodeverkTabellRepositoryImpl(repositoryRule.getEntityManager());

    @Test
    public void skal_hente_en_dokumentmal() {
        DokumentMalType resultat = repository.finnDokumentMalType(DokumentMalType.AVSLAGSVEDTAK_DOK);
        assertThat(resultat.getKode()).isEqualTo(DokumentMalType.AVSLAGSVEDTAK_DOK);
    }

}