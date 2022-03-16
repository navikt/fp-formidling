package no.nav.foreldrepenger.fpformidling.historikk;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import no.nav.vedtak.felles.jpa.HibernateVerktøy;

@Deprecated(forRemoval = true)
// Brukes ikke lenger - skal slettes etter siste task opprettet med historikkId er ferdig.
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

    public DokumentHistorikkinnslag hent(long historikkinnslagId) {
        TypedQuery<DokumentHistorikkinnslag> query = entityManager.createQuery("from DokumentHistorikkinnslag where id=:historikkinnslagId", DokumentHistorikkinnslag.class);
        query.setParameter("historikkinnslagId", historikkinnslagId);
        return HibernateVerktøy.hentEksaktResultat(query);
    }
}
