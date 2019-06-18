package no.nav.vedtak.felles.prosesstask.impl;

import java.io.IOException;
import java.sql.SQLNonTransientConnectionException;
import java.sql.SQLRecoverableException;
import java.sql.SQLTransientException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.enterprise.inject.Instance;
import javax.persistence.QueryTimeoutException;

import org.hibernate.exception.JDBCConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.vedtak.exception.IntegrasjonException;
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskFeil;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskStatus;
import no.nav.vedtak.felles.prosesstask.spi.ProsessTaskFeilhåndteringAlgoritme;
import no.nav.vedtak.util.FPDateUtil;

/**
 * Samler feilhåndtering og status publisering som skjer på vanlige prosess tasks.
 */
public class RunTaskFeilOgStatusEventHåndterer {

    /**
     * Disse delegres til Feilhåndteringsalgoritme for håndtering. Andre vil alltid gi FEILET status.
     */
    private static final List<Class<?>> FEILHÅNDTERING_EXCEPTIONS = Arrays.asList(IntegrasjonException.class, TekniskException.class,
            JDBCConnectionException.class, QueryTimeoutException.class, SQLTransientException.class, SQLNonTransientConnectionException.class,
            SQLRecoverableException.class);

    private static final Logger log = LoggerFactory.getLogger(RunTaskFeilOgStatusEventHåndterer.class);

    private final ProsessTaskEventPubliserer eventPubliserer;
    private final TaskManagerRepositoryImpl taskManagerRepository;
    private final Instance<ProsessTaskFeilhåndteringAlgoritme> feilhåndteringsalgoritmer;

    private final RunTaskInfo taskInfo;

    public RunTaskFeilOgStatusEventHåndterer(RunTaskInfo taskInfo, ProsessTaskEventPubliserer eventPubliserer,
                                             TaskManagerRepositoryImpl taskManagerRepository,
                                             Instance<ProsessTaskFeilhåndteringAlgoritme> feilhåndteringsalgoritmer) {
        this.taskInfo = taskInfo;
        this.eventPubliserer = eventPubliserer;
        this.taskManagerRepository = taskManagerRepository;
        this.feilhåndteringsalgoritmer = feilhåndteringsalgoritmer;
    }

    protected static String getFeiltekstOgLoggHvisFørstegang(ProsessTaskEntitet pte, Feil feil, Throwable t) {

        ProsessTaskFeil taskFeil = new ProsessTaskFeil(pte.tilProsessTask(), feil);

        String tidligereFeil = pte.getSisteFeilKode();
        String feilkode = taskFeil.getFeilkode();
        String feiltekst = null;
        try {
            feiltekst = taskFeil.writeValueAsString();
        } catch (IOException e1) {
            // kunne ikke skrive ut json, log stack trace
            log.warn("Kunne ikke skrive ut json struktur for feil: " + feilkode + ", json exception: " + e1, t); // NOSONAR
        }
        if (feilkode == null || !Objects.equals(tidligereFeil, feilkode)) {
            feil.log(log);
        }
        return feiltekst;
    }

    protected void publiserNyStatusEvent(ProsessTaskData data, ProsessTaskStatus gammelStatus, ProsessTaskStatus nyStatus) {
        publiserNyStatusEvent(data, gammelStatus, nyStatus, null, null);
    }

    protected void publiserNyStatusEvent(ProsessTaskData data, ProsessTaskStatus gammelStatus, ProsessTaskStatus nyStatus, Feil feil, Exception e) {
        if (eventPubliserer != null) {
            eventPubliserer.fireEvent(data, gammelStatus, nyStatus, feil, e);
        }
    }

