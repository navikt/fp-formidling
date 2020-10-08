package no.nav.foreldrepenger.melding.kodeverk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.NoResultException;

/**
 * Støtte for enhetstester som trenger kodeverk/liste instanser med mer fullstendig tilstand enn konstantene
 * (de statiske instansene i hver konkrete Kodeliste), men uten å måtte lese fra databasen.
 */
public class KodeverkTestHelper {

    public static KodeverkRepository getKodeverkRepository() {
        return new MockKodeverkRepository();
    }

    private static class MockKodeverkRepository implements KodeverkRepository {

        private static final List<Class<?>> initialisert = new ArrayList<>(); // NOSONAR
        private static Map<Class<? extends Kodeliste>, Map<String, Kodeliste>> kodelistePrKodePrKlasse = new HashMap<>(); // NOSONAR

        MockKodeverkRepository() {
        }

        private static synchronized void lazyLoadKodeliste(Class<? extends Kodeliste> cls) {

            if (initialisert.contains(cls)) {
                return;
            }

            List<? extends Kodeliste> kodelister = new KodeverkFraJson().lesKodeverkFraFil(cls);
            Map<String, Kodeliste> kodelistePrKode = new HashMap<>();
            kodelister.forEach(kodeliste -> {
                kodelistePrKode.put(kodeliste.getKode(), kodeliste);
            });
            kodelistePrKodePrKlasse.put(cls, kodelistePrKode);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <V extends Kodeliste> V finn(Class<V> cls, String kode) {
            V kodeliste = null;
            Map<String, Kodeliste> kodelisterForKlasse = getKodelistePrKodePrKlasse(cls);
            if (kodelisterForKlasse != null) {
                kodeliste = (V) kodelisterForKlasse.get(kode);
            }
            if (kodeliste != null) {
                return kodeliste;
            } else {
                throw new NoResultException("ingen kode \"" + kode + "\" for " + cls.getName());
            }
        }

        @Override
        public <V extends Kodeliste> List<V> finnListe(Class<V> cls, List<String> koder) {
            Map<String, Kodeliste> kodelisterForKlasse = getKodelistePrKodePrKlasse(cls);
            @SuppressWarnings("unchecked")
            List<V> resultList = kodelisterForKlasse.entrySet().stream().filter(e -> koder.contains(e.getKey()))
                    .map(e -> (V) e.getValue())
                    .collect(Collectors.toList());
            return resultList;
        }

        public synchronized Map<String, Kodeliste> getKodelistePrKodePrKlasse(Class<? extends Kodeliste> cls) {
            lazyLoadKodeliste(cls);
            return kodelistePrKodePrKlasse.get(cls);
        }

        @Override
        public Map<String, String> hentLandkodeISO2TilLandkoderMap() {
            throw new UnsupportedOperationException();
        }
    }
}
