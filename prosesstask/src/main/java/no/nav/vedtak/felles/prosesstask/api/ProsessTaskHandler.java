package no.nav.vedtak.felles.prosesstask.api;

/**
 * Implementerer en ProsessTask. Klasser som implementere bør også annoteres med {@link ProsessTask} for å angi type.
 */
public interface ProsessTaskHandler {
    void doTask(ProsessTaskData prosessTaskData);
}
