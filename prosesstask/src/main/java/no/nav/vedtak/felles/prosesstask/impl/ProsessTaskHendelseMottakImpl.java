package no.nav.vedtak.felles.prosesstask.impl;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHendelse;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHendelseMottak;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskStatus;

/**
 * Implementasjon av repository som er tilgjengelig for å lagre og opprette nye tasks.
 */
@ApplicationScoped
public class ProsessTaskHendelseMottakImpl implements ProsessTaskHendelseMottak {

    private static final Logger log = LoggerFactory.getLogger(ProsessTaskHendelseMottakImpl.class);

    private ProsessTaskRepositoryImpl repository;

    ProsessTaskHendelseMottakImpl() {
        // for CDI proxying
    }

    @Inject
    public ProsessTaskHendelseMottakImpl(ProsessTaskRepositoryImpl repository) {
        Objects.requireNonNull(repository, "repository");
        this.repository = repository;
    }

    @Override
    public void mottaHendelse(Long taskId, ProsessTaskHendelse hendelse) {
        ProsessTaskData task = repository.finn(taskId);
        if (task == null) {
            throw new IllegalStateException("Hendelse " + hendelse + " mottatt i ukjent prosesstaskId: " + taskId);
        }
        Optional<ProsessTaskHendelse> venterHendelse = task.getHendelse();
        if (!Objects.equals(ProsessTaskStatus.VENTER_SVAR, task.getStatus()) || !venterHendelse.isPresent()) {
            throw new IllegalStateException("Uventet hendelse " + hendelse + " mottatt i tilstand " + task.getStatus());
        }
        if (!Objects.equals(venterHendelse.get(), hendelse)) {
            throw new IllegalStateException("Uventet hendelse " + hendelse + " mottatt, venter hendelse " + venterHendelse.get());
        }
        task.setStatus(ProsessTaskStatus.KLAR);
        task.setNesteKjøringEtter(LocalDateTime.now());
        log.info("Behandler hendelse {} i task {}, behandling id {}", hendelse, taskId, task.getKoblingId()); //$NON-NLS-1$
        repository.lagre(task);
    }

}
