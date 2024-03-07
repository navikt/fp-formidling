package no.nav.foreldrepenger.fpformidling.domene.hendelser;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
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

    public int slettDokumentHendelserEldreEnn(LocalDate dato) {
        return entityManager.createQuery("delete from DokumentHendelse where opprettetTidspunkt < :opprettet").setParameter("opprettet", dato.atStartOfDay()).executeUpdate();
    }

    private void lagreOgFlush(Object objektTilLagring) {
        entityManager.persist(objektTilLagring);
        entityManager.flush();
    }

}
