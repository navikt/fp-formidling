package no.nav.foreldrepenger.melding.dokumentdata.repository;

import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import no.nav.foreldrepenger.melding.dokumentdata.DokumentData;

@ApplicationScoped
public class DokumentRepository {

    private EntityManager entityManager;

    public DokumentRepository() {

    }

    @Inject
    public DokumentRepository(EntityManager entityManager) {
        Objects.requireNonNull(entityManager, "entityManager");
        this.entityManager = entityManager;
    }

    public void lagre(DokumentData dokumentData) {
        entityManager.persist(dokumentData);
        dokumentData.getDokumentFelles().stream().forEach(entityManager::persist);
        entityManager.flush();
    }
}
