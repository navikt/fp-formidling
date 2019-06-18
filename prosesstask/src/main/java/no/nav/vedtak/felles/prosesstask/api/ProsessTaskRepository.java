package no.nav.vedtak.felles.prosesstask.api;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import no.nav.vedtak.felles.prosesstask.impl.ProsessTaskEntitet;
import no.nav.vedtak.felles.prosesstask.impl.ProsessTaskType;

public interface ProsessTaskRepository {

    /**
     * Lagre tasks, returner gruppe id.
     */
    String lagre(ProsessTaskGruppe tasks);

    /**
     * Lagre tasks, returner gruppe id for enkelt task.
     */
    String lagre(ProsessTaskData enkeltTask);

    ProsessTaskData finn(Long prosessTaskId);

    List<ProsessTaskData> finnAlle(ProsessTaskStatus... status);

    /**
     * En ikke-startet prosesstask har status KLAR og tom kolonne SISTE_KJOERING_TS
     */
    List<ProsessTaskData> finnIkkeStartet();

    /**
     * finne alle for matchendede status og sist kjørt innenfor angitt intervall.
     */
    List<ProsessTaskData> finnAlle(List<ProsessTaskStatus> statuser,
                                   LocalDateTime sisteKjoeringFraOgMed, LocalDateTime sisteKjoeringTilOgMed);

    List<ProsessTaskData> finnUferdigeBatchTasks(String task);

    List<TaskStatus> finnStatusForTaskIGruppe(String task, String gruppe);

    List<TaskStatus> finnStatusForGruppe(String gruppe);

    Optional<ProsessTaskTypeInfo> finnProsessTaskType(String kode);

    /**
     * finn alle som matcher angitt søk på statuser, gruppe (optional), som skal kjøres (evt. er kjørt), og som har parametere som matcher
     * angitt LIKE søk (SQL LIKE).
     */
    List<ProsessTaskData> finnAlleForAngittSøk(List<ProsessTaskStatus> statuser,
                                               String gruppeId,
                                               LocalDateTime nesteKjoeringFraOgMed, LocalDateTime nesteKjoeringTilOgMed,
                                               String paramLikeSearch);

    Map<ProsessTaskType, ProsessTaskEntitet> finnStatusForBatchTasks();

    /**
     * Suspender alle, eller ingen. Dersom noen allerede er suspendert, eller ferdig, vil denne metoden returnere false og ikke suspendere de
     * andre.
     */
    boolean suspenderAlle(Collection<ProsessTaskData> tasks);

    int rekjørAlleFeiledeTasks();
}
