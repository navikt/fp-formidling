package no.nav.vedtak.felles.prosesstask.batch;

import java.time.LocalDate;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import no.nav.vedtak.felles.prosesstask.api.ProsessTaskStatus;

@ApplicationScoped
public class BatchProsessTaskRepository {

    private EntityManager entityManager;

    BatchProsessTaskRepository() {
        // for CDI proxying
    }

    @Inject
    public BatchProsessTaskRepository(EntityManager entityManager) {
        Objects.requireNonNull(entityManager, "entityManager");
        this.entityManager = entityManager;
    }

    static String utledPartisjonsNr(LocalDate date) {
        int måned = date.plusMonths(1).getMonth().getValue();
        if (måned < 10) {
            return "0" + måned;
        }
        return "" + måned;
    }

    int rekjørAlleFeiledeTasks() {
        Query query = entityManager.createNativeQuery("UPDATE PROSESS_TASK " +
            "SET status = :status, " +
            "feilede_forsoek = feilede_forsoek-1, " +
            "neste_kjoering_etter = now() " +
            "WHERE STATUS = :feilet");
        query.setParameter("status", ProsessTaskStatus.KLAR.getDbKode())
            .setParameter("feilet", ProsessTaskStatus.FEILET.getDbKode());
        int updatedRows = query.executeUpdate();
        entityManager.flush();

        return updatedRows;
    }

    public int tømNestePartisjon() {
        String partisjonsNr = utledPartisjonsNr(LocalDate.now());
        Query query = entityManager.createNativeQuery("TRUNCATE prosess_task_partition_ferdig_" + partisjonsNr);
        int updatedRows = query.executeUpdate();
        entityManager.flush();

        return updatedRows;
    }
}
