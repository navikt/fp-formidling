package no.nav.foreldrepenger.melding.dokumentdata.repository;

import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import no.nav.foreldrepenger.melding.dokumentdata.DokumentAdresse;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.vedtak.felles.jpa.HibernateVerktøy;
import no.nav.vedtak.felles.jpa.VLPersistenceUnit;

@ApplicationScoped
public class DokumentRepositoryImpl implements DokumentRepository {

    private EntityManager entityManager;

    public DokumentRepositoryImpl() {
        //CDI
    }

    @Inject
    public DokumentRepositoryImpl(@VLPersistenceUnit EntityManager entityManager) {
        Objects.requireNonNull(entityManager, "entityManager"); //$NON-NLS-1$
        this.entityManager = entityManager;
    }

    @Override
    public DokumentMalType hentDokumentMalType(String kode) {
        TypedQuery<DokumentMalType> query = entityManager
                .createQuery("from DokumentMalType d where d.kode = :kode", DokumentMalType.class)
                .setParameter("kode", kode);
        return HibernateVerktøy.hentEksaktResultat(query);
    }

    //TODO: Cascade fra DokumentFelles entity
    @Override
    public Long lagre(DokumentAdresse adresse) {
        entityManager.persist(adresse);
        return adresse.getId();
    }
}
