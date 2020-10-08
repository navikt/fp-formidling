package no.nav.foreldrepenger.melding.kodeverk;

import static no.nav.foreldrepenger.melding.kodeverk.KodeverkFeil.FEILFACTORY;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.DiscriminatorValue;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.jpa.QueryHints;

import no.nav.vedtak.util.LRUCache;

@ApplicationScoped
public class KodeverkRepositoryImpl implements KodeverkRepository {

    private static final long CACHE_ELEMENT_LIVE_TIME = TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES);
    LRUCache<String, Kodeliste> kodelisteCache = new LRUCache<>(1000, CACHE_ELEMENT_LIVE_TIME);
    private EntityManager entityManager;

    public KodeverkRepositoryImpl() {
        // for CDI proxy
    }

    @Inject
    public KodeverkRepositoryImpl(EntityManager entityManager) {
        Objects.requireNonNull(entityManager, "entityManager"); //$NON-NLS-1$
        this.entityManager = entityManager;
    }

    @Override
    public <V extends Kodeliste> V finn(Class<V> cls, String kode) {
        String cacheKey = cls.getName() + kode;
        @SuppressWarnings("unchecked")
        Optional<V> fraCache = Optional.ofNullable((V) kodelisteCache.get(cacheKey));
        return fraCache.orElseGet(() -> {
            V finnEM = finnFraEM(cls, kode);
            kodelisteCache.put(cacheKey, finnEM);
            return finnEM;
        });
    }

    @Override
    public <V extends Kodeliste> List<V> finnListe(Class<V> cls, List<String> koder) {
        List<String> koderIkkeICache = new ArrayList<>();
        List<V> result = new ArrayList<>();
        koder.stream().forEach(kode -> {
            //For hver kode
            @SuppressWarnings("unchecked")
            V kodeliste = (V) kodelisteCache.get(kode);
            if (kodeliste == null) {
                koderIkkeICache.add(kode);
            } else {
                result.add(kodeliste);
            }
        });
        result.addAll(finnListeFraEm(cls, koderIkkeICache));
        return result;
    }


    private Query getLandkodeMappingQuery() {
        return entityManager.createNativeQuery(
                "SELECT k3.kode AS land_3bokstav, " +
                        "(SELECT k2.kode FROM kodeliste k2 " +
                        "WHERE k2.kodeverk = 'LANDKODE_ISO2' " +
                        "AND k2.navn = k3.navn ) AS land_2bokstav " +
                        "FROM kodeliste k3 " +
                        "WHERE k3.kodeverk = 'LANDKODER' " +
                        "AND EXISTS (SELECT k2.kode " +
                        "FROM kodeliste k2 " +
                        "WHERE k2.kodeverk = 'LANDKODE_ISO2' " +
                        "AND k2.navn = k3.navn)");
    }

    @Override
    public Map<String, String> hentLandkodeISO2TilLandkoderMap() {
        Query query = getLandkodeMappingQuery();

        int land3BokstavNr = 0;
        int land2BokstavNr = 1;

        HashMap<String, String> retval = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Object[]> koder = query.getResultList();
        for (Object[] k : koder) {
            retval.put((String) k[land2BokstavNr], (String) k[land3BokstavNr]);
        }
        return retval;
    }

    private <V extends Kodeliste> List<V> finnListeFraEm(Class<V> cls, List<String> koder) {
        CriteriaQuery<V> criteria = createCriteria(cls, koder);
        return entityManager.createQuery(criteria)
                .setHint(QueryHints.HINT_READONLY, "true")
                .getResultList();
    }

    private <V extends Kodeliste> V finnFraEM(Class<V> cls, String kode) {
        CriteriaQuery<V> criteria = createCriteria(cls, Collections.singletonList(kode));
        try {
            return entityManager.createQuery(criteria)
                    .setHint(QueryHints.HINT_READONLY, "true")
                    .getSingleResult();
        } catch (NoResultException e) {
            throw FEILFACTORY.kanIkkeFinneKodeverk(cls.getSimpleName(), kode, e).toException();
        }
    }

    protected <V extends Kodeliste> CriteriaQuery<V> createCriteria(Class<V> cls, List<String> koder) {
        return createCriteria(cls, "kode", koder);
    }

    protected <V extends Kodeliste> CriteriaQuery<V> createCriteria(Class<V> cls, String felt, List<String> koder) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<V> criteria = builder.createQuery(cls);

        DiscriminatorValue discVal = cls.getDeclaredAnnotation(DiscriminatorValue.class);
        Objects.requireNonNull(discVal, "Mangler @DiscriminatorValue i klasse:" + cls); //$NON-NLS-1$
        String kodeverk = discVal.value();
        Root<V> from = criteria.from(cls);
        criteria.where(builder.and(
                builder.equal(from.get("kodeverk"), kodeverk), //$NON-NLS-1$
                from.get(felt).in(koder))); // $NON-NLS-1$
        return criteria;
    }

}
