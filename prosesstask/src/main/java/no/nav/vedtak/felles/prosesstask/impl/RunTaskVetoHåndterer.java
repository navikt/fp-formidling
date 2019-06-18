package no.nav.vedtak.felles.prosesstask.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.AbstractMap.SimpleEntry;
import java.util.Optional;

import javax.persistence.EntityManager;

import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskFeil;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskLifecycleObserver;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskStatus;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskVeto;

public class RunTaskVetoHåndterer {
    private ProsessTaskEventPubliserer eventPubliserer;
    private EntityManager entityManager;

    public RunTaskVetoHåndterer(ProsessTaskEventPubliserer eventPubliserer, EntityManager entityManager) {
        this.eventPubliserer = eventPubliserer;
        this.entityManager = entityManager;
    }

    boolean vetoRunTask(ProsessTaskEntitet pte) throws IOException {
        if (eventPubliserer == null) {
            return false;
        }

        HandleProsessTaskLifecycleObserver lifecycleObserver = eventPubliserer.getHandleProsessTaskLifecycleObserver();
        ProsessTaskData prosessTaskData = pte.tilProsessTask();

        boolean vetoed = false;

        Optional<SimpleEntry<ProsessTaskLifecycleObserver, ProsessTaskVeto>> vetoRunTask = lifecycleObserver.vetoRunTask(prosessTaskData);
        if (vetoRunTask.isPresent()) {
            SimpleEntry<ProsessTaskLifecycleObserver, ProsessTaskVeto> entry = vetoRunTask.get();
            ProsessTaskVeto veto = entry.getValue();
            if (veto.isVeto()) {
                vetoed = true;
                Feil feil = TaskManagerFeil.FACTORY.kanIkkeKjøreFikkVeto(pte.getId(), pte.getTaskName(), veto.getBlokkertAvProsessTaskId(),
                        veto.getBegrunnelse());
                ProsessTaskFeil taskFeil = new ProsessTaskFeil(pte.tilProsessTask(), feil);
                taskFeil.setBlokkerendeProsessTaskId(veto.getBlokkertAvProsessTaskId());

                pte.setSisteFeil(taskFeil.getFeilkode(), taskFeil.writeValueAsString());
                pte.setStatus(ProsessTaskStatus.VETO); // setter også status slik at den ikke forsøker på nytt. Blokkerende task må resette denne.
                pte.setNesteKjøringEtter(LocalDateTime.now()); // kjør umiddelbart når veto opphører
                EntityManager em = entityManager;
                em.persist(pte);
                em.flush();
            }
        }

        return vetoed;
    }
}
