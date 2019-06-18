package no.nav.vedtak.felles.prosesstask.api;

/**
 * Implementerer funksjoner som lar en observere kjøring av en prosesstask og eventuelt avbryte kjøring.
 * <p>
 * Implementasjoner av dette interfacet må være som CDI beans og tilgjengelig på classpath. Disse må være @ApplicationScoped da de vil caches i minnet.
 * <p>
 * Merk at hvis det er flere implementasjoner vil de kjøres i vilkårlig rekkefølge (avh. av CDI implementasjon). For metoder som kan veto
 * kjøring holder det at en av implementasjonene legger ned veto.
 */
public interface ProsessTaskLifecycleObserver {

    /**
     * @return ProsessTaskVeto inneværende kjøring. Påvirker ikke antall forsøk eller status. Neste kjøring forsøkes igjen om kort tid (eks. 30 sek
     * default).
     */
    ProsessTaskVeto vetoKjøring(ProsessTaskData prosessTaskData);

    /**
     * Kalt etter oppretting av en gruppe tasks.  Gjelder også enkeltstående ProsessTasks (gruppe av 1).
     */
    void opprettetProsessTaskGruppe(ProsessTaskGruppe sammensattTask);

}