    /**
     * handle exception on task. Update failure count if less than max.
     * NB: Exception may be null if a lifecycle observer vetoed it (veto==true)
     */
    protected void handleTaskFeil(ProsessTaskEntitet pte, Exception e) {
        String taskName = pte.getTaskName();
        ProsessTaskType taskType = taskManagerRepository.getTaskType(taskName);
        ProsessTaskFeilhand feilhåndteringsData = taskType.getFeilhåndteringAlgoritme();
        ProsessTaskFeilhåndteringAlgoritme feilhåndteringsalgoritme = getFeilhåndteringsalgoritme(feilhåndteringsData.getKode());

        int failureAttempt = pte.getFeiledeForsøk() + 1;

        if (sjekkOmSkalKjøresPåNytt(e, taskType, feilhåndteringsalgoritme, failureAttempt)) {
            LocalDateTime nyTid = getNesteKjøringForNyKjøring(feilhåndteringsData, feilhåndteringsalgoritme, failureAttempt);

            Feil feil = TaskManagerFeil.FACTORY.kunneIkkeProsessereTaskVilPrøveIgjenEnkelFeilmelding(taskInfo.getId(), taskName, failureAttempt,
                    nyTid, e);

            String feiltekst = getFeiltekstOgLoggHvisFørstegang(pte, feil, e);
            taskManagerRepository.oppdaterStatusOgNesteKjøring(pte.getId(), ProsessTaskStatus.KLAR, nyTid, feil.getKode(), feiltekst, failureAttempt);

            // endrer ikke status ved nytt forsøk eller publiserer event p.t.
        } else {
            Feil feil = feilhåndteringsalgoritme.hendelserNårIkkeKjøresPåNytt(e, pte.tilProsessTask());
            if (feil == null) {
                feil = TaskManagerFeil.FACTORY.kunneIkkeProsessereTaskVilIkkePrøveIgjenEnkelFeilmelding(taskInfo.getId(), taskName, failureAttempt, e);
            }
            handleFatalTaskFeil(pte, feil, e);
        }
    }

    protected void handleFatalTaskFeil(ProsessTaskEntitet pte, Feil feil, Exception e) {
        ProsessTaskStatus nyStatus = ProsessTaskStatus.FEILET;
        try {
            publiserNyStatusEvent(pte.tilProsessTask(), pte.getStatus(), nyStatus, feil, e);
        } finally {
            int failureAttempt = pte.getFeiledeForsøk() + 1;
            String feiltekst = getFeiltekstOgLoggHvisFørstegang(pte, feil, e);
            taskManagerRepository.oppdaterStatusOgNesteKjøring(pte.getId(), nyStatus, null, feil.getKode(), feiltekst, failureAttempt);
        }
    }

    /**
     * handle recoverable / transient exception.
     */
    protected void handleTransientAndRecoverableException(Exception e) {
        /*
         * assume won't help to try and write to database just now, log only instead
         */
        TaskManagerFeil.FACTORY.kunneIkkeProsessereTaskTransientFeilVilPrøveIgjen(taskInfo.getId(), taskInfo.getTaskType(), e).log(log);
    }

    private LocalDateTime getNesteKjøringForNyKjøring(ProsessTaskFeilhand feilhåndteringsData, ProsessTaskFeilhåndteringAlgoritme feilhåndteringsalgoritme,
                                                      int failureAttempt) {
        int secsBetweenAttempts = feilhåndteringsalgoritme.getForsinkelseStrategi().sekunderTilNesteForsøk(failureAttempt,
                feilhåndteringsData);

        LocalDateTime nyTid = FPDateUtil.nå().plusSeconds(secsBetweenAttempts);
        return nyTid;
    }

    private boolean sjekkOmSkalKjøresPåNytt(Exception e, ProsessTaskType taskType, ProsessTaskFeilhåndteringAlgoritme feilhåndteringsalgoritme,
                                            int failureAttempt) {

        if (e.getCause() != null && feilhåndteringExceptions(e.getCause())) {
            return false;
        } else if (e.getCause() == null && feilhåndteringExceptions(e)) {
            return false;
        } else {
            return feilhåndteringsalgoritme.skalKjørePåNytt(taskType.tilProsessTaskTypeInfo(), failureAttempt, e);
        }
    }

    private boolean feilhåndteringExceptions(Throwable e) {
        return (FEILHÅNDTERING_EXCEPTIONS.stream().noneMatch(fatal -> fatal.isAssignableFrom(e.getClass())));
    }

    protected ProsessTaskFeilhåndteringAlgoritme getFeilhåndteringsalgoritme(String kode) {
        List<ProsessTaskFeilhåndteringAlgoritme> kandidater = new ArrayList<>(1);
        for (ProsessTaskFeilhåndteringAlgoritme algoritme : feilhåndteringsalgoritmer) {
            if (algoritme.kode().equals(kode)) {
                kandidater.add(algoritme);
            }
        }
        if (kandidater.size() == 1) {
            return kandidater.get(0);
        }
        throw new IllegalStateException("Forventet å finne 1 feilhåndteringsalgoritme for '" + kode + "', men fant " + kandidater); //$NON-NLS-1$ //$NON-NLS-2$
    }

}
