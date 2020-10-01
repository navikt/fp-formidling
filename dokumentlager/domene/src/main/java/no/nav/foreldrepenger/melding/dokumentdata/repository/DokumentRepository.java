package no.nav.foreldrepenger.melding.dokumentdata.repository;

import no.nav.foreldrepenger.melding.dokumentdata.DokumentData;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.Objects;

@ApplicationScoped
public class DokumentRepository {

    private EntityManager entityManager;

    public DokumentRepository() {
        //CDI
    }

    @Inject
    public DokumentRepository(EntityManager entityManager) {
        Objects.requireNonNull(entityManager, "entityManager"); //$NON-NLS-1$
        this.entityManager = entityManager;
    }

    public void lagre(DokumentData dokumentData) {
        entityManager.persist(dokumentData);
        for (DokumentFelles df : dokumentData.getDokumentFelles()) {
            entityManager.persist(df);
        }
        entityManager.flush();
    }
}
