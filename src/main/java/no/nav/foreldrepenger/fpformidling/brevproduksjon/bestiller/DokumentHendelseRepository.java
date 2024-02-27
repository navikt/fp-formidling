package no.nav.foreldrepenger.fpformidling.brevproduksjon.bestiller;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import no.nav.vedtak.felles.jpa.HibernateVerktøy;

@ApplicationScoped
public class DokumentHendelseRepository {

    private EntityManager entityManager;

    DokumentHendelseRepository() {
        //CDI
    }

    @Inject
    public DokumentHendelseRepository(EntityManager entityManager) {
        Objects.requireNonNull(entityManager, "entityManager"); //$NON-NLS-1$
        this.entityManager = entityManager;
    }

    public void lagre(DokumentHendelseEntitet dokumentHendelseEntitet) {
        lagreOgFlush(dokumentHendelseEntitet);
    }

    public boolean finnesHendelseMedUuidAllerede(UUID bestillingUuid) {
        var query = entityManager.createQuery("from DokumentHendelse where bestillingUuid = :bestillingUuid", DokumentHendelseEntitet.class);
        query.setParameter("bestillingUuid", bestillingUuid);
        return !query.getResultList().isEmpty();
    }

    public DokumentHendelseEntitet hentDokumentHendelseMedId(long hendelseId) {
        var query = entityManager.createQuery("from DokumentHendelse where id = :hendelseId", DokumentHendelseEntitet.class);
        query.setParameter("hendelseId", hendelseId);
        return HibernateVerktøy.hentEksaktResultat(query);
    }

    public DokumentHendelseEntitet hentDokumentHendelse(UUID bestillingUuid) {
        var query = entityManager.createQuery("from DokumentHendelse where bestillingUuid = :bestillingUuid", DokumentHendelseEntitet.class);
        query.setParameter("bestillingUuid", bestillingUuid);
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
