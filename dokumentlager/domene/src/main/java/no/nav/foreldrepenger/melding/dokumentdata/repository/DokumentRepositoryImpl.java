package no.nav.foreldrepenger.melding.dokumentdata.repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import no.nav.foreldrepenger.melding.dokumentdata.DokumentHendelse;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.eventmottak.EventmottakFeillogg;
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
    public void lagre(DokumentHendelse dokumentHendelse) {
        lagreOgFlush(dokumentHendelse);
    }

    @Override
    public void lagre(EventmottakFeillogg eventmottakFeillogg) {
        entityManager.persist(eventmottakFeillogg);
        entityManager.flush();
    }

    private void lagreOgFlush(Object objektTilLagring) {
        entityManager.persist(objektTilLagring);
        entityManager.flush();
    }

    @Override
    public Optional<DokumentHendelse> hentDokumentHendelseMedId(long hendelseId) {
        TypedQuery<DokumentHendelse> query = entityManager.createQuery("from DokumentHendelse where id=:hendelseId", DokumentHendelse.class);
        query.setParameter("hendelseId", hendelseId);
        return HibernateVerktøy.hentUniktResultat(query);
    }

    @Override
    public List<DokumentHendelse> hentDokumentHendelserForBehandling(long behandlingId) {
        TypedQuery<DokumentHendelse> query = entityManager.createQuery("from DokumentHendelse where behandlingId=:behandlingId", DokumentHendelse.class);
        query.setParameter("behandlingId", behandlingId);
        return query.getResultList();
    }

    @Override
    public DokumentMalType hentDokumentMalType(String kode) {
        TypedQuery<DokumentMalType> query = entityManager
                .createQuery("from DokumentMalType d where d.kode = :kode", DokumentMalType.class)
                .setParameter("kode", kode);
        return HibernateVerktøy.hentEksaktResultat(query);
    }
}
