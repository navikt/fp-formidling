package no.nav.vedtak.felles.prosesstask.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskGruppe;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskRepository;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskStatus;
import no.nav.vedtak.felles.prosesstask.dbstoette.UnittestRepositoryRule;
import no.nav.vedtak.felles.testutilities.cdi.CdiRunner;

@RunWith(CdiRunner.class)
public class OpprettProsessTaskIT {

    @Rule
    public final UnittestRepositoryRule repoRule = new UnittestRepositoryRule();

    @Inject
    private ProsessTaskRepository repo;

    @Before
    public void setupTestData() throws Exception {
        new TestProsessTaskTestData(repoRule.getEntityManager())
                .opprettTaskType("mytask1")
                .opprettTaskType("mytask2");
    }

    @Test
    public void skal_lagre_ProsessTask() throws Exception {
        ProsessTaskData pt = new ProsessTaskData("mytask1");
        repo.lagre(pt);
    }

    @Test
    public void skal_lagre_SammensattProsessTask() throws Exception {
        ProsessTaskData pt1 = new ProsessTaskData("mytask1");
        ProsessTaskData pt2 = new ProsessTaskData("mytask2");
        ProsessTaskGruppe sammensatt = new ProsessTaskGruppe();
        sammensatt
                .addNesteSekvensiell(pt1)
                .addNesteSekvensiell(pt2)
        ;

        // Act
        repo.lagre(sammensatt);
        repoRule.getRepository().flush();

        List<ProsessTaskData> list = repo.finnAlle(ProsessTaskStatus.KLAR);
        assertThat(list).hasSize(2);
        assertThat(list).containsOnly(pt1, pt2);

    }

}
