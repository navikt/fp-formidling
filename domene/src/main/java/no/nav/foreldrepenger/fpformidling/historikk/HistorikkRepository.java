package no.nav.foreldrepenger.fpformidling.historikk;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import no.nav.vedtak.felles.jpa.HibernateVerktøy;

@ApplicationScoped
public class HistorikkRepository {

    private EntityManager entityManager;

    public HistorikkRepository() {
        //CDI
    }

    @Inject
    public HistorikkRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void lagre(DokumentHistorikkinnslag dokumentHistorikkinnslag) {
        lagreOgFlush(dokumentHistorikkinnslag);
    }

    public DokumentHistorikkinnslag hent(long historikkinnslagId) {
        TypedQuery<DokumentHistorikkinnslag> query = entityManager.createQuery("from DokumentHistorikkinnslag where id=:historikkinnslagId", DokumentHistorikkinnslag.class);
        query.setParameter("historikkinnslagId", historikkinnslagId);
        return HibernateVerktøy.hentEksaktResultat(query);
    }

    private void lagreOgFlush(Object objektTilLagring) {
        entityManager.persist(objektTilLagring);
        entityManager.flush();
    }
}
