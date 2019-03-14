package no.nav.foreldrepenger.melding.historikk;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import no.nav.vedtak.felles.jpa.HibernateVerktøy;
import no.nav.vedtak.felles.jpa.VLPersistenceUnit;

@ApplicationScoped
public class HistorikkRepositoryImpl implements HistorikkRepository {

    private EntityManager entityManager;

    public HistorikkRepositoryImpl() {
        //CDI
    }

    @Inject
    public HistorikkRepositoryImpl(@VLPersistenceUnit EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void lagre(DokumentHistorikkinnslag dokumentHistorikkinnslag) {
        lagreOgFlush(dokumentHistorikkinnslag);
    }

    @Override
    public List<DokumentHistorikkinnslag> hentInnslagForBehandling(long behandlingId) {
        TypedQuery<DokumentHistorikkinnslag> query = entityManager.createQuery("from DokumentHistorikkinnslag where behandlingId=:behandlingId", DokumentHistorikkinnslag.class);
        query.setParameter("behandlingId", behandlingId);
        return query.getResultList();
    }

    @Override
    public DokumentHistorikkinnslag hentInnslagMedId(long historikkinnslagId) {
        TypedQuery<DokumentHistorikkinnslag> query = entityManager.createQuery("from DokumentHistorikkinnslag where id=:historikkinnslagId", DokumentHistorikkinnslag.class);
        query.setParameter("historikkinnslagId", historikkinnslagId);
        return HibernateVerktøy.hentEksaktResultat(query);
    }

    private void lagreOgFlush(Object objektTilLagring) {
        entityManager.persist(objektTilLagring);
        entityManager.flush();
    }
}
