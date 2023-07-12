package no.nav.foreldrepenger.fpformidling.hendelser;

import java.util.Objects;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;
import no.nav.vedtak.felles.jpa.HibernateVerktøy;

@ApplicationScoped
public class HendelseRepository {

    private EntityManager entityManager;

    HendelseRepository() {
        //CDI
    }

    @Inject
    public HendelseRepository(EntityManager entityManager) {
        Objects.requireNonNull(entityManager, "entityManager"); //$NON-NLS-1$
        this.entityManager = entityManager;
    }

    public void lagre(DokumentHendelse dokumentHendelse) {
        lagreOgFlush(dokumentHendelse);
    }

    public boolean finnesHendelseMedUuidAllerede(UUID bestillingUuid) {
        var query = entityManager.createQuery("from DokumentHendelse where bestillingUuid=:bestillingUuid", DokumentHendelse.class);
        query.setParameter("bestillingUuid", bestillingUuid);
        return !query.getResultList().isEmpty();
    }

    public DokumentHendelse hentDokumentHendelseMedId(long hendelseId) {
        var query = entityManager.createQuery("from DokumentHendelse where id=:hendelseId", DokumentHendelse.class);
        query.setParameter("hendelseId", hendelseId);
        return HibernateVerktøy.hentEksaktResultat(query);
    }

    public boolean erDokumentHendelseMottatt(UUID behandlingUuid, DokumentMalType dokumentMal) {
        var query = entityManager.createQuery("from DokumentHendelse where behandlingUuid=:behandlingUuid and dokumentMalType=:dokumentMalType",
            DokumentHendelse.class);
        query.setParameter("behandlingUuid", behandlingUuid);
        query.setParameter("dokumentMalType", dokumentMal);
        return !query.getResultList().isEmpty();
    }

    private void lagreOgFlush(Object objektTilLagring) {
        entityManager.persist(objektTilLagring);
        entityManager.flush();
    }

}
