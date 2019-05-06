package no.nav.foreldrepenger.melding.beregning.repository;

import static no.nav.vedtak.felles.jpa.HibernateVerktøy.hentEksaktResultat;

import java.time.LocalDate;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.hibernate.jpa.QueryHints;

import no.nav.foreldrepenger.melding.beregning.Sats;
import no.nav.foreldrepenger.melding.beregning.SatsType;
import no.nav.vedtak.felles.jpa.VLPersistenceUnit;

@ApplicationScoped
class BeregningRepositoryImpl implements BeregningRepository {

    private EntityManager entityManager;

    public BeregningRepositoryImpl() {
        //CDI
    }

    @Inject
    public BeregningRepositoryImpl(@VLPersistenceUnit EntityManager entityManager) {
        Objects.requireNonNull(entityManager, "entityManager"); //$NON-NLS-1$
        this.entityManager = entityManager;
    }

    @Override
    public Sats finnEksaktSats(SatsType satsType, LocalDate dato) {
        TypedQuery<Sats> query = entityManager.createQuery("from Sats where satsType=:satsType" + //$NON-NLS-1$
                " and periode.fomDato<=:dato" + //$NON-NLS-1$
                " and periode.tomDato>=:dato", Sats.class); //$NON-NLS-1$

        query.setParameter("satsType", satsType); //$NON-NLS-1$
        query.setParameter("dato", dato); //$NON-NLS-1$
        query.setHint(QueryHints.HINT_READONLY, "true");//$NON-NLS-1$
        query.getResultList();
        return hentEksaktResultat(query);
    }
}
