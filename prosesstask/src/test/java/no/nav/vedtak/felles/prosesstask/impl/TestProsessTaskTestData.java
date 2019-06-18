package no.nav.vedtak.felles.prosesstask.impl;

import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;

public class TestProsessTaskTestData {

    private EntityManager entityManager;

    public TestProsessTaskTestData(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public TestProsessTaskTestData opprettTaskType(String taskType) throws SQLException {
        entityManager.persist(new ProsessTaskType(taskType));
        return this;
    }

    public void slettAlleProssessTask() {
        entityManager.createQuery("delete from ProsessTaskEntitet ").executeUpdate();
    }

    public void slettUtvalgtProssessTaskType(String taskType) {
        Query query = entityManager.createQuery("delete from ProsessTaskType where kode = :taskKode");
        query.setParameter("taskKode", taskType);
        query.executeUpdate();
    }

    public TestProsessTaskTestData opprettTask(ProsessTaskData prosessTask) {
        entityManager.persist(new ProsessTaskEntitet().kopierFra(prosessTask));
        return this;
    }

    public void opprettTaskType(String taskType, String cronExpression) {
        entityManager.persist(new ProsessTaskType(taskType, cronExpression));
    }
}
