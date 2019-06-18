package no.nav.vedtak.felles.prosesstask.impl;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.CDI;

import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskGruppe;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskLifecycleObserver;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskVeto;

@ApplicationScoped
public class HandleProsessTaskLifecycleObserver {

    private final List<ProsessTaskLifecycleObserver> lifecycleObservers = new ArrayList<>();

    public HandleProsessTaskLifecycleObserver() {
        // for CDI proxies
        for (ProsessTaskLifecycleObserver obs : CDI.current().select(ProsessTaskLifecycleObserver.class)) {
            lifecycleObservers.add(obs);
        }
    }

    public Optional<SimpleEntry<ProsessTaskLifecycleObserver, ProsessTaskVeto>> vetoRunTask(ProsessTaskData prosessTaskData) {
        if (lifecycleObservers.isEmpty()) {
            return Optional.empty();
        }
        return lifecycleObservers.stream()
                .map(pt -> new SimpleEntry<>(pt, pt.vetoKjøring(prosessTaskData)))
                .filter(e -> e.getValue().isVeto())
                .findFirst();
    }

    public void opprettetProsessTaskGruppe(ProsessTaskGruppe sammensattTask) {
        lifecycleObservers.stream().forEach(pt -> pt.opprettetProsessTaskGruppe(sammensattTask));
    }

}
