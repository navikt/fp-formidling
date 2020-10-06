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

import no.nav.foreldrepenger.melding.aktør.AdresseType;
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

    public List<Postnummer> hentAllePostnummer() {
        TypedQuery<Postnummer> query = entityManager.createQuery("from Postnummer p where poststednummer <> :postnr", Postnummer.class)
                .setParameter("postnr", SYNK_POSTNUMMER)
                .setHint(QueryHints.HINT_READONLY, "true");
        return query.getResultList();
    }

    public Optional<Postnummer> finnPostnummer(String postnummer) {
        TypedQuery<Postnummer> query = entityManager.createQuery("from Postnummer p where poststednummer = :postnr", Postnummer.class)
                .setParameter("postnr", postnummer)
                .setHint(QueryHints.HINT_READONLY, "true");
        return HibernateVerktøy.hentUniktResultat(query);
    }

    public void lagrePostnummer(Postnummer postnummer) {
        entityManager.persist(postnummer);
    }

    public LocalDate getPostnummerKodeverksDato() {
        return finnPostnummer(SYNK_POSTNUMMER).map(Postnummer::getGyldigFom).orElse(Tid.TIDENES_BEGYNNELSE);
    }

    public void setPostnummerKodeverksDato(String versjon, LocalDate synkDato) {
        var postnummer = finnPostnummer(SYNK_POSTNUMMER).orElseGet(() -> new Postnummer(SYNK_POSTNUMMER, versjon, synkDato, Tid.TIDENES_ENDE));
        postnummer.setGyldigFom(synkDato);
        postnummer.setPoststednavn("VERSJON" + versjon);
        entityManager.persist(postnummer);
    }
}
