package no.nav.vedtak.felles.prosesstask.batch;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import no.nav.vedtak.felles.jpa.VLPersistenceUnit;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskRepository;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskStatus;
import no.nav.vedtak.felles.prosesstask.api.TaskStatus;

/**
 * Implementasjon av repository som er tilgjengelig for å lagre og opprette nye tasks.
 */
@ApplicationScoped
public class BatchProsessTaskRepository {

    private EntityManager entityManager;
    private ProsessTaskRepository prosessTaskRepository;

    BatchProsessTaskRepository() {
        // for CDI proxying
    }

    @Inject
    public BatchProsessTaskRepository(@VLPersistenceUnit EntityManager entityManager,
                                      ProsessTaskRepository prosessTaskRepository) {
        Objects.requireNonNull(entityManager, "entityManager");
        this.entityManager = entityManager;
        this.prosessTaskRepository = prosessTaskRepository;
    }

    public List<TaskStatus> finnStatusForGruppe(String gruppe) {
        final Query query = entityManager
            .createNativeQuery("SELECT pt.status, count(*) FROM PROSESS_TASK pt WHERE pt.TASK_GRUPPE = :gruppe GROUP BY pt.status")
            .setParameter("gruppe", gruppe);

        List<TaskStatus> statuser = new ArrayList<>();

        @SuppressWarnings("unchecked")
        List<Object[]> result = query.getResultList();
        for (Object[] objects : result) {
            statuser.add(new TaskStatus(ProsessTaskStatus.valueOf((String) objects[0]), (BigDecimal) objects[1])); // NOSONAR
        }
        return statuser;
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

    public String lagre(ProsessTaskData taskData) {
        return prosessTaskRepository.lagre(taskData);
    }
}
