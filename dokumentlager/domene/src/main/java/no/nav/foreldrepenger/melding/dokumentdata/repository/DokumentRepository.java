package no.nav.foreldrepenger.melding.dokumentdata.repository;

import java.util.List;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.hibernate.jpa.QueryHints;

import no.nav.foreldrepenger.melding.dokumentdata.DokumentData;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.vedtak.felles.jpa.HibernateVerktøy;
import no.nav.vedtak.felles.jpa.VLPersistenceUnit;

@ApplicationScoped
public class DokumentRepository {

    private EntityManager entityManager;

    public DokumentRepository() {
        //CDI
    }

    @Inject
    public DokumentRepository(@VLPersistenceUnit EntityManager entityManager) {
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

    public DokumentMalType hentDokumentMalType(String kode) {
        TypedQuery<DokumentMalType> query = entityManager
                .createQuery("from DokumentMalType d where d.kode = :kode", DokumentMalType.class)
                .setParameter("kode", kode);
        return HibernateVerktøy.hentEksaktResultat(query);
    }

    public List<DokumentMalType> hentAlleDokumentMalTyper() {
        return entityManager.createQuery("SELECT d FROM DokumentMalType d", DokumentMalType.class) //$NON-NLS-1$
                .setHint(QueryHints.HINT_READONLY, "true") //$NON-NLS-1$
                .getResultList();
    }
}
