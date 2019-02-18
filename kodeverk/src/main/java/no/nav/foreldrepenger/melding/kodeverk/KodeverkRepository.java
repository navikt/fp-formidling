package no.nav.foreldrepenger.melding.kodeverk;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
     * Finn instans av Kodeliste innslag for angitt offisiell kode verdi.
     */
    <V extends Kodeliste> V finnForKodeverkEiersKode(Class<V> cls, String offisiellKode);

    /**
     * Finn instans av Kodeliste innslag for angitt offisiell kode verdi, eller en default value hvis offisiell kode ikke git treff.
     */
    <V extends Kodeliste> V finnForKodeverkEiersKode(Class<V> cls, String offisiellKode, V defaultValue);

    /**
     * Finn instans av Kodeliste innslag for angitt offisiell kode verdi.
     */
    <V extends Kodeliste> List<V> finnForKodeverkEiersKoder(Class<V> cls, String... offisiellKoder);

    /**
     * Hent alle innslag for en gitt kodeliste og gitte koder.
     */
    <V extends Kodeliste> List<V> finnListe(Class<V> cls, List<String> koder);

    /**
     * Hent alle innslag for en gitt Kodeliste.
     */
    <V extends Kodeliste> List<V> hentAlle(Class<V> cls);

    /**
     * Henter et map med kobling mellom kodeverk, NB! benytter refleksjon for å utlede DISCRIMINATOR til hvilket kodeverk den skal slå opp.
     * Det må ligge innslag i KODELISTE_RELASJON for å få treff, støtter bare mapping fra kodeliste1 til kodeliste2.
     */
    <V extends Kodeliste, K extends Kodeliste> Map<V, Set<K>> hentKodeRelasjonForKodeverk(Class<V> kodeliste1, Class<K> kodeliste2);

    /**
     * Finn kode, return er optional empty hvis ikke finnes.
     */
    <V extends Kodeliste> Optional<V> finnOptional(Class<V> cls, String kode);
}
