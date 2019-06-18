package no.nav.vedtak.felles.prosesstask.api;

/**
 * Interface som må implementers for å håndtere implementasjoner av oppgaver. Dersom det er flere mulige
 * implementasjoner håndteres dette inni {@link #dispatch(ProsessTaskData)} metoden.
 * <p>
 * I tillegg må
 * <ul>
 * <li>klassen markeres med {@link ProsessTask} annotation slik at den oppdages og kan plugges inn runtime.</li>
 * <li>Samme navn registreres i PROSESS_TASK_TYPE tabell</li>
 * </ul>
 */
public interface ProsessTaskDispatcher {

    void dispatch(ProsessTaskData task) throws Exception; // NOSONAR

}
