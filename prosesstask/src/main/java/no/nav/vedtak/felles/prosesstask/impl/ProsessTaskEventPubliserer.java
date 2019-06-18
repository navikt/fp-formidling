package no.nav.vedtak.felles.prosesstask.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskEvent;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskStatus;

@ApplicationScoped
public class ProsessTaskEventPubliserer {

    private static final Logger log = LoggerFactory.getLogger(ProsessTaskEventPubliserer.class);

    private Event<ProsessTaskEvent> publiserer;

    private HandleProsessTaskLifecycleObserver handleProsessTaskLifecycleObserver;

    protected ProsessTaskEventPubliserer() {
        // for CDI
    }

    @Inject
    public ProsessTaskEventPubliserer(Event<ProsessTaskEvent> publiserer) {
        super();
        this.publiserer = publiserer;
        handleProsessTaskLifecycleObserver = new HandleProsessTaskLifecycleObserver();
    }

    public void fireEvent(ProsessTaskData data, ProsessTaskStatus gammelStatus, ProsessTaskStatus nyStatus) {
        fireEvent(data, gammelStatus, nyStatus, null, null);
    }

    public void fireEvent(ProsessTaskData data, ProsessTaskStatus gammelStatus, ProsessTaskStatus nyStatus, Feil feil, Exception orgException) {
        // I CDI 1.2 skjer publisering av event kun synkront så feil kan
        // avbryte inneværende transaksjon. Logger eventuelle exceptions fra event observere uten å la tasken endre status.
        try {
            publiserer.fire(new ProsessTaskEvent(data, gammelStatus, nyStatus, feil, orgException));
        } catch (RuntimeException e) { // NOSONAR
            // logger og svelger exception her. Feil oppstått i event observer
            String orgExceptionMessage = orgException == null ? null : String.valueOf(orgException);
            TaskManagerFeil.FACTORY
                    .feilVedPubliseringAvEvent(data.getId(), data.getTaskType(), orgExceptionMessage, e)
                    .log(log); // NOSONAR
        }

    }

    public HandleProsessTaskLifecycleObserver getHandleProsessTaskLifecycleObserver() {
        return handleProsessTaskLifecycleObserver;
    }
}
