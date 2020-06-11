package no.nav.foreldrepenger.melding.geografisk;

import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import no.nav.foreldrepenger.melding.aktør.AdresseType;
import no.nav.vedtak.felles.jpa.HibernateVerktøy;
import no.nav.vedtak.felles.jpa.VLPersistenceUnit;

@ApplicationScoped
public class PoststedKodeverkRepository {

    private EntityManager entityManager;

    PoststedKodeverkRepository() {
        // for CDI proxy
    }

    @Inject
    public PoststedKodeverkRepository(@VLPersistenceUnit EntityManager entityManager) {
        Objects.requireNonNull(entityManager, "entityManager"); //$NON-NLS-1$
        this.entityManager = entityManager;
    }

    public Optional<Poststed> finnPoststed(String postnummer) {
        TypedQuery<Poststed> query = entityManager.createQuery("from Poststed where kode=:kode",
                Poststed.class);
        query.setParameter("kode", postnummer);
        return HibernateVerktøy.hentUniktResultat(query);
    }

    public Optional<AdresseType> finnAdresseType(String kode) {
        TypedQuery<AdresseType> query = entityManager.createQuery("from AdresseType where kode=:kode",
                AdresseType.class);
        query.setParameter("kode", kode);
        return HibernateVerktøy.hentUniktResultat(query);
    }
}
