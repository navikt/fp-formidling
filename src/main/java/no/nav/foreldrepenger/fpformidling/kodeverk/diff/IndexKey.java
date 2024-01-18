package no.nav.foreldrepenger.fpformidling.kodeverk.diff;

/**
 * Bruk for å hente et key for et innslag i en collection (hvis ikke vil key ved diff være basert på index, noe
 * som kan være mer ustabilt).
 * <p>
 * {@link #getIndexKey()} bør være unikt i En collection lokalt (trenger ikke være globalt unik, kun i kontekst av collection det brukes (eks. gitt en parent)).
 */
public interface IndexKey {

    /**
     * Hjelpe metode for å effektivt generere keys.
     */
    static String createKey(Object... keyParts) {
        return IndexKeyComposer.createKey(keyParts);
    }

    /**
     * Returnerer et lokal indexkey for et objekt i en collection.
     * Må være en stabil string key
     */
    String getIndexKey();

}
