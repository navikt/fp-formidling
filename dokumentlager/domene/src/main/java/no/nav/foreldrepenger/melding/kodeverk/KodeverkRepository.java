package no.nav.foreldrepenger.melding.kodeverk;

import java.util.List;

/**
 * Få tilgang til kodeverk.
 */
public interface KodeverkRepository {

    /**
     * Finn instans av Kodeliste innslag for angitt kode verdi.
     */
    <V extends Kodeliste> V finn(Class<V> cls, String kode);

    /**
     * Hent alle innslag for en gitt kodeliste og gitte koder.
     */
    <V extends Kodeliste> List<V> finnListe(Class<V> cls, List<String> koder);

}
