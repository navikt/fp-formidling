package no.nav.vedtak.felles.prosesstask.impl;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Properties;

import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.feil.FeilFactory;
import no.nav.vedtak.feil.LogLevel;
import no.nav.vedtak.feil.deklarasjon.DeklarerteFeil;
import no.nav.vedtak.feil.deklarasjon.TekniskFeil;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskException;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskKonfigException;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskKritiskException;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskMidlertidigException;

/**
 * Feilkoder knyttet til kjøring av TaskManager.
 */
interface TaskManagerFeil extends DeklarerteFeil {

    TaskManagerFeil FACTORY = FeilFactory.create(TaskManagerFeil.class);

    @TekniskFeil(feilkode = "FP-119673", feilmelding = "numberOfTaskRunnerThreads<=0: ugyldig", logLevel = LogLevel.ERROR, exceptionClass = ProsessTaskKonfigException.class)
    Feil ugyldigAntallTråder();

    @TekniskFeil(feilkode = "FP-955392", feilmelding = "maxNumberOfTasksToPoll<=0: ugyldig", logLevel = LogLevel.ERROR, exceptionClass = ProsessTaskKonfigException.class)
    Feil ugyldigAntallTasks();

    @TekniskFeil(feilkode = "FP-739415", feilmelding = "Transient datase connection feil, venter til neste runde (runde=%s): %s: %s", logLevel = LogLevel.WARN, exceptionClass = ProsessTaskMidlertidigException.class)
    Feil midlertidigUtilgjengeligDatabase(Integer round, Class<?> exceptionClass, String exceptionMessage);

    @TekniskFeil(feilkode = "FP-996896", feilmelding = "Kunne ikke polle database, venter til neste runde(runde=%s)", logLevel = LogLevel.WARN, exceptionClass = ProsessTaskException.class)
    Feil kunneIkkePolleDatabase(Integer round, Exception e);

    @TekniskFeil(feilkode = "FP-996897", feilmelding = "Kunne ikke polle grunnet kritisk feil, venter (%ss)", logLevel = LogLevel.ERROR, exceptionClass = ProsessTaskKritiskException.class)
    Feil kritiskKunneIkkePolleDatabase(Long waitSecs, Throwable t);

    @TekniskFeil(feilkode = "FP-415564", feilmelding = "Kunne ikke prosessere task, id=%s, taskName=%s. Vil automatisk prøve igjen. Forsøk=%s, Neste kjøring=%s", logLevel = LogLevel.WARN, exceptionClass = ProsessTaskMidlertidigException.class)
    Feil kunneIkkeProsessereTaskVilPrøveIgjenEnkelFeilmelding(Long taskId, String taskName, int failureAttempt, LocalDateTime localDateTime, Exception e);

    @TekniskFeil(feilkode = "FP-415565", feilmelding = "Kunne ikke registrere feil på task pga uventet feil ved oppdatering av status/feil, id=%s, taskName=%s.", logLevel = LogLevel.ERROR, exceptionClass = ProsessTaskException.class)
    Feil kunneIkkeLoggeUventetFeil(Long taskId, String taskName, Throwable e);

    @TekniskFeil(feilkode = "FP-876625", feilmelding = "Kunne ikke prosessere task, id=%s, taskName=%s. Har feilet %s ganger. Vil ikke prøve igjen", logLevel = LogLevel.WARN, exceptionClass = ProsessTaskException.class)
    Feil kunneIkkeProsessereTaskVilIkkePrøveIgjenEnkelFeilmelding(Long taskId, String taskName, Integer feiletAntall, Exception e);

    @TekniskFeil(feilkode = "FP-876627", feilmelding = "Kunne ikke prosessere task pga fatal feil (forårsaker transaksjon rollback), id=%s, taskName=%s. Har feilet %s ganger. Vil ikke prøve igjen", logLevel = LogLevel.ERROR, exceptionClass = ProsessTaskException.class)
    Feil kunneIkkeProsessereTaskPgaFatalFeilVilIkkePrøveIgjen(Long taskId, String taskName, Integer feiletAntall, Throwable t);

    @TekniskFeil(feilkode = "FP-876628", feilmelding = "Kritisk database feil som gir rollback. Kan ikke prosessere task, vil logge til db i ny transaksjon, id=%s, taskName=%s pga uventet feil.", logLevel = LogLevel.WARN, exceptionClass = ProsessTaskException.class)
    Feil kritiskFeilKunneIkkeProsessereTaskPgaFatalFeil(Long taskId, String taskName, Throwable t);

    @TekniskFeil(feilkode = "FP-876631", feilmelding = "Fikk ikke lås på prosess task id [%s], type [%s]. Allerede låst eller ryddet. Kan ikke oppdatere status i databasen nå.", logLevel = LogLevel.WARN, exceptionClass = ProsessTaskException.class)
    Feil kritiskFeilKanIkkeSkriveFeilTilbakeTilDatabaseFikkIkkeLås(Long taskId, String taskName);

    @TekniskFeil(feilkode = "FP-853562", feilmelding = "Kunne ikke prosessere task, id=%s, taskName=%s. Feil konfigurasjon", logLevel = LogLevel.ERROR, exceptionClass = ProsessTaskKonfigException.class)
    Feil kunneIkkeProsessereTaskFeilKonfigurasjon(Long id, String name, Exception e);

    @TekniskFeil(feilkode = "FP-853572", feilmelding = "Kunne ikke prosessere task, id=%s, taskName=%s. Veto nedlagt", logLevel = LogLevel.ERROR, exceptionClass = ProsessTaskException.class)
    Feil kunneIkkeProsessereTaskVeto(Long id, String name);

    @TekniskFeil(feilkode = "FP-530440", feilmelding = "Kunne ikke prosessere task pga transient database feil: id=%s, taskName=%s. Vil automatisk prøve igjen", logLevel = LogLevel.WARN, exceptionClass = ProsessTaskMidlertidigException.class)
    Feil kunneIkkeProsessereTaskTransientFeilVilPrøveIgjen(Long taskId, String taskName, Exception e);

    @TekniskFeil(feilkode = "FP-265358", feilmelding = "Kunne ikke lagre task, skriving til database mislykkes: task=%s, kjøresEtter=%s, parametere=%s.", logLevel = LogLevel.WARN, exceptionClass = ProsessTaskException.class)
    Feil feilVedLagringAvProsessTask(String taskName, LocalDateTime runAfter, Properties params, SQLException e);

    @TekniskFeil(feilkode = "FP-314160", feilmelding = "Finner ikke sql i fil=%s. Konfigurasjon er ugyldig.", logLevel = LogLevel.ERROR, exceptionClass = ProsessTaskKonfigException.class)
    Feil finnerIkkeSqlFil(String pollSqlFilNavn, IOException e);

    @TekniskFeil(feilkode = "FP-314162", feilmelding = "Pollet task for kjøring: id=%s, type=%s, originalException=%s", logLevel = LogLevel.WARN, exceptionClass = ProsessTaskException.class)
    Feil feilVedPubliseringAvEvent(Long taskId, String taskName, String orgExceptionMessage, RuntimeException eventObserverException);

    @TekniskFeil(feilkode = "FP-900909", feilmelding = "Kan ikke kjøre task [%s, %s], fikk veto. Blokkert av task [%s]: %s", logLevel = LogLevel.INFO, exceptionClass = ProsessTaskException.class)
    Feil kanIkkeKjøreFikkVeto(Long taskId, String taskName, Long blokkertAvProsessTaskId, String vetoBegrunnelse);


}
