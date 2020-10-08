package no.nav.foreldrepenger.melding.geografisk;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.hibernate.jpa.QueryHints;

import no.nav.vedtak.felles.jpa.HibernateVerktøy;
import no.nav.vedtak.konfig.Tid;

@ApplicationScoped
public class PoststedKodeverkRepository {

    private static final String SYNK_POSTNUMMER = "SYNK";

    private EntityManager entityManager;

    PoststedKodeverkRepository() {
        // for CDI proxy
    }

    @Inject
    public PoststedKodeverkRepository(EntityManager entityManager) {
        Objects.requireNonNull(entityManager, "entityManager"); //$NON-NLS-1$
        this.entityManager = entityManager;
    }

    public List<Poststed> hentAllePostnummer() {
        TypedQuery<Poststed> query = entityManager.createQuery("from Poststed p where poststednummer <> :postnr", Poststed.class)
                .setParameter("postnr", SYNK_POSTNUMMER)
                .setHint(QueryHints.HINT_READONLY, "true");
        return query.getResultList();
    }

    public Optional<Poststed> finnPostnummer(String postnummer) {
        TypedQuery<Poststed> query = entityManager.createQuery("from Poststed p where poststednummer = :postnr", Poststed.class)
                .setParameter("postnr", postnummer)
                .setHint(QueryHints.HINT_READONLY, "true");
        return HibernateVerktøy.hentUniktResultat(query);
    }

    public void lagrePostnummer(Poststed postnummer) {
        entityManager.persist(postnummer);
    }

    public LocalDate getPostnummerKodeverksDato() {
        return finnPostnummer(SYNK_POSTNUMMER).map(Poststed::getGyldigFom).orElse(Tid.TIDENES_BEGYNNELSE);
    }

    public void setPostnummerKodeverksDato(String versjon, LocalDate synkDato) {
        var postnummer = finnPostnummer(SYNK_POSTNUMMER).orElseGet(() -> new Poststed(SYNK_POSTNUMMER, versjon, synkDato, Tid.TIDENES_ENDE));
        postnummer.setGyldigFom(synkDato);
        postnummer.setPoststednavn("VERSJON" + versjon);
        entityManager.persist(postnummer);
    }
}
