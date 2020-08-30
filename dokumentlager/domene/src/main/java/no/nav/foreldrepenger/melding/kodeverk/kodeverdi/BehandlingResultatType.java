package no.nav.foreldrepenger.melding.kodeverk.kodeverdi;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public enum BehandlingResultatType implements Kodeverdi {

    IKKE_FASTSATT("IKKE_FASTSATT", "Ikke fastsatt"),
    INNVILGET("INNVILGET", "Innvilget"),
    AVSLÅTT("AVSLÅTT", "Avslått"),
    OPPHØR("OPPHØR", "Opphør"),
    HENLAGT_SØKNAD_TRUKKET("HENLAGT_SØKNAD_TRUKKET", "Henlagt, søknaden er trukket"),
    HENLAGT_FEILOPPRETTET("HENLAGT_FEILOPPRETTET", "Henlagt, søknaden er feilopprettet"),
    HENLAGT_BRUKER_DØD("HENLAGT_BRUKER_DØD", "Henlagt, brukeren er død"),
    MERGET_OG_HENLAGT("MERGET_OG_HENLAGT", "Mottatt ny søknad"),
    HENLAGT_SØKNAD_MANGLER("HENLAGT_SØKNAD_MANGLER", "Henlagt søknad mangler"),
    FORELDREPENGER_ENDRET("FORELDREPENGER_ENDRET", "Sak er endret"),
    INGEN_ENDRING("INGEN_ENDRING", "Ingen endring"),
    MANGLER_BEREGNINGSREGLER("MANGLER_BEREGNINGSREGLER", "Mangler beregningsregler"),

    // Klage
    KLAGE_AVVIST("KLAGE_AVVIST", "Klage er avvist"),
    KLAGE_MEDHOLD("KLAGE_MEDHOLD", "Medhold"),
    KLAGE_YTELSESVEDTAK_OPPHEVET("KLAGE_YTELSESVEDTAK_OPPHEVET", "Ytelsesvedtak opphevet"),
    KLAGE_YTELSESVEDTAK_STADFESTET("KLAGE_YTELSESVEDTAK_STADFESTET", "Ytelsesvedtak stadfestet"),
    KLAGE_TILBAKEKREVING_VEDTAK_STADFESTET("KLAGE_TILBAKEKREVING_VEDTAK_STADFESTET", "Vedtak tilbakekreving stadfestet"), // Brukes av kun Tilbakekreving eller Tilbakekreving Revurdering
    HENLAGT_KLAGE_TRUKKET("HENLAGT_KLAGE_TRUKKET", "Henlagt, klagen er trukket"),
    DELVIS_MEDHOLD_I_KLAGE("DELVIS_MEDHOLD_I_KLAGE", "Delvis medhold i klage"),
    HJEMSENDE_UTEN_OPPHEVE("HJEMSENDE_UTEN_OPPHEVE", "Behandlingen er hjemsendt"),
    UGUNST_MEDHOLD_I_KLAGE("UGUNST_MEDHOLD_I_KLAGE", "Ugunst medhold i klage"),

    // Anke
    ANKE_AVVIST("ANKE_AVVIST", "Anke er avvist"),
    ANKE_OMGJOER("ANKE_OMGJOER", "Bruker har fått omgjøring i anke"),
    ANKE_OPPHEVE_OG_HJEMSENDE("ANKE_OPPHEVE_OG_HJEMSENDE", "Bruker har fått vedtaket opphevet og hjemsendt i anke"),
    ANKE_YTELSESVEDTAK_STADFESTET("ANKE_YTELSESVEDTAK_STADFESTET", "Anken er stadfestet/opprettholdt"),
    ANKE_DELVIS_OMGJOERING_TIL_GUNST("ANKE_DELVIS_OMGJOERING_TIL_GUNST", "Anke er delvis omgjøring, til gunst"),
    ANKE_TIL_UGUNST("ANKE_TIL_UGUNST", "Gunst omgjør i anke"),

    // Innsyn
    INNSYN_INNVILGET("INNSYN_INNVILGET", "Innsynskrav er innvilget"),
    INNSYN_DELVIS_INNVILGET("INNSYN_DELVIS_INNVILGET", "Innsynskrav er delvis innvilget"),
    INNSYN_AVVIST("INNSYN_AVVIST", "Innsynskrav er avvist"),
    HENLAGT_INNSYN_TRUKKET("HENLAGT_INNSYN_TRUKKET", "Henlagt, innsynskrav er trukket"),

    ;

    private static final Set<BehandlingResultatType> HENLEGGELSESKODER_FOR_SØKNAD = Set.of(HENLAGT_SØKNAD_TRUKKET, HENLAGT_FEILOPPRETTET, HENLAGT_BRUKER_DØD, HENLAGT_SØKNAD_MANGLER, MANGLER_BEREGNINGSREGLER);
    private static final Set<BehandlingResultatType> HENLEGGELSESKODER_FOR_KLAGE = Set.of(HENLAGT_KLAGE_TRUKKET, HENLAGT_FEILOPPRETTET);
    private static final Set<BehandlingResultatType> HENLEGGELSESKODER_FOR_INNSYN = Set.of(HENLAGT_INNSYN_TRUKKET, HENLAGT_FEILOPPRETTET);
    private static final Set<BehandlingResultatType> ALLE_HENLEGGELSESKODER = Set.of(HENLAGT_SØKNAD_TRUKKET, HENLAGT_FEILOPPRETTET, HENLAGT_BRUKER_DØD, HENLAGT_KLAGE_TRUKKET, MERGET_OG_HENLAGT, HENLAGT_SØKNAD_MANGLER, HENLAGT_INNSYN_TRUKKET, MANGLER_BEREGNINGSREGLER);
    private static final Set<BehandlingResultatType> KLAGE_KODER = Set.of(KLAGE_MEDHOLD, KLAGE_YTELSESVEDTAK_STADFESTET, KLAGE_YTELSESVEDTAK_OPPHEVET, KLAGE_AVVIST, DELVIS_MEDHOLD_I_KLAGE, HJEMSENDE_UTEN_OPPHEVE, UGUNST_MEDHOLD_I_KLAGE);
    private static final Set<BehandlingResultatType> INNSYN_KODER = Set.of(INNSYN_INNVILGET, INNSYN_DELVIS_INNVILGET, INNSYN_AVVIST);
    private static final Set<BehandlingResultatType> INNVILGET_KODER = Set.of(INNVILGET, FORELDREPENGER_ENDRET);

    private static final Map<String, BehandlingResultatType> KODER = new LinkedHashMap<>();

    public static final String KODEVERK = "BEHANDLING_RESULTAT_TYPE";

    static {
        for (var v : values()) {
            if (KODER.putIfAbsent(v.kode, v) != null) {
                throw new IllegalArgumentException("Duplikat : " + v.kode);
            }
        }
    }

    @JsonIgnore
    private String navn;

    private String kode;

    private BehandlingResultatType(String kode, String navn) {
        this.kode = kode;
        this.navn = navn;
    }

    @JsonCreator
    public static BehandlingResultatType fraKode(@JsonProperty("kode") String kode) {
        if (kode == null) {
            return null;
        }
        var ad = KODER.get(kode);
        if (ad == null) {
            throw new IllegalArgumentException("Ukjent BehandlingResultatType: " + kode);
        }
        return ad;
    }

    @Override
    public String getNavn() {
        return navn;
    }

    @JsonProperty
    @Override
    public String getKode() {
        return kode;
    }

    @JsonProperty
    @Override
    public String getKodeverk() {
        return KODEVERK;
    }

    public static void main(String[] args) {
        System.out.println(KODER.keySet());
    }

    public static Set<BehandlingResultatType> getAlleHenleggelseskoder() {
        return ALLE_HENLEGGELSESKODER;
    }

    public static Set<BehandlingResultatType> getHenleggelseskoderForKlage() {
        return HENLEGGELSESKODER_FOR_KLAGE;
    }

    public static Set<BehandlingResultatType> getHenleggelseskoderForInnsyn() {
        return HENLEGGELSESKODER_FOR_INNSYN;
    }

    public static Set<BehandlingResultatType> getHenleggelseskoderForSøknad() {
        return HENLEGGELSESKODER_FOR_SØKNAD;
    }

    public static Set<BehandlingResultatType> getKlageKoder() {
        return KLAGE_KODER;
    }

    public static Set<BehandlingResultatType> getInnsynKoder() {
        return INNSYN_KODER;
    }

    public static Set<BehandlingResultatType> getInnvilgetKoder() {
        return INNVILGET_KODER;
    }

    public boolean erHenlagt() {
        return ALLE_HENLEGGELSESKODER.contains(this);
    }
}
