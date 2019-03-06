package no.nav.foreldrepenger.melding.dokumentdata.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import no.nav.foreldrepenger.melding.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;

public class DokumentRepositoryImplTest {

    @Rule
    public final UnittestRepositoryRule repositoryRule = new UnittestRepositoryRule();

    private DokumentRepository dokumentRepository;

    @Before
    public void setUp() {
        this.dokumentRepository = new DokumentRepositoryImpl(repositoryRule.getEntityManager());
    }

    @Test
    public void hentVillkårDokumentMal() {
        assertThat(dokumentRepository.hentDokumentMalType(DokumentMalType.AVSLAGSVEDTAK_DOK).getNavn())
                .isEqualToIgnoringCase("Avslagsbrev");
    }
}