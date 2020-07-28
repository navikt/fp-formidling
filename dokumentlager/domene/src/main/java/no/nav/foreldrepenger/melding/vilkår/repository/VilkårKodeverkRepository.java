package no.nav.foreldrepenger.melding.vilkår.repository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.foreldrepenger.melding.vilkår.VilkårType;

@ApplicationScoped
public class VilkårKodeverkRepository {

    private EntityManager entityManager;
    private KodeverkRepository kodeverkRepository;

    VilkårKodeverkRepository() {
        // for CDI proxy
    }

    @Inject
    public VilkårKodeverkRepository(EntityManager entityManager, KodeverkRepository kodeverkRepository) {
        this.entityManager = entityManager;
        this.kodeverkRepository = kodeverkRepository;
    }

    public List<VilkårType> finnVilkårTypeListe(String avslagsårsakKode) {
        // Vi skal støtte at relasjon mellom kode1 og kode2 kan settes inn i begge rekkefølger. Dvs at både avslagårsak
        // og vilkårtype kan være både kode1 og kode2.
        final Query query = entityManager
                .createNativeQuery("SELECT k.kode1 FROM KODELISTE_RELASJON k WHERE k.kode2 =:avslagsarsak AND k.kodeverk1=:vilkar_type " +
                        "UNION SELECT k.kode2 FROM KODELISTE_RELASJON k WHERE k.kode1 =:avslagsarsak  AND k.kodeverk2=:vilkar_type");
        query.setParameter("avslagsarsak", avslagsårsakKode);
        query.setParameter("vilkar_type", "VILKAR_TYPE");
        @SuppressWarnings("unchecked")
        List<String> koder = query.getResultList();
        List<String> unikeKoder = koder.stream().distinct().collect(Collectors.toList());

        if (unikeKoder.isEmpty()) {
            return Collections.emptyList();
        }
        return kodeverkRepository.finnListe(VilkårType.class, unikeKoder);
    }
}
