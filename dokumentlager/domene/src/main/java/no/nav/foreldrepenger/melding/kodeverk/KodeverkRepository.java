package no.nav.foreldrepenger.melding.kodeverk;

import java.util.List;
import java.util.Map;

/**
 * Få tilgang til kodeverk.
 */
public interface KodeverkRepository {

    /**
     * Finn instans av Kodeliste innslag for angitt kode verdi.
     */
    <V extends Kodeliste> V finn(Class<V> cls, String kode);

    /**
     * Finn instans av Kodeliste innslag for angitt Kodeliste.
     * For oppslag av fulle instanser fra de ufullstendige i hver konkrete subklasse av Kodeliste.
     */
    <V extends Kodeliste> V finn(Class<V> cls, V kodelisteKonstant);

    /**
     * Hent alle innslag for en gitt kodeliste og gitte koder.
     */
    <V extends Kodeliste> List<V> finnListe(Class<V> cls, List<String> koder);

    Map<String, String> hentLandkodeISO2TilLandkoderMap();

}
