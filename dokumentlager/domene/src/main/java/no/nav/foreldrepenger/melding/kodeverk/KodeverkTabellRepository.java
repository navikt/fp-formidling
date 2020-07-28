package no.nav.foreldrepenger.melding.kodeverk;

import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.hibernate.jpa.QueryHints;

import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;

@ApplicationScoped
public class KodeverkTabellRepository {

    private EntityManager entityManager;

    KodeverkTabellRepository() {
        //CDI
    }

    @Inject
    public KodeverkTabellRepository(EntityManager entityManager) {
        Objects.requireNonNull(entityManager, "entityManager"); //$NON-NLS-1$
        this.entityManager = entityManager;
    }

    public DokumentMalType finnDokumentMalType(String kode) {
        TypedQuery<DokumentMalType> query = entityManager.createQuery("from DokumentMalType where kode=:kode", DokumentMalType.class);
        query.setParameter("kode", kode);
        query.setHint(QueryHints.HINT_READONLY, "true");
        return query.getSingleResult();
    }

}
